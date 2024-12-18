package com.ojr.core;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum-based singleton class that provides a thread-safe way to manage HTTP headers.
 */
public enum HeadersSupplier {
    /**
     * The single instance of the HeadersSupplier.
     */
    INSTANCE;

    /**
     * A map to store HTTP headers.
     */
    private final Map<String, String> headers = new HashMap<>();

    /**
     * Updates the headers with a new set of headers. This method is synchronized to ensure thread safety.
     *
     * @param newHeaders the new headers to be set
     */
    public synchronized void updateHeaders(Map<String, String> newHeaders) {
        headers.clear();
        headers.putAll(newHeaders);
    }

    /**
     * Returns a copy of the current headers. Returning a copy ensures that the original headers cannot be modified from outside this class.
     *
     * @return a new HashMap containing the current headers
     */
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
}
