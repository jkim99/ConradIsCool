package com.example.jonat.scheduleapp2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Notify extends AppCompatActivity {
	private ScheduleChecker scheduleChecker;
	private Intent defaultScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDefaultScreen();
		mainUI();
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		int minutes = Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5));
		String s = scheduleChecker.getClass(MainActivity.timesSwiped, scheduleChecker.getCurrentPeriod(minutes, 0));
		String[] array = s.split("\n");
		String period = array[0];
		String course = array[2];
		Log.e("debugging", course);
		NotificationCompat.Builder builder = (NotificationCompat.Builder)new NotificationCompat.Builder(this)
				.setSmallIcon(changeNotificationDayIcon())
				.setContentTitle(period)
				.setContentText(course);
		//Intent resultIntent = new Intent(this, Settings.class);
		//PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		int notificationId = 001;
		NotificationManager notifyMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notifyMgr.notify(notificationId, builder.build());
		startActivity(defaultScreen);
	}

	public void mainUI() {
		File scheduleFile = new File((new ContextWrapper(this)).getFilesDir() + "/schedule.txt");
		ArrayList<String> classes = new ArrayList<String>();
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
			scheduleChecker = new ScheduleChecker(this, classes);
		}
		catch(IOException ioe) {}

	}

	public int changeNotificationDayIcon() {
		int day = scheduleChecker.getSchoolDayRotation(0);
		switch(day) {
			case 1:
				return R.drawable.ic_filter_1_black_24dp;
			case 2:
				return R.drawable.ic_filter_2_black_24dp;
			case 3:
				return R.drawable.ic_filter_3_black_24dp;
			case 4:
				return R.drawable.ic_filter_4_black_24dp;
			case 5:
				return R.drawable.ic_filter_5_black_24dp;
			case 6:
				return R.drawable.ic_filter_6_black_24dp;
			case 7:
				return R.drawable.ic_filter_7_black_24dp;
			case 8:
				return R.drawable.ic_filter_8_black_24dp;
			default:
				return R.drawable.ic_filter_none_black_24dp;
		}
	}

	public void getDefaultScreen() {
		File settingsCache = new File(new ContextWrapper(this).getFilesDir() + "/settings.txt");
		try {
			Scanner scan = new Scanner(settingsCache);
			String line;
			String opt;
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				opt = line.substring(0, line.indexOf(":") + 1);
				if(opt.equals("defaultView:"))
					setDefaultView(line.substring(line.indexOf(":") + 1));
			}
		}
		catch(IOException ioe) {}
	}

	public void setDefaultView(String str) {
		Log.e("debugging", str);
		try {
			switch(str) {
				case "day_view":
					defaultScreen = new Intent(Notify.this, DayViewActivity.class);
					break;
				case "current_view":
					defaultScreen = new Intent(Notify.this, CurrentViewActivity.class);
					break;
				default:
					defaultScreen = new Intent(Notify.this, MainActivity.class);
					Log.e("debugging", "error");
			}
		}
		catch(Exception e) {}
	}
}
