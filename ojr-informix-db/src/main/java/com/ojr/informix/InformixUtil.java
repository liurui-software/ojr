package com.ojr.informix;

import java.util.Base64;

public class InformixUtil {
    private InformixUtil() {
        //Private Constructor
    }

    public static final String DB_HOST_AND_VERSION_SQL = "SELECT  FIRST 1 DBINFO('VERSION','FULL') AS VERSION,  DBINFO('DBHOSTNAME') AS HOSTNAME FROM SYSTABLES;";
    public static final String AVAILABLE_DATA_BASES = "SELECT NAME, OWNER, PARTNUM  FROM SYSDATABASES;";
    //Instance & Active Instance information (KPI)
    public static final String INSTANCE_COUNT_SQL = "SELECT COUNT(DISTINCT DBSERVERNAME) AS SERVER FROM SYSTABLES;";
    public static final String INSTANCE_ACTIVE_COUNT_SQL = "SELECT COUNT(DISTINCT NAME) AS ACTIVE_SERVER FROM SYSCLUSTER WHERE SERVER_STATUS = 'Active';";

    //Session & Active Session information (KPI)
    public static final String ACTIVE_SESSION = "SELECT COUNT(1) FROM SYSSESSIONS;";
    public static final String SESSION_COUNT_SQL = "SELECT COUNT(1) FROM SYSSESSIONS;";


    //I/O Read & Write information (KPI) : As number of Reads/Writes PER CHUNK hence doing the sum
    public static final String IO_READ_COUNT_SQL = "SELECT SUM(SYSCHUNKS.PAGESIZE * SYSCHKIO.PAGESREAD) FROM SYSCHKIO INNER JOIN SYSCHUNKS ON SYSCHUNKS.CHKNUM=SYSCHKIO.CHUNKNUM;";
    public static final String IO_WRITE_COUNT_SQL = "SELECT SUM(SYSCHUNKS.PAGESIZE * SYSCHKIO.PAGESWRITTEN) FROM SYSCHKIO INNER JOIN SYSCHUNKS ON SYSCHUNKS.CHKNUM=SYSCHKIO.CHUNKNUM;";

    public static final String MEMORY_UTILIZATION_SQL = "SELECT (SUM(SEG_BLKUSED) * 100) / (SUM(SEG_BLKUSED) + SUM(SEG_BLKFREE)) FROM SYSSEGLST;";

    public static final String SQL_COUNT_SQL = "SELECT COUNT(1) FROM SYSSQLTRACE WHERE (DBINFO('UTC_CURRENT') - SQL_FINISHTIME)<24*60*60;";
    public static final String TRANSACTION_COUNT_SQL = "SELECT COUNT(1) FROM SYSTRANS;";
    public static final String SQL_ELAPSED_TIME_SQL = "SELECT SQL_RUNTIME * 1000 AS ELAPSED_TIME_MILLIS, SQL_ID AS SQL_ID, SQL_STATEMENT AS SQL_TEXT FROM INFORMIX.SYSSQLTRACE WHERE SQL_FINISHTIME >= (DBINFO('UTC_CURRENT') - %s) AND SQL_DATABASE = %s ORDER BY ELAPSED_TIME_MILLIS DESC LIMIT 20;";
    //Table Space Queries
    public static final String TABLESPACE_SIZE_SQL = "SELECT (PT.NPTOTAL * PT.PAGESIZE) * 1024 AS TOTAL_KB,TABNAME,(PT.NPUSED * PT.PAGESIZE) * 1024 AS USED_KB,CASE WHEN (PT.NPTOTAL > 0) THEN ((PT.NPUSED) / PT.NPTOTAL) * 100 ELSE 0 END AS TABLE_UTILIZATION  FROM SYSMASTER:SYSPTNHDR PT INNER JOIN SYSMASTER:SYSTABNAMES TN ON TN.PARTNUM = PT.PARTNUM WHERE TN.DBSNAME =  %s  ORDER BY TABNAME DESC LIMIT 20;";
    public static final String TABLESPACE_USED_SQL = "SELECT (PT.NPUSED * PT.PAGESIZE) * 1024 AS USED_KB, TABNAME FROM SYSMASTER:SYSPTNHDR PT INNER JOIN SYSMASTER:SYSTABNAMES TN ON TN.PARTNUM = PT.PARTNUM WHERE TN.DBSNAME = %s ORDER BY TABNAME DESC LIMIT 20;";
    public static final String TABLESPACE_UTILIZATION_SQL = "SELECT CASE WHEN (PT.NPTOTAL > 0) THEN ((PT.NPUSED) / PT.NPTOTAL) * 100 ELSE 0 END AS TABLE_UTILIZATION, TABNAME FROM SYSMASTER:SYSPTNHDR PT INNER JOIN SYSMASTER:SYSTABNAMES TN ON TN.PARTNUM = PT.PARTNUM WHERE TN.DBSNAME = %s ORDER BY TABNAME DESC LIMIT 20;";
    public static final String TABLESPACE_MAX_SQL = "SELECT (PT.NPTOTAL * PT.PAGESIZE) * 1024 AS TOTAL_KB, TABNAME FROM SYSMASTER:SYSPTNHDR PT INNER JOIN SYSMASTER:SYSTABNAMES TN ON TN.PARTNUM = PT.PARTNUM WHERE TN.DBSNAME = %s ORDER BY TABNAME DESC LIMIT 20;";
    //Database Queries

