package com.ojr.informix;

import com.ojr.core.DcUtil;
import com.ojr.core.ResourceEnricher;
import com.ojr.core.metric.MetricCalculationMode;
import com.ojr.core.metric.MetricQueryResult;
import com.ojr.informix.metrics.MetricCollectionMode;
import com.ojr.informix.metrics.MetricDataConfig;
import com.ojr.informix.metrics.MetricsDataConfigRegister;
import com.ojr.informix.metrics.MetricsDataQueryConfig;
import com.ojr.informix.metrics.strategy.MetricsCollector;
import com.ojr.rdb.AbstractDbDc;
import com.ojr.rdb.DbDcConfig;
import com.ojr.rdb.DbDcUtil;
import com.ojr.rdb.SemanticAttributes;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ojr.informix.Constants.*;
import static com.ojr.informix.InformixUtil.DB_HOST_AND_VERSION_SQL;
import static com.ojr.rdb.DbDcUtil.*;

/**
 * Represents a data collector for Informix Database, extending the functionality of AbstractDbDc.
 * This class is responsible for processing database parameters, registering metrics, and collecting data from the Informix
 * database.
 */
public class InformixDc extends AbstractDbDc<DbDcConfig> {
    private static final Logger logger = Logger.getLogger(InformixDc.class.getName());

    private static final int DEFAULT_ELAPSED_TIME = 900;
    private String tableSpaceSizeQuery;
    private String sequentialScanQuery;
    private String sequentialScanTableQuery;
    private String sqlElapsedTimeQuery;
    private boolean customPollRateEnabled = true;
    private ScheduledExecutorService executorService;
    private BasicDataSource dataSource;
    private MetricsDataQueryConfig metricDataQueryConfig;

    private MetricsCollector metricCollector;

    private Boolean sqlTraceEnabled;


    // Process database parameters, including decoding the password and finding the database name and version
    @Override
    public void processParameters(Map<String, Object> properties, DbDcConfig dbDcConfig) throws Exception {
        parseCustomAttributes(properties);
        OnstatCommandExecutor onstatCommandExecutor = new OnstatCommandExecutor(getDbPath(), getServerName());
        setDbConnUrl();

        dataSource = getDataSource();
        metricDataQueryConfig = new MetricsDataQueryConfig(tableSpaceSizeQuery, List.class, this.dataSource, SemanticAttributes.TOTAL_KB.getKey(), DB_TABLESPACE_SIZE_KEY, SemanticAttributes.USED_KB.getKey(), SemanticAttributes.TABLE_UTILIZATION.getKey());

        if (getServiceInstanceId() == null) {
            setServiceInstanceId(getDbAddress() + ":" + getDbPort() + "@" + getDbName());
        }
        getDbNameAndVersion();
        parseCustomPollRate(properties);
        registerMetricsMetadata();
        metricCollector = new MetricsCollector(dataSource, onstatCommandExecutor);
    }

    @Override
    public void enrichResourceAttributes(ResourceEnricher enricher) {
        super.enrichResourceAttributes(enricher);
        enricher.enrich(DcUtil.OJR_PLUGIN, "informix-db");
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
        logger.info("Start to collect metrics for Informix DB");
        getallMetrics();
    }

    private void setDbConnUrl() {
        String url = String.format("jdbc:informix-sqli://%s:%s/sysmaster:informixserver=%s;user=%s;Password=%s", getDbAddress(), getDbPort(), getServerName(), getDbUserName(), getDbPassword());
        setDbConnUrl(url);
    }

