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
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;


/* DayViewActivity allows the user to view their full schedule for that day.
 * This view also displays the times of each class.
 */
public class DayViewActivity extends AppCompatActivity {

	private OnSwipeTouchListener on;
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
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					return true;
				case R.id.navigation_day_view:
					return true;
				case R.id.navigation_month_view:
					Intent intent3 = new Intent(DayViewActivity.this, MonthViewActivity.class);
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
		setContentView(R.layout.day_view);

		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		final BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		this.navigation = navigation;
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		navigation.setSelectedItemId(R.id.navigation_day_view);

		final TextView[] times = {(TextView)findViewById(R.id.time1), (TextView)findViewById(R.id.time2), (TextView)findViewById(R.id.time3), (TextView)findViewById(R.id.time4), (TextView)findViewById(R.id.time5)};

		lunch = (TextView)findViewById(R.id.lunch);
		lunch.setElevation(1000);

		final Button[] buttons = {(Button)findViewById(R.id.p1),(Button)findViewById(R.id.p2),(Button)findViewById(R.id.p3),(Button)findViewById(R.id.p4),(Button)findViewById(R.id.p5)};
		final ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.content);
		updateUI(constraintLayout, 'x', buttons, times);

		on = new OnSwipeTouchListener(this) {

			public void onSwipeLeft() {
				MainActivity.swipeDirectionOffset++;
				updateUI(constraintLayout, 'l', buttons, times);
			}

			public void onSwipeRight() {
				MainActivity.swipeDirectionOffset--;
				updateUI(constraintLayout, 'r', buttons, times);
			}

		};
		updateUI(constraintLayout, 'x', buttons, times);
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
				Intent intent = new Intent(DayViewActivity.this, SettingsActivity.class);
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
			b.setText(MainActivity.scheduleChecker.getClass(MainActivity.swipeDirectionOffset, x));
			b.setBackgroundResource(Utility.backgroundFix(this, x));
			b.setOnTouchListener(on);
			x++;
		}
	}

	public void updateUI(ConstraintLayout constraintLayout, char dir, Button[] buttons, TextView[] times) {
		try {
			Utility.changeDayIcon(navigation.getMenu());
			Utility.changePeriodIcon(navigation.getMenu());

			int direction = R.anim.fade_in;

			if(dir == 'l')
				direction = R.anim.slide_left;
			if(dir == 'r')
				direction = R.anim.slide_right;

			Animation animation = AnimationUtils.loadAnimation(this, direction);
			constraintLayout.startAnimation(animation);

			int[] timeStamps = Utility.getTimeStamps(MainActivity.swipeDirectionOffset);
			for(int i = 0; i < timeStamps.length; i++) {
				times[i].setText(Utility.timeStampToString(timeStamps[i]));
				times[i].setElevation(1000);
			}

			changeButtons(buttons);
			lunch.setText(Utility.getLunch(MainActivity.swipeDirectionOffset, 3));
		}
		catch(NullPointerException npe) {
			npe.printStackTrace();
			startActivity(new Intent(DayViewActivity.this, AspenPage.class));
		}
	}

}
