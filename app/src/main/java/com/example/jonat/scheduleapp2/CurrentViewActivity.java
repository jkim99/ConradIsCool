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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

/*
 * CurrentViewActivity Class allows the user to view the current class and
 * next class quickly in a simple view.
 */
public class CurrentViewActivity extends AppCompatActivity {

	private OnSwipeTouchListener on;
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

		final TextView[] textViews = {(TextView)findViewById(R.id.time1), (TextView)findViewById(R.id.time2), (TextView)findViewById(R.id.lunch1), (TextView)findViewById(R.id.lunch2)};
		final Button[] buttons = {(Button)findViewById(R.id.currentClass), (Button)findViewById(R.id.nextClass)};
		final ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.content);
		updateUI(constraintLayout, 'x', buttons, textViews);

		on = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {
				MainActivity.swipeDirectionOffset++;
				updateUI(constraintLayout, 'l', buttons, textViews);
			}
			public void onSwipeRight() {
				MainActivity.swipeDirectionOffset--;
				updateUI(constraintLayout, 'r', buttons, textViews);
			}
		};
		updateUI(constraintLayout, 'x', buttons, textViews);
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
				Intent intent = new Intent(CurrentViewActivity.this, SettingsActivity.class);
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
		int minutes = Utility.getCurrentMinutes();
		current.setText(getResources().getString(R.string.current_class) + "\n" + MainActivity.scheduleChecker.getClass(MainActivity.swipeDirectionOffset, MainActivity.scheduleChecker.getCurrentPeriod(minutes, 0)));
		next.setText(getResources().getString(R.string.next_class) + "\n" +  MainActivity.scheduleChecker.getClass(MainActivity.swipeDirectionOffset, MainActivity.scheduleChecker.getCurrentPeriod(minutes, 1)));
		current.setOnTouchListener(on);
		next.setOnTouchListener(on);
	}

	/** Changes text on TextViews to update based on the time and lunch period*/
	public void changeTextViews(TextView[] textViews) {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		int period = MainActivity.scheduleChecker.getCurrentPeriod(Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5)), 0);

		TextView time1 = textViews[0];
		TextView time2 = textViews[1];
		TextView lunch1 = textViews[2];
		TextView lunch2 = textViews[3];

		try {
			int[] timeStamps = Utility.getTimeStamps(MainActivity.swipeDirectionOffset);
			time1.setText(Utility.timeStampToString(timeStamps[period]));
			time2.setText(Utility.timeStampToString(timeStamps[period + 1]));
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {

		}

		if(period == 3)
			lunch1.setText(Utility.getLunch(MainActivity.swipeDirectionOffset, 3));
		if(period == 2)
			lunch2.setText(Utility.getLunch(MainActivity.swipeDirectionOffset, 2));

		for(TextView t : textViews)
			t.setElevation(1000);
	}

	public void updateUI(ConstraintLayout constraintLayout, char dir, Button[] buttons, TextView[] textViews) {
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

			changeButtons(buttons);
			changeTextViews(textViews);
		}
		catch(NullPointerException npe) {
			Log.e("UI_update", npe.toString());
			startActivity(new Intent(CurrentViewActivity.this, AspenPage.class));
		}
	}

}
