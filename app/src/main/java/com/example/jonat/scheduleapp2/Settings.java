package com.example.jonat.scheduleapp2;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

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
	private boolean notifications;
	private int defaultView;

	private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch(item.getItemId()) {
				case R.id.navigation_current_view:
					Intent intent1 = new Intent(Settings.this, CurrentViewActivity.class);
					startActivity(intent1);
					return true;
				case R.id.navigation_day_view:
					Intent intent2 = new Intent(Settings.this, DayViewActivity.class);
					startActivity(intent2);
					return true;
				case R.id.navigation_month_view:
					return true;
			}
			return false;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkSettings();
		mainUI();
		setContentView(R.layout.settings);
		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		changeDayIcon(navigation.getMenu());
		changePeriodIcon(navigation.getMenu());

		unCheckAllMenuItems(navigation.getMenu());

		final ViewGroup transitionsContainer = (ViewGroup)this.findViewById(android.R.id.content);
		Switch notification = (Switch)findViewById(R.id.notification);
		notification.setChecked(notifications);
		notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				notifications = isChecked;
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
			//case "month_view":
				//defaultViews.check(monthViewOption.getId());
				//break;
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
		/*monthViewOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				defaultView = R.layout.month_view;
				updateSettings();
			}
		});*/

		updateSettings();
	}

	public void changeDayIcon(Menu menu) {
		int day = scheduleChecker.getSchoolDayRotation(MainActivity.timesSwiped);
		MenuItem icon = menu.findItem(R.id.navigation_day_view);
		switch(day) {
			case 1:
				icon.setIcon(R.drawable.ic_filter_1_black_24dp);
				break;
			case 2:
				icon.setIcon(R.drawable.ic_filter_2_black_24dp);
				break;
			case 3:
				icon.setIcon(R.drawable.ic_filter_3_black_24dp);
				break;
			case 4:
				icon.setIcon(R.drawable.ic_filter_4_black_24dp);
				break;
			case 5:
				icon.setIcon(R.drawable.ic_filter_5_black_24dp);
				break;
			case 6:
				icon.setIcon(R.drawable.ic_filter_6_black_24dp);
				break;
			case 7:
				icon.setIcon(R.drawable.ic_filter_7_black_24dp);
				break;
			case 8:
				icon.setIcon(R.drawable.ic_filter_8_black_24dp);
				break;
			default:
				icon.setIcon(R.drawable.ic_filter_none_black_24dp);
				break;
		}
	}

	public void changePeriodIcon(Menu menu) {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		int period = scheduleChecker.getCurrentPeriod(Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3)), 0);
		MenuItem icon = menu.findItem(R.id.navigation_current_view);
		switch(period) {
			case 0:
				icon.setIcon(R.drawable.ic_looks_one_black_24dp);
				break;
			case 1:
				icon.setIcon(R.drawable.ic_looks_two_black_24dp);
				break;
			case 2:
				icon.setIcon(R.drawable.ic_looks_3_black_24dp);
				break;
			case 3:
				icon.setIcon(R.drawable.ic_looks_4_black_24dp);
				break;
			case 4:
				icon.setIcon(R.drawable.ic_looks_5_black_24dp);
				break;
			default:
				icon.setIcon(R.drawable.ic_looks_none_black_24dp);
				break;
		}
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
					case "notifications:":
						notifications = !(line.substring(line.indexOf(":") + 1).equals("off"));
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
			pw.println("defaultView:" + viewToString(defaultView));
			pw.println("notifications:" + (notifications ? "on" : "off"));
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
			//case R.layout.month_view:
			//	return "month_view";
			default:
				return "current_view";
		}
	}

	public int stringToView(String s) {
		Log.e("debugging", s);
		switch(s) {
			case "current_view":
				return R.layout.current_view;
			case "day_view":
				return R.layout.day_view;
			//case "month_view":
			//	return R.layout.month_view;
			default:
				return R.layout.day_view;
		}
	}

	public void unCheckAllMenuItems(@NonNull final Menu menu) {
		int size = menu.size();
		for(int i = 0; i < size; i++) {
			final MenuItem item = menu.getItem(i);
			if(item.hasSubMenu())
				unCheckAllMenuItems(item.getSubMenu());
			else
				item.setChecked(false);
		}
	}
}
