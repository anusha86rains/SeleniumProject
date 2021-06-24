package com.htc.qa.ui;

/**
 * Custom Exception Class to throw exceptions in the TestNG utilities modules.
 * 
 *  @author anushar
 *
 */
@SuppressWarnings("serial")
public class FeatureAutomationException extends RuntimeException {

    public FeatureAutomationException() {
        // no op default constructor
    }

    /**
     * Constructor thats accepts string message.
     * 
     * @param message
     */
    public FeatureAutomationException(String message) {
        super(message);
    }

    /**
     * Constructor thats accepts throwable exception
     * 
     * @param exception
     *            to be thrown.
     */
    public FeatureAutomationException(Throwable e) {
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
    public FeatureAutomationException(String message, Throwable e) {
        super(message, e);
    }
}
