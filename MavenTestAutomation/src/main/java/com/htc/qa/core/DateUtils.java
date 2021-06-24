package com.htc.qa.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This Class provides the utilities for handling dates related operations
 * 
 *  @author anushar
 */
public class DateUtils {

	private DateUtils() {
		// no -op
	}

	/**
	 * Method to getTime in Date format
	 * 
	 * @param millis
	 * @return
	 */
	public static Date getTime(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}

	public static String getCustomTimeStamp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String getDayOfWeek() {
		String dayOfWeek = "day";
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_WEEK);

		switch (day) {
		case Calendar.SUNDAY:
			dayOfWeek = "Sunday";
			break;
		case Calendar.MONDAY:
			dayOfWeek = "Monday";
			break;
		case Calendar.TUESDAY:
			dayOfWeek = "Tuesday";
			break;
		case Calendar.WEDNESDAY:
			dayOfWeek = "Wednesday";
			break;
		case Calendar.THURSDAY:
			dayOfWeek = "Thursday";
			break;
		case Calendar.FRIDAY:
			dayOfWeek = "Friday";
			break;
		case Calendar.SATURDAY:
			dayOfWeek = "Saturday";
			break;
		default:
			break;
		}
		return dayOfWeek;
	}
}
