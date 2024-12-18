package com.ojr.dameng;

import com.ojr.core.DcUtil;
import com.ojr.core.ResourceEnricher;
import com.ojr.core.metric.MetricCalculationMode;
import com.ojr.rdb.AbstractDbDc;
import com.ojr.rdb.DbDcConfig;
import com.ojr.rdb.DbDcUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ojr.rdb.SemanticAttributes.*;
import static com.ojr.rdb.DbDcUtil.*;
import static com.ojr.dameng.DamengUtil.*;

/**
 * Represents a data collector for Dameng Database, extending the functionality of AbstractDbDc.
 * This class is responsible for processing database parameters, registering metrics, and collecting data from the Dameng database.
 */
public class DamengDc extends AbstractDbDc<DbDcConfig> {
    private static final Logger logger = Logger.getLogger(DamengDc.class.getName());

    @Override
    public void processParameters(Map<String, Object> properties, DbDcConfig dbDcConfig) throws Exception {
        findDbNameAndVersion();
        if (getServiceInstanceId() == null) {
            setServiceInstanceId(getDbAddress() + ":" + getDbPort() + "@" + getDbName());
        }
    }

    @Override
    public void enrichResourceAttributes(ResourceEnricher enricher) {
        super.enrichResourceAttributes(enricher);
        enricher.enrich(DcUtil.OJR_PLUGIN, "dameng-db");
    }

        // Register metrics with specific calculation modes
    @Override
    public void registerMetrics() {
        super.registerMetrics();
        getRawMetric(DB_TRANSACTION_RATE_NAME).setCalculationMode(MetricCalculationMode.RATE);
        getRawMetric(DB_SQL_RATE_NAME).setCalculationMode(MetricCalculationMode.RATE);
        getRawMetric(DB_IO_READ_RATE_NAME).setCalculationMode(MetricCalculationMode.RATE);
        getRawMetric(DB_IO_WRITE_RATE_NAME).setCalculationMode(MetricCalculationMode.RATE);
    }

    // Collect data from the database and update metrics
    @Override
    public void collectData() {
        logger.info("Start to collect metrics");
        try (Connection conn = getConnection()) {
            getRawMetric(DB_STATUS_NAME).setValue(1);
            getRawMetric(DB_INSTANCE_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, INSTANCE_COUNT_SQL));
            getRawMetric(DB_INSTANCE_ACTIVE_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, INSTANCE_ACTIVE_COUNT_SQL));

            getRawMetric(DB_SESSION_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, SESSION_COUNT_SQL));
            getRawMetric(DB_SESSION_ACTIVE_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, SESSION_ACTIVE_COUNT_SQL));
            getRawMetric(DB_TRANSACTION_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, TRANSACTION_COUNT_SQL));
            getRawMetric(DB_TRANSACTION_RATE_NAME).setValue(getSimpleMetricWithSql(conn, TRANSACTION_COUNT_SQL));
            getRawMetric(DB_TRANSACTION_LATENCY_NAME).setValue(getSimpleMetricWithSql(conn, TRANSACTION_LATENCY_SQL));
            getRawMetric(DB_SQL_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, SQL_COUNT_SQL));
            getRawMetric(DB_SQL_RATE_NAME).setValue(getSimpleMetricWithSql(conn, SQL_COUNT_SQL));
            getRawMetric(DB_IO_READ_RATE_NAME).setValue(getSimpleMetricWithSql(conn, IO_READ_COUNT_SQL));
            getRawMetric(DB_IO_WRITE_RATE_NAME).setValue(getSimpleMetricWithSql(conn, IO_WRITE_COUNT_SQL));
            getRawMetric(DB_TASK_WAIT_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, TASK_WAIT_COUNT_SQL));
            getRawMetric(DB_TASK_AVG_WAIT_TIME_NAME).setValue(getSimpleMetricWithSql(conn, TASK_AVG_WAIT_TIME_SQL));

            getRawMetric(DB_CACHE_HIT_NAME).setValue(getMetricWithSql(conn, CACHE_HIT_SQL, DB_CACHE_HIT_KEY));
            getRawMetric(DB_SQL_ELAPSED_TIME_NAME).setValue(getMetricWithSql(conn, SQL_ELAPSED_TIME_SQL, DB_SQL_ELAPSED_TIME_KEY, SQL_TEXT.getKey()));
            getRawMetric(DB_LOCK_COUNT_NAME).setValue(getMetricWithSql(conn, LOCK_COUNT_SQL, DB_LOCK_COUNT_KEY));
            getRawMetric(DB_LOCK_TIME_NAME).setValue(getMetricWithSql(conn, LOCK_TIME_SQL, DB_LOCK_TIME_KEY, BLOCKING_SESS_ID.getKey(), BLOCKER_SESS_ID.getKey(), LOCKED_OBJ_NAME.getKey()));

            getRawMetric(DB_TABLESPACE_SIZE_NAME).setValue(getMetricWithSql(conn, TABLESPACE_SIZE_SQL, DB_TABLESPACE_SIZE_KEY));
            getRawMetric(DB_TABLESPACE_USED_NAME).setValue(getMetricWithSql(conn, TABLESPACE_USED_SQL, DB_TABLESPACE_USED_KEY));
            getRawMetric(DB_TABLESPACE_UTILIZATION_NAME).setValue(getMetricWithSql(conn, TABLESPACE_UTILIZATION_SQL, DB_TABLESPACE_UTILIZATION_KEY));
            getRawMetric(DB_TABLESPACE_MAX_NAME).setValue(getMetricWithSql(conn, TABLESPACE_MAX_SQL, DB_TABLESPACE_MAX_KEY));
            getRawMetric(DB_CPU_UTILIZATION_NAME).setValue(getSimpleMetricWithSql(conn, CPU_UTILIZATION_SQL));
            List<Long> listMemData = getSimpleListWithSql(conn, MEM_UTILIZATION_SQL);
            if (listMemData != null && !listMemData.isEmpty()) {
                getRawMetric(DB_MEM_UTILIZATION_NAME).setValue((double) listMemData.get(0) / listMemData.get(1));
            }
            List<Long> listDiskData = getSimpleListWithSql(conn, DISK_USAGE_SQL);
            if (listDiskData != null && !listDiskData.isEmpty()) {
                long free = listDiskData.get(0);
                long total = listDiskData.get(1);
                getRawMetric(DB_DISK_UTILIZATION_NAME).getDataPoint("default").setValue((double) free / total, Collections.singletonMap("path", "default"));
                getRawMetric(DB_DISK_USAGE_NAME).getDataPoint("default").setValue(total - free, Collections.singletonMap("path", "default"));
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update metric with exception", e);
            getRawMetric(DB_STATUS_NAME).setValue(0);
        }
    }

    // Find the database name and version by executing a SQL query
    private void findDbNameAndVersion() throws SQLException {
        try (Connection connection = getConnection()) {
            ResultSet rs = DbDcUtil.executeQuery(connection, DB_NAME_VERSION_SQL);
            rs.next();
            if (getDbName() == null)
                setDbName(rs.getString(1));
            setDbVersion(rs.getString(2));
        }
    }

}
