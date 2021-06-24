package com.htc.qa.config;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.htc.qa.core.DateUtils;
import org.apache.commons.lang3.StringUtils;

import com.htc.qa.reporting.Log;

/**
 * Class to initialize the properties. Default properties are defined in the
 * {@link enum} which can be overwritten via system properties or
 * test.propetries file. first enum properties are read and then test.properties
 * and then system properties
 * 
 * @author anushar
 */
public class Config {

	private static Properties configProperties;
	private static final String DEFAULTFILE = "test.properties";
	private static final String ERRORMESSAGE = "The Search property can not be null ";
	private static final String DEFAULT_FALSE_VALUE = "false";

	/**
	 * Method to return the default property value for the enum defined
	 * properties
	 * 
	 * @param property
	 * @return
	 */
	public static String getConfigProperty(ConfigProperty property) {
		String propertyValue = null;
		if (configProperties == null) {
			getConfig();
		}
		if (property != null) {
			propertyValue = (String) Config.configProperties.get(property.getPropName());
		} else
			Log.error(ERRORMESSAGE);
		return propertyValue;
	}

	/**
	 * return the property values as defined in the properties file
	 * 
	 * @param key
	 * @return
	 */
	public static String getConfigProperty(String key) {
		String propertyValue = null;
		if (configProperties == null) {
			getConfig();
		}
		if (!StringUtils.isBlank(key)) {
			propertyValue = (String) configProperties.get(key);
		} else
			Log.error(ERRORMESSAGE + key);
		return propertyValue;
	}

	/**
	 * set enum properties
	 *
	 * @param property
	 * @param value
	 */
	public static void setConfigProperty(ConfigProperty property, String value) {
		if (null != value) {
			Config.getConfig().put(property.getPropName(), value);
		}
	}

	/**
	 * default properties read first during property initialization. enum will
	 * have the properties only for the framework utils classes.
	 * 
	 */
	public enum ConfigProperty {
		BROWSER_TYPE("browserType", "chrome"),
		PLATFORM("platform", "windows"),
		BROWSER_VERSION("browserVersion", "60"),
		EXPLICIT_WAIT("explicit.wait", "30"),
		/*
		 * MONGODB_HOST("mongodb.host", "red-app-vrest-1"), MONGODB_PORT("mongodb.port",
		 * "27017"), MONGODB_USER("mongodb.user", "extent_svc"),
		 * MONGODB_PWD("mongodb.pwd", "t06AIYDyfzP1v55Ixs3TdnfFje8MY+Cd"),
		 * MONGODB_DB("mongodb.db", "extent"),
		 */
		REPORT_DASHBOARD_ENABLED("report.dashboard.enabled", DEFAULT_FALSE_VALUE),
		REPORT_DASHBOARD_HOST("report.dashboard.host", "ist000532"),
		REPORT_DASHBOARD_PORT("report.dashboard.port", "1337"),
		PROJECT_NAME("project.name", DEFAULT_FALSE_VALUE),
		REPORT_NAME("report.name", "TestAutomationReport"),
		REPORT_FOLDER("report.folder", "Report" + DateUtils.getCustomTimeStamp()),
		BUILD_TAG("build.tag", "1.0-SNAPSHOT"),
		GRID_HUB("grid.hub", "red-app-jen-p01.htc.com"),
		GRID_URL("grid.url", "http://red-app-jen-p01.htc.com:4444/wd/hub"),
		GRID_ENABLED("grid.enabled", "false"),
		GRID_PORT("grid.port", "4444"),		
		CBT_URL("cbt.host", "https://crossbrowsertesting.com/api/v3/"),
		CBT_KEY("cbt.key", "9c132209f1661dc"), CBT_ENABLED("cbt.enabled",
		DEFAULT_FALSE_VALUE), CBT_USER("cbt.user", "anushar@htcindiaht.com"),		
		SCREENSHOT_ON_SUCCESS("screenshot.on.success", DEFAULT_FALSE_VALUE),
		SCREENSHOT_ENABLED("screenshot.enabled", DEFAULT_FALSE_VALUE),
		/*
		 * API_SOCKET_TO("http.socket.timeout", "120000"),
		 * API_CONNECTION_TO("http.connection.timeout", "12000"),
		 */
		QUIT_BROWSER("quit.browser", "true"),
		CRYPTO_PROPERTY("crypto.property", "w3bp@$$w0rd$@f3k3y"),
		TEST_ENVIRONMENT_PROPERTY("test.environment", "QA");
		
		
		private String propName = null;
		private String propDefaultValue = null;

		/**
		 * Constructor for this enum class.
		 * 
		 */
		private ConfigProperty(String prop, String defaultValue) {
			this.propName = prop;
			this.propDefaultValue = defaultValue;
		}

		/**
		 * Returns the name of the configuration property
		 * 
		 * @return The name of the configuration property
		 */
		public String getPropName() {
			return this.propName;
		}

		/**
		 * Returns the default value for the configuration property
		 * 
		 * @return property value
		 */
		public String getPropDefaultValue() {
			return this.propDefaultValue;
		}
	}

	private static Properties getConfig() {
		if (configProperties == null) {
			initializeConfig();
		}
		return configProperties;
	}

	private static Properties readPropertiesFile() {
		PropertiesReader propReader = new PropertiesReader(DEFAULTFILE);
		return propReader.getProperties();
	}

	/**
	 * method to initialize properties
	 */
	private static synchronized void initializeConfig() {
		configProperties = new Properties();

		// Use defaults
		ConfigProperty[] configProps = ConfigProperty.values();
		for (int i = 0; i < configProps.length; i++) {
			Log.debug("Default Property: Key: " + configProps[i].getPropName() + " System Value: " + configProps[i].getPropDefaultValue());
			configProperties.put(configProps[i].getPropName(), configProps[i].getPropDefaultValue());
		}

		// Read the test properties file.
		Map<Object, Object> userConfig = new HashMap<>();
		try {
			userConfig = readPropertiesFile();
		} catch (Exception e) {
			Log.warn("Could not read the config file test.properties. Only default properties are available "
					+ e.getMessage());
		}

		// Load the test properties values if found.
		if (!userConfig.isEmpty()) {
			for (Map.Entry<Object, Object> entry : userConfig.entrySet()) {
				Log.debug("Added/Replaced Test File Property: Key: " + entry.getKey() + " System Value: " + entry.getValue());
				configProperties.put(entry.getKey(), entry.getValue());
			}
		}

		// Load in environment variables (if defined)
		Enumeration<Object> keys = configProperties.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = System.getenv(key);
			Log.debug("System Environment Property: Key: " + key + " System Value: " + value);
			if (!StringUtils.isBlank(value)) {
				configProperties.put(key, value);
			}
		}

		// Load in System properties variables (if defined)
		Enumeration<Object> keyss = configProperties.keys();
		while (keyss.hasMoreElements()) {
			String key = (String) keyss.nextElement();
			String value = System.getProperty(key);
			Log.debug("System Property: Key: " + key + " System Value: " + value);
			if (!StringUtils.isBlank(value)) {
				configProperties.put(key, value);
			}
		}
	}

}