    private void getDbNameAndVersion() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ResultSet rs = DbDcUtil.executeQuery(connection, DB_HOST_AND_VERSION_SQL);
            rs.next();
            setDbVersion(rs.getString("Version"));
        }
    }

    private BasicDataSource getDataSource() {
        final BasicDataSource basicDataSource;
        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(getDbDriver());
        basicDataSource.setUsername(getDbUserName());
        basicDataSource.setPassword(getDbPassword());
        basicDataSource.setUrl(getDbConnUrl());
        basicDataSource.setInitialSize(3);
        basicDataSource.setMaxIdle(1);
        return basicDataSource;
    }

    /**
     * Util method to register all the available metrics Informix support with some basic metadata like Mode of execution, Query or command details etc.
     */
    private void registerMetricsMetadata() {
        //Metrics via SQL
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_TABLESPACE_SIZE_NAME, new MetricDataConfig(tableSpaceSizeQuery, MetricCollectionMode.SQL, List.class, DB_TABLESPACE_SIZE_KEY));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_TABLESPACE_USED_NAME, new MetricDataConfig(tableSpaceSizeQuery, MetricCollectionMode.SQL, List.class, DB_TABLESPACE_USED_KEY));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_TABLESPACE_UTILIZATION_NAME, new MetricDataConfig(tableSpaceSizeQuery, MetricCollectionMode.SQL, List.class, DB_TABLESPACE_UTILIZATION_KEY));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_TABLESPACE_MAX_NAME, new MetricDataConfig(tableSpaceSizeQuery, MetricCollectionMode.SQL, List.class, DB_TABLESPACE_MAX_KEY));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_SQL_ELAPSED_TIME_NAME, new MetricDataConfig(sqlElapsedTimeQuery, MetricCollectionMode.SQL, List.class, DB_SQL_ELAPSED_TIME_KEY, SemanticAttributes.SQL_TEXT.getKey()));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_INSTANCE_COUNT_NAME, new MetricDataConfig(InformixUtil.INSTANCE_COUNT_SQL, MetricCollectionMode.SQL, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_INSTANCE_ACTIVE_COUNT_NAME, new MetricDataConfig(InformixUtil.INSTANCE_ACTIVE_COUNT_SQL, MetricCollectionMode.SQL, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_DATABASE_LOG_ENABLED_NAME, new MetricDataConfig(InformixUtil.DB_DATABASE_LOG_ENABLED_SQL, MetricCollectionMode.SQL, List.class, DB_DATABASE_LOG_ENABLED_KEY));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_DATABASE_BUFF_LOG_ENABLED_NAME, new MetricDataConfig(InformixUtil.DB_DATABASE_BUFF_LOG_ENABLED_SQL, MetricCollectionMode.SQL, List.class, DB_DATABASE_BUFF_LOG_ENABLED_KEY));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_DATABASE_ANSI_COMPLAINT_NAME, new MetricDataConfig(InformixUtil.DB_DATABASE_ANSI_COMPLAINT_SQL, MetricCollectionMode.SQL, List.class, DB_DATABASE_ANSI_COMPLAINT_KEY));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_DATABASE_NLS_ENABLED_NAME, new MetricDataConfig(InformixUtil.DB_DATABASE_NLS_ENABLED_SQL, MetricCollectionMode.SQL, List.class, DB_DATABASE_NLS_ENABLED_KEY));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_DATABASE_CASE_INCENSITIVE_NAME, new MetricDataConfig(InformixUtil.DB_DATABASE_CASE_INCENSITIVE_SQL, MetricCollectionMode.SQL, List.class, DB_DATABASE_CASE_INCENSITIVE_KEY));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_SEQ_SCAN_NAME, new MetricDataConfig(sequentialScanQuery, MetricCollectionMode.SQL, List.class, DB_SEQ_SCAN_KEY));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_SEQ_SCAN_TABLE_NAME, new MetricDataConfig(sequentialScanTableQuery, MetricCollectionMode.SQL, Number.class));

        //Metrics via onstat command
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_SQL_COUNT_NAME, new MetricDataConfig(DB_SQL_COUNT_NAME, SQL_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_SQL_RATE_NAME, new MetricDataConfig(DB_SQL_RATE_NAME, SQL_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_TRANSACTION_COUNT_NAME, new MetricDataConfig(DB_TRANSACTION_COUNT_NAME, TRANSACTION_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_TRANSACTION_RATE_NAME, new MetricDataConfig(DB_TRANSACTION_RATE_NAME, TRANSACTION_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_SESSION_COUNT_NAME, new MetricDataConfig(DB_SESSION_COUNT_NAME, TOTAL_SESSION_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_SESSION_ACTIVE_COUNT_NAME, new MetricDataConfig(DB_SESSION_ACTIVE_COUNT_NAME, ACTIVE_SESSION_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_IO_READ_RATE_NAME, new MetricDataConfig(DB_IO_READ_RATE_NAME, IO_READ_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_IO_WRITE_RATE_NAME, new MetricDataConfig(DB_IO_WRITE_RATE_NAME, IO_WRITE_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_MEM_UTILIZATION_NAME, new MetricDataConfig(DB_MEM_UTILIZATION_NAME, MEMORY_UTILIZATION_SCRIPT, MetricCollectionMode.CMD, Double.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_DISK_READ_COUNT_NAME, new MetricDataConfig(DB_DISK_READ_COUNT_NAME, DISK_READ_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_DISK_WRITE_COUNT_NAME, new MetricDataConfig(DB_DISK_WRITE_COUNT_NAME, DISK_WRITE_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_LOCK_COUNT_NAME, new MetricDataConfig(DB_LOCK_COUNT_NAME, LOCK_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_TASK_WAIT_COUNT_NAME, new MetricDataConfig(DB_TASK_WAIT_COUNT_NAME, TASK_WAIT_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_LOCK_WAITS_NAME, new MetricDataConfig(DB_LOCK_WAITS_NAME, LOCK_WAITS_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_CACHE_READ_RATIO_NAME, new MetricDataConfig(DB_CACHE_READ_RATIO_NAME, CACHE_READ_RATIO_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_CACHE_WRITE_RATIO_NAME, new MetricDataConfig(DB_CACHE_WRITE_RATIO_NAME, CACHE_WRITE_RATIO_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_LRU_WRITES_NAME, new MetricDataConfig(DB_LRU_WRITES_NAME, LRU_WRITES_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_LOCK_TABLE_OVERFLOW_NAME, new MetricDataConfig(DB_LOCK_TABLE_OVERFLOW_NAME, OVERFLOW_LOCK_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_TRANSACTION_OVERFLOW_NAME, new MetricDataConfig(DB_TRANSACTION_OVERFLOW_NAME, OVERFLOW_TRANSACTION_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));
        MetricsDataConfigRegister.subscribeMetricDataConfig(DB_USER_OVERFLOW_NAME, new MetricDataConfig(DB_USER_OVERFLOW_NAME, OVERFLOW_USER_COUNT_SCRIPT, MetricCollectionMode.CMD, Number.class));


    }

    /**
     * Util method to parse the user input
     *
     * @param properties : user inputs
     */
    @SuppressWarnings("unchecked")
    private void parseCustomPollRate(Map<String, Object> properties) {
        Map<String, Object> customInput = (Map<String, Object>) properties.get("custom.poll.interval");
        if (null == customInput || customInput.isEmpty()) {
            customPollRateEnabled = false;
            logger.info("No custom polling interval fallback to default");
            return;
        }

        executorService = Executors.newScheduledThreadPool(3);

        for (Map.Entry<String, Object> entry : customInput.entrySet()) {
            IntervalType type = getPollingInterval(entry.getKey());
            int pollInterval = (int) entry.getValue();
            assert type != null;
            scheduleCustomPollRate(pollInterval, type);
        }
    }


    /**
     * Util method to schedule custom Poll Rate based on the user Input
     *
     * @param pollInterval : Polling value
     * @param intervalType : Type of the Interval
     */
    private void scheduleCustomPollRate(int pollInterval, IntervalType intervalType) {
        switch (intervalType) {
            case HIGH:
                logger.info("Starting Long Polling Scheduler");
                executorService.scheduleWithFixedDelay(this::longPollingInterval, 1, pollInterval, TimeUnit.SECONDS);
                break;
            case MEDIUM:
                logger.info("Starting Medium Polling Scheduler");
                executorService.scheduleWithFixedDelay(this::mediumPollingInterval, 1, pollInterval, TimeUnit.SECONDS);
                break;
            case LOW:
                logger.info("Starting Low Polling Scheduler");
                executorService.scheduleWithFixedDelay(this::shortPollingInterval, 1, pollInterval, TimeUnit.SECONDS);
                break;
        }
    }

    /**
     * Util method to parse the config and get the custom Attributes from the Config
     *
     * @param properties : Config data
     */
    @SuppressWarnings("unchecked")
    private void parseCustomAttributes(Map<String, Object> properties) {
        Map<String, Object> customInput = (Map<String, Object>) properties.get("custom.input");
        sqlTraceEnabled = (Boolean) customInput.getOrDefault(DB_SQL_TRACE_ENABLED, false);
        int sequentialScanCount = (Integer) customInput.getOrDefault("db.sequential.scan.count", 0);
        long elapsedTimeFrame = Long.parseLong((customInput.getOrDefault("db.sql.elapsed.timeframe", DEFAULT_ELAPSED_TIME)).toString());
        StringBuilder databaseName = new StringBuilder(Constants.SINGLE_QUOTES + getDbName() + Constants.SINGLE_QUOTES);
        sequentialScanQuery = String.format(InformixUtil.DB_SEQ_SCAN_SQL, databaseName, sequentialScanCount);
        sequentialScanTableQuery = String.format(InformixUtil.DB_SEQ_SCAN_TABLE_SQL, databaseName, sequentialScanCount);
        tableSpaceSizeQuery = String.format(InformixUtil.TABLESPACE_SIZE_SQL, databaseName);
        sqlElapsedTimeQuery = String.format(InformixUtil.SQL_ELAPSED_TIME_SQL, elapsedTimeFrame, databaseName);
    }

    private enum IntervalType {
        HIGH, MEDIUM, LOW
    }

    /**
     * Util method to get the Polling Interval
     *
     * @param pollingInterval : User input of the Interval
     * @return : Mapped Type of the Interval
     */
    private IntervalType getPollingInterval(String pollingInterval) {
        for (IntervalType interval : IntervalType.values()) {
            if (pollingInterval.equalsIgnoreCase(interval.name())) {
                return interval;
            }
        }
        logger.log(Level.SEVERE, "Invalid Polling Interval : {}", pollingInterval);
        return null;
    }

    private void getallMetrics() {
        longPollingInterval();
        mediumPollingInterval();
        shortPollingInterval();
    }

    @SuppressWarnings("unchecked")
    private void mediumPollingInterval() {
        if (sqlTraceEnabled) {
            getRawMetric(DB_SQL_COUNT_NAME).setValue((Number) metricCollector.collectMetrics(DB_SQL_COUNT_NAME));
            getRawMetric(DB_SQL_ELAPSED_TIME_NAME).setValue((List<MetricQueryResult>) metricCollector.collectMetrics(DB_SQL_ELAPSED_TIME_NAME));
        }
        getRawMetric(DB_SQL_RATE_NAME).setValue((Number) metricCollector.collectMetrics(DB_SQL_RATE_NAME));
        getRawMetric(DB_TRANSACTION_COUNT_NAME).setValue((Number) metricCollector.collectMetrics(DB_TRANSACTION_COUNT_NAME));
        getRawMetric(DB_TRANSACTION_RATE_NAME).setValue((Number) metricCollector.collectMetrics(DB_TRANSACTION_COUNT_NAME));
    }

    @SuppressWarnings("unchecked")
    private void shortPollingInterval() {
        getRawMetric(DbDcUtil.DB_STATUS_NAME).setValue(1);
        getRawMetric(DB_INSTANCE_COUNT_NAME).setValue((Number) metricCollector.collectMetrics(DB_INSTANCE_COUNT_NAME));
        getRawMetric(DB_INSTANCE_ACTIVE_COUNT_NAME).setValue((Number) metricCollector.collectMetrics(DB_INSTANCE_ACTIVE_COUNT_NAME));
        getRawMetric(DB_LOCK_TABLE_OVERFLOW_NAME).setValue((Number) metricCollector.collectMetrics(DB_LOCK_TABLE_OVERFLOW_NAME));
        getRawMetric(DB_TRANSACTION_OVERFLOW_NAME).setValue((Number) metricCollector.collectMetrics(DB_TRANSACTION_OVERFLOW_NAME));
        getRawMetric(DB_USER_OVERFLOW_NAME).setValue((Number) metricCollector.collectMetrics(DB_USER_OVERFLOW_NAME));
        getRawMetric(DB_SESSION_COUNT_NAME).setValue((Number) metricCollector.collectMetrics(DB_SESSION_COUNT_NAME));
        getRawMetric(DB_SEQ_SCAN_NAME).setValue((List<MetricQueryResult>) metricCollector.collectMetrics(DB_SEQ_SCAN_NAME));
        getRawMetric(DB_SEQ_SCAN_TABLE_NAME).setValue((Number) metricCollector.collectMetrics(DB_SEQ_SCAN_TABLE_NAME));
        getRawMetric(DB_SESSION_ACTIVE_COUNT_NAME).setValue((Number) metricCollector.collectMetrics(DB_SESSION_ACTIVE_COUNT_NAME));
        getRawMetric(DB_IO_READ_RATE_NAME).setValue((Number) metricCollector.collectMetrics(DB_IO_READ_RATE_NAME));
        getRawMetric(DB_IO_WRITE_RATE_NAME).setValue((Number) metricCollector.collectMetrics(DB_IO_WRITE_RATE_NAME));
        getRawMetric(DB_MEM_UTILIZATION_NAME).setValue((Number) metricCollector.collectMetrics(DB_MEM_UTILIZATION_NAME));
        getRawMetric(DB_DISK_WRITE_COUNT_NAME).setValue((Number) metricCollector.collectMetrics(DB_DISK_WRITE_COUNT_NAME));
        getRawMetric(DB_DISK_READ_COUNT_NAME).setValue((Number) metricCollector.collectMetrics(DB_DISK_READ_COUNT_NAME));
        getRawMetric(DB_LOCK_WAITS_NAME).setValue((Number) metricCollector.collectMetrics(DB_LOCK_WAITS_NAME));
        getRawMetric(DB_CACHE_READ_RATIO_NAME).setValue((Number) metricCollector.collectMetrics(DB_CACHE_READ_RATIO_NAME));
        getRawMetric(DB_CACHE_WRITE_RATIO_NAME).setValue((Number) metricCollector.collectMetrics(DB_CACHE_WRITE_RATIO_NAME));
        getRawMetric(DB_LRU_WRITES_NAME).setValue((Number) metricCollector.collectMetrics(DB_LRU_WRITES_NAME));
    }

    @SuppressWarnings("unchecked")
    private void longPollingInterval() {
        //TODO: A method to execute the query, store it in object, call that object in subsequent lines
        metricDataQueryConfig.fetchQueryResults();
        getRawMetric(DB_TABLESPACE_SIZE_NAME).setValue(metricDataQueryConfig.getResults(SemanticAttributes.TOTAL_KB.getKey()));
        getRawMetric(DB_TABLESPACE_USED_NAME).setValue(metricDataQueryConfig.getResults(SemanticAttributes.USED_KB.getKey()));
        getRawMetric(DB_TABLESPACE_UTILIZATION_NAME).setValue(metricDataQueryConfig.getResults(SemanticAttributes.TABLE_UTILIZATION.getKey()));
        getRawMetric(DB_TABLESPACE_MAX_NAME).setValue(metricDataQueryConfig.getResults(SemanticAttributes.TOTAL_KB.getKey()));
        getRawMetric(DB_DATABASE_LOG_ENABLED_NAME).setValue((List<MetricQueryResult>) metricCollector.collectMetrics(DB_DATABASE_LOG_ENABLED_NAME));
        getRawMetric(DB_DATABASE_BUFF_LOG_ENABLED_NAME).setValue((List<MetricQueryResult>) metricCollector.collectMetrics(DB_DATABASE_BUFF_LOG_ENABLED_NAME));
        getRawMetric(DB_DATABASE_ANSI_COMPLAINT_NAME).setValue((List<MetricQueryResult>) metricCollector.collectMetrics(DB_DATABASE_ANSI_COMPLAINT_NAME));
        getRawMetric(DB_DATABASE_NLS_ENABLED_NAME).setValue((List<MetricQueryResult>) metricCollector.collectMetrics(DB_DATABASE_NLS_ENABLED_NAME));
        getRawMetric(DB_DATABASE_CASE_INCENSITIVE_NAME).setValue((List<MetricQueryResult>) metricCollector.collectMetrics(DB_DATABASE_CASE_INCENSITIVE_NAME));
    }

}
