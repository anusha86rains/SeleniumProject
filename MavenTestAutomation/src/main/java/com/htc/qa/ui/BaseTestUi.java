package com.htc.qa.ui;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.SystemOutLogger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import com.htc.qa.config.Config;
import com.htc.qa.config.Config.ConfigProperty;
import com.htc.qa.reporting.Log;
import com.htc.qa.reporting.ReportLogger;
import com.htc.qa.testng.TestNGUtilities;

/**
 * This Class will be extended in each of the test class to initialize webDriver
 * and open the URL The TestNG annotation is before and after methods.
 * 
 * @author anushar
 * 
 */
public class BaseTestUi {

	private String p_AppName;
	private String fileName;
	private String startRow;
	private String endRow;
	private String bVersion;
	private Browser browser;
	private String buildTag = Config.getConfigProperty(ConfigProperty.BUILD_TAG);
	private boolean cbtEnabled = Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.CBT_ENABLED));
	private boolean quitBrowser = Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.QUIT_BROWSER));
	private boolean isScreenshotEnabled = Boolean
			.parseBoolean(Config.getConfigProperty(ConfigProperty.SCREENSHOT_ENABLED));
	private boolean networkAnalysisEnabled = Boolean.parseBoolean(Config.getConfigProperty("network.analysis.enabled"));
	protected WebDriver driver;
	protected ReportLogger reportLogger;
	protected String url;
	protected Proxy proxy;
	protected String bType;
	protected String platform;
	protected String dimension;

	public BaseTestUi(String fileName, String startRow, String endRow, String bType, String bVersion, String platform,
			String url, String dimension) {
		this.fileName = fileName;
		this.startRow = startRow;
		this.endRow = endRow;
		this.bType = bType;
		this.bVersion = bVersion;
		this.platform = platform;
		this.url = url;
		this.dimension = dimension;
	}

	/**
	 * This method is invoked at the beginning of each test class and sets up
	 * the browser instance.
	 * 
	 * @param context
	 * @throws UnknownHostException
	 */
	@BeforeClass(alwaysRun = true)
	public void initializeBrowser() throws UnknownHostException {
		Log.info("Initializing before class objects");
		p_AppName = this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.') + 1);

		browser = new Browser(bType, bVersion, platform, p_AppName, dimension, proxy);
		driver = browser.getDriver();
	}

	/**
	 * This method is invoked at the beginning of each test and sets up the
	 * report logger instance
	 * 
	 */
	@BeforeMethod(alwaysRun = true)
	public void beforeMethodSetup(ITestResult tr) {

		// Set the attributes to be printed in the HTML report and create the
		// report object.
		tr.setAttribute("BrowserType", bType);
		tr.setAttribute("BrowserVersion", ((RemoteWebDriver) driver).getCapabilities().getVersion());
		tr.setAttribute("Platform", System.getProperty("os.name"));
		tr.setAttribute("BuildEnv", Config.getConfigProperty("test.environment"));
		tr.setAttribute("driver", driver);
		reportLogger = new ReportLogger(driver, tr, isScreenshotEnabled);
        System.out.println(url);
		// open the url if it is passed from xml file.
		if (!StringUtils.isBlank(url)) {
			PageObjects page = new PageObjects(driver);
			page.openURL(url);
		}

	}

	/**
	 * After every Test method the tear down method is called to close the
	 * session.
	 * 
	 * @param result
	 * @param context
	 */
	@AfterClass(alwaysRun = true)
	public void tearDown(ITestContext context) {
		if (quitBrowser) {
			browser.quitBrowser();
		}
	}

	@DataProvider(name = "csv")
	public Object[][] createData() throws IOException {
		return TestNGUtilities.parseCsvToMap(fileName, Integer.parseInt(startRow), Integer.parseInt(endRow));
	}

	@DataProvider(name = "csvParallel", parallel = true)
	public Object[][] createParallelData() throws IOException {
		return TestNGUtilities.parseCsvToMap(fileName, Integer.parseInt(startRow), Integer.parseInt(endRow));
	}

}
