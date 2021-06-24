package com.htc.qa.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import com.htc.qa.config.Config;
import com.htc.qa.config.Config.ConfigProperty;
import com.htc.qa.reporting.Log;

/**
 * Implementation of the WebDriver related functions and actions to be performed
 * on a WebElement. This class is to be extended in all the page objects
 * 
 *  @author anushar
 *
 */
public class PageObjects {

	private WebDriver driver;
	private long explicitWaitTime = Long.parseLong(Config.getConfigProperty(ConfigProperty.EXPLICIT_WAIT));
	protected FluentWait<WebDriver> wait;
	private static final String ERRORMESSAGE = "Web Element not available ";
	private static String checkBoxAttributeSelection = "value";

	/**
	 * Constructor to initialize WebDriver
	 * 
	 * @param driver
	 *            The {@link WebDriver} element to use.
	 */
	public PageObjects(WebDriver driver) {
		this.driver = driver;
		initializeWait();
	}

	protected void initializeWait() {
		if (wait == null) {
			Log.info("initialising wait");
			wait = new FluentWait<>(driver).withTimeout(explicitWaitTime, TimeUnit.SECONDS).
					pollingEvery(5, TimeUnit.SECONDS).ignoring(StaleElementReferenceException.class)
					.ignoring(NoSuchElementException.class);
		}
	}

	/**
	 * This Method is to login through the windows popup for IE
	 * 
	 * 
	 * @throws Throwable
	 */

	public void alertAuthentication(String userName, String password) {
		try {
			Log.info("alertAuth");
			Alert alert = wait.until(ExpectedConditions.alertIsPresent());
			alert.sendKeys(userName); 
			alert.sendKeys(password);
			//alert.authenticateUsing(new UserAndPassword(userName, password));
		} catch (Exception e) {
			Log.error("The Alert Authentication has been failed.", e);
			throw new WebDriverException("The Alert Authentication has been failed.", e);
		}
	}

	/**
	 * return the webDriver.
	 * 
	 * @return WebDriver
	 */
	public WebDriver getDriver() {
		return driver;
	}

	/**
	 * refresh page
	 */
	public void refreshPage() {
		driver.navigate().refresh();
	}

	/**
	 * Performing the browser back button click
	 * 
	 */
	public void clickBrowserBackButton() {
		driver.navigate().back();
	}

	/**
	 * method to open the url
	 * 
	 * @param url
	 * @return
	 */
	public void openURL(String url) {
		getDriver().navigate().to(url);
		waitForPageToLoad();
	}

	/**
	 * Get the title of the web page.
	 * 
	 * @return The title of the page, if found. If not found, an empty string
	 */
	public String getTitle() {
		String value = driver.getTitle();
		return (value == null) ? "" : value; // empty value instead of null
	}

	/**
	 * Close Browser
	 * 
	 */
	public void closeBrowser() {
		driver.close();
	}

	/**
	 * Quit Browser
	 * 
	 */
	public void quitBrowser() {
		driver.quit();
	}

	/**
	 * @param webElement
	 *            The {@link WebElement} element is present and found.
	 * @return A {@link Boolean} value
	 * @throws Exception
	 */
	private boolean isElementPresent(WebElement webElement) {
		Boolean isAvailable = false;
		if (wait == null) {
			initializeWait();
		}
		try {
			if (wait.until(ExpectedConditions.visibilityOf(webElement)).isDisplayed()
					|| wait.until(ExpectedConditions.visibilityOf(webElement)).isEnabled()) {
				isAvailable = true;
			}
		} catch (Exception e) {
			Log.error(ERRORMESSAGE, e);
			throw new FeatureAutomationException("Webelement is not available on this page DOM structure." + webElement);
		}
		return isAvailable;
	}

