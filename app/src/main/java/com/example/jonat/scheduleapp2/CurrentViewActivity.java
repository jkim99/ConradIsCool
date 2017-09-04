package com.example.jonat.scheduleapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;

public class CurrentViewActivity extends AppCompatActivity {

	private OnSwipeTouchListener on;
	private ScheduleChecker scheduleChecker;

	private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch(item.getItemId()) {
				case R.id.navigation_current_view:
					return true;
				case R.id.navigation_day_view:
					Intent intent2 = new Intent(CurrentViewActivity.this, DayViewActivity.class);
					startActivity(intent2);
					return true;
				case R.id.navigation_month_view:
					Intent intent3 = new Intent(CurrentViewActivity.this, MonthViewActivity.class);
					startActivity(intent3);
					return true;
			}
			return false;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scheduleChecker = Utility.initializeScheduleChecker(this);
		setContentView(R.layout.current_view);

		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		navigation.setSelectedItemId(R.id.navigation_current_view);
		Utility.changeDayIcon(scheduleChecker, navigation.getMenu());
		Utility.changePeriodIcon(scheduleChecker, navigation.getMenu());
		final ViewGroup transitionsContainer = (ViewGroup)this.findViewById(android.R.id.content);
		final Button currentClass = (Button)findViewById(R.id.currentClass);
		final Button nextClass = (Button)findViewById(R.id.nextClass);
		on = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {
				MainActivity.swipeDirectionOffset++;
//				Transition transition = new Slide(android.view.Gravity.LEFT);
//				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
				changeButtons(currentClass, nextClass);
			}
			public void onSwipeRight() {
				MainActivity.swipeDirectionOffset--;
//				Transition transition = new Slide(android.view.Gravity.RIGHT);
//				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
				changeButtons(currentClass, nextClass);
			}
		};
		changeButtons(currentClass, nextClass);
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

	public void changeButtons(Button current, Button next) {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		int minutes = Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3, 5));
		current.setText("Current Class: \n" + scheduleChecker.getClass(MainActivity.swipeDirectionOffset, scheduleChecker.getCurrentPeriod(minutes, 0)));
		next.setText("Next Class: \n" + scheduleChecker.getClass(MainActivity.swipeDirectionOffset, scheduleChecker.getCurrentPeriod(minutes, 1)));
		current.setOnTouchListener(on);
		next.setOnTouchListener(on);
	}
}
