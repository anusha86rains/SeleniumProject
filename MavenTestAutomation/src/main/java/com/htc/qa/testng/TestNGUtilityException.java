package com.htc.qa.testng;

/**
 * Custom Exception Class to throw exceptions in the TestNG utilities modules.
 * 
 *  @author anushar
 *
 */
@SuppressWarnings("serial")
public class TestNGUtilityException extends RuntimeException {

    public TestNGUtilityException() {
        // no op default constructor
    }

    /**
     * Constructor thats accepts string message.
     * 
     * @param message
     */
    public TestNGUtilityException(String message) {
        super(message);
    }

    /**
     * Constructor thats accepts throwable exception
     * 
     * @param exception
     *            to be thrown.
     */
    public TestNGUtilityException(Throwable e) {
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
    public TestNGUtilityException(String message, Throwable e) {
        super(message, e);
    }
}
