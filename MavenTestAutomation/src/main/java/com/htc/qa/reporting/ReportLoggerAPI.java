package com.htc.qa.reporting;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;
import org.testng.collections.Maps;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.htc.qa.config.Config;
import com.htc.qa.config.Config.ConfigProperty;
import com.htc.qa.core.DateUtils;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

/**
 * Soft Assert which allows the test to continue even in event of failure
 * 
 *  @author anushar
 */
public class ReportLoggerAPI extends Assertion {

	private Map<AssertionError, IAssert<?>> failedAsserts = Maps.newHashMap();
	private ExtentTest test;
	private WebDriver driver;
	private static final String ERROR_REPORT_LOG = "Unable to print assertion status in the report";
	private boolean takeScreenshot;
	private ITestResult testResult;
	private static final String ACTUAL = " Actual: ";

	/**
	 * @param driver
	 * @param testResult
	 *            is passed to capture method name
	 * @param takeScreenshot
	 */
	public ReportLoggerAPI(ITestResult testResult) {
		this.testResult = testResult;
	}

	public ReportLoggerAPI() {
		// no-op
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public boolean isTakeScreenshot() {
		return takeScreenshot;
	}

	public void setTakeScreenshot(boolean takeScreenshot) {
		this.takeScreenshot = takeScreenshot;
	}

	@Override
	public void executeAssert(IAssert<?> assertCommand) {
		// Set Test Instance
		setExtentTest();
		try {
			assertCommand.doAssert();
			onAssertSuccess(assertCommand);
		} catch (AssertionError ex) {
			onAssertFailure(assertCommand, ex);
			failedAsserts.put(ex, assertCommand);
		}
	}

	@Override
	protected void doAssert(IAssert<?> assertCommand) {
		executeAssert(assertCommand);
	}

	@Override
	public void onAssertSuccess(IAssert<?> assertCommand) {
		printAssertionLog(assertCommand, null, false);
		try {
			if (takeScreenshot && "true".equalsIgnoreCase(Config.getConfigProperty(ConfigProperty.SCREENSHOT_ON_SUCCESS))) {
				test.pass(assertCommand.getMessage() + ACTUAL + assertCommand.getActual(),
						MediaEntityBuilder.createScreenCaptureFromPath(takeScreenshot(test)).build());
			} else {
				test.pass(assertCommand.getMessage() + ACTUAL + assertCommand.getActual());
			}
		} catch (IOException e) {
			Log.error(ERROR_REPORT_LOG, e);
		}
	}

	/**
	 * Method to print in the report at times of failure.
	 * 
	 * @param assertCommand
	 * @param ex
	 */
	@Override
	public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
		printAssertionLog(assertCommand, ex, true);
		try {
			if (takeScreenshot) {
				test.fail(assertCommand.getMessage() + ACTUAL + assertCommand.getActual() + " Expected: " + assertCommand.getExpected(),
						MediaEntityBuilder.createScreenCaptureFromPath(takeScreenshot(test)).build());
			} else {
				test.fail(assertCommand.getMessage() + ACTUAL + assertCommand.getActual() + " Expected: " + assertCommand.getExpected());
			}
		} catch (IOException e) {
			Log.error(ERROR_REPORT_LOG, e);
		}
	}

	/**
	 * Method to print the info messages in the report
	 * 
	 * @param message
	 */
	public void info(String description) {
		Log.info(description);
		setExtentTest();
		test.info(description);
	}

	public void infoLink(String LinkText, String LinkURL){
	    ExtentLink lnkL = new ExtentLink();
	    lnkL.setLinkText(LinkText);
	    lnkL.setLinkUrl(LinkURL);

        Log.info(String.format("%s-%s",LinkText,LinkURL));
        setExtentTest();
        test.log(Status.INFO,lnkL);
    }

	/**
	 * Method to print the pass messages in the report
	 * 
	 * @param message
	 */
	public void pass(String description) {
		setExtentTest();
		test.pass(description);
	}
	
