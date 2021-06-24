package com.htc.qa.core;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.htc.qa.reporting.Log;

/**
 * This Class provides the utilities for TESTNG to create data provider and
 * other features
 * 
 *  @author anushar
 */
public class CollectionUtils {

	/**
	 * The key in the maps for which the data will be compared If required We
	 * can change this variable from the calling functions to set any key.
	 * 
	 */
	private static String comparisonKey = "requestId";

	private CollectionUtils() {
		// no-op
	}

	public static void setComparisonKey(String compareKey) {
		CollectionUtils.comparisonKey = compareKey;
	}

	/**
	 * check if the parameterized map is not null or empty
	 * 
	 * @param map
	 * @return
	 */
	public static Boolean isNotEmpty(Map<?, ?> map) {
		if (map != null && !map.isEmpty()) {
			return true;
		} else {
			Log.debug("Map is null or empty");
		}
		return false;
	}

	/**
	 * check if the parameterized map is not null or empty
	 * 
	 * @param map
	 * @param mapName
	 * @return
	 */
	public static Boolean isNotEmpty(Map<?, ?> map, String mapName) {
		if (map != null && !map.isEmpty()) {
			return true;
		} else {
			Log.debug(mapName + "Map is null or empty");
		}
		return false;
	}

	/**
	 * @param sourceMap
	 *            actual result map
	 * @param destMap
	 *            equals to expected map
	 * 
	 *            Both the maps should be sorted in same manner for comparison
	 * @return
	 */
	public static Boolean compareTwoMaps(Map<String, Object> sourceMap, Map<String, Object> destMap) {
		StringBuilder builder = new StringBuilder();
		if (isNotEmpty(sourceMap) && isNotEmpty(destMap)) {
			if (sourceMap.equals(destMap)) {
				builder.append("Data matches for: " + comparisonKey + " - " + destMap.get(comparisonKey));
				return true;
			} else {
				builder.append("Data mismatches for: " + comparisonKey + " - " + destMap.get(comparisonKey));
				// compare both the maps field by field and find what fields mismatches
				for (Entry<String, Object> entry : destMap.entrySet()) {
					if (sourceMap.containsKey(entry.getKey())) {
						// check if values are same
						if (entry.getValue() != sourceMap.get(entry.getKey())) {
							builder.append("The value mismatches for field: " + entry.getKey() + "Expected"
									+ entry.getValue() + "Actual" + sourceMap.get(entry.getKey()));
						}
					} else {
						// keys not found in actual result
						builder.append("The fields are missing in actual result: " + entry.getKey() + "Value"
								+ entry.getValue());
					}
				}
			}
		} else {
			Log.error("The maps to compare are not initialized correctly");
		}
		return false;
	}
	
	public static String[] convertStringToArray(String inputString) {
		String[] outputList = null;
		if (!StringUtils.isBlank(inputString)) {
			outputList = inputString.split("\\|");
		}
		return outputList;
	}
}
