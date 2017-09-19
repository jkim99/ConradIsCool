/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */
package com.example.jonat.scheduleapp2;

import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/*
 * Settings page handles user custom settings and saves them onto settings file
 */
public class Settings extends AppCompatActivity {

	private File settings;
	private boolean dailyNotificationsChecked;
	private boolean periodicNotificationsChecked;
	private int defaultView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkSettings();
		setContentView(R.layout.settings);
		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		Switch everyClassNotification = (Switch)findViewById(R.id.every_class_notification);
		everyClassNotification.setChecked(periodicNotificationsChecked);
		everyClassNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				periodicNotificationsChecked = isChecked;
				updateSettings();
			}
		});
		Switch dailyNotification = (Switch)findViewById(R.id.daily_notification);
		dailyNotification.setChecked(dailyNotificationsChecked);
		dailyNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				dailyNotificationsChecked = isChecked;
				updateSettings();
			}
		});
		final RadioGroup defaultViews = (RadioGroup)findViewById(R.id.default_view);
		RadioButton currentViewOption = (RadioButton)findViewById(R.id.current_view_option);
		RadioButton dayViewOption = (RadioButton)findViewById(R.id.day_view_option);
		RadioButton monthViewOption = (RadioButton)findViewById(R.id.month_view_option);
		currentViewOption.setTextColor(getResources().getColor(R.color.white));
		dayViewOption.setTextColor(getResources().getColor(R.color.white));
		monthViewOption.setTextColor(getResources().getColor(R.color.white));
		switch(viewToString(defaultView)) {
			case "current_view":
				defaultViews.check(currentViewOption.getId());
				break;
			case "day_view":
				defaultViews.check(dayViewOption.getId());
				break;
			case "month_view":
				defaultViews.check(monthViewOption.getId());
				break;
		}
		currentViewOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				defaultView = R.layout.current_view;
				updateSettings();
			}
		});
		dayViewOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				defaultView = R.layout.day_view;
				updateSettings();
			}
		});
		monthViewOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				defaultView = R.layout.month_view;
				updateSettings();
			}
		});
		updateSettings();

		Button sendLogs = (Button)findViewById(R.id.send_logs);
		sendLogs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendLogs();
			}
		});

		Button clear = (Button)findViewById(R.id.clear_button);
		final File scheduleFile = new File(this.getFilesDir(), "schedule.txt");
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utility.purge(scheduleFile, settings);
				startActivity(new Intent(Settings.this, MainActivity.class));
			}
		});
	}

	public void checkSettings() {
		settings = new File(this.getFilesDir(), "settings.txt");
		try {
			Scanner scan = new Scanner(settings);
			String line;
			String opt;
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				opt = line.substring(0, line.indexOf(":") + 1);
				switch(opt) {
					case "version:":
						MainActivity.version = Double.valueOf(line.substring(line.indexOf(":") + 1));
						break;
					case "defaultView:":
						defaultView = stringToView(line.substring(line.indexOf(":") + 1));
						break;
					case "dailyNotifications:":
						dailyNotificationsChecked = !(line.substring(line.indexOf(":") + 1).equals("off"));
						break;
					case "periodicNotifications:":
						periodicNotificationsChecked = !(line.substring(line.indexOf(":") + 1).equals("off"));
						break;
					default:
						Log.i("checkSettings", "settings file corrupted or missing");
				}
			}
		}
		catch(IOException ioe) {}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);
		return true;
	}

	public void updateSettings() {
		try {
			File f = new File(this.getFilesDir(), "settings.txt");
			PrintWriter pw = new PrintWriter(f);
			pw.println("--Settings--");
			pw.println("version:" + MainActivity.version);
			pw.println("defaultView:" + viewToString(defaultView));
			pw.println("dailyNotifications:" + (dailyNotificationsChecked ? "on" : "off"));
			pw.println("periodicNotifications:" + (periodicNotificationsChecked ? "on" : "off"));
			pw.close();
		}
		catch(Exception e) {
			Log.e("settings", "failed to update settings");
			Log.e("settings", e.toString());
		}
	}

	public String viewToString(int x) {
		switch(x) {
			case R.layout.current_view:
				return "current_view";
			case R.layout.day_view:
				return "day_view";
			case R.layout.month_view:
				return "month_view";
			default:
				return "current_view";
		}
	}

	public int stringToView(String s) {
		switch(s) {
			case "current_view":
				return R.layout.current_view;
			case "day_view":
				return R.layout.day_view;
			case "month_view":
				return R.layout.month_view;
			default:
				return R.layout.day_view;
		}
	}

	public void sendLogs() {
		try {
			File logs = File.createTempFile("logs.txt", ".tmp", this.getExternalCacheDir());
			Runtime.getRuntime().exec("logcat -f " + logs.getAbsolutePath());

			String[] addressTo = {"jkim2018@k12.andoverma.us"};

			Intent mail = new Intent(Intent.ACTION_SEND);
			mail.setType("vnd.android.cursor.dir/email");
			mail.putExtra(Intent.EXTRA_EMAIL, addressTo);
			mail.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logs));
			mail.putExtra(Intent.EXTRA_SUBJECT, "Seven H App Version: " + MainActivity.version);
			startActivityForResult(Intent.createChooser(mail, "Send Logs..."), 0);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
