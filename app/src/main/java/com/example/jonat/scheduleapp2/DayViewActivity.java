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

import org.w3c.dom.Text;

/* DayViewActivity allows the user to view their full schedule for that day.
 * This view also displays the times of each class.
 */
public class DayViewActivity extends AppCompatActivity {

	private OnSwipeTouchListener on;
	private ScheduleChecker scheduleChecker;
	private BottomNavigationView navigation;
	private TextView lunch;

	private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(MenuItem item) {
			switch(item.getItemId()) {
				case R.id.navigation_current_view:
					Intent intent2 = new Intent(DayViewActivity.this, CurrentViewActivity.class);
					startActivity(intent2);
					return true;
				case R.id.navigation_day_view:
					return true;
				case R.id.navigation_month_view:
					Intent intent3 = new Intent(DayViewActivity.this, MonthViewActivity.class);
					startActivity(intent3);
					return true;
			}
			return false;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.day_view);

		scheduleChecker = Utility.initializeScheduleChecker(this);

		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		final BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		this.navigation = navigation;
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		navigation.setSelectedItemId(R.id.navigation_day_view);

		TextView[] times = {(TextView)findViewById(R.id.time1), (TextView)findViewById(R.id.time2), (TextView)findViewById(R.id.time3), (TextView)findViewById(R.id.time4), (TextView)findViewById(R.id.time5)};
		for(TextView t : times)
			t.setElevation(1000);

		lunch = (TextView)findViewById(R.id.lunch);
		lunch.setElevation(1000);

		final Button[] buttons = {(Button)findViewById(R.id.p1),(Button)findViewById(R.id.p2),(Button)findViewById(R.id.p3),(Button)findViewById(R.id.p4),(Button)findViewById(R.id.p5)};
		updateUI(buttons);

		on = new OnSwipeTouchListener(this) {

			public void onSwipeLeft() {
				MainActivity.swipeDirectionOffset++;
				updateUI(buttons);
			}

			public void onSwipeRight() {
				MainActivity.swipeDirectionOffset--;
				updateUI(buttons);
			}

		};
		updateUI(buttons);
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
				Intent intent = new Intent(DayViewActivity.this, Settings.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/** Changes text on buttons to update based on the <int>swipeDirectionOffset</int> in the <class>MainActivity</class> class*/
	public void changeButtons(Button[] buttons) {
		int x = 0;
		for(Button b : buttons) {
			b.setText(scheduleChecker.getClass(MainActivity.swipeDirectionOffset, x));
			b.setBackgroundResource(Utility.backgroundFix(this, x));
			b.setOnTouchListener(on);
			x++;
		}
	}

	public void updateUI(Button[] buttons) {
		try {
			Utility.changeDayIcon(scheduleChecker, navigation.getMenu());
			Utility.changePeriodIcon(scheduleChecker, navigation.getMenu());
			changeButtons(buttons);
			lunch.setText(Utility.getLunch(scheduleChecker, MainActivity.swipeDirectionOffset));
		}
		catch(NullPointerException npe) {
			Log.e("UI_update", npe.toString());
			startActivity(new Intent(DayViewActivity.this, AspenPage.class));
		}
	}

}
