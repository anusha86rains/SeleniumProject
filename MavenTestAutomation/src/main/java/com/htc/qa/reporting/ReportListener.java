package com.htc.qa.reporting;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.htc.qa.config.Config;
import com.htc.qa.config.Config.ConfigProperty;
import com.htc.qa.config.Env;
import com.htc.qa.core.DateUtils;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

/**
 * A custom listener for Extent Reporting
 * 
 *  @author anushar
 * 
 */
public class ReportListener extends TestListenerAdapter {

	private static Map<String, ExtentTest> extentTestMap = new HashMap<>();
	private static Map<String, Object[]> paramsMap = new HashMap<>();
	private static final String OVERALLRESULT = "Overall Test Case Result: ";
	private StringBuilder testInfo;
	private boolean isScreenshotEnabled = Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.SCREENSHOT_ENABLED));

	/**
	 * default constructor
	 */
	public ReportListener() {
		super();
	}

	public static Map<String, ExtentTest> getExtentTestMap() {
		return extentTestMap;
	}

	@Override
	public void onStart(ITestContext testContext) {

	    //Set the test environment for all tests
        Log.info(String.format("The current test environment %s", Env.get("TESTENVIRONMENT")));

    }

	@Override
	public synchronized void onTestStart(ITestResult result) {
		ExtentTest extentTest;
		Long threadId = Thread.currentThread().getId();
		Log.info("Started Test: " + result.getMethod().getMethodName());
		extentTest = ExtentReporter.extent.createTest(result.getMethod().getMethodName());
		extentTest.getModel().setStartTime(DateUtils.getTime(result.getStartMillis()));
		extentTestMap.put(result.getMethod().getMethodName() + threadId, extentTest);
		paramsMap.put(result.getMethod().getMethodName() + threadId, result.getParameters());
		for (String group : result.getMethod().getGroups()) {
			extentTest.assignCategory(group);
		}
    }

	/**
	 * report for passed tests
	 */
	@Override
	public synchronized void onTestSuccess(ITestResult tr) {
		logReportStatus(Status.PASS, tr);
	}

	/**
	 * report for failed tests
	 */
	@Override
	public synchronized void onTestFailure(ITestResult tr) {
		logReportStatus(Status.FAIL, tr);
	}

	/**
	 * report for skipped tests
	 */
	@Override
	public void onTestSkipped(ITestResult tr) {
		Long threadId = Thread.currentThread().getId();
		if (!extentTestMap.containsKey(tr.getMethod().getMethodName() + threadId)) {
			ExtentTest extentTest = ExtentReporter.extent.createTest(tr.getMethod().getMethodName());
			extentTestMap.put(tr.getMethod().getMethodName() + threadId, extentTest);
		}
		logReportStatus(Status.SKIP, tr);
	}

	@Override
	public synchronized void onFinish(ITestContext testContext) {
		for (String s : Reporter.getOutput()) {
			ExtentReporter.extent.setTestRunnerOutput(s);
		}
	}

	private synchronized void setTestAttributes(ITestResult tr, ExtentTest test, Object[] params) {
		Set<String> attributes = tr.getAttributeNames();
		setTestDescription(tr, attributes);
		setTestName(params, test, attributes, tr);

		// print test description
		if (!StringUtils.isBlank(testInfo.toString())) {
			test.getModel().setDescription(testInfo.toString());
		}
	}

	private void setTestDescription(ITestResult tr, Set<String> attributes) {
		testInfo = new StringBuilder();			
		if (!attributes.isEmpty()) {
			for (String attribute : attributes) {
				if (tr.getAttribute(attribute) != null && !StringUtils.isBlank(tr.getAttribute(attribute).toString())) {
					testInfo.append(attribute + ": " + tr.getAttribute(attribute) + "<br>");
				}
			}
		}
	}

	private void setTestName(Object[] params, ExtentTest test, Set<String> attributes, ITestResult tr) {
		String[] paramsArray = null;
		String[] excludedParameters = { "URL", "BrowserType", "BrowserVersion", "Platform" };
		String testNameDescription = null;
		StringBuilder testName = new StringBuilder();

		if (params != null && params.length != 0) {
			paramsArray = params[0].toString().split(",");
		}

		// print custom test case name from csv file in the report.
		if (params != null && paramsArray != null) {
			if (params.length == 1 && paramsArray.length >= 3) {
				for (String parameter : paramsArray) {
					if (parameter.contains("url")) {
						testInfo.append("URL" + ": " + parameter.replaceAll("url=", "").replace("}", "") + "<br>");
					}
					if (parameter.contains("TestDescription")) {
						testNameDescription = parameter.replaceAll("TestDescription=", "").replace("{", "");
						test.getModel().setName(test.getModel().getName() + " - " + testNameDescription);
					}
				}
			} else if (params.length > 1 && !attributes.isEmpty()) {
				testName.append(test.getModel().getName()).append(" with parameters - ");
				for (String attribute : attributes) {
					if (!Arrays.asList(excludedParameters).contains(attribute)) {
						testName.append(attribute + ": " + tr.getAttribute(attribute) + ", ");
					}
				}
				if (testName.toString().contains(",")) {
					String name = testName.toString().substring(0, testName.toString().lastIndexOf(','));
					test.getModel().setName(name);
				}
			}
		}
	}

	/**
	 * Method to print/log the status in the extent report.
	 * 
	 * @param status
	 * @param tr
	 * @throws IOException
	 */
	private void logReportStatus(Status status, ITestResult tr) {
		Long threadId = Thread.currentThread().getId();
		Object[] params = paramsMap.get(tr.getMethod().getMethodName() + threadId);
		ExtentTest test = extentTestMap.get(tr.getMethod().getMethodName() + threadId);

		if (test == null) {
			Log.error("Test report instance is null; Test result will not be printed in the report.");
			return;
		}

		try {
			test.getModel().setEndTime(DateUtils.getTime(tr.getEndMillis()));
			setTestAttributes(tr, test, params);
			switch (status) {
			case SKIP:
				test.skip(tr.getThrowable());
				break;
			case FAIL:
				if (params != null && params.length == 1) {
					testInfo.append("parameters: " + Arrays.toString(params) + "<br>");
				}
				if (isScreenshotEnabled) {
					test.fail(tr.getThrowable().getMessage(), MediaEntityBuilder.createScreenCaptureFromPath(takeScreenshot(test, tr)).build());
				} else {
					test.fail(tr.getThrowable().getMessage());
				}
				break;
			case PASS:
				if (isScreenshotEnabled) {
					test.pass(OVERALLRESULT + tr.getMethod().getMethodName() + " - Passed",
							MediaEntityBuilder.createScreenCaptureFromPath(takeScreenshot(test, tr)).build());
				} else {
					test.pass(OVERALLRESULT + tr.getMethod().getMethodName() + " - Passed");
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.error("unable to print status in the report", e);
		}

		if (extentTestMap.containsKey(tr.getMethod().getMethodName() + threadId)) {
			extentTestMap.remove(tr.getMethod().getMethodName() + threadId);
		}
	}

	public String takeScreenshot(ExtentTest test, ITestResult tr) {
		String destination = null;
		try {
			Screenshot screenshot = new AShot().takeScreenshot((WebDriver) tr.getAttribute("driver"));
			String fileName = test.getModel().getName() + "_" + DateUtils.getCustomTimeStamp() + ".png";

			File diffDirectory = new File(String.format("./%s/",Config.getConfigProperty(ConfigProperty.REPORT_FOLDER)));
			if (!diffDirectory.exists()) {
				diffDirectory.mkdirs();
			}
			destination = String.format("./%s/%s",Config.getConfigProperty(ConfigProperty.REPORT_FOLDER),fileName);
			ImageIO.write(screenshot.getImage(), "PNG", new File(destination));
			destination=fileName;
			JavascriptExecutor js = (JavascriptExecutor)(WebDriver) tr.getAttribute("driver");
			js.executeScript("window.scrollTo(0,0);");
		} catch (Exception e) {
			Log.warn("Unable to capture screenshot ", e);
		}
		return destination;
	}

}
