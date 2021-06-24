package com.htc.qa.config;

/**
 * Custom Exception Class to throw exceptions in the Browser instances.
 * 
 *  @author anushar
 *
 */
@SuppressWarnings("serial")
public class PropertyConfigurationException extends RuntimeException {

    public PropertyConfigurationException() {
        // no op default constructor
    }

    /**
     * Constructor thats accepts string message.
     * 
     * @param message
     */
    public PropertyConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructor thats accepts throwable exception
     * 
     * @param exception
     *            to be thrown.
     */
    public PropertyConfigurationException(Throwable e) {
        super(e);
    }

    /**
     * Constructor thats accepts throwable exception
     * 
     * @param message
     *            to be shown on the report
     * @param exception
     *            message to be thrown
     */
    public PropertyConfigurationException(String message, Throwable e) {
        super(message, e);
    }
}
