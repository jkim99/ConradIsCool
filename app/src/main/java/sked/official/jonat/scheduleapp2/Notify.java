package sked.official.jonat.scheduleapp2;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Notify extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			int notificationID = intent.getIntExtra("notification_id", 0);
			Notification notification = intent.getParcelableExtra("notification_object");
			int[] times = intent.getIntArrayExtra("notification_times");

			switch(notificationID) {
				case 10:
					//periodic
					sendPeriodicNotification(times, context, notificationID, notification);
					break;
				case 20:
					//daily
					sendDailyNotification(context, notificationID, notification);
					break;
			}
		}
		catch(NullPointerException npe) {
			Log.e("notify", npe.toString());
		}
	}

	public void sendPeriodicNotification(int[] times, Context context, int notificationID, Notification notification) {
		boolean isTimeToNotify = false;
		int minutes = Utility.getCurrentMinutes();
		for(int time : times) {
			isTimeToNotify = minutes == time || isTimeToNotify;
		}
		if(isTimeToNotify) {
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(notificationID, notification);
		}
	}

	public void sendDailyNotification(Context context, int notificationID, Notification notification) {
		if(Utility.getCurrentMinutes() == Utility.SEVEN_O_CLOCK && Utility.getSchoolDayRotation(0) > 0) {
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(notificationID, notification);
		}
	}

}