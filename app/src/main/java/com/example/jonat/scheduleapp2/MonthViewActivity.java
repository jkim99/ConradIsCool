package com.example.jonat.scheduleapp2;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class MonthViewActivity extends AppCompatActivity {
	private OnSwipeTouchListener on;
	private ScheduleChecker scheduleChecker;

	private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch(item.getItemId()) {
				case R.id.navigation_current_view:
					Intent intent1 = new Intent(MonthViewActivity.this, CurrentViewActivity.class);
					startActivity(intent1);
					return true;
				case R.id.navigation_day_view:
					Intent intent2 = new Intent(MonthViewActivity.this, DayViewActivity.class);
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
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);

		mainUI();
		setContentView(R.layout.month_view);

		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		navigation.setSelectedItemId(R.id.navigation_month_view);
		changeDayIcon(navigation.getMenu());
		changePeriodIcon(navigation.getMenu());
		final ViewGroup transitionsContainer = (ViewGroup)this.findViewById(android.R.id.content);
		on = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {
				MainActivity.timesSwiped++;
				Transition transition = new Slide(3);
				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
				startActivity(new Intent(MonthViewActivity.this, MonthViewActivity.class));
			}
			public void onSwipeRight() {
				MainActivity.timesSwiped--;
				Transition transition = new Slide(5);
				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
				startActivity(new Intent(MonthViewActivity.this, MonthViewActivity.class));
			}
		};

		final TextView dayRotation = (TextView)findViewById(R.id.day_rotation);
		CalendarView calendarView = (CalendarView)findViewById(R.id.calendarView);
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, 0);
		//dayRotation.setText(scheduleChecker.getSchoolDayRotation(MainActivity.timesSwiped));
		calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, 0);
				LocalDate d1 = new LocalDate();
				LocalDate d2 = new LocalDate(year, month + 1, dayOfMonth);
				int days = Days.daysBetween(d1, d2).getDays();
				//dayRotation.setText(scheduleChecker.getSchoolDayRotation(x));
				MainActivity.timesSwiped = days;
			}
		});
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.DATE, MainActivity.timesSwiped);
		calendarView.setDate(c2.getTimeInMillis());
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
				Intent intent = new Intent(MonthViewActivity.this, Settings.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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
		int period = scheduleChecker.getCurrentPeriod(Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5)), 0);
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
	
	public void addDayRotationEvent(LocalDate d) {
		int day = scheduleChecker.getSchoolDayRotation();
		
		if (!(day==-1 || day==-2 || day==-3 || day==-10)){
			
		}
	}
	
}