	/**
	 * Method to print the fail messages in the report
	 * 
	 * @param message
	 */
	public void fail(String description) {
		setExtentTest();
		test.fail(description);
	}

	/**
	 * Method to print the error messages in the report
	 * 
	 * @param message
	 */
	public void error(String description) {
		setExtentTest();
		test.error(description);
	}

	/**
	 * Method to print the warn messages in the report
	 * 
	 * @param String
	 *            message
	 */
	public void warn(String description) {
		setExtentTest();
		test.warning(description);
	}

	/**
	 * Method to print the warn messages in the report
	 * 
	 * @param String
	 *            message
	 */
	public void logErrorWithScreenshot(String description, String screenshotPath) {
		setExtentTest();
		try {
			test.fail(description, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
		} catch (IOException e) {
			Log.error(ERROR_REPORT_LOG, e);
		}
	}

	private void setExtentTest() {
		test = ReportListener.getExtentTestMap()
				.get(testResult.getMethod().getMethodName() + Thread.currentThread().getId());
	}

	/**
	 * assert all the individuals asserts
	 * 
	 */
	public void assertAll() {
		StringBuilder sb = new StringBuilder();
		if (failedAsserts.isEmpty()) {
			return;
		}
		ITestResult result = Reporter.getCurrentTestResult();
		String msg = String.format("Following soft asserts failed in %s.%s(): ", result.getTestClass().getName(),
				result.getMethod().getMethodName());
		sb.append(msg);
		for (Entry<AssertionError, IAssert<?>> eachEntry : failedAsserts.entrySet()) {
			IAssert<?> eachAssert = eachEntry.getValue();
			if (!eachAssert.getMessage().trim().isEmpty()) {
				sb.append(" \"").append(eachAssert.getMessage()).append("\" ");
			}
			sb.append("failed because the expected value of [").append(eachAssert.getExpected()).append("] ")
					.append("was different from the actual value [").append(eachAssert.getActual()).append("]");
		}
		Reporter.log(sb.toString());
		throw new AssertionError(sb.toString());
	}

	private void printAssertionLog(IAssert<?> assertCommand, AssertionError ex, boolean isfailed) {
		String methodName = Reporter.getCurrentTestResult().getMethod().getMethodName();
		StringBuilder sb = new StringBuilder();
		sb.append("Assert ");
		if (assertCommand.getMessage() != null && !assertCommand.getMessage().trim().isEmpty()) {
			sb.append("\"").append(assertCommand.getMessage()).append("\"");
		}
		sb = isfailed ? sb.append(" failed in ") : sb.append(" passed in ");
		sb.append(methodName).append("()");
		if (isfailed) {
			Reporter.getCurrentTestResult().setThrowable(ex);
		}
		Log.info(sb.toString());
	}

	private String takeScreenshot(ExtentTest test) {
		String destination = null;
		try {
			Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
			String fileName = test.getModel().getName() + "_" + DateUtils.getCustomTimeStamp() + ".png";

			File diffDirectory = new File(String.format("./%s/",Config.getConfigProperty(ConfigProperty.REPORT_FOLDER)));
			if (!diffDirectory.exists()) {
				diffDirectory.mkdirs();
			}
			destination = String.format("./%s/%s",Config.getConfigProperty(ConfigProperty.REPORT_FOLDER),fileName);
			ImageIO.write(screenshot.getImage(), "PNG", new File(destination));
			destination=fileName;
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0,0);");
		} catch (Exception e) {
			Log.warn("Unable to capture screenshot ", e);
		}
		return destination;
	}

    class ExtentLink implements Markup {
        private String linkUrl;
        public String getLinkUrl() {
            return this.linkUrl;
        }
        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }
        public String getLinkText() {
            return this.linkText;
        }
        public void setLinkText(String linkText) {
            this.linkText = linkText;
        }
        private String linkText;
        @Override
        public String getMarkup() {
            final String htmlTag = String.format("<a target=\"_blank\" href=\"%s\">%s</a>",linkUrl,linkText) ;
            return htmlTag;
        }
        @Override
        public String toString() {
            return this.linkText;
        }

    }
}
