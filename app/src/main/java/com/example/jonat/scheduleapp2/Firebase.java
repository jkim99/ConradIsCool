package com.example.jonat.scheduleapp2;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Firebase extends FirebaseMessagingService {
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		Log.d("firebase", remoteMessage.getData() + "");
	}
}
