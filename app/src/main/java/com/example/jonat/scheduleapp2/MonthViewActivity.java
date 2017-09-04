package com.example.jonat.scheduleapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Calendar;

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

		scheduleChecker = Utility.initializeScheduleChecker(this);
		setContentView(R.layout.month_view);

		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		final BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		navigation.setSelectedItemId(R.id.navigation_month_view);
		Utility.changeDayIcon(scheduleChecker, navigation.getMenu());
		Utility.changePeriodIcon(scheduleChecker, navigation.getMenu());
		final ViewGroup transitionsContainer = (ViewGroup)this.findViewById(android.R.id.content);

		final TextView dayRotationTextView = (TextView)findViewById(R.id.day_rotation);
		CalendarView calendarView = (CalendarView)findViewById(R.id.calendarView);
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, 0);
		int dayRotation = Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset);
		dayRotationTextView.setText(dayRotation < 0 ? "No School" : "" + dayRotation);
		calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, 0);
				LocalDate d1 = new LocalDate();
				LocalDate d2 = new LocalDate(year, month + 1, dayOfMonth);
				int days = Days.daysBetween(d1, d2).getDays();
				MainActivity.swipeDirectionOffset = days;
				int dayRotation = Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset);
				dayRotationTextView.setText(dayRotation < 0 ? "No School" : "" + dayRotation);
				Utility.changeDayIcon(scheduleChecker, navigation.getMenu());
				Utility.changePeriodIcon(scheduleChecker, navigation.getMenu());

			}
		});
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.DATE, MainActivity.swipeDirectionOffset);
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


	
}
