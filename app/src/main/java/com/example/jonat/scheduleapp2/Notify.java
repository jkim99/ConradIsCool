/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.0
 * @since 7/19/2017
 */

package com.example.jonat.scheduleapp2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.*;

/*
 * Notification class sets the timer to send notification at the specified times in the <class>Utility</class> Class
 */

public class Notify extends Service {
	private Timer timer;
	private TimerTask timerTask;
	final Handler HANDLER = new Handler();
	int periodicNotificationID = 0;
	int dailyNotificationID = 1;
	int updateNotificationID = 2;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		startTimer();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		stopTimerTask();
		super.onDestroy();
	}

	/** Starts timer for boolean check to send notification at regular intervals */
	public void startTimer() {
		timer = new Timer();
		if(MainActivity.scheduleChecker != null) {
			initializeTimerTask();
			timer.schedule(timerTask, 5000, 30000);
		}
	}

	/** Stops timer when <variable>timer</variable> is null */
	public void stopTimerTask() {
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	/** Calls <method>periodicNotification</method> and/or <method>dailyNotification</method> depending on user settings */
	public void initializeTimerTask() {
		timerTask = new TimerTask() {
			@Override
			public void run() {
				HANDLER.post(new Runnable() {
					@Override
					public void run() {
						int minutes = Utility.getCurrentMinutes();
						Log.d("notification", minutes + "");
						if(MainActivity.dailyNotifications) {
							dailyNotification(minutes);
						}
						if(MainActivity.periodicNotifications) {
							periodicNotification(minutes);
						}
						if(MainActivity.needsUpdate) {
							versionUpdateNotification(minutes);
						}
					}
				});
			}
		};
	}

	/** @param minutes Number of minutes after 12:00 AM
	 *  Builds and sends notification if <parameter>minutes</parameter> is equal to one fo the Utility PERIOD constants */
	public void periodicNotification(int minutes) {
		int[] notificationTimes =  {Utility.PERIOD_1_BELL,
									Utility.PERIOD_2_BELL,
									Utility.PERIOD_3_BELL,
									Utility.PERIOD_4_BELL,
									Utility.PERIOD_5_BELL};
		boolean sendNotification = false;
		String nextClass;
		for(int x : notificationTimes) {
			sendNotification = minutes == x || sendNotification;
		}
		try {
			nextClass = MainActivity.scheduleChecker.getClass(0, MainActivity.scheduleChecker.getCurrentPeriod(minutes, 0));
		}
		catch(NullPointerException npe) {
			nextClass = "";
		}
		if((Utility.getSchoolDayRotation(0) >= 0) && sendNotification) {
			android.support.v4.app.NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
			String[] classInfo = nextClass.split("\n");
			for(String s : classInfo)
				inboxStyle.addLine(s);
			android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
					.setContentTitle("Your schedule for today")
					.setContentText(nextClass)
					.setSmallIcon(R.drawable.ic_launcher_proto)
					.setStyle(inboxStyle);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(CurrentViewActivity.class);
			stackBuilder.addNextIntent(new Intent(this, CurrentViewActivity.class));
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(periodicNotificationID, builder.build());
		}
	}

	/** @param minutes Number of minutes after 12:00 AM
	 *  Builds and sends notification if <parameter>minutes</parameter> is equal to 7:00 AM constant */
	public void dailyNotification(int minutes) {
		if(minutes == Utility.SEVEN_AM) {
			Log.d("notification", "daily");
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
			stackBuilder.addNextIntent(new Intent(this, DayViewActivity.class));
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(dailyNotificationID, builder.build());
		}
	}

	public void versionUpdateNotification(int minutes) {
		if(minutes == Utility.SEVEN_AM) {
			android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
					.setContentTitle("Your app could use an update")
					.setContentText("Go to: https://drive.google.com/file/d/0B25a9GoL5A8hM1YyNDV3YUFUTU0/view?usp=sharing")
					.setSmallIcon(R.drawable.ic_launcher_proto);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addNextIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/0B25a9GoL5A8hM1YyNDV3YUFUTU0/view?usp=sharing")));
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(resultPendingIntent);
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(updateNotificationID, builder.build());
		}
	}

}
