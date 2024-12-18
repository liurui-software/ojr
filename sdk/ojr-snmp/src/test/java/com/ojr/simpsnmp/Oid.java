package com.ojr.simpsnmp;

import org.snmp4j.smi.OID;

public class Oid {
    //scalar_oids:
    public static final OID CPU_TIME__USER = new OID(".1.3.6.1.4.1.2021.11.50.0");
    public static final OID CPU_TIME__SYSTEM = new OID(".1.3.6.1.4.1.2021.11.52.0");
    public static final OID CPU_TIME__IDLE = new OID(".1.3.6.1.4.1.2021.11.53.0");
    public static final OID CPU_TIME__NICE = new OID(".1.3.6.1.4.1.2021.11.51.0");
    public static final OID CPU_LOAD_1M = new OID(".1.3.6.1.4.1.2021.10.1.6.1");
    public static final OID CPU_LOAD_5M = new OID(".1.3.6.1.4.1.2021.10.1.6.2");
    public static final OID CPU_LOAD_15M = new OID(".1.3.6.1.4.1.2021.10.1.6.3");
    public static final OID MEMORY_USAGE__TOTAL = new OID(".1.3.6.1.4.1.2021.4.5.0");
    public static final OID MEMORY_USAGE__FREE = new OID(".1.3.6.1.4.1.2021.4.6.0");
    public static final OID MEMORY_USAGE__BUFFERED = new OID(".1.3.6.1.4.1.2021.4.14.0");
    public static final OID MEMORY_USAGE__CACHED = new OID(".1.3.6.1.4.1.2021.4.15.0");
    public static final OID HOST_NAME = new OID("1.3.6.1.2.1.1.5.0");
    public static final OID OS_TYPE = new OID("1.3.6.1.2.1.1.1.0");


    //column_oids:
    public static final OID DISK_IO__READ = new OID(".1.3.6.1.4.1.2021.13.15.1.1.3");
    public static final OID DISK_IO__WRITE = new OID(".1.3.6.1.4.1.2021.13.15.1.1.4");
    public static final OID DISKDEVICE = new OID(".1.3.6.1.4.1.2021.13.15.1.1.2");

    public static final OID FILESYSTEMDEVICE = new OID(".1.3.6.1.2.1.25.2.3.1.3");
    public static final OID FILESYSTEM_USAGE__USED = new OID(".1.3.6.1.2.1.25.2.3.1.6");
    public static final OID FILESYSTEM_USAGE__ALL = new OID(".1.3.6.1.2.1.25.2.3.1.5");
    public static final OID FILESYSTEM_USAGE__UNIT = new OID(".1.3.6.1.2.1.25.2.3.1.4");

    public static final OID NETWORKDEVICE = new OID(".1.3.6.1.2.1.2.2.1.2");
    public static final OID NETWORK_DROPPED_RECEIVE = new OID(".1.3.6.1.2.1.2.2.1.13");
    public static final OID NETWORK_DROPPED_TRANSMIT = new OID(".1.3.6.1.2.1.2.2.1.19");
    public static final OID NETWORK_ERRORS_RECEIVE = new OID(".1.3.6.1.2.1.2.2.1.14");
    public static final OID NETWORK_ERRORS_TRANSMIT = new OID(".1.3.6.1.2.1.2.2.1.20");
    public static final OID NETWORK_IO_RECEIVE = new OID(".1.3.6.1.2.1.2.2.1.10");
    public static final OID NETWORK_IO_TRANSMIT = new OID(".1.3.6.1.2.1.2.2.1.16");
    public static final OID NETWORK_PACKAGES_RECEIVE = new OID(".1.3.6.1.2.1.2.2.1.11");
    public static final OID NETWORK_PACKAGES_TRANSMIT = new OID(".1.3.6.1.2.1.2.2.1.17");
}
