package com.htc.features;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.htc.qa.ui.PageObjects;

public class RocheLoginPageFeatures extends PageObjects {
	
	private Map<String, String> inputDataMap;
	private RocheLoginPageObjects eportalLoginPageObjects;

	public RocheLoginPageFeatures(WebDriver driver, Map<String, String> inputDataMap) {
		super(driver);
		this.inputDataMap = inputDataMap;
		this.eportalLoginPageObjects = new RocheLoginPageObjects(driver);
	}
	

	public void clickSingaporeGateWay() {
		click(eportalLoginPageObjects.singaporeGateWay); 
	}
	

	// This method is to click on Login Button
	public void clickConnectButton() {
	   click(eportalLoginPageObjects.connectButton);
	}
}
