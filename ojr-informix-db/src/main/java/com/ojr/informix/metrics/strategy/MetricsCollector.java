package com.ojr.informix.metrics.strategy;

import com.ojr.informix.OnstatCommandExecutor;
import com.ojr.informix.metrics.MetricCollectionMode;
import com.ojr.informix.metrics.MetricDataConfig;
import com.ojr.informix.metrics.MetricsDataConfigRegister;
import org.apache.commons.dbcp2.BasicDataSource;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MetricsCollector {
    private static final Logger LOGGER = Logger.getLogger(MetricsCollector.class.getName());
    private final MetricsExecutionStrategy sqlExecutorStrategy;
    private final MetricsExecutionStrategy commandExecutorStrategy;

    public MetricsCollector(BasicDataSource dataSource, OnstatCommandExecutor onstatCommandExecutor) {
        this.commandExecutorStrategy = new CommandExecutorStrategy(onstatCommandExecutor);
        this.sqlExecutorStrategy = new SqlExecutorStrategy(dataSource);
    }

    /**
     * Public Method exposed to the DC to get the Metric Data by metricName
     * Based on the metric metadata it will be using the appropriate strategy
     *
     * @param metricName : Name of the metric
     * @param <T>:       Return Type
     * @return : metric value
     */
    public <T> T collectMetrics(String metricName) {
        try {
            MetricDataConfig metricDataConfig = MetricsDataConfigRegister.getMetricDataConfig(metricName);
            if (null == metricDataConfig) {
                LOGGER.log(Level.INFO, "No Metrics Data for metric: {}", metricName);
                return null;
            }
            MetricCollectionMode mode = metricDataConfig.getSelectedMode();
            if (mode == MetricCollectionMode.SQL) {
                T response = sqlExecutorStrategy.collectMetrics(metricDataConfig);
                LOGGER.info("SQL - For Metric: " + metricName + " response: " + response);
                return response;
            } else if (mode == MetricCollectionMode.CMD) {
                T response = commandExecutorStrategy.collectMetrics(metricDataConfig);
                LOGGER.info("CMD - For Metric: " + metricName + " response: " + response);
                return response;
            }
            throw new IllegalStateException("For Metric: " + metricName + " Invalid Mode selected: " + mode);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to update metric {0} due to exception: {1}", new Object[]{metricName, e});
        }
        return null;
    }
}