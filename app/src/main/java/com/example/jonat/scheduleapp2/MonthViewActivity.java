package com.example.jonat.scheduleapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Calendar;

public class MonthViewActivity extends AppCompatActivity {
	private ScheduleChecker scheduleChecker;
	private BottomNavigationView navigation;

	private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(MenuItem item) {
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
		setContentView(R.layout.month_view);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);

		scheduleChecker = Utility.initializeScheduleChecker(this);

		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		final BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		this.navigation = navigation;
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		navigation.setSelectedItemId(R.id.navigation_month_view);

		final Button[] buttons = {(Button)findViewById(R.id.p1),(Button)findViewById(R.id.p2),(Button)findViewById(R.id.p3),(Button)findViewById(R.id.p4),(Button)findViewById(R.id.p5)};
		final TextView dayRotationTextView = (TextView)findViewById(R.id.day_rotation);

		updateUI(buttons);

		CalendarView calendarView = (CalendarView)findViewById(R.id.calendarView);
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, 0);
		int dayRotation = Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset);
		dayRotationTextView.setText(dayRotation < 0 ? "No School" : "" + dayRotation);
		calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				MainActivity.swipeDirectionOffset = Days.daysBetween(new LocalDate(), new LocalDate(year, month + 1, dayOfMonth)).getDays();
				int dayRotation = Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset);
				dayRotationTextView.setText(dayRotation < 0 ? "No School" : "" + dayRotation);
				updateUI(buttons);

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

	public void changeButtons(Button[] buttons) {
		String[] schedule = Utility.oneLineClassNames(scheduleChecker);
		for(int x = 0; x < buttons.length; x++) {
			buttons[x].setText(schedule[x]);
			buttons[x].setBackgroundResource(Utility.backgroundFix(this, x));
			//buttons[x].setOnTouchListener();
		}
	}

	public void updateUI(Button[] buttons) {
		try {
			Utility.changeDayIcon(scheduleChecker, navigation.getMenu());
			Utility.changePeriodIcon(scheduleChecker, navigation.getMenu());
			changeButtons(buttons);
		}
		catch(NullPointerException npe) {
			Log.e("UI_update", npe.toString());
			startActivity(new Intent(MonthViewActivity.this, AspenPage.class));
		}
	}
	
}
