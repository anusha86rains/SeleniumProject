package com.htc.features;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RocheLoginPageObjects {

	
	@FindBy(xpath="//*[@id='locations-list']/div[2]/ul[2]/li[3]/label")
	WebElement singaporeGateWay;
	
	@FindBy(xpath="//*[@id='access-basic']")
	WebElement connectButton;
	
	
	/**
	 * Initialize the PageObject for ecommerce web page
	 * 
	 * @param driver
	 */
	public RocheLoginPageObjects(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}
}
