/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */
package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
 * Utility Class is the class that holds all static methods needed by
 * multiple classes and objects.
 */
public class Utility {
	static final int SEVEN_O_CLOCK = 420; //  7:00
	static final int PERIOD_1_BELL = 464; //  7:44
	static final int PERIOD_2_BELL = 524; //  8:44
	static final int PERIOD_3_BELL = 603; // 10:03
	static final int PERIOD_4_BELL = 667; // 11:07
	static final int PERIOD_5_BELL = 784; //  1:04
	static final int NO_SCHOOL = -1;
	static final int EXAM = -2;
	static final int SNOW_DAY = -3;
	static final int DIFF_DAY = -4;
	static final int ERROR_CODE = -10;
	static final double LAV = 1.5;

	static boolean verifyScheduleFile(File f) {
		try {
			Scanner scan = new Scanner(f);
			return scan.nextLine().equals("--Schedule--");
		}
		catch(IOException ioe) {
			return false;
		}
		catch(NoSuchElementException nsee) {
			return false;
		}
		catch(Exception e) {
			Log.e("file_verification", "verification failed");
			return false;
		}
	}

	static void changeDayIcon(Menu menu) {
		int day = Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset);
		MenuItem icon = menu.findItem(R.id.navigation_day_view);
		switch(day) {
			case 1:
				icon.setIcon(R.drawable.ic_filter_1_black_24dp);
				break;
			case 2:
				icon.setIcon(R.drawable.ic_filter_2_black_24dp);
				break;
			case 3:
				icon.setIcon(R.drawable.ic_filter_3_black_24dp);
				break;
			case 4:
				icon.setIcon(R.drawable.ic_filter_4_black_24dp);
				break;
			case 5:
				icon.setIcon(R.drawable.ic_filter_5_black_24dp);
				break;
			case 6:
				icon.setIcon(R.drawable.ic_filter_6_black_24dp);
				break;
			case 7:
				icon.setIcon(R.drawable.ic_filter_7_black_24dp);
				break;
			case 8:
				icon.setIcon(R.drawable.ic_filter_8_black_24dp);
				break;
			default:
				icon.setIcon(R.drawable.ic_filter_none_black_24dp);
				break;
		}
	}

	static void changePeriodIcon(Menu menu) {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		int period = MainActivity.scheduleChecker.getCurrentPeriod(Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5)), 0);
		MenuItem icon = menu.findItem(R.id.navigation_current_view);
		switch(period) {
			case 0:
				icon.setIcon(R.drawable.ic_looks_one_black_24dp);
				break;
			case 1:
				icon.setIcon(R.drawable.ic_looks_two_black_24dp);
				break;
			case 2:
				icon.setIcon(R.drawable.ic_looks_3_black_24dp);
				break;
			case 3:
				icon.setIcon(R.drawable.ic_looks_4_black_24dp);
				break;
			case 4:
				icon.setIcon(R.drawable.ic_looks_5_black_24dp);
				break;
			default:
				icon.setIcon(R.drawable.ic_looks_none_black_24dp);
				break;
		}
	}

	static int getSchoolDayRotation(int off) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, off);
		String date = new SimpleDateFormat("MM dd").format(c.getTime());
		return CalendarChecker.getDayRotation(Integer.valueOf(date.substring(0, 2)), Integer.valueOf(date.substring(3, 5)));
	}

	/** @param context simply to initialize the scheduleChecker
	 *  @param block determines which background source to use
	 *  @return resource drawable file associated with <param>block</param> */
	static int backgroundFix(Context context, int block) {
//		ScheduleChecker scheduleChecker = initializeScheduleChecker(context);
//		char x;
//		try {
//			x = scheduleChecker.getBlock(Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset) - 1, block);
//		}
//		catch(ArrayIndexOutOfBoundsException aioobe) {
//			return R.drawable.x;
//		}
//		switch(x) {
//			case 'A':
//				return R.drawable.a;
//			case 'B':
//				return R.drawable.b;
//			case 'C':
//				return R.drawable.c;
//			case 'D':
//				return R.drawable.d;
//			case 'E':
//				return R.drawable.e;
//			case 'F':
//				return R.drawable.f;
//			case 'G':
//				return R.drawable.g;
//			case 'H':
//				return R.drawable.h;
//		}
		return R.drawable.x;
	}

	static String[] oneLineClassNames() {
		String[][] schedule = new String[5][4];
		String[] returnArray = new String[5];
		for(int x = 0; x < 5; x++)
			schedule[x] = MainActivity.scheduleChecker.getClass(MainActivity.swipeDirectionOffset, x).split("\n");
		for(int i = 0; i < 5; i++) {
			try {
				returnArray[i] = MainActivity.scheduleChecker.getBlock(Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset) - 1, i) + ": " + schedule[i][0];
			}
			catch(ArrayIndexOutOfBoundsException aioobe) {
				returnArray[i] = schedule[i][0];
			}
		}
		return returnArray;
	}

	static boolean validateHTML(String html) {
		try {
			String[] lines = html.split("\n");
			boolean check = lines.length > 1000;
			Log.d("HTML_verification", "" + check);
			return check;
		}
		catch(Exception e) {
			Log.e("HTML_verification", "Invalid html. Possibly due to user internet failure");
			return false;
		}
	}

	static void purge(File scheduleFile, File settingsFile) {
		try {
			PrintWriter pw = new PrintWriter(scheduleFile);
			pw.print("");
			pw.close();
			Log.i("purge", "Schedule File deleted");
		}
		catch(Exception e) {
			Log.e("purge", e.toString());
		}
	}

	static String getLunch(int off) {
		String course = MainActivity.scheduleChecker.getClass(off, 3);
		try {
			return "Lunch: " + LunchChecker.getLunchBlock(course.split("\n")[1]);
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			return "";
		}
	}

	static int getCurrentMinutes() {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		return Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5));
	}

}
