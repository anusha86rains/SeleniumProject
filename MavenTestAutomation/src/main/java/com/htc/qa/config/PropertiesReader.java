package com.htc.qa.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.htc.qa.reporting.Log;

/**
 * Class to read the properties file and create Properties object. It can read
 * list of properties file or one file.
 * 
 * Usage: Create the constructor passing the list of file or one file. only
 * filename is sufficient the class will try to find the file in the classpath.
 * The file should be located in the resource directory for easy access.
 * 
 * @author anushar
 */
public class PropertiesReader {

	private Properties properties;
	private String fileName;
	private List<String> filesList;
	private static final String ERROR_MESSAGE = "Unable to load the property file: ";
	private static final String ERROR_LIST_MESSAGE = "Files List cannot be null or empty";
	private static final String ERROR_INVALID_FILENAME_MESSAGE = "FileName cannot be null or empty";

	/**
	 * Constructor to initialize the list of fileNames to be loaded in the
	 * properties
	 * 
	 * @param files
	 */
	public PropertiesReader(List<String> files) {
		this.filesList = files;
	}

	/**
	 * Constructor to initialize the fileName variable to be loaded in the
	 * properties
	 * 
	 * @param fileName
	 */
	public PropertiesReader(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Getter to return the properties object
	 * 
	 * @return {@link Properties}
	 */
	public Properties getProperties() {
		if (properties == null) {
			setProperties();
		}
		return properties;
	}

	/**
	 * return the property values as defined in the properties file
	 * 
	 * @param key
	 * @return value for the input property
	 */
	public String getConfigProperty(String key) {
		String propertyValue = null;
		if (properties == null && key != null) {
			getProperties();
		}
		if (properties != null) {
			propertyValue = (String) properties.get(key);
		}
		return propertyValue;
	}

	/**
	 * Initialize properties object
	 * 
	 */
	private void setProperties() {
		if (!StringUtils.isBlank(this.fileName)) {
			this.properties = loadPropertiesFile(fileName);
		} else {
			this.properties = loadPropertiesFiles(filesList);
		}
	}

	private Properties loadPropertiesFile(String fileName) {
		Properties prop = new Properties();
		String filePath = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		if (StringUtils.isBlank(fileName)) {
			throw new PropertyConfigurationException(ERROR_INVALID_FILENAME_MESSAGE);
		}

		try (BufferedReader input = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(fileName)))) {
			prop.load(input);
		} catch (IOException ex) {
			Log.error(ERROR_MESSAGE + filePath + ex);
			throw new PropertyConfigurationException(ERROR_MESSAGE + fileName, ex);
		}
		return prop;
	}

	private Properties loadPropertiesFiles(List<String> filesList) {
		Properties prop = new Properties();
		if (filesList != null && !filesList.isEmpty()) {
			Log.warn(ERROR_LIST_MESSAGE);
			throw new PropertyConfigurationException(ERROR_LIST_MESSAGE);
		}
		for (String file : filesList) {
			Map<Object, Object> userConfig;
			userConfig = loadPropertiesFile(file);

			// Load the test properties values if found.
			if (!userConfig.isEmpty()) {
				for (Map.Entry<Object, Object> entry : userConfig.entrySet()) {
					properties.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return prop;
	}

}