/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */
package com.example.jonat.scheduleapp2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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

	static final int HALF_DAY_PERIOD_1_BELL = 464; //  7:44
	static final int HALF_DAY_PERIOD_2_BELL = 518; //  8:38
	static final int HALF_DAY_PERIOD_3_BELL = 546; //  9:06
	static final int HALF_DAY_PERIOD_4_BELL = 600; // 10:00

	static final int DELAY_PERIOD_1_BELL_60 = 524; //  8:44
	static final int DELAY_PERIOD_2_BELL_60 = 570; //  9:30
	static final int DELAY_PERIOD_3_BELL_60 = 616; // 10:16
	static final int DELAY_PERIOD_4_BELL_60 = 662; // 11:02
	static final int DELAY_PERIOD_5_BELL_60 = 795; //  1:15

	static final int DELAY_PERIOD_1_BELL_90 = 554; //  9:14
	static final int DELAY_PERIOD_2_BELL_90 = 594; //  9:54
	static final int DELAY_PERIOD_3_BELL_90 = 634; // 10:34
	static final int DELAY_PERIOD_4_BELL_90 = 674; // 11:14
	static final int DELAY_PERIOD_5_BELL_90 = 804; //  1:27

	static final int DELAY_PERIOD_1_BELL_120 = 0; //  9:44
	static final int DELAY_PERIOD_2_BELL_120 = 0; // 10:19
	static final int DELAY_PERIOD_3_BELL_120 = 0; // 10:54
	static final int DELAY_PERIOD_4_BELL_120 = 0; // 11:29
	static final int DELAY_PERIOD_5_BELL_120 = 0; //  1:42

	static final int NO_SCHOOL = -1;
	static final int EXAM_DAY = -2;
	static final int SNOW_DAY = -3;
	static final int DIFF_DAY = -4;
	static final int ERROR_CODE = -10;
	static final String SCHEDULE_FILE_VERIFICATION_TAG = "-X-Schedule-X-";
	static final String SETTINGS_FILE_VERIFICATION_TAG = "-X-Settings-X-";

	static boolean verifyScheduleFile(File f) {
		try {
			Scanner scan = new Scanner(f);
			return scan.nextLine().equals(SCHEDULE_FILE_VERIFICATION_TAG);
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
		int returnDay = CalendarChecker.getDayRotation(Integer.valueOf(date.substring(0, 2)), Integer.valueOf(date.substring(3, 5)));
		return returnDay > -100 ? returnDay : returnDay + 100;
	}

	/** @param block determines which background source to use
	 *  @return resource drawable file associated with <param>block</param> */
	static int backgroundFix(int block) {
		char x;
		try {
			x = MainActivity.scheduleChecker.getBlock(Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset) - 1, block);
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			return R.drawable.x;
		}
		switch(x) {
			case 'A':
				return R.drawable.a;
			case 'B':
				return R.drawable.b;
			case 'C':
				return R.drawable.c;
			case 'D':
				return R.drawable.d;
			case 'E':
				return R.drawable.e;
			case 'F':
				return R.drawable.f;
			case 'G':
				return R.drawable.g;
			case 'H':
				return R.drawable.h;
		}
		return R.drawable.x3;
	}

	static String[] oneLineClassNames(int off) {
		String[] classNames = new String[5];
		for(int i = 0; i < 5; i++) {
			try {
				String block =  MainActivity.scheduleChecker.getBlock(getSchoolDayRotation(off) - 1, i) + ": ";
				classNames[i] = block + MainActivity.scheduleChecker.getClassName(off, i);
			}
			catch(ArrayIndexOutOfBoundsException aioobe) {
				classNames[i] = MainActivity.scheduleChecker.getClassName(off, i);
			}
		}
		return classNames;
	}

	static void purge(File[] files) {
		try {
			for(File f : files) {
				PrintWriter pw = new PrintWriter(f);
				pw.print("");
				pw.close();
				Log.i("purge", f.getAbsolutePath() + " deleted.");
			}
		}
		catch(Exception e) {
			Log.e("purge", e.toString());
		}
	}

	static String getLunch(int off, int period) {
		try {
			return MainActivity.scheduleChecker.getLunchBlock(off, period);
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			return "";
		}
	}

	static int getCurrentMinutes() {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		return Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5));
	}

	static String viewToString(int x) {
		switch(x) {
			case R.layout.current_view:
				return "current_view";
			case R.layout.day_view:
				return "day_view";
			case R.layout.month_view:
				return "month_view";
			default:
				return "current_view";
		}
	}

	static Intent stringToIntent(Activity activity, String str) {
		try {
			switch(str) {
				case "day_view":
					return new Intent(activity, DayViewActivity.class);
				case "current_view":
					return new Intent(activity, CurrentViewActivity.class);
				case "month_view":
					return new Intent(activity, MonthViewActivity.class);
				default:
					return new Intent(activity, MainActivity.class);
			}
		}
		catch(Exception e) {
			return new Intent(activity, MainActivity.class);
		}
	}

	static int[] getTimeStamps(int off) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, off);
		String date = new SimpleDateFormat("MM dd").format(c.getTime());
		int day = CalendarChecker.getDayRotation(Integer.valueOf(date.substring(0, 2)), Integer.valueOf(date.substring(3, 5)));

		if(day <= -20 && day >= -30) {
			int[] timeStamps = {HALF_DAY_PERIOD_1_BELL, HALF_DAY_PERIOD_2_BELL, HALF_DAY_PERIOD_3_BELL, HALF_DAY_PERIOD_4_BELL};
			return timeStamps;
		}
		else if(day <= -100 && day > -200) {
			int[] timeStamps = {DELAY_PERIOD_1_BELL_60, DELAY_PERIOD_2_BELL_60, DELAY_PERIOD_3_BELL_60, DELAY_PERIOD_4_BELL_60, DELAY_PERIOD_5_BELL_60};
			return timeStamps;
		}
		else if(day <= -100 && day > -200) {
			int[] timeStamps = {DELAY_PERIOD_1_BELL_90, DELAY_PERIOD_2_BELL_90, DELAY_PERIOD_3_BELL_90, DELAY_PERIOD_4_BELL_90, DELAY_PERIOD_5_BELL_90};
			return timeStamps;
		}
		else if(day <= -100 && day > -200) {
			int[] timeStamps = {DELAY_PERIOD_1_BELL_120, DELAY_PERIOD_2_BELL_120, DELAY_PERIOD_3_BELL_120, DELAY_PERIOD_4_BELL_120, DELAY_PERIOD_5_BELL_120};
			return timeStamps;
		}
		else {
			int[] timeStamps = {PERIOD_1_BELL, PERIOD_2_BELL, PERIOD_3_BELL, PERIOD_4_BELL, PERIOD_5_BELL};
			return timeStamps;
		}
	}

	static String timeStampToString(int time) {
		int hour = time / 60;
		String stringHour = (hour <= 12 ? hour : hour - 12) + "";
		int minutes = time % 60;
		String stringMinutes = (minutes >= 10 ? minutes : "0" + minutes) + "";
		return stringHour + ":" + stringMinutes;
	}

}
