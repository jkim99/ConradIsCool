/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */

package sked.official.jonat.scheduleapp2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

/*
 * The MainActivity class is the conventional class that is called for Android applications.
 * In this class, the startup values and protocols are called and initialized.
 * Many of these functions and fields are used for initial setup of the app.
 */

public class MainActivity extends AppCompatActivity {

	private File calendarFile;
	private File scheduleFile;
	private File settingsFile;
	public AlarmManager periodicAlarmManager;
	public AlarmManager dailyAlarmManager;
	public static int swipeDirectionOffset = 0;
	public static boolean dev = false;
	public static boolean dailyNotifications;
	public static boolean periodicNotifications;
	public static String name;
	public static ScheduleChecker scheduleChecker;
	public static final int[] periodNotificationID = {1, 2, 3, 4, 5};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo_page);

		scheduleFile = new File(this.getFilesDir(), "schedule.txt");
		settingsFile = new File(this.getFilesDir(), "settings.txt");
		calendarFile = new File(this.getFilesDir(), "calendar.txt");
		new SettingsHandler(settingsFile);

		if(dev) {
			File[] files = {scheduleFile, calendarFile, settingsFile};
			Utility.purge(files);
		}

		if(!Utility.verifyScheduleFile(scheduleFile)) {
			dev = false;
			startActivity(new Intent(MainActivity.this, AspenPage.class));
		}
		else {
			new DownloadData().execute();
		}
	}

	private void initializeScheduleChecker(File scheduleFile) {
		ArrayList<String> classes = new ArrayList<>();
		if(Utility.verifyScheduleFile(scheduleFile)) {
			try {
				Scanner scan = new Scanner(scheduleFile);
				scan.nextLine();
				scan.nextLine();
				name = scan.nextLine();
				scan.nextLine();
				while(scan.hasNextLine()) {
					classes.add(
						scan.nextLine() + "\n" +
						scan.nextLine() + "\n" +
						scan.nextLine() + "\n" +
						scan.nextLine() + "\n" +
						scan.nextLine() + "\n"
					);
					scan.nextLine();
				}
				scheduleChecker = new ScheduleChecker(classes);
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public void update(boolean isOnline) {
		if(isOnline) {
			CalendarChecker.updateCalendar(calendarFile);
		}
	}

	private void setAllAlarms() {
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
	}

	public boolean isOnline() {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	public void setAlarm(long notificationTime, int pendingIntentId, Notification notification) {
		Intent intent = new Intent(this, Notify.class);
		intent.putExtra("notification_id", 10);
		intent.putExtra("notification_object", notification);
		intent.putExtra("notification_times", Utility.getTimeStamps(0));

		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, pendingIntentId, intent, 0);

		periodicAlarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		periodicAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, notificationTime, AlarmManager.INTERVAL_DAY, alarmIntent);
	}

	public int setPeriodicAlarms() { //adjust for no school days
		String nextClass;
		android.support.v4.app.NotificationCompat.Builder builder;
		String[] classInfo;
		TaskStackBuilder stackBuilder;
		PendingIntent resultPendingIntent;
		Notification notification;
		android.support.v4.app.NotificationCompat.InboxStyle inboxStyle;

		int[] notificationTimes = Utility.getTimeStamps(0);

		for(int i = 0; i < notificationTimes.length; i++) {
			nextClass = MainActivity.scheduleChecker.getClass(0, i);
			inboxStyle = new NotificationCompat.InboxStyle();
			classInfo = nextClass.split("\n");
			for(String s : classInfo)
				inboxStyle.addLine(s);

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.set(Calendar.HOUR_OF_DAY, notificationTimes[i] / 60);
			cal.set(Calendar.MINUTE, notificationTimes[i] % 60);
			long notificationTime = cal.getTimeInMillis();

			builder = new NotificationCompat.Builder(this)
					.setContentTitle("Your next class: ")
					.setContentText(nextClass)
					.setSmallIcon(R.drawable.ic_launcher_proto)
					.setStyle(inboxStyle)
					.setWhen(notificationTime);

			stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(DayViewActivity.class);
			stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
			resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);
			notification = builder.build();

			setAlarm(notificationTime, periodNotificationID[i], notification);
		}
		return 0;
	}

	public int setDailyAlarms() {
		try {
			android.support.v4.app.NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
			String[] schedule = Utility.oneLineClassNames(1);
			for(String s : schedule)
				inboxStyle.addLine(s);

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.set(Calendar.HOUR_OF_DAY, 7);
			cal.set(Calendar.MINUTE, 0);
			long notificationTime = cal.getTimeInMillis();

			android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
					.setContentTitle("Your schedule for today")
					.setContentText("Day " + Utility.getSchoolDayRotation(1))
					.setSmallIcon(R.drawable.ic_launcher_proto)
					.setStyle(inboxStyle)
					.setWhen(notificationTime);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(DayViewActivity.class);
			stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);
			Notification notification = builder.build();

			Intent intent = new Intent(this, Notify.class);
			intent.putExtra("notification_id", 20);
			intent.putExtra("notification_object", notification);
			intent.putExtra("notification_times", Utility.getTimeStamps(1));

			Log.d("notification: ", notification.toString());

			PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 20, intent, 0);

			dailyAlarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
			dailyAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, notificationTime, AlarmManager.INTERVAL_DAY, alarmIntent);

			return 0;
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			return -1;
		}
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
			npe.printStackTrace();
		}
	}

	private class DownloadData extends AsyncTask {

		@Override
		protected void onPreExecute() {

			initializeScheduleChecker(scheduleFile);
			CalendarChecker.updateCalendar(calendarFile);
			setAllAlarms();
			//startActivity(new Intent(MainActivity.this, EditInfoActivity.class));
			startActivity(new Intent(MainActivity.this, DayViewActivity.class));
			setSupportActionBar((Toolbar)findViewById(R.id.my_toolbar));
		}

		@Override
		protected Object doInBackground(Object[] params) {
			update(isOnline());
			return null;
		}

		@Override
		protected void onPostExecute(Object o) {
			startActivity(new Intent(MainActivity.this, DayViewActivity.class));
		}
	}

}