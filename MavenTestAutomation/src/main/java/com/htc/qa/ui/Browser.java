package com.htc.qa.ui;

//import java.awt.Dimension;
//import java.net.Proxy;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;

import com.htc.qa.config.Config;
import com.htc.qa.config.Config.ConfigProperty;
import com.htc.qa.reporting.Log;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of the WebDriver related functions and actions to be performed
 * on browser.
 * 
 */
public class Browser {

	private WebDriver webDriver;
	private BrowserTyoe browserType;
	private String browserVersion;
	private String platform = Config.getConfigProperty(ConfigProperty.PLATFORM);
	private String testName;
	private boolean gridEnabled = Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.GRID_ENABLED));
	private boolean cbtEnabled = Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.CBT_ENABLED));
	private String errorMessage = "Unable to initialize remote webdriver";
	private String errorLocalMessage = "Unable to initialize local webdriver";
	private Proxy proxy;
	private String dimension;

	/**
	 * Creates the Driver constructor
	 * 
	 * @param browserType
	 * @param browserVersion
	 */
	public Browser(String browserType, String browserVersion, String platform, String testName, String dimension,
			Proxy proxy) {
		initializeVariables(browserType, browserVersion, platform, testName, dimension, proxy);
	}

	public Browser() {
		this.browserType = getBrowserType("");
	}

	public Browser(Proxy proxy) {
		this.browserType = getBrowserType("");
		this.proxy = proxy;
	}

	/**
	 * Returns WebDriver it to the user
	 * 
	 * @return {WebDriver}
	 */
	public WebDriver getDriver() {
		if (webDriver == null) {
			initializeDriver();
		}
		return webDriver;
	}

	private void initializeVariables(String browserType, String browserVersion, String platform, String testName,
			String dimension, Proxy proxy) {
		if (StringUtils.isBlank(browserType)) {
			this.browserType = getBrowserType(Config.getConfigProperty(ConfigProperty.BROWSER_TYPE));
		} else {
			this.browserType = getBrowserType(browserType);
		}
		if (StringUtils.isBlank(platform)) {
			this.platform = Config.getConfigProperty(ConfigProperty.PLATFORM);
		} else {
			this.platform = platform;
		}
		if (StringUtils.isBlank(dimension)) {
			this.dimension = "max";
		} else {
			this.dimension = dimension;
		}
		this.browserVersion = browserVersion;
		this.testName = testName;
		this.proxy = proxy;
	}

	/***
	 * Creates the browser object.
	 *
	 * @param browserType BrowserVersion BrowserType object specifying what type of
	 *                    browser to open
	 * 
	 */
	private void initializeDriver() {
		Log.debug("Creating an instance of a SeleniumBrowser." + browserType.toString());
		DriverCapabilities caps = new DriverCapabilities(browserType, browserVersion, platform, testName, dimension,
				proxy);
		DesiredCapabilities capabilities = caps.setCapabilities();
		webDriver = createDriver(capabilities);
	}

	/***
	 * Creates an instance of WebDriver to pass to Selenium. Will create a
	 * RemoteWebDriver if the grid.enabled property is set to true, otherwise it
	 * will create a local instance of the WebDriver corresponding with the
	 * browserType.
	 * 
	 * @param {BrowserTyoe}         Type of browser to create
	 * @param {DesiredCapabilities} DesiredCapabilities of the browser
	 * @return {WebDriver} instance of the browser to pass to Selenium
	 */
	private WebDriver createDriver(DesiredCapabilities capabilities) {
		if (gridEnabled) {
			try {
				webDriver = new RemoteWebDriver(new URL(Config.getConfigProperty(ConfigProperty.GRID_URL)),
						capabilities);
				webDriver = new Augmenter().augment(webDriver);
				((RemoteWebDriver) webDriver).setFileDetector(new LocalFileDetector());
				Log.info(((RemoteWebDriver) webDriver).getSessionId() + " Remote WebDriver running on Thread:  "
						+ Thread.currentThread().getId());
			} catch (Exception ex) {
				Log.error(errorMessage + ex);
				throw new WebDriverException(errorMessage, ex);
			}
		} else if (cbtEnabled) {
			try {
				String username = Config.getConfigProperty(ConfigProperty.CBT_USER);
				String authkey = Config.getConfigProperty(ConfigProperty.CBT_KEY);
				this.webDriver = new RemoteWebDriver(
						new URL("http://" + username + ":" + authkey + "@hub.crossbrowsertesting.com:80/wd/hub"),
						capabilities);
				((RemoteWebDriver) webDriver).setFileDetector(new LocalFileDetector());
				webDriver = new Augmenter().augment(webDriver);
				Log.info(((RemoteWebDriver) webDriver).getSessionId() + " Remote WebDriver running on Thread:  "
						+ Thread.currentThread().getId());
			} catch (Exception ex) {
				Log.error(errorMessage + ex);
				throw new WebDriverException(errorMessage, ex);
			}
		} else {
			Log.info("Using local WebDriver instance: " + browserType.getBrowserString());
			try {
				switch (browserType) {
				case IE:
					InternetExplorerOptions ieOptions = new InternetExplorerOptions();
					ieOptions.merge(capabilities);
					webDriver = new InternetExplorerDriver(ieOptions);
					break;
				case FIREFOX:
					System.setProperty("webdriver.gecko.driver", "D:\\Software\\geckodriver.exe");
					FirefoxOptions ffoptions = new FirefoxOptions();
					ffoptions.merge(capabilities);
					webDriver = new FirefoxDriver(ffoptions);
					webDriver.manage().window().maximize();
					break;
				case CHROME:
					System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");
					ChromeOptions options = new ChromeOptions();
					options.addArguments("--start-maximized");
					options.merge(capabilities);
					webDriver = new ChromeDriver(options);
					break;
				case SAFARI:
					SafariOptions safarioptions = new SafariOptions();
					safarioptions.merge(capabilities);
					webDriver = new SafariDriver(safarioptions);
					break;
				default:
					System.setProperty("webdriver.gecko.driver", "D:\\Software\\geckodriver.exe");
					FirefoxOptions ffoption = new FirefoxOptions();
					ffoption.merge(capabilities);
					webDriver = new FirefoxDriver(ffoption);
				}
			} catch (Exception e) {
				Log.error(errorLocalMessage, e);
				throw new IllegalStateException(errorLocalMessage, e);
			}
		}
		setDimension(dimension);
		return webDriver;
	}

	/**
	 * Converts the string browserType to enum type
	 * 
	 * @param bType
	 * @return {BrowserType}
	 */
	public static BrowserTyoe getBrowserType(String bType) {
		BrowserTyoe browserType;
		switch (bType) {
		case "ie":
			browserType = BrowserTyoe.IE;
			break;
		case "firefox":
			browserType = BrowserTyoe.FIREFOX;
			break;
		case "chrome":
			browserType = BrowserTyoe.CHROME;
			break;
		case "safari":
			browserType = BrowserTyoe.SAFARI;
			break;
		default:
			browserType = BrowserTyoe.CHROME;
		}
		return browserType;
	}

	private void setDimension(String dimension) {
		Log.debug(browserType.toString());
		switch (dimension) {
		case "max":
			if (!browserType.toString().equalsIgnoreCase("chrome")) {
				webDriver.manage().window().maximize();
			}
			break;
		case "iphone7":
			webDriver.manage().window().setSize(new Dimension(375, 667));
			break;
		case "iphone7Plus":
			webDriver.manage().window().setSize(new Dimension(414, 736));
			break;
		case "samsung":
			webDriver.manage().window().setSize(new Dimension(480, 853));
			break;
		case "nexus":
			webDriver.manage().window().setSize(new Dimension(411, 731));
			break;
		case "tablet":
			webDriver.manage().window().setSize(new Dimension(768, 1024));
			break;
		default:
			webDriver.manage().window().maximize();
		}
	}

	/***
	 * Closes the browser in use
	 */
	public void closeBrowser() {
		webDriver.close();
	}

	/***
	 * quit the browser in use
	 */
	public void quitBrowser() {
		webDriver.quit();
	}

	/***
	 * maximize the browser instance
	 */
	public void maximize() {
		webDriver.manage().window().maximize();
	}

	/***
	 * get page source of the browser instance
	 */
	public void pageSource() {
		webDriver.getPageSource();
	}

	public String getTitle() {
		return webDriver.getTitle();
	}

	public void getHandle() {
		webDriver.getWindowHandle();
	}

}
