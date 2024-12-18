package com.ojr.simpsnmp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.snmp4j.smi.OID;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

// By its nature, this is an integration tests.
public class SimpSnmpTest {
    private static final String FILE_SNMP_HOST = "/tmp/snmphost";
    private static SimpSnmp simpSnmp = null;

    private static void noHostErr() {
        System.err.println("cannot access file in " + FILE_SNMP_HOST);
    }

    @BeforeAll
    public static void setupSnmpServer() {
        List<String> lines;
        try {
            String snmpHost;
            lines = Files.readAllLines(Paths.get(FILE_SNMP_HOST), StandardCharsets.UTF_8);
            snmpHost = lines.get(0).trim();
            System.out.println("snmpHost: " + snmpHost);
            simpSnmp = new SimpSnmp(snmpHost);
        } catch (Exception e) {
            noHostErr();
        }
    }

    @Test
    void testQueryScalarOids() throws Exception {
        if (simpSnmp == null) {
            noHostErr();
            return;
        }
        Map<OID, SnmpValue> result = simpSnmp.queryScalarOids(Oid.HOST_NAME, Oid.OS_TYPE,
                Oid.CPU_TIME__USER, Oid.CPU_LOAD_1M, Oid.MEMORY_USAGE__TOTAL);
        assertEquals(5, result.size());
        System.out.println("=== testQueryScalarOids ===");
        result.forEach((key, value) -> System.out.println(key + ":" + value));
    }

    @Test
    void testQueryColumnOids1() throws Exception {
        if (simpSnmp == null) {
            noHostErr();
            return;
        }
        List<Map<OID, SnmpValue>> result = simpSnmp.queryColumnOids(Oid.DISKDEVICE, Oid.DISK_IO__READ, Oid.DISK_IO__WRITE);
        assertNotEquals(0, result.size());
        System.out.println("=== testQueryColumnOids1 ===");
        for (Map<OID, SnmpValue> result1 : result) {
            System.out.println("------>>");
            result1.forEach((key, value) -> System.out.println(key + ":" + value));
        }
    }

    @Test
    void testQueryColumnOids2() throws Exception {
        if (simpSnmp == null) {
            noHostErr();
            return;
        }
        List<Map<OID, SnmpValue>> result = simpSnmp.queryColumnOids(
                Oid.FILESYSTEMDEVICE, Oid.FILESYSTEM_USAGE__USED, Oid.FILESYSTEM_USAGE__ALL, Oid.FILESYSTEM_USAGE__UNIT);
        assertNotEquals(0, result.size());
        System.out.println("=== testQueryColumnOids2 ===");
        for (Map<OID, SnmpValue> result1 : result) {
            System.out.println("------>>");
            result1.forEach((key, value) -> System.out.println(key + ":" + value));
        }
    }

    @Test
    void testQueryColumnOids3() throws Exception {
        if (simpSnmp == null) {
            noHostErr();
            return;
        }
        List<Map<OID, SnmpValue>> result = simpSnmp.queryColumnOids(Oid.NETWORKDEVICE,
                Oid.NETWORK_DROPPED_RECEIVE, Oid.NETWORK_DROPPED_TRANSMIT,
                Oid.NETWORK_ERRORS_RECEIVE, Oid.NETWORK_ERRORS_TRANSMIT);
        assertNotEquals(0, result.size());
        System.out.println("=== testQueryColumnOids3 ===");
        for (Map<OID, SnmpValue> result1 : result) {
            System.out.println("------>>");
            result1.forEach((key, value) -> System.out.println(key + ":" + value));
        }
    }

    @Test
    void testQueryColumnOids4() throws Exception {
        if (simpSnmp == null) {
            noHostErr();
            return;
        }
        List<Map<OID, SnmpValue>> result = simpSnmp.queryColumnOids(Oid.NETWORKDEVICE,
                Oid.NETWORK_IO_RECEIVE, Oid.NETWORK_IO_TRANSMIT,
                Oid.NETWORK_PACKAGES_RECEIVE, Oid.NETWORK_PACKAGES_TRANSMIT);
        assertNotEquals(0, result.size());
        System.out.println("=== testQueryColumnOids4 ===");
        for (Map<OID, SnmpValue> result1 : result) {
            System.out.println("------>>");
            result1.forEach((key, value) -> System.out.println(key + ":" + value));
        }
    }

}
