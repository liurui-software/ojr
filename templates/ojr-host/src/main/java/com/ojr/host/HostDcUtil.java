package com.ojr.host;

import java.io.*;

public class HostDcUtil {

    public static class MeterName{
        public static final String CPU = "cpu";
        public static final String MEMORY = "memory";
        public static final String NETWORK = "network";
        public static final String LOAD = "load";
        public static final String DISK = "disk";
        public static final String FILESYSTEM = "filesystem";
        public static final String PROCESSES = "processes";
        public static final String PAGING = "paging";
    }

    /* Configurations for Metrics:
     */
    public static final String UNIT_S = "s";
    public static final String UNIT_BY = "By";
    public static final String UNIT_1 = "1";

    public static final String SYSTEM_CPU_TIME_NAME = "system.cpu.time";
    public static final String SYSTEM_CPU_TIME_DESC = "Seconds each logical CPU spent on each mode";
    public static final String SYSTEM_CPU_TIME_UNIT = UNIT_S;

    public static final String SYSTEM_MEMORY_USAGE_NAME = "system.memory.usage";
    public static final String SYSTEM_MEMORY_USAGE_DESC = "Bytes of memory in use";
    public static final String SYSTEM_MEMORY_USAGE_UNIT = UNIT_BY;

    public static final String SYSTEM_CPU_LOAD1_NAME = "system.cpu.load_average.1m";
    public static final String SYSTEM_CPU_LOAD1_DESC = "Average CPU Load over 1 minutes";
    public static final String SYSTEM_CPU_LOAD1_UNIT = UNIT_1;

    public static final String SYSTEM_CPU_LOAD5_NAME = "system.cpu.load_average.5m";
    public static final String SYSTEM_CPU_LOAD5_DESC = "Average CPU Load over 5 minutes";
    public static final String SYSTEM_CPU_LOAD5_UNIT = UNIT_1;

    public static final String SYSTEM_CPU_LOAD15_NAME = "system.cpu.load_average.15m";
    public static final String SYSTEM_CPU_LOAD15_DESC = "Average CPU Load over 15 minutes";
    public static final String SYSTEM_CPU_LOAD15_UNIT = UNIT_1;

    public static final String SYSTEM_NETWORK_CONNECTIONS_NAME = "system.network.connections";
    public static final String SYSTEM_NETWORK_CONNECTIONS_DESC = "The number of connections";
    public static final String SYSTEM_NETWORK_CONNECTIONS_UNIT = "{connections}";

    public static final String SYSTEM_NETWORK_DROPPED_NAME = "system.network.dropped";
    public static final String SYSTEM_NETWORK_DROPPED_DESC = "The number of packets dropped";
    public static final String SYSTEM_NETWORK_DROPPED_UNIT = "{packets}";

    public static final String SYSTEM_NETWORK_ERRORS_NAME = "system.network.errors";
    public static final String SYSTEM_NETWORK_ERRORS_DESC = "The number of errors encountered";
    public static final String SYSTEM_NETWORK_ERRORS_UNIT = "{errors}";

    public static final String SYSTEM_NETWORK_IO_NAME = "system.network.io";
    public static final String SYSTEM_NETWORK_IO_DESC = "The number of bytes transmitted and received";
    public static final String SYSTEM_NETWORK_IO_UNIT = UNIT_BY;

    public static final String SYSTEM_NETWORK_PACKETS_NAME = "system.network.packets";
    public static final String SYSTEM_NETWORK_PACKETS_DESC = "The number of packets transferred";
    public static final String SYSTEM_NETWORK_PACKETS_UNIT = "{packets}";


    public static final String SYSTEM_DISK_IO_NAME = "system.disk.io";
    public static final String SYSTEM_DISK_IO_DESC = "Disk bytes transferred";
    public static final String SYSTEM_DISK_IO_UNIT = UNIT_BY;

    public static final String SYSTEM_DISK_IO_TIME_NAME = "system.disk.io_time";
    public static final String SYSTEM_DISK_IO_TIME_DESC = "Time disk spent activated. On Windows, this is calculated as the inverse of disk idle time";
    public static final String SYSTEM_DISK_IO_TIME_UNIT = UNIT_S;

