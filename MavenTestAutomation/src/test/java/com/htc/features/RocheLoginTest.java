package com.htc.features;

import java.util.Map;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.htc.qa.reporting.Log;
import com.htc.qa.ui.BaseTestUi;

public class RocheLoginTest extends BaseTestUi {

	private RocheLoginPageFeatures loginPageFeatures;

	@Parameters({ "fileName", "startRow", "endRow", "bType", "bVersion", "platform", "url", "dimension" })
	public RocheLoginTest(@Optional String fileName, @Optional String startRow, @Optional String endRow,
			@Optional String bType, @Optional String bVersion, @Optional String platform, @Optional String url,
			@Optional String dimension) {
		super(fileName, startRow, endRow, bType, bVersion, platform, url, dimension);

	}

	/**
	 * 
	 * @param inputDataMap
	 * @throws Exception
	 */

	@Test(dataProvider = "csv", groups = { "AfterLogin", "Regressions" }, priority = 1)
	public void Login(Map<String, String> inputDataMap) throws Exception {
		Log.info("Validate Ecommerce Site After Login");
		loginPageFeatures = new RocheLoginPageFeatures(driver, inputDataMap);
		loginPageFeatures.clickSingaporeGateWay();		
		loginPageFeatures.clickConnectButton();
	}

}
