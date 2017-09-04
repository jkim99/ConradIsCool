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

public class DayViewActivity extends AppCompatActivity {

	private OnSwipeTouchListener on;
	private ScheduleChecker scheduleChecker;

	private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
		scheduleChecker = Utility.initializeScheduleChecker(this);
		setContentView(R.layout.day_view);

		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		final BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		navigation.setSelectedItemId(R.id.navigation_day_view);
		Utility.changeDayIcon(scheduleChecker, navigation.getMenu());
		Utility.changePeriodIcon(scheduleChecker, navigation.getMenu());
		final ViewGroup transitionsContainer = (ViewGroup)this.findViewById(android.R.id.content);
		final Button[] buttons = {(Button)findViewById(R.id.p1),(Button)findViewById(R.id.p2),(Button)findViewById(R.id.p3),(Button)findViewById(R.id.p4),(Button)findViewById(R.id.p5)};
		on = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {
				MainActivity.swipeDirectionOffset++;
//				Transition transition = new Slide(android.view.Gravity.LEFT);
//				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
				Utility.changeDayIcon(scheduleChecker, navigation.getMenu());
				changeButtons(buttons);
			}
			public void onSwipeRight() {
				MainActivity.swipeDirectionOffset--;
//				Transition transition = new Slide(android.view.Gravity.RIGHT);
//				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
				Utility.changeDayIcon(scheduleChecker, navigation.getMenu());
				changeButtons(buttons);
			}
		};
		changeButtons(buttons);
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

	public void changeButtons(Button[] buttons) {
		int x = 0;
		for(Button b : buttons) {
			b.setText(scheduleChecker.getClass(MainActivity.swipeDirectionOffset, x));
			b.setBackgroundResource(backgroundFix(x));
			b.setOnTouchListener(on);
			x++;
		}
	}

	public int backgroundFix(int block) {
		char x;
		try {
			x = scheduleChecker.getBlock(Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset) - 1, block);
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			return R.drawable.x;
		}
		switch(x) {
			case 'A':
				return R.drawable.a;
			case 'B':
				return R.drawable.b;
			case 'C':
				return R.drawable.c;
			case 'D':
				return R.drawable.d;
			case 'E':
				return R.drawable.e;
			case 'F':
				return R.drawable.f;
			case 'G':
				return R.drawable.g;
			case 'H':
				return R.drawable.h;
		}
		return R.drawable.x;
	}

}
