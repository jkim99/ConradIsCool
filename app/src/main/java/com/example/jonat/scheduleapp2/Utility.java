package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class Utility {
	public static int PERIOD_1_BELL = 464;
	public static int PERIOD_2_BELL = 524;
	public static int PERIOD_3_BELL = 603;
	public static int PERIOD_4_BELL = 667;
	public static int PERIOD_5_BELL = 781;

	public static boolean verifyScheduleFile(File f) {
		try {
			Scanner scan = new Scanner(f);
			return scan.nextLine().equals("--Schedule--");
		}
		catch(Exception e) {
			Log.e("debugging", "verification failed");
			Log.e("debugging", e.toString());
			return false;
		}
	}

	public static void changeDayIcon(ScheduleChecker scheduleChecker, Menu menu) {
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

	public static void changePeriodIcon(ScheduleChecker scheduleChecker, Menu menu) {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		int period = scheduleChecker.getCurrentPeriod(Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5)), 0);
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

	public static ScheduleChecker initializeScheduleChecker(Context context) {
		File scheduleFile = new File((new ContextWrapper(context)).getFilesDir() + "/schedule.txt");
		ArrayList<String> classes = new ArrayList<String>();
		if(Utility.verifyScheduleFile(scheduleFile)) {
			try {
				Scanner scan = new Scanner(scheduleFile);
				scan.nextLine();
				while(scan.hasNextLine()) {
					classes.add(
						scan.nextLine() + "\n" +
						scan.nextLine() + "\n" +
						scan.nextLine() + "\n" +
						scan.nextLine() + "\n"
					);
					scan.nextLine();
				}
				return new ScheduleChecker(context, classes);
			}
			catch(IOException ioe) {}
		}
		//should never get to this point unless there is an error
		Log.e("schedule_checker", "schedule validation failed");
		return null;
	}

	public static void initializeCalendar(Context context) {
		SchoolCalendar.calendar = new File((new ContextWrapper(context)).getFilesDir() + "/calendar.txt");
	}

	public static int getSchoolDayRotation(int off) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, off);
		String date = new SimpleDateFormat("MM dd").format(c.getTime());
		return SchoolCalendar.getDayRotation(Integer.valueOf(date.substring(0, 2)), Integer.valueOf(date.substring(3, 5)));
	}
}
