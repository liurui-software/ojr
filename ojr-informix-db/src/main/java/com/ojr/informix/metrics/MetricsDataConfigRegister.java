package com.ojr.informix.metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Register class to store the Mapping of the MetricDataConfig for each metrics
 */
public class MetricsDataConfigRegister {
    private static final Map<String, MetricDataConfig> metricConfigMap = new HashMap<>();

    public static MetricDataConfig getMetricDataConfig(String metricName) {
        return metricConfigMap.get(metricName);
    }

    public static void subscribeMetricDataConfig(String metricKey, MetricDataConfig metricDataConfig) {
        metricConfigMap.put(metricKey, metricDataConfig);
    }
}

