/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */

package com.example.jonat.scheduleapp2;

import android.os.StrictMode;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/*
 * CalendarChecker is the class specifically for getting and updating
 * calendar information. This is especially useful for Administration
 * to update days quickly and does not require a full application
 * update to receive current information.
 */

public class CalendarChecker {
	private static File calendar;
	private static final String SNOW_DAY = "SSSSS";
	private static final String EXAM_DAY = "EEEEE";
	private static final String HALF_DAY = "HHHH";

	private static void setCalendarFile(File calendarFile) {
		calendar = calendarFile;
	}

	static void updateCalendar(File calendar) {
		setCalendarFile(calendar);
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			FileUtils.copyURLToFile(new URL("https://rawgit.com/jkim99/ConradIsCool/master/calendar"), calendar);
		}
		catch(IOException ioe) {
			Log.e("calendar", ioe.toString());
		}
	}

	static int getDayRotation(int month, int day) {
		String searchDate = month + "/" + day;
		String returnValue;
		try {
			Scanner scan = new Scanner(calendar);
			String line = "";
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				if(line.contains(searchDate))
					break;
			}
			returnValue = line.substring(line.indexOf(" = ") + 3);
			if(returnValue.contains(HALF_DAY))
				return -20 - Integer.valueOf(line.substring(line.indexOf(" = ") + 7));
			switch(returnValue) {
				case "XXXXX":
					return Utility.NO_SCHOOL;
				case EXAM_DAY:
					return Utility.EXAM;
				case SNOW_DAY:
					return Utility.SNOW_DAY;
				case "A":
				case "B":
				case "C":
					return Utility.DIFF_DAY;
				default:
					return Integer.valueOf(returnValue) >= 1 ? Integer.valueOf(returnValue) : Utility.ERROR_CODE;
			}
		}
		catch(IOException ioe) {
			return Utility.ERROR_CODE;
		}
		catch(NullPointerException npe) {
			return Utility.ERROR_CODE;
		}
	}
}