    public static final String DB_DATABASE_LOG_ENABLED_SQL = "SELECT is_logging, name as database_name FROM sysdatabases";
    public static final String DB_DATABASE_BUFF_LOG_ENABLED_SQL = "SELECT is_buff_log, name as database_name FROM sysdatabases";
    public static final String DB_DATABASE_ANSI_COMPLAINT_SQL = "SELECT is_ansi, name as database_name FROM sysdatabases";
    public static final String DB_DATABASE_NLS_ENABLED_SQL = "SELECT is_nls, name as database_name FROM sysdatabases";
    public static final String DB_DATABASE_CASE_INCENSITIVE_SQL = "SELECT is_case_insens, name as database_name FROM sysdatabases";

    public static final String LOCK_OVF_SQL = "select coalesce(cast(value as int)) as value from sysprofile where name = 'ovlock'";
    public static final String TRANSACTION_OVF_SQL = "select coalesce(cast(value as int)) as value from sysprofile where name = 'ovtrans'";
    public static final String USER_OVF_SQL = "select coalesce(cast(value as int)) as value from sysprofile where name = 'ovuser'";
    public static final String DB_SEQ_SCAN_SQL = "select coalesce(cast(seqscans as int))seqscan,tabname as table_name from sysmaster:sysptprof where dbsname = %s and seqscans >= %d and tabname not like 'sys%%'";
    public static final String DB_SEQ_SCAN_TABLE_SQL = "SELECT count(tabname)  as number_of_tables_having_sequential_scans FROM SYSPTPROF WHERE dbsname = %s and seqscans >= %d and tabname not like 'sys%%';";

    //Disk Read & Write
    public static final String DB_DISK_WRITE_COUNT_SQL = "SELECT VALUE FROM SYSPROFILE WHERE NAME = 'dskwrites';";
    public static final String DB_DISK_READ_COUNT_SQL = "SELECT VALUE FROM SYSPROFILE WHERE NAME = 'dskreads';";

    public  static  final String DB_LOCK_WAITS_SQL = "select value from sysprofile where name = 'lockwts'";
    public static final String DB_CACHE_READ_RATIO_SQL = "select (1-(dskReadsVal / bufReadsVal)) * 100 as res  from (select  sp0.value as dskReadsVal, (select sp1.value from sysprofile sp1 where sp1.name = 'bufreads') as bufReadsVal from sysprofile sp0  where sp0.name= 'dskreads' )";
    public static final String DB_CACHE_WRITE_RATIO_SQL = "select (1-(dskWritesVal / bufWritesVal)) * 100 as res  from (select  sp0.value as dskWritesVal, (select sp1.value from sysprofile sp1 where sp1.name = 'bufwrites') as bufWritesVal from sysprofile sp0  where sp0.name= 'dskwrites' )";
    public static final String DB_LRU_WRITES_SQL = "select value from sysprofile where name = 'lruwrites'";

    public static String decodePassword(String encodedPwd) {
        return new String(Base64.getDecoder().decode(encodedPwd));
    }
}