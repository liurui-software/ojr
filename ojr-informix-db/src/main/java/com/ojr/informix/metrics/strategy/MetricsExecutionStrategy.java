package com.ojr.informix.metrics.strategy;

import com.ojr.informix.metrics.MetricDataConfig;

import java.util.List;

public abstract class MetricsExecutionStrategy {
    protected abstract <T> T collectMetrics(MetricDataConfig metricDataConfig);

    static class TypeChecker {

        public static <T> T checkCast(Object object, Class<T> returnType) {
            try {
                return returnType.cast(object);
            } catch (ClassCastException e) {
                throw new IllegalStateException("Error casting the metric", e);
            }
        }

        public static boolean isDouble(Class<?> type) {
            return Double.class.isAssignableFrom(type);
        }
        public static boolean isNumber(Class<?> type) {
            return Number.class.isAssignableFrom(type);
        }

        public static boolean isString(Class<?> type) {
            return String.class.isAssignableFrom(type);
        }

        public static boolean isList(Class<?> type) {
            return List.class.isAssignableFrom(type);
        }
    }
}
