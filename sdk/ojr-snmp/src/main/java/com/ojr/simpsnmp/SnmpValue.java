package com.ojr.simpsnmp;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

/**
 * SNMP value
 */
public class SnmpValue {
    private final int type;
    private final long longValue;

    private final double doubleValue;

    private final String stringValue;

    public static final int TYPE_LONG = 1;
    public static final int TYPE_DOUBLE = 2;
    public static final int TYPE_STRING = 3;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_NOT_FOUND = 1;

    private OID oid = null;
    private int status = STATUS_NORMAL;

    public SnmpValue(long longValue) {
        this.type = TYPE_LONG;
        this.longValue = longValue;
        this.doubleValue = longValue;
        this.stringValue = String.valueOf(longValue);
    }

    public SnmpValue(double doubleValue) {
        this.type = TYPE_DOUBLE;
        this.doubleValue = doubleValue;
        this.longValue = (long) doubleValue;
        this.stringValue = String.valueOf(doubleValue);
    }

    public SnmpValue(String stringValue, boolean parse) {
        this.type = TYPE_STRING;
        this.stringValue = stringValue;
        if (parse) {
            this.longValue = Long.parseLong(stringValue);
            this.doubleValue = Double.parseDouble(stringValue);
        } else {
            this.longValue = 0;
            this.doubleValue = 0;
        }
    }

    public SnmpValue(String stringValue) {
        this(stringValue, false);
    }

    public int getType() {
        return type;
    }

    public long toLong() {
        return longValue;
    }

    public double toDouble() {
        return doubleValue;
    }

    public String toString() {
        return stringValue;
    }

    public OID getOid() {
        return oid;
    }

    public SnmpValue setOid(OID oid) {
        this.oid = oid;
        return this;
    }

    private static double opaqueHexToFloat(String hexString) {
        String removeColonString = hexString.replaceAll("[:\\s]", "");
        // Remove the prefix '9f78'
        String cleanHexString = removeColonString.substring(6);

        // Convert hex string to byte array
        byte[] bytes = new byte[cleanHexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(cleanHexString.substring(index, index + 2), 16);
            bytes[i] = (byte) j;
        }

        // Convert byte array to float
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN); // Ensure big-endian order
        return buffer.getFloat();
    }

    public static SnmpValue getValue(VariableBinding vb) {
        Variable v = vb.getVariable();
        if (v == null) {
            return null;
        }
        String type = v.getSyntaxString();
        SnmpValue snmpValue;
        if ("NoSuchObject".equals(type)) {
            snmpValue = new SnmpValue(v.toString());
            snmpValue.setStatus(SnmpValue.STATUS_NOT_FOUND);
        } else if ("Opaque".equals(type)) {
            snmpValue = new SnmpValue(opaqueHexToFloat(v.toString()));
        } else if ("OCTET STRING".equals(type)) {
            snmpValue = new SnmpValue(v.toString());
        } else {
            snmpValue = new SnmpValue(v.toLong());
        }
        return snmpValue.setOid(new OID(vb.getOid()));
    }

    public static String getString(Map<OID, SnmpValue> map, OID key, String defaultValue) {
        SnmpValue result = map.get(key);
        if (result != null) {
            return result.toString();
        }
        return defaultValue;
    }

    public static Double getDouble(Map<OID, SnmpValue> map, OID key, Double defaultValue) {
        SnmpValue result = map.get(key);
        if (result != null) {
            return result.toDouble();
        }
        return defaultValue;
    }

    public static Long getLong(Map<OID, SnmpValue> map, OID key, Long defaultValue) {
        SnmpValue result = map.get(key);
        if (result != null) {
            return result.toLong();
        }
        return defaultValue;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isNormal(){
        return this.status == STATUS_NORMAL;
    }
}


