/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */

package sked.official.jonat.scheduleapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.Calendar;

/* DayViewActivity allows the user to view their full schedule for that day.
 * This view also displays the times of each class.
 */
public class DayViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	private OnSwipeTouchListener on;
	private TextView lunch;
	private MenuItem icon;
	private FloatingActionButton floatingActionButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.day_view);
		overridePendingTransition(R.anim.no_animation, R.anim.fade_out);

		AdView adView = (AdView)findViewById(R.id.ad);
		MobileAds.initialize(this, "ca-app-pub-8214178121454691/3528585357");
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		lunch = (TextView)findViewById(R.id.lunch);
		lunch.setElevation(1000);

		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		toggle.syncState();

		NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		View navigationHeaderView = navigationView.getHeaderView(0);

		TextView studentName = (TextView)navigationHeaderView.findViewById(R.id.student_name);
		studentName.setText(MainActivity.name != null ? MainActivity.name : "");

		floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
		floatingActionButton.setImageResource(Utility.floatingButtonFix(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(DayViewActivity.this, MonthViewPopout.class));
				overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
			}
		});

		on = new OnSwipeTouchListener(this) {

			public void onSwipeLeft() {
				MainActivity.swipeDirectionOffset++;
				updateUI('l');
			}

			public void onSwipeRight() {
				MainActivity.swipeDirectionOffset--;
				updateUI('r');
			}

		};
		updateUI('x');
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);
		icon = menu.findItem(R.id.month_view_popup);
		changeDayIcon();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.month_view_popup:
				int temp = MainActivity.swipeDirectionOffset;
				MainActivity.swipeDirectionOffset = 0;
				if(temp > 0)
					updateUI('r');
				else if(temp < 0)
					updateUI('l');
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		if(drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		}
		else {
			super.onBackPressed();
		}
	}

	/** Changes text on buttons to update based on the <int>swipeDirectionOffset</int> in the <class>MainActivity</class> class*/
	public void changeButtons(Button[] buttons) {
		int x = 0;
		for(Button b : buttons) {
			b.setText(MainActivity.scheduleChecker.getClass(MainActivity.swipeDirectionOffset, x));
			b.setOnTouchListener(on);
			x++;
		}
	}

	public void changeImageViews(ImageView[] imageViews) {
		int x = 0;
		for(ImageView i : imageViews) {
			i.setImageResource(Utility.backgroundFix(x));
			x++;
		}
	}

	public void updateUI(char dir) {
		ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.content);
		Button[] buttons = {(Button)findViewById(R.id.p1),(Button)findViewById(R.id.p2),(Button)findViewById(R.id.p3),(Button)findViewById(R.id.p4),(Button)findViewById(R.id.p5)};
		TextView[] times = {(TextView)findViewById(R.id.time1), (TextView)findViewById(R.id.time2), (TextView)findViewById(R.id.time3), (TextView)findViewById(R.id.time4), (TextView)findViewById(R.id.time5)};
		ImageView[] imageViews = {(ImageView)findViewById(R.id.img1), (ImageView)findViewById(R.id.img2), (ImageView)findViewById(R.id.img3), (ImageView)findViewById(R.id.img4), (ImageView)findViewById(R.id.img5)};
		ImageView[] markers = {(ImageView)findViewById(R.id.marker1),
				(ImageView)findViewById(R.id.marker2),
				(ImageView)findViewById(R.id.marker3),
				(ImageView)findViewById(R.id.marker4),
				(ImageView)findViewById(R.id.marker5)};

		try {
			int direction = R.anim.fade_in;

			if(dir == 'l')
				direction = R.anim.slide_left;
			if(dir == 'r')
				direction = R.anim.slide_right;
			if(dir == 'x')
				direction = R.anim.no_animation;

			Animation animation = AnimationUtils.loadAnimation(this, direction);
			constraintLayout.startAnimation(animation);

			int[] timeStamps = Utility.getTimeStamps(MainActivity.swipeDirectionOffset);
			for(int i = 0; i < timeStamps.length; i++) {
				times[i].setText(Utility.timeStampToString(timeStamps[i]));
				times[i].setElevation(1000);
			}

			for(int i = 0; i < markers.length; i++) {
				markers[i].setVisibility(View.INVISIBLE);
				if(i == MainActivity.scheduleChecker.getCurrentPeriod(Utility.getCurrentMinutes(), 0)) {
					markers[i].setVisibility(View.VISIBLE);
				}
			}
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, MainActivity.swipeDirectionOffset);
			floatingActionButton.setImageResource(Utility.floatingButtonFix(calendar.get(Calendar.DAY_OF_MONTH)));
			changeImageViews(imageViews);
			changeButtons(buttons);
			lunch.setText(Utility.getLunch(MainActivity.swipeDirectionOffset, 3));
			changeDayIcon();
		}
		catch(NullPointerException npe) {
			npe.printStackTrace();
			startActivity(new Intent(this, MonthViewPopout.class));
		}
	}

	public void changeDayIcon() {
		try {
			int day = Utility.getSchoolDayRotation(MainActivity.swipeDirectionOffset);
			MenuItem icon = this.icon;
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
		catch(NullPointerException npe) {
			//do nothing
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

//		if(id == R.id.nav_H_block) {
//			//todo
//		}
		if(id == R.id.nav_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
		}
		else if(id == R.id.nav_update) {
			updateScheduleFile(new File(this.getFilesDir(), "schedule.txt"));
		}
		else if(id == R.id.nav_review) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/forms/KU1GzxoxkvQDAU8J2")));
		}
		else if(id == R.id.nav_log) {
			sendLogs();
		}

		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	public void updateScheduleFile(File scheduleFile) {
		File[] files = {scheduleFile};
		Utility.purge(files);
		startActivity(new Intent(this, MainActivity.class));
	}

	public void sendLogs() {
		try {
			File logs = File.createTempFile("logs.txt", ".tmp", this.getExternalCacheDir());
			Runtime.getRuntime().exec("logcat -f " + logs.getAbsolutePath());

			String[] addressTo = {"jkim2018@k12.andoverma.us"};

			Intent mail = new Intent(Intent.ACTION_SEND);
			mail.setType("vnd.android.cursor.dir/email");
			mail.putExtra(Intent.EXTRA_EMAIL, addressTo);
			mail.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logs));
			mail.putExtra(Intent.EXTRA_SUBJECT, "sked");
			startActivityForResult(Intent.createChooser(mail, "Send Logs..."), 0);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
