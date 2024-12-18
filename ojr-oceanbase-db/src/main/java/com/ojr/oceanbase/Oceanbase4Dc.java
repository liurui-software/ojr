package com.ojr.oceanbase;

import com.ojr.core.DcException;
import com.ojr.core.DcUtil;
import com.ojr.core.ResourceEnricher;
import com.ojr.core.metric.MetricCalculationMode;
import com.ojr.rdb.AbstractDbDc;
import com.ojr.rdb.DbDcConfig;
import com.ojr.rdb.DbDcUtil;

import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ojr.oceanbase.Oceanbase4Util.*;
import static com.ojr.rdb.DbDcUtil.*;
import static com.ojr.rdb.SemanticAttributes.SQL_TEXT;

/**
 * Represents a data collector for Oceanbase Database, extending the functionality of AbstractDbDc.
 * This class is responsible for processing database parameters, registering metrics, and collecting data from the
 * Oceanbase database.
 */
public class Oceanbase4Dc extends AbstractDbDc<DbDcConfig> {
    private static final Logger logger = Logger.getLogger(Oceanbase4Dc.class.getName());

    boolean isCluster = false;
    boolean isTenant = false;

    @Override
    public void processParameters(Map<String, Object> properties, DbDcConfig dbDcConfig) throws Exception {
        if (getServiceInstanceId() == null) {
            setServiceInstanceId(getDbAddress() + ":" + getDbPort() + "@" + getDbName());
        }

        try (Connection conn = getConnection()) {
            setDbVersion(getSimpleStringWithSql(conn, DB_VERSION_SQL));

            if (this.getDbEntityType().equals(TYPE_CLUSTER)) {
                isCluster = true;
                this.setDbTenantId("1");
                this.setDbTenantName("sys");

            } else if (getDbEntityType().equals(TYPE_TENANT)) {
                isTenant = true;
                String tId = getDbTenantId();
                String tName = getDbTenantName();
                if (tId == null) {
                    if (tName == null) {
                        throw new DcException(DB_TENANT_ID + " or " + DbDcUtil.DB_TENANT_NAME + " must be provided!");
                    }
                    setDbTenantId(getSimpleStringWithSql(conn, DB_TENANT_NAME2ID_SQL.replace(TENANT_HOLDER, tName)));
                } else if (tName == null) {
                    setDbTenantName(getSimpleStringWithSql(conn, DB_TENANT_ID2NAME_SQL.replace(TENANT_HOLDER, tId)));
                }
            } else {
                throw new DcException("Unsupported entity type of Oceanbase");
            }
        }
    }

    @Override
    public void enrichResourceAttributes(ResourceEnricher enricher) {
        super.enrichResourceAttributes(enricher);
        enricher.enrich(DcUtil.OJR_PLUGIN, "oceanbase-db");
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

            if (isCluster) {
                getRawMetric(DB_SESSION_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, SESSION_COUNT_SQL0));
                getRawMetric(DB_SESSION_ACTIVE_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, SESSION_ACTIVE_COUNT_SQL0));
                getRawMetric(DB_TRANSACTION_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, TRANSACTION_COUNT_SQL0));
                getRawMetric(DB_TRANSACTION_RATE_NAME).setValue(getSimpleMetricWithSql(conn, TRANSACTION_COUNT_SQL0));
                getRawMetric(DB_TRANSACTION_LATENCY_NAME).setValue(getSimpleMetricWithSql(conn, TRANSACTION_LATENCY_SQL0));
                getRawMetric(DB_SQL_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, SQL_COUNT_SQL0));
                getRawMetric(DB_SQL_RATE_NAME).setValue(getSimpleMetricWithSql(conn, SQL_COUNT_SQL0));
                getRawMetric(DB_IO_READ_RATE_NAME).setValue(getSimpleMetricWithSql(conn, IO_READ_COUNT_SQL0));
                getRawMetric(DB_IO_WRITE_RATE_NAME).setValue(getSimpleMetricWithSql(conn, IO_WRITE_COUNT_SQL0));
                getRawMetric(DB_TASK_WAIT_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, TASK_WAIT_COUNT_SQL0));
                getRawMetric(DB_TASK_AVG_WAIT_TIME_NAME).setValue(getSimpleMetricWithSql(conn, TASK_AVG_WAIT_TIME_SQL0));

                getRawMetric(DB_CACHE_HIT_NAME).setValue(getMetricWithSql(conn, CACHE_HIT_SQL0, DB_CACHE_HIT_KEY));
                getRawMetric(DB_SQL_ELAPSED_TIME_NAME).setValue(getMetricWithSql(conn, SQL_ELAPSED_TIME_SQL0, DB_SQL_ELAPSED_TIME_KEY, SQL_TEXT.getKey()));
            } else if (isTenant) {
                getRawMetric(DB_SESSION_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(SESSION_COUNT_SQL1)));
                getRawMetric(DB_SESSION_ACTIVE_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(SESSION_ACTIVE_COUNT_SQL1)));
                getRawMetric(DB_TRANSACTION_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(TRANSACTION_COUNT_SQL1)));
                getRawMetric(DB_TRANSACTION_RATE_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(TRANSACTION_COUNT_SQL1)));
                getRawMetric(DB_TRANSACTION_LATENCY_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(TRANSACTION_LATENCY_SQL1)));
                getRawMetric(DB_SQL_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(SQL_COUNT_SQL1)));
                getRawMetric(DB_SQL_RATE_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(SQL_COUNT_SQL1)));
                getRawMetric(DB_IO_READ_RATE_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(IO_READ_COUNT_SQL1)));
                getRawMetric(DB_IO_WRITE_RATE_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(IO_WRITE_COUNT_SQL1)));
                getRawMetric(DB_TASK_WAIT_COUNT_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(TASK_WAIT_COUNT_SQL1)));
                getRawMetric(DB_TASK_AVG_WAIT_TIME_NAME).setValue(getSimpleMetricWithSql(conn, withTenant(TASK_AVG_WAIT_TIME_SQL1)));

                getRawMetric(DB_CACHE_HIT_NAME).setValue(getMetricWithSql(conn, withTenant(CACHE_HIT_SQL1), DB_CACHE_HIT_KEY));
                getRawMetric(DB_SQL_ELAPSED_TIME_NAME).setValue(getMetricWithSql(conn, withTenant(SQL_ELAPSED_TIME_SQL1), DB_SQL_ELAPSED_TIME_KEY, SQL_TEXT.getKey()));
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update metric with exception", e);
            getRawMetric(DB_STATUS_NAME).setValue(0);
        }
    }

    private String withTenant(String rawStr) {
        return rawStr.replace(TENANT_HOLDER, getDbTenantId());
    }
}
