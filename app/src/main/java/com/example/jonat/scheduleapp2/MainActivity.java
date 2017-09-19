/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */

package com.example.jonat.scheduleapp2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

/*
 * The MainActivity class is the conventional class that is called for Android applications.
 * In this class, the startup values and protocols are called and initialized.
 * Many of these functions and fields are used for initial setup of the app.
 */

public class MainActivity extends AppCompatActivity {

	private File settings;
	private Intent defaultScreen;
	public double latestVersion = 1.5;
	public AlarmManager periodicAlarmManager;
	public AlarmManager dailyAlarmManager;
	public static int swipeDirectionOffset = 0;
	public static ScheduleChecker scheduleChecker;
	public static boolean dailyNotifications;
	public static boolean periodicNotifications;
	public static final int[] periodNotificationID = {1, 2, 3, 4, 5};
	public static final int[] notificationTimes = {Utility.PERIOD_1_BELL,
												   Utility.PERIOD_2_BELL,
												   Utility.PERIOD_3_BELL,
												   Utility.PERIOD_4_BELL,
												   Utility.PERIOD_5_BELL};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		File scheduleFile = new File(this.getFilesDir(), "schedule.txt");
		File calendarFile = new File(this.getFilesDir(), "calendar.txt");
		File lunchFile = new File(this.getFilesDir(), "lunch.txt");
		settings = new File(this.getFilesDir(), "settings.txt");

		checkSettingsFile();

