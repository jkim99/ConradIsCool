/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */

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
import android.widget.TextView;

/*
 * CurrentViewActivity Class allows the user to view the current class and
 * next class quickly in a simple view.
 */
public class CurrentViewActivity extends AppCompatActivity {

	private OnSwipeTouchListener on;
	private ScheduleChecker scheduleChecker;
	private BottomNavigationView navigation;

	private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(MenuItem item) {
			switch(item.getItemId()) {
				case R.id.navigation_current_view:
					return true;
				case R.id.navigation_day_view:
					Intent intent2 = new Intent(CurrentViewActivity.this, DayViewActivity.class);
					startActivity(intent2);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					return true;
				case R.id.navigation_month_view:
					Intent intent3 = new Intent(CurrentViewActivity.this, MonthViewActivity.class);
					startActivity(intent3);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					return true;
			}
			return false;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_view);

		final BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		this.navigation = navigation;
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		navigation.setSelectedItemId(R.id.navigation_current_view);

		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		scheduleChecker = Utility.initializeScheduleChecker(this);

		final TextView[] textViews = {(TextView)findViewById(R.id.time1), (TextView)findViewById(R.id.time2), (TextView)findViewById(R.id.lunch1), (TextView)findViewById(R.id.lunch2)};
		final Button[] buttons = {(Button)findViewById(R.id.currentClass), (Button)findViewById(R.id.nextClass)};
		updateUI(buttons, textViews);

		on = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {
				MainActivity.swipeDirectionOffset++;
				updateUI(buttons, textViews);
			}
			public void onSwipeRight() {
				MainActivity.swipeDirectionOffset--;
				updateUI(buttons, textViews);
			}
		};
		updateUI(buttons, textViews);
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
				Intent intent = new Intent(CurrentViewActivity.this, Settings.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/** Changes text on buttons to update based on the <int>swipeDirectionOffset</int> in the <class>MainActivity</class> class*/
	public void changeButtons(Button[] buttons) {
		Button current = buttons[0];
		Button next = buttons[1];
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		int minutes = Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5));
		current.setText(getResources().getString(R.string.current_class) + "\n" + scheduleChecker.getClass(MainActivity.swipeDirectionOffset, scheduleChecker.getCurrentPeriod(minutes, 0)));
		next.setText(getResources().getString(R.string.next_class) + "\n" +  scheduleChecker.getClass(MainActivity.swipeDirectionOffset, scheduleChecker.getCurrentPeriod(minutes, 1)));
		current.setOnTouchListener(on);
		next.setOnTouchListener(on);
	}

	/** Changes text on TextViews to update based on the time and lunch period*/
	public void changeTextViews(TextView[] textViews) {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		int period = scheduleChecker.getCurrentPeriod(Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5)), 0);

		TextView time1 = textViews[0];
		TextView time2 = textViews[1];
		TextView lunch1 = textViews[2];
		TextView lunch2 = textViews[3];

		time1.setText(getTimeText(0));
		time2.setText(getTimeText(1));

		if(period == 3)
			lunch1.setText(Utility.getLunch(Utility.initializeScheduleChecker(this), MainActivity.swipeDirectionOffset));
		if(period == 2)
			lunch2.setText(Utility.getLunch(Utility.initializeScheduleChecker(this), MainActivity.swipeDirectionOffset));
	}

	/** @return string of times depending on the period*/
	public String getTimeText(int periodAfter) {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		int period = scheduleChecker.getCurrentPeriod(Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5)), periodAfter);
		if(Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset) >= 0) {
			switch(period) {
				case 0:
					return getResources().getString(R.string.time1);
				case 1:
					return getResources().getString(R.string.time2);
				case 2:
					return getResources().getString(R.string.time3);
				case 3:
					return getResources().getString(R.string.time4);
				case 4:
					return getResources().getString(R.string.time5);
				default:
					return "";
			}
		}
		else {
			return "";
		}
	}

	public void updateUI(Button[] buttons, TextView[] textViews) {
		try {
			Utility.changeDayIcon(scheduleChecker, navigation.getMenu());
			Utility.changePeriodIcon(scheduleChecker, navigation.getMenu());
			changeButtons(buttons);
			changeTextViews(textViews);
		}
		catch(NullPointerException npe) {
			Log.e("UI_update", npe.toString());
			startActivity(new Intent(CurrentViewActivity.this, AspenPage.class));
		}
	}

}
