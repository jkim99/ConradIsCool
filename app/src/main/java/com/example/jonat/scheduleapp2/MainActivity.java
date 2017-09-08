/**
 * what it does: saves you 10 minutes of time at a specific high school in a specific town for the cost of 1 hour learning this app
 * who to get mad at: josh krinsky (the jew kid in my AP physics 1 class
 * @author Jonathan S. Kim
 * @version whatever the current version of Minecraft is minus 16
 * @since 7/19/2017
 **/

package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
	Context context = this;
	private File settings;
	private File scheduleFile;
	private File errorLogs;
	public static int swipeDirectionOffset = 0;
	private ScheduleChecker scheduleChecker;
	private Intent defaultScreen;
	public static boolean dailyNotifications;
	public static boolean periodicNotifications;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		scheduleFile = new File(this.getFilesDir(), "schedule.txt");
		settings = new File(this.getFilesDir() + "settings.txt");

		try {
			Scanner scan = new Scanner(scheduleFile);
			while(scan.hasNextLine())
				Log.d("schedule_file", scan.nextLine());
		}
		catch(Exception e) {}

		if(!settings.exists())
			createSettings();
		checkSettings();

		if(!Utility.verifyScheduleFile(scheduleFile))
			startActivity(new Intent(MainActivity.this, AspenPage.class));
		else {
			scheduleChecker = Utility.initializeScheduleChecker(context);
			try {
				startActivity(defaultScreen);
			}
			catch(NullPointerException npe) {
				startActivity(new Intent(MainActivity.this, CurrentViewActivity.class));
			}
			Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
			setSupportActionBar(myToolbar);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		Utility.initializeCalendar(this);
		if((dailyNotifications || periodicNotifications)&& Utility.getSchoolDayRotation(0) >= 0)
			startService(new Intent(this, Notify.class));
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

	public void createSettings() { //consider using JSON or using a .properties file for each property
		try {
			PrintWriter pw = new PrintWriter(settings);
			pw.println("--Settings--");
			pw.println("defaultView:current_view"); //consider equals
			pw.println("dailyNotifications:on");
			pw.println("periodicNotifications:on");
			pw.close();
			Log.i("debugging","M:created settings file");
		}
		catch(Exception e) {
			Log.e("debugging", "error creating settings");
		}
	}

	public void checkSettings() {
		errorLogs = new File(this.getFilesDir(), "logs.txt");
		try {
			Scanner scan = new Scanner(settings);
			scan.nextLine();
			String line;
			String opt;
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				opt = line.substring(0, line.indexOf(":") + 1);
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
						Log.i("debugging", "settings file corrupted or missing");
						createSettings();
				}
			}
		}
		catch(IOException ioe) {}
	}

	public void setDefaultView(String str) {
		errorLogs = new File(new ContextWrapper(this).getFilesDir() + "/logs.txt");
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
					Log.e("debugging", "error");
					Scanner scan = new Scanner(errorLogs);
					PrintWriter pw = new PrintWriter(errorLogs);
					while(scan.hasNextLine())
						pw.println(scan.nextLine());
					pw.println("error: unable to get default view");
					pw.close();
					break;
			}
		}
		catch(Exception e) {}
	}
}
