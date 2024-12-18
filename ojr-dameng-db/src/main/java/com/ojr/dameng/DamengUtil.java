package com.ojr.dameng;

public class DamengUtil {
    public static final String DB_NAME_VERSION_SQL = "select (select name from v$database) name, (select substr(banner, instr(banner, ' ', -1) + 1) version from v$version where rownum < 2) version from dual";

    public static final String INSTANCE_COUNT_SQL = "select count(*) from gv$instance";
    public static final String INSTANCE_ACTIVE_COUNT_SQL = "select count(*) from gv$instance where status$ = 'OPEN'";

    public static final String SESSION_COUNT_SQL = "select count(*) from v$sessions";
    public static final String SESSION_ACTIVE_COUNT_SQL = "select count(*) from v$sessions where state='ACTIVE'";
    public static final String TRANSACTION_COUNT_SQL = "select stat_val from v$sysstat where name in ('transaction total count')";
    public static final String TRANSACTION_LATENCY_SQL = "select (select stat_val from v$sysstat where name in ('transaction total time in sec'))/(select stat_val from v$sysstat where name in ('transaction total count')) from dual";
    public static final String SQL_COUNT_SQL = "select stat_val from v$sysstat where name in ('sql executed count')";
    public static final String IO_READ_COUNT_SQL = "select stat_val from v$sysstat where name in ('physical read count')";
    public static final String IO_WRITE_COUNT_SQL = "select stat_val from v$sysstat where name in ('physical write count')";
    public static final String TASK_WAIT_COUNT_SQL = "select waiting from v$task_queue";
    public static final String TASK_AVG_WAIT_TIME_SQL = "select average_wait_time from v$task_queue";

    public static final String CACHE_HIT_SQL = "SELECT SUM(rat_hit) / COUNT(*), name FROM v$bufferpool GROUP BY name";
    public static final String SQL_ELAPSED_TIME_SQL =
            "SELECT EXEC_TIME as ELAPSED_TIME_MILLIS, SQL_ID as sql_id, SQL_TEXT as sql_text FROM V$SYSTEM_LONG_EXEC_SQLS ORDER BY EXEC_TIME DESC LIMIT 20";
    public static final String LOCK_COUNT_SQL = "select count(*), ltype from v$lock where blocked =1 group by ltype";
    public static final String LOCK_TIME_SQL = "SELECT timestampdiff(second, DS.create_time, sysdate) AS metric_value, L.ADDR AS lock_id, DS.SESS_ID AS blocking_sess_id, SS.SESS_ID AS blocker_sess_id, obj.OBJECT_NAME AS locked_obj_name FROM v$lock L LEFT JOIN v$sessions DS ON DS.TRX_ID = L.TRX_ID LEFT JOIN v$sessions SS ON SS.TRX_ID = L.TID LEFT JOIN dba_objects obj ON L.TABLE_ID = obj.OBJECT_ID WHERE L.BLOCKED = 1 LIMIT 20";

    public static final String TABLESPACE_SIZE_SQL = "SELECT D.TOT_GROOTTE_BY size, Upper(F.TABLESPACE_NAME) tablespace_name FROM (SELECT TABLESPACE_NAME, Round(Sum(BYTES), 2) TOTAL_BYTES, Round(Max(BYTES), 2) MAX_BYTES FROM SYS.DBA_FREE_SPACE GROUP BY TABLESPACE_NAME) F, (SELECT DD.TABLESPACE_NAME, Round(Sum(DD.BYTES), 2) TOT_GROOTTE_BY FROM SYS.DBA_DATA_FILES DD GROUP BY DD.TABLESPACE_NAME) D WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME";
    public static final String TABLESPACE_USED_SQL = "SELECT D.TOT_GROOTTE_BY - F.TOTAL_BYTES used, Upper(F.TABLESPACE_NAME) tablespace_name FROM (SELECT TABLESPACE_NAME, Round(Sum(BYTES), 2) TOTAL_BYTES, Round(Max(BYTES), 2) MAX_BYTES FROM SYS.DBA_FREE_SPACE GROUP BY TABLESPACE_NAME) F, (SELECT DD.TABLESPACE_NAME, Round(Sum(DD.BYTES), 2) TOT_GROOTTE_BY FROM SYS.DBA_DATA_FILES DD GROUP BY DD.TABLESPACE_NAME) D WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME";
    public static final String TABLESPACE_UTILIZATION_SQL = "SELECT Round(( D.TOT_GROOTTE_BY - F.TOTAL_BYTES ) / D.TOT_GROOTTE_BY, 5) utilization, Upper(F.TABLESPACE_NAME) tablespace_name FROM (SELECT TABLESPACE_NAME, Round(Sum(BYTES), 2) TOTAL_BYTES, Round(Max(BYTES), 2) MAX_BYTES FROM SYS.DBA_FREE_SPACE GROUP BY TABLESPACE_NAME) F, (SELECT DD.TABLESPACE_NAME, Round(Sum(DD.BYTES), 2) TOT_GROOTTE_BY FROM SYS.DBA_DATA_FILES DD GROUP BY DD.TABLESPACE_NAME) D WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME";
    public static final String TABLESPACE_MAX_SQL = "SELECT F.MAX_BYTES max, Upper(F.TABLESPACE_NAME) tablespace_name FROM (SELECT TABLESPACE_NAME, Round(Sum(BYTES), 2) TOTAL_BYTES, Round(Max(BYTES), 2) MAX_BYTES FROM SYS.DBA_FREE_SPACE GROUP BY TABLESPACE_NAME) F, (SELECT DD.TABLESPACE_NAME, Round(Sum(DD.BYTES), 2) TOT_GROOTTE_BY FROM SYS.DBA_DATA_FILES DD GROUP BY DD.TABLESPACE_NAME) D WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME";
    public static final String MEM_UTILIZATION_SQL = "SELECT (SELECT stat_val FROM v$sysstat WHERE name = 'memory used bytes' ) AS USED_MEM_SIZE, (SELECT stat_val FROM v$sysstat WHERE name = 'memory pool size in bytes') AS TOTAL_MEM_SIZE FROM dual";
    public static final String CPU_UTILIZATION_SQL = "SELECT (CPU_USER_RATE + CPU_SYSTEM_RATE)/100 AS CPU_UTILIZATION FROM V$SYSTEMINFO";
    public static final String DISK_USAGE_SQL = "SELECT FREE_DISK_SIZE, TOTAL_DISK_SIZE FROM V$SYSTEMINFO";
}
