package com.ojr.core;

/**
 * Custom exception class for Data Collector (DC) related errors.
 */
public class DcException extends Exception {
    /**
     * Constructs a new DcException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public DcException(String message) {
        super(message);
    }
}
