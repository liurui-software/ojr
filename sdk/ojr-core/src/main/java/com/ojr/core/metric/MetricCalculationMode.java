package com.ojr.core.metric;

/**
 * An enum representing different modes for metric calculation.
 */
public enum MetricCalculationMode {
    /**
     * Direct mode for metric calculation, where metrics are calculated as-is without any transformation.
     */
    DIRECT,

    /**
     * Rate mode for metric calculation, where metrics are calculated as a rate (e.g., per second).
     */
    RATE
}
