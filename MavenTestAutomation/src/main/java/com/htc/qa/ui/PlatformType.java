package com.htc.qa.ui;

/**
 * enum to restrict the usage of platforms in test automation framework.
 * 
 * @author anushar
 */
public enum PlatformType {
	MAC("mac"), WINDOWS("windows");

	private final String platformValue;

	PlatformType(String platformString) {
		platformValue = platformString;
	}

	public String getPlatformString() {
		return platformValue;
	}
	
}
