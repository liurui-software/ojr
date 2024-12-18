package com.ojr.simpsnmp;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Simple SNMP agent
 */
public class SimpSnmp implements Closeable {
    private final String endpoint;

    private final Target<Address> myTarget;
    private final TransportMapping<UdpAddress> transport;
    private final Snmp protocol;

    private final SnmpOption option;

    public SimpSnmp(String endpoint, SnmpOption option) throws IOException {
        String securityName = option.getSecurityName();
        this.endpoint = endpoint;
        this.option = option;

        transport = new DefaultUdpTransportMapping();
        protocol = new Snmp(transport);
        if (option.getVersion() == SnmpConstants.version3) {
            if (option.getSecurityLevel() != SecurityLevel.NOAUTH_NOPRIV) {
                SecurityProtocols sp = new SecurityProtocols(SecurityProtocols.SecurityProtocolSet.maxCompatibility);
                /*sp.addDefaultProtocols();
                sp.addAuthenticationProtocol(new AuthSHA());
                sp.addAuthenticationProtocol(new AuthMD5());
                sp.addAuthenticationProtocol(new AuthHMAC128SHA224());
                sp.addAuthenticationProtocol(new AuthHMAC192SHA256());
                sp.addAuthenticationProtocol(new AuthHMAC256SHA384());
                sp.addAuthenticationProtocol(new AuthHMAC384SHA512());
                sp.addPrivacyProtocol(new PrivDES());
                sp.addPrivacyProtocol(new Priv3DES());
                sp.addPrivacyProtocol(new PrivAES128());
                sp.addPrivacyProtocol(new PrivAES192());
                sp.addPrivacyProtocol(new PrivAES256());*/
                USM usm = new USM(sp, new OctetString(MPv3.createLocalEngineID()), 0);
                SecurityModels.getInstance().addSecurityModel(usm);
                transport.listen();
                protocol.getUSM().addUser(new UsmUser(
                        new OctetString(securityName), new OID(option.getAuthType()),
                        new OctetString(option.getAuthPassword()), new OID(option.getPrivacyType()),
                        new OctetString(option.getPrivacyPassword())));
            }
        } else {
            transport.listen();
        }

        if (option.getVersion() != SnmpConstants.version3) {
            myTarget = new CommunityTarget<>();
            ((CommunityTarget<?>) myTarget).setCommunity(new OctetString(option.getCommunity()));
        } else {
            myTarget = new UserTarget<>();
            myTarget.setSecurityLevel(option.getSecurityLevel());
            myTarget.setSecurityName(new OctetString(securityName));
        }

        myTarget.setAddress(GenericAddress.parse(this.endpoint));
        myTarget.setRetries(option.getRetries());
        myTarget.setTimeout(option.getTimeout());
        myTarget.setVersion(option.getVersion());
    }

    public SimpSnmp(String endpoint) throws IOException {
        this(endpoint, new SnmpOption());
    }

    public String getEndpoint() {
        return endpoint;
    }

    public TransportMapping<UdpAddress> getTransport() {
        return transport;
    }

    public Snmp getProtocol() {
        return protocol;
    }

    public SnmpOption getOption() {
        return option;
    }

    public void close() throws IOException {
        transport.close();
    }

    public Map<OID, SnmpValue> queryScalarOids(List<OID> oids) throws IOException {
        PDU request;
        if (option.getVersion() < SnmpConstants.version3) {
            request = new PDU();
        } else {
            request = new ScopedPDU();
        }
        request.setType(PDU.GET);
        for (OID oid : oids) {
            request.add(new VariableBinding(oid));
        }
        ResponseEvent<Address> responseEvent = protocol.send(request, myTarget);
        PDU response = responseEvent.getResponse();
        Map<OID, SnmpValue> result = new HashMap<>();
        if (response != null) {
            for (int i = 0; i < response.size(); i++) {
                VariableBinding vb = response.get(i);
                SnmpValue snmpValue = SnmpValue.getValue(vb);
                if (snmpValue != null && snmpValue.isNormal()) {
                    result.put(snmpValue.getOid(), snmpValue);
                }
            }
        }
        return result;
    }

    public Map<OID, SnmpValue> queryScalarOids(OID... oids) throws IOException {
        return queryScalarOids(Arrays.stream(oids).collect(Collectors.toList()));
    }

    private static OID findParentOid(List<OID> oids, OID oid1) {
        for (OID oid : oids) {
            if (oid1.startsWith(oid)) {
                return oid;
            }
        }
        return oid1;
    }

    public List<Map<OID, SnmpValue>> queryColumnOids(List<OID> oids) throws IOException {
        TableUtils tableUtils = new TableUtils(protocol, new DefaultPDUFactory(PDU.GETBULK));

        List<TableEvent> events = tableUtils.getTable(myTarget, oids.toArray(OID[]::new), null, null);

        List<Map<OID, SnmpValue>> result = new ArrayList<>();
        for (TableEvent event : events) {
            if (event.isError()) {
                System.err.println("SNMP Error: " + event.getErrorMessage());
            } else {
                VariableBinding[] vbs = event.getColumns();
                if (vbs != null) {
                    Map<OID, SnmpValue> result1 = new HashMap<>();
                    for (VariableBinding vb : vbs) {
                        SnmpValue snmpValue = SnmpValue.getValue(vb);
                        if (snmpValue != null && snmpValue.isNormal()) {
                            result1.put(findParentOid(oids, snmpValue.getOid()), snmpValue);
                        }
                    }
                    result.add(result1);
                }
            }
        }
        return result;
    }

    public List<Map<OID, SnmpValue>> queryColumnOids(OID... oids) throws IOException {
        return queryColumnOids(Arrays.stream(oids).collect(Collectors.toList()));
    }

}