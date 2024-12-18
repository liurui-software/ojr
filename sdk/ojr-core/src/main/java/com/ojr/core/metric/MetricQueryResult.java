package com.ojr.core.metric;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the result of a metric query, including a value, an optional key, and a map of attributes.
 */
public class MetricQueryResult {
    /**
     * The numeric value of the metric.
     */
    private final Number value;

    /**
     * An optional key associated with the metric.
     */
    private String key;

    /**
     * A map to store additional attributes related to the metric.
     */
    private final Map<String, Object> attributes = new HashMap<>();

    /**
     * Constructs a new MetricQueryResult with the specified value.
     *
     * @param value the numeric value of the metric
     */
    public MetricQueryResult(Number value) {
        this.value = value;
    }

    /**
     * Returns the numeric value of the metric.
     *
     * @return the numeric value
     */
    public Number getValue() {
        return value;
    }

    /**
     * Returns the key associated with the metric.
     *
     * @return the key, or null if not set
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key for the metric.
     *
     * @param key the key to set
     * @return this MetricQueryResult instance for method chaining
     */
    public MetricQueryResult setKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Returns the map of attributes associated with the metric.
     *
     * @return the attributes map
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Retrieves the value of a specific attribute.
     *
     * @param key the key of the attribute to retrieve
     * @return the attribute value, or null if the attribute is not found
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Sets an attribute for the metric.
     *
     * @param key       the key of the attribute to set
     * @param attribute the value of the attribute to set
     * @return this MetricQueryResult instance for method chaining
     */
    public MetricQueryResult setAttribute(String key, Object attribute) {
        attributes.put(key, attribute);
        return this;
    }
}
