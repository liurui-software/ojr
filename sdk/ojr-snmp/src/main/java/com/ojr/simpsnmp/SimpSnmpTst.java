package com.ojr.simpsnmp;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.fluent.SnmpBuilder;
import org.snmp4j.fluent.SnmpCompletableFuture;
import org.snmp4j.fluent.TargetBuilder;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

class SimpSnmpTst {
    public static void main(String[] args) throws IOException {
        SnmpBuilder snmpBuilder = new SnmpBuilder();
        Snmp snmp = snmpBuilder.udp().securityProtocols(
                SecurityProtocols.SecurityProtocolSet.maxCompatibility).v3().usm().build();

        Address targetAddress = GenericAddress.parse("udp:9.30.78.59/161");
        byte[] targetEngineID = snmp.discoverAuthoritativeEngineID(targetAddress, 1000);

        if (targetEngineID != null) {
            TargetBuilder<?> targetBuilder = snmpBuilder.target(targetAddress);

            Target<?> target = targetBuilder
                    .user("linuser", targetEngineID)
                    .auth(TargetBuilder.AuthProtocol.sha1).authPassphrase("linuserpass")
                    .priv(TargetBuilder.PrivProtocol.des).privPassphrase("linprivpass")
                    .done()
                    .timeout(1500).retries(2)
                    .build();
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);

            PDU pdu = targetBuilder.pdu().type(PDU.GET).contextName("").build();
            pdu.add(new VariableBinding(new OID(".1.3.6.1.2.1.1.1.0")));

            SnmpCompletableFuture snmpRequestFuture = SnmpCompletableFuture.send(snmp, target, pdu);
            try {
                List<VariableBinding> vbs = snmpRequestFuture.get().getAll();
                System.out.println("Received: " + snmpRequestFuture.getResponseEvent().getResponse());
            } catch (ExecutionException | InterruptedException ex) {
                System.err.println("Request failed: " + ex.getCause().getMessage());
            }
        } else {
            System.err.println("Timeout on engine ID discovery for " + targetAddress + ", GET not sent.");
        }
        snmp.close();

    }
}
