package com.htc.qa.ui;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.htc.qa.config.Config;
import com.htc.qa.reporting.Log;

/**
 * Implementation of the CBT capabilities
 * 
 */
public class CBTCapabilities {
	private String bType = Config.getConfigProperty("cbt.browserType");
	private String bVersion = Config.getConfigProperty("cbt.browserVersion");
	private String platform = Config.getConfigProperty("cbt.platform");

	/**
	 * Creates the constructor
	 */
	public CBTCapabilities() {
		// no-op
	}

	private String setOSName(String platform) {
		String os;
		switch (platform) {
		case "mac10.12":
			os = "Mac10.12";
			break;
		case "mac10.11":
			os = "Mac10.11";
			break;
		case "windows10.ie":
			os = "Win10-E15";
			break;
		case "windows8.1":
			os = "Win8.1";
			break;
		case "windows8":
			os = "Win8";
			break;
		case "windows7":
			os = "Win7x64-C1";
			break;
		default:
			os = "Win10-E14";
		}
		return os;
	}

	private String setBrowserName(String browserType, String browserVersion) {
		String browserName;
		Log.debug(browserType.toString().concat(browserVersion));
		switch (browserType.toString().toLowerCase().concat(browserVersion)) {
		case "chrome57":
			browserName = "Chrome57x64";
			break;
		case "chrome56":
			browserName = "Chrome56x64";
			break;
		case "firefox46":
			browserName = "FF46";
			break;
		case "firefox45":
			browserName = "FF45";
			break;
		case "safari9":
			browserName = "Safari9";
			break;
		case "safari10":
			browserName = "Safari10";
			break;
		case "ieedge":
			browserName = "Edge14";
			break;
		case "ie11":
			browserName = "IE11";
			break;
		case "ie10":
			browserName = "IE10";
			break;
		default:
			browserName = "Chrome56x64";
		}
		return browserName;
	}
	
	private void setDimension(DesiredCapabilities caps, String dimension) {
		switch (dimension) {
		case "max":
			caps.setCapability("screen_resolution", "1400x1050");
			break;
		default:
			caps.setCapability("screen_resolution", "1400x1050");
		}
	}

	/**
	 * Sets the CBT capabilities that will be used when creating the WebDriver
	 * instance
	 * 
	 * @param bType
	 * @param bVersion
	 * @param platform
	 * @param testName
	 * @return
	 */
	public DesiredCapabilities setCBTCapabilities(String testName, String dimension) {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		
		// Set capabilities to browser type and browser specific capabilities
		//String osName = setOSName(platform);
		//String browserName = setBrowserName(bType, bVersion);
		if (!StringUtils.isBlank(testName)) {
			capabilities.setCapability("name", testName);
		}
		//capabilities.setCapability("browser_api_name", browserName);
		//capabilities.setCapability("os_api_name", osName);
		
		capabilities.setCapability("browserName", bType);
		capabilities.setCapability("version", bVersion);
		capabilities.setCapability("platform", platform);
		
		capabilities.setCapability("record_video", "true");
		capabilities.setCapability("record_network", "true");
		
		capabilities.setCapability("browserConnectionEnabled", "true");
		capabilities.setCapability("webStorageEnabled", "true");
		capabilities.setCapability("acceptSslCerts", "true");
		capabilities.setCapability("ignoreProtectedModeSettings", "true");
		capabilities.setCapability("requireWindowFocus", "true");
		capabilities.setCapability("ie.ensureCleanSession", "true");
		capabilities.setCapability("browserAttachTimeout", "30000");
		
		setDimension(capabilities, dimension);
		return capabilities;
	}

}
