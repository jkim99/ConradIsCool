package com.example.jonat.scheduleapp2;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Settings extends AppCompatActivity {

	private OnSwipeTouchListener on;
	private ScheduleChecker scheduleChecker;
	private File settingsCache;
	private File errorLogs;
	private boolean dailyNotificationsChecked;
	private boolean periodicNotificationsChecked;
	private int defaultView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkSettings();
		scheduleChecker = Utility.initializeScheduleChecker(this);
		setContentView(R.layout.settings);
		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		final ViewGroup transitionsContainer = (ViewGroup)this.findViewById(android.R.id.content);
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
		Button clear = (Button)findViewById(R.id.clear_button);
		final File scheduleFile = new File(new ContextWrapper(this).getFilesDir() + "/schedule.txt");
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utility.purge(scheduleFile, settingsCache);
				startActivity(new Intent(Settings.this, MainActivity.class));
			}
		});
	}

	public void checkSettings() {
		errorLogs = new File(new ContextWrapper(this).getFilesDir() + "/logs.txt");
		settingsCache = new File(new ContextWrapper(this).getFilesDir() + "/settings.txt");
		try {
			Scanner scan = new Scanner(settingsCache);
			String line;
			String opt;
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				opt = line.substring(0, line.indexOf(":") + 1);
				switch(opt) {
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
						Log.i("debugging", "settings file corrupted or missing");
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
			File f = new File(new ContextWrapper(this).getFilesDir() + "/settings.txt");
			PrintWriter pw = new PrintWriter(f);
			pw.println("--Settings--");
			pw.println("defaultView:" + viewToString(defaultView));
			pw.println("dailyNotifications:" + (dailyNotificationsChecked ? "on" : "off"));
			pw.println("periodicNotifications:" + (periodicNotificationsChecked ? "on" : "off"));
			pw.close();
		}
		catch(Exception e) {
			Log.e("debugging", "failed to update settings");
			Log.e("debugging", e.toString());
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
}
