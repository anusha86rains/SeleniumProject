package com.htc.qa.reporting;

import org.apache.log4j.Logger;

/**
 * Logger Utility to write custom logs in the application
 * 
 *  @author anushar
 *
 */
public class Log {

	private static final Logger LOGGER = Logger.getLogger(Log.class);

	private Log() {
		// no-op
	}

	private static String getCallingMethod(int callStackDepth) {
		String className = Thread.currentThread().getStackTrace()[callStackDepth].getClassName();
		String methodName = Thread.currentThread().getStackTrace()[callStackDepth].getMethodName();
		return "[" + className + "." + methodName + "] ";
	}

	/**
	 * This method writes info messages in the log arguments are custom message
	 * and the throwable exception
	 * 
	 * @param message
	 * @param t
	 */
	public static void info(String message, Throwable t) {
		LOGGER.info(getCallingMethod(3) + message, t);
	}

	/**
	 * This method writes error messages in the log arguments are custom message
	 * and the throwable exception
	 * 
	 * @param message
	 * @param t
	 */
	public static void error(String message, Throwable t) {
		LOGGER.error(getCallingMethod(3) + message, t);
	}

	/**
	 * This method writes warn messages in the log arguments are custom message
	 * and the throwable exception
	 * 
	 * @param message
	 * @param t
	 */
	public static void warn(String message, Throwable t) {
		LOGGER.warn(getCallingMethod(3) + message, t);
	}

	/**
	 * This method writes debug messages in the log arguments are custom message
	 * and the throwable exception
	 * 
	 * @param message
	 * @param t
	 */
	public static void debug(String message, Throwable t) {
		LOGGER.debug(getCallingMethod(3) + message, t);
	}

	/**
	 * This method writes custom info messages in the log,
	 * 
	 * @param message
	 */
	public static void info(String message) {
		LOGGER.info(getCallingMethod(3) + message);
	}

	/**
	 * This method writes custom error messages in the log,
	 * 
	 * @param message
	 */
	public static void error(String message) {
		LOGGER.error(getCallingMethod(3) + message);
	}

	/**
	 * This method writes custom warn messages in the log,
	 * 
	 * @param message
	 */
	public static void warn(String message) {
		LOGGER.warn(getCallingMethod(3) + message);
	}

	/**
	 * This method writes custom debug messages in the log,
	 * 
	 * @param message
	 */
	public static void debug(String message) {
		LOGGER.debug(getCallingMethod(3) + message);
	}

}
