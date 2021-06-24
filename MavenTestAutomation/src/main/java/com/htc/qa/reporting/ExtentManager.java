package com.htc.qa.reporting;

import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTestInterruptedException;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentXReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.htc.qa.config.Config;
import com.htc.qa.config.Config.ConfigProperty;

public class ExtentManager {

	private static ExtentReports extent;
	private static final String ERROR_MSG = "Unable to initialize reporting tools";

	private ExtentManager() {
		// no-op
	}

	public static ExtentReports getInstance() {
		if (extent == null) {
			File diffDirectory = new File(String.format("./%s/",Config.getConfigProperty(ConfigProperty.REPORT_FOLDER)));
			if (!diffDirectory.exists()) {
				diffDirectory.mkdirs();
			}

			String fileName = String.format("./%s/%s.html",Config.getConfigProperty(ConfigProperty.REPORT_FOLDER),Config.getConfigProperty(ConfigProperty.REPORT_NAME));
			createInstance(fileName);
		}
		return extent;
	}

	public static void createInstance(String fileName) {	
		
		try {
			ExtentXReporter extentxReporter ;
			ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(fileName);
			extent = new ExtentReports();
			htmlReporter.config().setTestViewChartLocation(ChartLocation.BOTTOM);
			htmlReporter.config().setChartVisibilityOnOpen(false);
			htmlReporter.config().setTheme(Theme.STANDARD);
			htmlReporter.config().setDocumentTitle(Config.getConfigProperty(ConfigProperty.REPORT_NAME));
			htmlReporter.config().setEncoding("utf-8");
			htmlReporter.config().setReportName(Config.getConfigProperty(ConfigProperty.REPORT_NAME));
			extent.attachReporter(htmlReporter);
		} catch (Exception e) {
			Log.error(ERROR_MSG, e);
			throw new ExtentTestInterruptedException(ERROR_MSG, e);
		}
	}

}