		if(isOnline()) {
			Log.i("updates", "isOnline");
			CalendarChecker.updateCalendar(calendarFile);
			LunchChecker.updateLunch(lunchFile);
		}
		if(!Utility.verifyScheduleFile(scheduleFile)) {
			startActivity(new Intent(MainActivity.this, AspenPage.class));
		}
		else {
			try {
				startActivity(defaultScreen);
			}
			catch(NullPointerException npe) {
				startActivity(new Intent(MainActivity.this, CurrentViewActivity.class));
			}
			Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
			setSupportActionBar(myToolbar);
		}
		initializeScheduleChecker(scheduleFile);
		if(periodicNotifications) {
			setPeriodicAlarms();
		}
		else {
			cancelAlarms(periodicAlarmManager, periodNotificationID);
		}
		if(dailyNotifications) {
			setDailyAlarms();
		}
		else {
			cancelAlarm(dailyAlarmManager, 20);
		}
		if(latestVersion > Utility.version) {
			FirebaseMessaging.getInstance().subscribeToTopic("update");
		}
		else {
			FirebaseMessaging.getInstance().unsubscribeFromTopic("update");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.navigation_settings:
				Intent intent = new Intent(MainActivity.this, Settings.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void createSettingsFile() {
		try {
			PrintWriter pw = new PrintWriter(settings);
			pw.println("--Settings--");
			pw.println("defaultView:current_view");
			pw.println("dailyNotifications:on");
			pw.println("periodicNotifications:on");
			pw.close();
			Log.i("settings","M:created settings file");
		}
		catch(Exception e) {
			Log.e("debugging", "error creating settings");
		}
	}

	public void checkSettingsFile() {
		try {
			Scanner scan = new Scanner(settings);
			if(!scan.nextLine().equals("--Settings--"))
				createSettingsFile();
			String line, opt;
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				opt = line.substring(0, line.indexOf(":") + 1);
				Log.i("settings", line);
				switch(opt) {
					case "defaultView:":
						setDefaultView(line.substring(line.indexOf(":") + 1));
						break;
					case "dailyNotifications:":
						dailyNotifications = !(line.substring(line.indexOf(":")).equals("off"));
						break;
					case "periodicNotifications:":
						periodicNotifications = !(line.substring(line.indexOf(":")).equals("off"));
						break;
					default:
						Log.i("settings_check", "settings file corrupted or missing");
						createSettingsFile();
				}
			}
		}
		catch(IOException ioe) {
			createSettingsFile();
		}
	}

	private void initializeScheduleChecker(File scheduleFile) {
		ArrayList<String> classes = new ArrayList<String>();
		if(Utility.verifyScheduleFile(scheduleFile)) {
			try {
				Scanner scan = new Scanner(scheduleFile);
				scan.nextLine();
				while(scan.hasNextLine()) {
					classes.add(
						scan.nextLine() + "\n" +
						scan.nextLine() + "\n" +
						scan.nextLine()
					);
					scan.nextLine();
					scan.nextLine();
				}
				scheduleChecker = new ScheduleChecker(classes);
			}
			catch(IOException ioe) {}
		}
	}

	public void setDefaultView(String str) {
		try {
			switch(str) {
				case "day_view":
					defaultScreen = new Intent(MainActivity.this, DayViewActivity.class);
					break;
				case "current_view":
					defaultScreen = new Intent(MainActivity.this, CurrentViewActivity.class);
					break;
				case "month_view":
					defaultScreen = new Intent(MainActivity.this, MonthViewActivity.class);
					break;
				default:
					defaultScreen = new Intent(MainActivity.this, MainActivity.class);
					break;
			}
		}
		catch(Exception e) {}
	}

	public boolean isOnline() {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	public void setAlarm(int hour, int minute, int pendingIntentId, Notification notification) {
		Intent intent = new Intent(this, Notify.class);
		intent.putExtra("notification_id", 10);
		intent.putExtra("notification_object", notification);
		intent.putExtra("notification_times", notificationTimes);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, pendingIntentId, intent, 0);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		periodicAlarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		periodicAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
	}

	public int setPeriodicAlarms() {
		String nextClass;
		android.support.v4.app.NotificationCompat.Builder builder;
		String[] classInfo;
		TaskStackBuilder stackBuilder;
		PendingIntent resultPendingIntent;
		Notification notification;
		android.support.v4.app.NotificationCompat.InboxStyle inboxStyle;

		for(int i = 0; i < notificationTimes.length; i++) {
			nextClass = MainActivity.scheduleChecker.getClass(0, i);
			inboxStyle = new NotificationCompat.InboxStyle();
			classInfo = nextClass.split("\n");
			for(String s : classInfo)
				inboxStyle.addLine(s);
			builder = new NotificationCompat.Builder(this)
					.setContentTitle("Your next class: ")
					.setContentText(nextClass)
					.setSmallIcon(R.drawable.ic_launcher_proto)
					.setStyle(inboxStyle);
			stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(CurrentViewActivity.class);
			stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
			resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);
			notification = builder.build();

			setAlarm(notificationTimes[i] / 60, notificationTimes[i] % 60, periodNotificationID[i], notification);
		}
		return 0;
	}

	public int setDailyAlarms() {
		android.support.v4.app.NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		String[] schedule = Utility.oneLineClassNames();
		for(String s : schedule)
			inboxStyle.addLine(s);
		android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setContentTitle("Your schedule for today")
				.setContentText("Day " + Utility.getSchoolDayRotation(0))
				.setSmallIcon(R.drawable.ic_launcher_proto)
				.setStyle(inboxStyle);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(DayViewActivity.class);
		stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		Notification notification = builder.build();
		Intent intent = new Intent(this, Notify.class);
		intent.putExtra("notification_id", 20);
		intent.putExtra("notification_object", notification);
		intent.putExtra("notification_times", notificationTimes);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 20, intent, 0);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 7);
		cal.set(Calendar.MINUTE, 0);
		dailyAlarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		dailyAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
		return 0;
	}

	public void cancelAlarms(AlarmManager am, int[] id) {
		for(int x : id) {
			cancelAlarm(am, x);
		}
	}

	public void cancelAlarm(AlarmManager am, int id) {
		PendingIntent alarmIntent;
		try {
			alarmIntent = PendingIntent.getBroadcast(this, id, new Intent(this, MainActivity.class), 0);
			am.cancel(alarmIntent);
		}
		catch(NullPointerException npe) {

		}
	}

}