	/**
	 * @param webElement
	 *            The {@link WebElement} element is present and found.
	 * @return A {@link Boolean} value
	 * @throws Exception
	 */
	public boolean isElementAvailable(WebElement webElement) {
		Boolean isAvailable = false;
		if (wait == null) {
			initializeWait();
		}
		try {
			if (wait.until(ExpectedConditions.visibilityOf(webElement)).isDisplayed()
					|| wait.until(ExpectedConditions.visibilityOf(webElement)).isEnabled()) {
				isAvailable = true;
			}
		} catch (Exception e) {
			Log.warn(ERRORMESSAGE + e.getMessage());
		}
		return isAvailable;
	}

	/**
	 * @param webElement
	 *            The {@link WebElement} element is present and found.
	 * @return A {@link Boolean} value
	 * @throws Exception
	 */
	public boolean isElementAvailable(WebElement webElement, String waitTime) {
		Boolean isAvailable = false;
		FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
				.withTimeout(Long.parseLong(waitTime), TimeUnit.SECONDS).pollingEvery(5, TimeUnit.SECONDS)
				.ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class);
		try {
			if (fluentWait.until(ExpectedConditions.visibilityOf(webElement)).isDisplayed()
					|| fluentWait.until(ExpectedConditions.visibilityOf(webElement)).isEnabled()) {
				isAvailable = true;
			}
		} catch (Exception e) {
			Log.info(String.format("Explicit wait for %s is completed", waitTime));
		}
		return isAvailable;
	}

	/**
	 * @param webElement
	 *            The {@link WebElement} element is present and found.
	 * @return A {@link Boolean} value
	 * @throws Exception
	 */
	public boolean isElementDisappear(WebElement webElement) {
		Boolean isNotAvailable = false;
		if (wait == null) {
			initializeWait();
		}
		try {
			if (wait.until(ExpectedConditions.invisibilityOf(webElement))) {
				isNotAvailable = true;
			}
		} catch (Exception e) {
			Log.warn(ERRORMESSAGE + e.getMessage());
		}
		return isNotAvailable;
	}

	/**
	 * Click the webElement once it is visible
	 * 
	 * @param element
	 *            The {@link WebElement} element to click.
	 */
	public void click(WebElement element) {
		if (isElementPresent(element)) {
			element.click();
			waitForPageToLoad(20);
		}
	}

	/**
	 * Click the webElement once it is visible after scrolling to it
	 * 
	 * @param element
	 *            The {@link WebElement} element to click.
	 */
	public void clickAfterScroll(WebElement element) {
		if (isElementPresent(element)) {
			scrollIntoViewByElement(element);
			element.click();
			waitForPageToLoad(20);
		}
	}

	/**
	 * Click the webElement once it is visible
	 * 
	 * @param element
	 *            The {@link WebElement} element to click.
	 */
	public void clickNoWait(WebElement element) {
		if (isElementPresent(element)) {
			element.click();
		}
	}

	public void clickByJavaScript(String xpathValue) {
		try {
			WebElement element = driver.findElement(By.xpath(xpathValue));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", element);
			waitForPageToLoad(2);
		} catch (Exception e) {
			Log.error(ERRORMESSAGE, e);
			throw new FeatureAutomationException("Unable to click the webelemnet by using java script executor", e);
		}
	}

	public void clickByJavaScript(WebElement element) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", element);
			waitForPageToLoad(2);
		} catch (Exception e) {
			Log.error(ERRORMESSAGE, e);
			throw new FeatureAutomationException("Unable to click the webelemnet by using java script executor", e);
		}
	}

	/**
	 * Input the text when the element is visible
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 * @param text
	 *            The text to type.
	 * @throws Exception
	 */
	public void inputText(WebElement element, String text) {
		if (isElementPresent(element)) {
			if (text == null || text.isEmpty())
				Log.error("the input text is null");
			else {
				try {
					element.clear();
					element.sendKeys(text);
				} catch (Exception e) {
					String js = "arguments[0].value='" + text + "';";
					((JavascriptExecutor) driver).executeScript(js, element);
				}
			}
		}
	}

	/**
	 * Input non-printable keys to a visible element. Ex: Keys.BACKS_PACE or
	 * Keys.ENTER
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 * @param k
	 *            the key to input
	 */
	public void inputKey(WebElement element, Keys k) {
		if (isElementPresent(element)) {
			element.sendKeys(k);
		}
	}

	/**
	 * @param element
	 *            The {@link WebElement} element to use.
	 */
	public void hitEnter(WebElement element) {
		if (isElementPresent(element)) {
			element.sendKeys(Keys.ENTER);
		}
	}

	/**
	 * clear the text of the element of input type
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 */
	public void clearText(WebElement element) {
		if (isElementPresent(element)) {
			element.clear();
		}
	}

	/**
	 * Return the text of the element
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 * @return The text to retrieve.
	 */
	public String getText(WebElement element) {
		String value = null;
		if (isElementPresent(element)) {
			value = element.getText();
		}
		return (value == null) ? "" : value;
	}

	/**
	 * Return the desired attribute of the element
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 * @param attribute
	 *            value of attribute to be returned
	 * @return The value for the attribute passed as arg2.
	 */
	public String getAttribute(WebElement element, String attribute) {
		String value = null;
		if (isElementPresent(element)) {
			value = element.getAttribute(attribute);
		}
		return (value == null) ? "" : value;
	}

	/**
	 * Return the desired CSS value of the element
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 * @param attribute
	 *            value of attribute to be returned
	 * @return The value for the css value for param passed as arg2
	 */
	public String getCSSValues(WebElement element, String attribute) {
		String value = null;
		if (isElementPresent(element)) {
			value = element.getCssValue(attribute);
		}
		return (value == null) ? "" : value;
	}

	/**
	 * Return the tag of the element
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 * @return The value for the tag
	 */
	public String getTagName(WebElement element) {
		String value = null;
		if (isElementPresent(element)) {
			value = element.getTagName();
		}
		return (value == null) ? "" : value;
	}

	/**
	 * Return the boolean value for the element to be enabled or not.
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 * @return boolean value for the element to be eanbled or not
	 */
	public Boolean isEnabled(WebElement element) {
		if (isElementPresent(element)) {
			return element.isEnabled();
		}
		return false;
	}

	/**
	 * Return the boolean value for the element to be displayed or not.
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 * @return boolean value for the element to be displayed or not
	 */
	public Boolean isDisplayed(WebElement element) {
		if (isElementPresent(element)) {
			return element.isDisplayed();
		}
		return false;
	}

	/**
	 * Return the boolean value for the element to be selected or not.
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 * @return boolean value for the element to be selected or not
	 */
	public Boolean isSelected(WebElement element) {
		if (isElementPresent(element)) {
			return element.isSelected();
		}
		return false;
	}

	/**
	 * Return the String value for the selected element in list of radio button
	 * or checkboxes.
	 * 
	 * @param listElements
	 *            The {@link WebElement} element to use. List of String values
	 *            for the selected elements in drop down
	 * @return
	 */
	public List<String> getSelectedValues(List<WebElement> listElements) {
		boolean selected;
		List<String> selectedValues = new ArrayList<>();
		if (listElements != null) {
			for (int i = 0; i < listElements.size(); i++) {
				selected = listElements.get(i).isSelected();
				if (selected) {
					selectedValues.add(listElements.get(i).getAttribute(checkBoxAttributeSelection));
				}
			}
		}
		return selectedValues;
	}

	/**
	 * Select a single radio button or checkboxes provided in the input String
	 * param.
	 * 
	 * @param listElements
	 * @param sValue
	 */
	public void selectBox(List<WebElement> listElements, String sValue) {
		if (listElements != null && sValue != null) {
			for (int i = 0; i < listElements.size(); i++) {
				String labelValue = listElements.get(i).getAttribute(checkBoxAttributeSelection);
				if (labelValue.equalsIgnoreCase(sValue)) {
					listElements.get(i).click();
					break;
				}
			}
		}
	}

	/**
	 * Select the radio button or checkboxes provided in the list param.
	 * 
	 * @param listElements
	 *            The {@link WebElement} element to use.
	 * @param sValues
	 */
	public void selectBoxes(List<WebElement> listElements, List<String> sValues) {
		if (listElements != null && sValues != null) {
			for (int i = 0; i < listElements.size(); i++) {
				String labelValue = listElements.get(i).getAttribute(checkBoxAttributeSelection);
				for (int j = 0; j < sValues.size(); j++) {
					if (labelValue.equalsIgnoreCase(sValues.get(j))) {
						listElements.get(i).click();
						break;
					}
				}
			}
		} else {
			Log.error("Unable to select values from down");
		}
	}

	/**
	 * Select the value from a drop down based on the visible text.
	 * 
	 * @param webElement
	 *            The {@link WebElement} element to use.
	 * @param text
	 *            The Visible Text to be selected
	 */
	public void selectByText(WebElement webElement, String text) {
		if (isElementPresent(webElement) && text != null) {
			Select oSelect = new Select(webElement);
			oSelect.selectByVisibleText(text);
		}
	}

	/**
	 * Method to return the text of first selected value from the drop down
	 * 
	 * @param webElement
	 *            WebElement to select the drop down
	 * @return value of the first option from drop down
	 */
	public String getSelectedText(WebElement webElement) {
		String value = null;
		if (isElementPresent(webElement)) {
			Select oSelect = new Select(webElement);
			value = oSelect.getFirstSelectedOption().getText();
		}
		return (value == null) ? "" : value;
	}

	/**
	 * Select the value from a drop down based on the visible text.
	 * 
	 * @param webElement
	 *            The {@link WebElement} element to use.
	 * @param index
	 *            The index to be selected
	 */
	public void selectByIndex(WebElement webElement, int index) {
		if (isElementPresent(webElement)) {
			Select oSelect = new Select(webElement);
			oSelect.selectByIndex(index);
		}
	}

	/**
	 * Select the value from a drop down based on the visible text.
	 * 
	 * @param webElement
	 *            The {@link WebElement} element to use.
	 * @param value
	 *            The Value to be selected
	 */
	public void selectByValue(WebElement webElement, String value) {
		if (isElementPresent(webElement)) {
			Select oSelect = new Select(webElement);
			oSelect.selectByValue(value);
		} else
			Log.error("The WebElement to select is not available");
	}

	/**
	 * Select the value from a drop down based on the visible text.
	 * 
	 * @param webElement
	 *            The {@link WebElement} element to use.
	 * @return List<WebElement> The list of selected web elements
	 */
	public List<WebElement> getDropDownList(WebElement webElement) {
		List<WebElement> dropDownList = new ArrayList<>();
		if (isElementPresent(webElement)) {
			Select oSelect = new Select(webElement);
			dropDownList = oSelect.getOptions();
		}
		return dropDownList;
	}

	/**
	 * Select the value from a drop down based on the visible text.
	 * 
	 * @param webElement
	 *            The {@link WebElement} element to use.
	 * @return List of values of selected webelements.
	 */
	public List<String> printDropDownList(WebElement webElement) {
		List<String> values = new ArrayList<>();
		List<WebElement> dropDownList = new ArrayList<>();
		if (isElementPresent(webElement)) {
			Select oSelect = new Select(webElement);
			dropDownList = oSelect.getOptions();
		}
		for (WebElement element : dropDownList) {
			values.add(element.getText());
		}
		return values;
	}

	/**
	 * De-Select the value from a multi-select box based on the visible text.
	 * 
	 * @param webElement
	 *            The {@link WebElement} element to use.
	 * @param text
	 *            The Visible Text to be selected
	 */

	public void deselectByText(WebElement webElement, String text) {
		if (isElementPresent(webElement) || text == null) {
			Select oSelect = new Select(webElement);
			oSelect.deselectByVisibleText(text);
		} else
			Log.info("the check box to de-select by text is not available");
	}

	/**
	 * De-Select the value from a multi-select box based on the index.not
	 * applicable for dropdowns
	 * 
	 * @param webElement
	 *            The {@link WebElement} element to use.
	 * @param index
	 *            The index to be selected
	 */
	public void deselectByIndex(WebElement webElement, int index) {
		if (isElementPresent(webElement)) {
			Select oSelect = new Select(webElement);
			oSelect.deselectByIndex(index);
		} else
			Log.error("the check box to de-select by index is not available");
	}

	/**
	 * De-Select the value from a multi-select box based on the value. not
	 * applicable for dropdowns
	 * 
	 * @param webElement
	 *            The {@link WebElement} element to use.
	 * @param value
	 *            The Value to be selected
	 */
	public void deselectByValue(WebElement webElement, String value) {
		if (isElementPresent(webElement) && value != null) {
			Select oSelect = new Select(webElement);
			oSelect.deselectByValue(value);
		}
	}

	/**
	 * De-Select the value from a multi-select box based on the value. Not
	 * applicable for dropdowns
	 * 
	 * @param webElement
	 *            The {@link WebElement} element to use.
	 */
	public void deselectAll(WebElement webElement) {
		if (isElementPresent(webElement)) {
			Select oSelect = new Select(webElement);
			oSelect.deselectAll();
		}
	}

	/**
	 * return the size of list of web elements
	 */
	public int getSize(List<WebElement> webElements) {
		int webElementListSize = 0;
		if (isElementAvailable(webElements.get(0))) {
			webElementListSize = webElements.size();
		}
		return webElementListSize;
	}

	/**
	 * Method to hover the mouse to the element passes as parameter
	 * 
	 * @param element
	 *            to which the mouse needs to be hovered
	 */
	public void hoverMouseOver(WebElement element) {
		changeVisibility(element);
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
	}

	/**
	 * Method to hover the mouse to the element and click it
	 * 
	 * @param element
	 *            to which the mouse needs to be hovered
	 */
	public void hoverMouseClick(WebElement element) {
		changeVisibility(element);
		Actions action = new Actions(driver);
		action.moveToElement(element).click().build().perform();
	}

	/**
	 * Input non-printable keys to a visible element. Ex: Keys.BACKS_PACE or
	 * Keys.ENTER
	 * 
	 * @param element
	 *            The {@link WebElement} element to use.
	 * @param k
	 *            the key to input
	 */
	public void hoverMouseAndInputKey(WebElement element, Keys k) {
		if (isElementPresent(element)) {
			Actions action = new Actions(driver);
			action.moveToElement(element).click().build().perform();
			action.sendKeys(k).build().perform();
		}
	}

	/**
	 * Method to accept the alert
	 * 
	 */
	public void acceptAlert() {
		try {
			if (wait.until(ExpectedConditions.alertIsPresent()) != null) {
				Log.info("Alert is present; accepting it");
				Alert alert = driver.switchTo().alert();
				alert.accept();
			}
		} catch (Exception e) {
			Log.warn("No alert " + e.getMessage());
		}
	}

	/**
	 * Method to dismiss the alert
	 * 
	 */
	public void dismissAlert() {
		try {
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alert.dismiss();
		} catch (Exception e) {
			Log.warn("No alert " + e.getMessage());
		}
	}

	/**
	 * Method to switch to frame element
	 * 
	 * @param element
	 */
	public void switchToFrame(WebElement element) {
		try {
			driver.switchTo().frame(element);
		} catch (Exception e) {
			Log.error("unable to switch to frame", e);
			throw new FeatureAutomationException("unable to switch to frame", e);
		}
	}

	/**
	 * Method to switch to default content
	 * 
	 */
	public void switchToParentWindow(int bowserWindowNo) {
		closeBrowser();
		switchToChildWindow(bowserWindowNo);
	}

	/**
	 * Method to switch to child window.
	 *
	 * @param bowserWindowNo
	 */
	public void switchToChildWindow(int bowserWindowNo) {
		List<String> browserWindows = new ArrayList<>(driver.getWindowHandles());
		driver.switchTo().window(browserWindows.get(bowserWindowNo - 1));
	}

	/**
	 * Wait for the web page to load by checking the page ready state. Sub
	 * classes can override this to verify that a page has completed loading
	 * based on other means, such as a loading icon is not present.
	 */
	public void waitForPageToLoad() {
		waitForPageToLoad(25);
	}

	/**
	 * Wait for the Page to load by looping x times and checking after every 1
	 * second if the page is ready.
	 * 
	 * @param numLoops
	 *            the number of times loop.
	 */
	public void waitForPageToLoad(int numLoops) {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// This loop will rotate for 25 times to check If page Is ready after
		// every 1 second.
		for (int i = 0; i < numLoops; i++) {
			try {
				Log.debug("Waiting for " + driver.getTitle() + " Page to Load");
				Thread.sleep(1000);
			} catch (Exception e) {
				Log.error("Waiting for page to load failed", e);
			}
			// To check page ready state.
			Boolean ajaxIsComplete = (Boolean) ((JavascriptExecutor) driver)
					.executeScript("return (window.jQuery != null) && jQuery.active == 0");
			Log.debug("AJAX STATE:" + ajaxIsComplete);
			if ("complete".equals(js.executeScript("return document.readyState").toString())) {
				break;
			}
		}
	}

	/**
	 * This method will return the current window handle.
	 * 
	 *  @author anushar
	 * 
	 */
	public String getCurrentWindowHandle() {
		String currentWin = null;
		try {
			currentWin = driver.getWindowHandle();
		} catch (Exception e) {
			Log.error("unable to switch windows ", e);
			throw new FeatureAutomationException("unable to switch windows ", e);
		}
		return currentWin;
	}

	/**
	 * This method will Upload the File using selenium send keys.
	 * 
	 *  @author anushar
	 * @param fileName
	 * @param web
	 *            element of input type=file
	 */
	public void uploadFileusingSelenium(WebElement element, String fileName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String filePath = classLoader.getResource(fileName).getPath().substring(1); 
		changeVisibility(element);
		element.sendKeys(filePath);
	}

	public void changeVisibility(WebElement element) {
		if (!element.isDisplayed()) {
			Log.info("making visible");
			String js = "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';";
			((JavascriptExecutor) driver).executeScript(js, element);
		}
	}

	public WebElement convertXpathToWebElement(String sValue) {
		String sElementValue = sValue.substring(52, sValue.length() - 1);
		return driver.findElement(By.xpath(sElementValue));
	}

	public List<WebElement> convertXpathToWebElementsList(String sValue) {
		String sElementValue = sValue.substring(52, sValue.length() - 1);
		return driver.findElements(By.xpath(sElementValue));
	}

	public WebElement convertCSSToWebElement(String sValue) {
		String sElementValue = sValue.substring(58, sValue.length() - 1);
		return driver.findElement(By.cssSelector(sElementValue));
	}

	public void scrollIntoViewByXpath(String xpathValue) {
		WebElement element = driver.findElement(By.xpath(xpathValue));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public void scrollIntoViewByCSS(String cssValue) {
		WebElement element = driver.findElement(By.cssSelector(cssValue));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public void scrollIntoViewByElement(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public void scrollToTop() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(document.body.scrollHeight,0);");
	}

	public void scrollToBottom() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
	}

	public void enterTextIntoFrame(WebElement element, String description) {
		try {
			driver.switchTo().frame(element);
			WebElement editable = driver.switchTo().activeElement();
			editable.sendKeys(description);
			driver.switchTo().defaultContent();
		} catch (Exception e) {
			Log.error("unable to enter data into frame", e);
			throw new FeatureAutomationException("unable to enter data into frame", e);
		}
	}

}