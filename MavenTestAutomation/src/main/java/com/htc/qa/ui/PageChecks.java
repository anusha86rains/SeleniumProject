package com.htc.qa.ui;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Class to perform tests on the page
 * 404 checks
 * load times
 *  
 *  @author anushar
 */
public class PageChecks {

	private JavascriptExecutor js;

	/**
	 * Initialize the constructor passing WebDriver
	 * 
	 * @param driver
	 */
	public PageChecks(WebDriver driver) {
		this.js = (JavascriptExecutor) driver;
	}

	/**
	 * Retrieve the page load time
	 * 
	 * @return
	 */
	public double retrievePageLoadTime() {
		return (Double) js.executeScript(
				"return (window.performance.timing.loadEventEnd - window.performance.timing.navigationStart) / 1000");
	}

	/**
	 * Retrieve the Dom load time
	 * 
	 * @return
	 */
	public double retrieveDomLoadTime() {
		return (Double) js.executeScript(
				"return (window.performance.timing.domContentLoadedEventEnd - window.performance.timing.navigationStart) / 1000");
	}

}
