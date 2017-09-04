package com.example.jonat.scheduleapp2;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Notify extends Service {
	private Timer timer;
	private TimerTask timerTask;
	private ScheduleChecker scheduleChecker;
	final Handler HANDLER = new Handler();

	@Nullable
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

	public void startTimer() {
		timer = new Timer();
		scheduleChecker = Utility.initializeScheduleChecker(this);
		initializeTimerTask();
		timer.schedule(timerTask, 5000, 5000);
	}

	public void stopTimerTask() {
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void initializeTimerTask() {
		timerTask = new TimerTask() {
			@Override
			public void run() {
				HANDLER.post(new Runnable() {
					@Override
					public void run() {
						if(MainActivity.dailyNotifications)
							dailyNotification();
						if(MainActivity.periodicNotifications)
							everyClassNotification();
					}
				});
			}
		};
	}

	public void everyClassNotification() {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		int minutes = Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5));
		int[] notificationTimes =  {Utility.PERIOD_1_BELL,
									Utility.PERIOD_2_BELL,
									Utility.PERIOD_3_BELL,
									Utility.PERIOD_4_BELL,
									Utility.PERIOD_5_BELL};
		boolean sendNotification = false;
		for(int x : notificationTimes)
			sendNotification = minutes == x || sendNotification;
		String nextClass = scheduleChecker.getClass(0, scheduleChecker.getCurrentPeriod(minutes, 0));
		if((Utility.getSchoolDayRotation(0) >= 0) && sendNotification) {
			android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setContentTitle("Your next class")
				.setContentText(nextClass)
				.setSmallIcon(R.drawable.ic_launcher_web);
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(0, builder.build());
		}
	}

	public void dailyNotification() {
		String[][] schedule = new String[5][4];
		for(int x = 0; x < 5; x++)
			schedule[x] = scheduleChecker.getClass(0, x).split("\n");
		String contentText = schedule[0][0] + "\n" +
							 schedule[1][0] + "\n" +
							 schedule[2][0] + "\n" +
							 schedule[3][0] + "\n" +
							 schedule[4][0] + "\n";
		Log.e("debugging", contentText);
		android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
			.setContentTitle("Your schedule for today")
			.setContentText(contentText)
			.setSmallIcon(R.drawable.ic_launcher_web);
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, builder.build());
	}

}
