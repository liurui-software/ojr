package com.ojr.informix.metrics.strategy;

import com.ojr.informix.metrics.MetricDataConfig;
import com.ojr.rdb.DbDcUtil;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlExecutorStrategy extends MetricsExecutionStrategy {

    private static final Logger LOGGER = Logger.getLogger(SqlExecutorStrategy.class.getName());
    private final BasicDataSource dataSource;

    protected SqlExecutorStrategy(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected <T> T collectMetrics(MetricDataConfig metricDataConfig) {
        try (Connection connection = dataSource.getConnection()) {
            T metricValue = collectMetricsUsingSQL(metricDataConfig, connection);
            TypeChecker.checkCast(metricValue, metricDataConfig.getReturnType());
            return metricValue;
        } catch (SQLException exp) {
            LOGGER.log(Level.SEVERE, "Unable to execute the sql command, Exception: " + exp);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T collectMetricsUsingSQL(MetricDataConfig metricDataConfig, Connection connection) {
        if (TypeChecker.isNumber(metricDataConfig.getReturnType())) {
            return (T) DbDcUtil.getSimpleMetricWithSql(connection, metricDataConfig.getQuery());
        } else if (TypeChecker.isList(metricDataConfig.getReturnType())) {
            return (T) DbDcUtil.getMetricWithSql(connection, metricDataConfig.getQuery(), metricDataConfig.getAttr());
        }
        return null;
    }

}
