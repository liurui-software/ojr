package com.ojr.core;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.resources.Resource;

import static io.opentelemetry.api.common.AttributeKey.*;

/**
 * A class to enrich OpenTelemetry resources with additional attributes.
 */
public class ResourceEnricher {
    private Resource resource;

    /**
     * Constructor to initialize the ResourceEnricher with an existing resource.
     *
     * @param resource The initial resource to be enriched.
     */
    public ResourceEnricher(Resource resource) {
        this.resource = resource;
    }

    /**
     * Get the current resource.
     *
     * @return The current resource.
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Set a new resource.
     *
     * @param resource The new resource to set.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Enrich the resource with a specific attribute key and value.
     *
     * @param key   The attribute key.
     * @param value The attribute value.
     */
    public <T> void enrich(AttributeKey<T> key, T value) {
        if (value == null)
            return;
        resource = resource.merge(
                Resource.create(Attributes.of(key, value))
        );
    }

    /**
     * Enrich the resource with a string key and an object value. The method handles different types of values
     * (Integer, Long, Double, and other types) and converts them to the appropriate attribute type.
     *
     * @param key   The attribute key as a string.
     * @param value The attribute value.
     */
    public void enrich(String key, Object value) {
        if (value == null)
            return;
        if (value instanceof Integer) {
            resource = resource.merge(
                    Resource.create(Attributes.of(longKey(key), ((Integer) value).longValue()))
            );
        } else if (value instanceof Long) {
            resource = resource.merge(
                    Resource.create(Attributes.of(longKey(key), (Long) value))
            );
        } else if (value instanceof Double) {
            resource = resource.merge(
                    Resource.create(Attributes.of(doubleKey(key), (Double) value))
            );
        } else {
            resource = resource.merge(
                    Resource.create(Attributes.of(stringKey(key), value.toString()))
            );
        }
    }
    /**
     * Enriches the current resource by merging it with the provided resource.
     *
     * @param resource1 The resource to merge with the current resource.
     */
    public void enrich(Resource resource1) {
        resource = resource.merge(resource1);
    }
}