package com.ojr.core.metric;

/**
 * Enum representing different types of metric instruments.
 */
public enum MetricInstrumentType {
    /**
     * A gauge is a metric instrument that represents a single numerical value that can go up and down.
     */
    GAUGE,

    /**
     * A counter is a metric instrument that represents a non-negative, monotonically increasing cumulative measurement.
     */
    COUNTER,

    /**
     * An up-down counter is a metric instrument that represents a non-negative, monotonically changing cumulative measurement.
     * It allows both increases and decreases.
     */
    UPDOWN_COUNTER,

    /**
     * A histogram is a metric instrument that records the distribution of values in a stream of data.
     * It can be used to understand the range, variability, and central tendency of the data.
     */
    HISTOGRAM
}
