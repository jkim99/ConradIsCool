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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainUI();
		String s = scheduleChecker.getRoom(MainActivity.timesSwiped, 0);
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

}