    public static final String SYSTEM_DISK_MERGED_NAME = "system.disk.merged";
    public static final String SYSTEM_DISK_MERGED_DESC = "The number of disk reads/writes merged into single physical disk access operations";
    public static final String SYSTEM_DISK_MERGED_UNIT = "{operations}";

    public static final String SYSTEM_DISK_OPERATION_TIME_NAME = "system.disk.operation_time";
    public static final String SYSTEM_DISK_OPERATION_TIME_DESC = "Time spent in disk operations";
    public static final String SYSTEM_DISK_OPERATION_TIME_UNIT = UNIT_S;

    public static final String SYSTEM_DISK_OPERATIONS_NAME = "system.disk.operations";
    public static final String SYSTEM_DISK_OPERATIONS_DESC = "Disk operations count";
    public static final String SYSTEM_DISK_OPERATIONS_UNIT = "{operations}";

    public static final String SYSTEM_DISK_PENDING_OPERATIONS_NAME = "system.disk.pending_operations";
    public static final String SYSTEM_DISK_PENDING_OPERATIONS_DESC = "The queue size of pending I/O operations";
    public static final String SYSTEM_DISK_PENDING_OPERATIONS_UNIT = "{operations}";

    public static final String SYSTEM_DISK_WEIGHTED_IO_TIME_NAME = "system.disk.weighted_io_time";
    public static final String SYSTEM_DISK_WEIGHTED_IO_TIME_DESC = "Time disk spent activated multiplied by the queue length";
    public static final String SYSTEM_DISK_WEIGHTED_IO_TIME_UNIT = UNIT_S;


    public static final String SYSTEM_FILESYSTEM_INODES_USAGE_NAME = "system.filesystem.inodes.usage";
    public static final String SYSTEM_FILESYSTEM_INODES_USAGE_DESC = "FileSystem inodes used";
    public static final String SYSTEM_FILESYSTEM_INODES_USAGE_UNIT = "{inodes}";

    public static final String SYSTEM_FILESYSTEM_USAGE_NAME = "system.filesystem.usage";
    public static final String SYSTEM_FILESYSTEM_USAGE_DESC = "Filesystem bytes used";
    public static final String SYSTEM_FILESYSTEM_USAGE_UNIT = UNIT_BY;


    public static final String SYSTEM_PAGING_FAULTS_NAME = "system.paging.faults";
    public static final String SYSTEM_PAGING_FAULTS_DESC = "The number of page faults";
    public static final String SYSTEM_PAGING_FAULTS_UNIT = "{faults}";

    public static final String SYSTEM_PAGING_OPERATIONS_NAME = "system.paging.operations";
    public static final String SYSTEM_PAGING_OPERATIONS_DESC = "The number of paging operations";
    public static final String SYSTEM_PAGING_OPERATIONS_UNIT = "{operations}";

    public static final String SYSTEM_PAGING_USAGE_NAME = "system.paging.usage";
    public static final String SYSTEM_PAGING_USAGE_DESC = "Swap (unix) or pagefile (windows) usage";
    public static final String SYSTEM_PAGING_USAGE_UNIT = UNIT_BY;


    public static final String SYSTEM_PROCESSES_COUNT_NAME = "system.processes.count";
    public static final String SYSTEM_PROCESSES_COUNT_DESC = "Total number of processes in each state";
    public static final String SYSTEM_PROCESSES_COUNT_UNIT = "{processes}";

    public static final String SYSTEM_PROCESSES_CREATED_NAME = "system.processes.created";
    public static final String SYSTEM_PROCESSES_CREATED_DESC = "Total number of created processes";
    public static final String SYSTEM_PROCESSES_CREATED_UNIT = "{processes}";

    public static String readFileText(String filePath) throws IOException {
        try (InputStream is = new FileInputStream(filePath)) {
            int n = is.available();
            byte[] ba = new byte[n];
            is.read(ba);
            return new String(ba).trim();
        }
    }

    public static String readFileText(String filePath, int max) throws IOException {
        try (InputStream is = new FileInputStream(filePath)) {
            byte[] ba = new byte[max];
            int n = is.read(ba);
            return new String(ba, 0, n).trim();
        }
    }

    public static String readFileTextLine(String filePath) throws IOException {
        try (Reader rd = new FileReader(filePath)) {
            BufferedReader reader = new BufferedReader(rd);
            String line = reader.readLine();
            reader.close();
            return line;
        }
    }
}