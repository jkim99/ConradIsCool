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
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DayViewActivity extends AppCompatActivity {

	private OnSwipeTouchListener on;
	private ScheduleChecker scheduleChecker;
	private int timesSwiped = 0;

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
				case R.id.navigation_notifications:

					return true;
			}
			return false;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainUI();
		setContentView(R.layout.day_view);

		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		navigation.setSelectedItemId(R.id.navigation_day_view);
		changeDayIcon(navigation.getMenu());
		changePeriodIcon(navigation.getMenu());
		final ViewGroup transitionsContainer = (ViewGroup)this.findViewById(android.R.id.content);
		on = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {
				timesSwiped++;
				Transition transition = new Slide(3);
				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
			}
			public void onSwipeRight() {
				timesSwiped--;
				Transition transition = new Slide(5);
				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
			}
		};
		Button p1 = (Button)findViewById(R.id.p1);
		Button p2 = (Button)findViewById(R.id.p2);
		Button p3 = (Button)findViewById(R.id.p3);
		Button p4 = (Button)findViewById(R.id.p4);
		Button p5 = (Button)findViewById(R.id.p5);
		p1.setText(scheduleChecker.getRoom(scheduleChecker.getSchoolDayRotation(timesSwiped), 0));
		p2.setText(scheduleChecker.getRoom(scheduleChecker.getSchoolDayRotation(timesSwiped), 1));
		p3.setText(scheduleChecker.getRoom(scheduleChecker.getSchoolDayRotation(timesSwiped), 2));
		p4.setText(scheduleChecker.getRoom(scheduleChecker.getSchoolDayRotation(timesSwiped), 3));
		p5.setText(scheduleChecker.getRoom(scheduleChecker.getSchoolDayRotation(timesSwiped), 4));
		p1.setOnTouchListener(on);
		p2.setOnTouchListener(on);
		p3.setOnTouchListener(on);
		p4.setOnTouchListener(on);
		p5.setOnTouchListener(on);
	}

	public void changeDayIcon(Menu menu) {
		int day = scheduleChecker.getSchoolDayRotation(timesSwiped);
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
		String time = scheduleChecker.getTime();
		int period = scheduleChecker.getCurrentPeriod(Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3)));
		MenuItem icon = menu.findItem(R.id.navigation_current_view);
		switch(period) {
			case 1:
				icon.setIcon(R.drawable.ic_looks_one_black_24dp);
				break;
			case 2:
				icon.setIcon(R.drawable.ic_looks_two_black_24dp);
				break;
			case 3:
				icon.setIcon(R.drawable.ic_looks_3_black_24dp);
				break;
			case 4:
				icon.setIcon(R.drawable.ic_looks_4_black_24dp);
				break;
			case 5:
				icon.setIcon(R.drawable.ic_looks_5_black_24dp);
				break;
			default:
				icon.setIcon(R.drawable.ic_button);
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
}
