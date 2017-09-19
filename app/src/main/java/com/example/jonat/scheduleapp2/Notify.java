package com.example.jonat.scheduleapp2;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Notify extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int notificationID = intent.getIntExtra("notification_id", 0);
		Notification notification = intent.getParcelableExtra("notification_object");

		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationID, notification);
	}

}