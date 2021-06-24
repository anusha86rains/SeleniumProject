package com.htc.qa.ui;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.htc.qa.config.Config;
import com.htc.qa.config.Config.ConfigProperty;
import com.htc.qa.reporting.Log;

/**
 * Implementation of the WebDriver related functions and actions to be performed
 * on browser.
 * 
 */
public class DriverCapabilities {

	private BrowserTyoe browserType;
	private String browserVersion;
	private String platform;
	private String testName;
	private Proxy proxy;
	private String dimension;
	private boolean gridEnabled = Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.GRID_ENABLED));
	private boolean cbtEnabled = Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.CBT_ENABLED));

	/**
	 * Creates the Driver constructor
	 * 
	 * @param browserType
	 * @param browserVersion
	 */
	public DriverCapabilities(BrowserTyoe bType, String bVersion, String platform, String testName, String dimension, Proxy proxy) {
		this.browserType = bType;
		this.platform = platform;
		this.browserVersion = bVersion;
		this.testName = testName;
		this.proxy = proxy;
		this.dimension = dimension;
	}

	/***
	 * Set any Internet Explorer specific capabilities
	 * 
	 * @return IE specific capabilities
	 */
	private DesiredCapabilities setIECapabilities() {
		DesiredCapabilities capabilities;
		capabilities = DesiredCapabilities.internetExplorer();
		capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		return capabilities;
	}

	/***
	 * Set any Chrome specific capabilities
	 * 
	 * @return Chrome specific capabilities
	 */
	private DesiredCapabilities setChromeCapabilities() {
		DesiredCapabilities capabilities;		
		capabilities = DesiredCapabilities.chrome();
		if ("max".equals(dimension)) {
			capabilities.setCapability(ChromeOptions.CAPABILITY,
					maximizeChromeOnLocal());	
		}
		return capabilities;
	}

	/***
	 * Set any Firefox specific capabilities
	 * 
	 * @return Firefox specific capabilities
	 */
	private DesiredCapabilities setFirefoxCapabilities() {
		DesiredCapabilities capabilities;
		capabilities = DesiredCapabilities.firefox();
		try {
			FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("browser.link.open_newwindow.restriction", 0);
			capabilities.setCapability(FirefoxDriver.PROFILE, profile);
			capabilities.setCapability("marionette", true);
		} catch (Exception e) {
			Log.warn("Property 'firefox Profile' is not available in framework.properties files." + e);
		}
		return capabilities;
	}
	
	private void gridCapabilities(DesiredCapabilities capabilities) {
		if (gridEnabled) {
			if (!StringUtils.isBlank(browserVersion)) {
				Log.info("Setting browser version:" + browserVersion);
				capabilities.setVersion(browserVersion);
			}
			if (!StringUtils.isBlank(platform)) {
				if (platform.contains("mac")) {
					capabilities.setPlatform(Platform.MAC);
				} else {
					capabilities.setPlatform(Platform.WINDOWS);
				}
			}
			capabilities.setCapability("name", "Remote File Upload using Selenium 2's FileDetectors");
		}

	}

	private ChromeOptions maximizeChromeOnLocal() {
		Log.debug(System.getProperty("os.name"));
		String operatingSystem = System.getProperty("os.name");
		ChromeOptions options = new ChromeOptions();
		if (operatingSystem.contains("Mac")) {
			options.addArguments("--kiosk");
		} else {
			options.addArguments("--start-maximized");
		}
		return options;
	}

	/***
	 * Sets the capabilities that will be used when creating the WebDriver
	 * instance.
	 * 
	 * @param {BrowserTyoe}
	 *            Type of browser to create.
	 * @param {String}
	 *            browserVersion - Type of browser to create.
	 * @return An instance of DesiredCapabilities populated based on browser
	 *         type and framework.properties
	 */
	public DesiredCapabilities setCapabilities() {
		DesiredCapabilities capabilities = new DesiredCapabilities();

		// Set capabilities to browser type and browser specific capabilities
		if (cbtEnabled) {
			CBTCapabilities cbtCaps = new CBTCapabilities();
			capabilities = cbtCaps.setCBTCapabilities(testName, dimension);
		} else {
			switch (browserType) {
			case IE:
				capabilities = setIECapabilities();
				break;
			case FIREFOX:
				Log.debug("setting firefox caps");
				capabilities = setFirefoxCapabilities();
				capabilities.setCapability("marionette", true);
				break;
			case CHROME:
				Log.debug("setting chrome caps");
				capabilities = setChromeCapabilities();
				break;
			case SAFARI:
				Log.debug("setting safari caps");
				capabilities = DesiredCapabilities.safari();
				break;
			default:
				Log.debug("setting chrome caps");
				capabilities = setChromeCapabilities();
			}
		}
		// Set Platform and browser version if grid is enabled
		gridCapabilities(capabilities);

		if (proxy != null) {
			capabilities.setCapability(CapabilityType.PROXY, proxy);
		}
		return capabilities;
	}

}
