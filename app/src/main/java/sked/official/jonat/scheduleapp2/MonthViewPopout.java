/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */
package sked.official.jonat.scheduleapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.CalendarView;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Calendar;

/*
 * MonthView allows the user to manually chose a specific date for
 * more information on their schedule for that day. This updates
 * the rest of the app for more detail available in the other views.
 */
public class MonthViewPopout extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.month_view);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels;
		int height = dm.heightPixels;

		getWindow().setLayout((int)(width * 0.8), (int)(height * 0.4 ));

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);

		CalendarView calendarView = (CalendarView)findViewById(R.id.calendarView);
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, 0);
		calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				MainActivity.swipeDirectionOffset = Days.daysBetween(new LocalDate(), new LocalDate(year, month + 1, dayOfMonth)).getDays();
				startActivity(new Intent(MonthViewPopout.this, DayViewActivity.class));
				overridePendingTransition(R.anim.slide_down, R.anim.slide_right);
			}
		});
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.DATE, MainActivity.swipeDirectionOffset);
		calendarView.setDate(c2.getTimeInMillis());
	}

	@Override
	protected void onStop() {
		super.onStop();
		startActivity(new Intent(this, DayViewActivity.class));
		overridePendingTransition(R.anim.slide_up, R.anim.slide_right);
	}
}
