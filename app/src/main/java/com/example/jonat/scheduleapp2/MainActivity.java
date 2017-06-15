package com.example.jonat.scheduleapp2;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
	private WebView aspenLogin;
	private File settingsCache;
	private File scheduleFile;
	private File errorLogs;
	public static int timesSwiped = 0;
	private ScheduleChecker scheduleChecker;
	private Intent defaultScreen;
	private boolean notifications;

	private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch(item.getItemId()) {
				case R.id.navigation_current_view:
					Intent intent1 = new Intent(MainActivity.this, CurrentViewActivity.class);
					startActivity(intent1);
					return true;
				case R.id.navigation_day_view:
					Intent intent2 = new Intent(MainActivity.this, DayViewActivity.class);
					startActivity(intent2);
					return true;
				case R.id.navigation_month_view:
					Intent intent3 = new Intent(MainActivity.this, MonthViewActivity.class);
					startActivity(intent3);
					return true;
			}
			return false;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scheduleFile = new File(new ContextWrapper(this).getFilesDir() + "/schedule.txt");
		settingsCache = new File(new ContextWrapper(this).getFilesDir() + "/settings.txt");

		Intent myIntent = new Intent(MainActivity.this , Notify.class);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);
		startActivity(myIntent);

		/*NotificationCompat.Builder builder = (NotificationCompat.Builder)new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_settings_black_24dp).setContentTitle("My notification").setContentText("Hello World!");
		Intent resultIntent = new Intent(this, Settings.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		int notificationId = 001;
		NotificationManager notifyMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notifyMgr.notify(notificationId, builder.build());*/

		if(!scheduleFile.exists())
			aspenPage();
		if(!settingsCache.exists() && settingsCache.isDirectory())
			createSettings();
		checkSettings();
		mainUI();
		try {
			Scanner scan = new Scanner(errorLogs);
			while(scan.hasNextLine())
				Log.e("debugging", scan.nextLine());
		}
		catch(Exception e) {}
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
				Intent intent = new Intent(MainActivity.this, Settings.class);
				startActivity(intent);
				return true;
			case R.id.navigation_search:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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
		int period = scheduleChecker.getCurrentPeriod(Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3)), 0);
		MenuItem icon = menu.findItem(R.id.navigation_current_view);
		switch(period) {
			case 0:
				icon.setIcon(R.drawable.ic_looks_one_black_24dp);
				break;
			case 1:
				icon.setIcon(R.drawable.ic_looks_two_black_24dp);
				break;
			case 2:
				icon.setIcon(R.drawable.ic_looks_3_black_24dp);
				break;
			case 3:
				icon.setIcon(R.drawable.ic_looks_4_black_24dp);
				break;
			case 4:
				icon.setIcon(R.drawable.ic_looks_5_black_24dp);
				break;
			default:
				icon.setIcon(R.drawable.ic_looks_none_black_24dp);
				break;
		}
	}

	public void aspenPage() {

		aspenLogin = new WebView(this);
		aspenLogin.getSettings().setJavaScriptEnabled(true);
		aspenLogin.getSettings().setUserAgentString("Mozilla/5.0 (Windows; U; Windows NT 6.2; en-US; rv:1.9) Gecko/2008062901 IceWeasel/3.0");
		aspenLogin.loadUrl("https://ma-andover.myfollett.com/aspen/logon.do");
		aspenLogin.addJavascriptInterface(new JSInterface(this), "HTMLOUT");
		setContentView(aspenLogin);
		aspenLogin.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				String javascript = "javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
				aspenLogin.loadUrl(javascript);
				if(url.contains("home")) {
					aspenLogin.loadUrl("https://ma-andover.myfollett.com/aspen/studentScheduleContextList.do?navkey=myInfo.sch.list");
					javascript = "javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
					aspenLogin.loadUrl(javascript);
					aspenLogin.destroy();
				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
	}

	public void mainUI() {
		scheduleFile = new File(new ContextWrapper(this).getFilesDir() + "/schedule.txt");
		aspenPage();
		ArrayList<String> classes = new ArrayList<String>();
		try {
			Scanner scan = new Scanner(scheduleFile);
			if(!scan.nextLine().equals("--Schedule--"))
				aspenPage();
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
			try {
				startActivity(defaultScreen);
			}
			catch(NullPointerException npe) {
				startActivity(new Intent(MainActivity.this, CurrentViewActivity.class));
			}
			Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
			setSupportActionBar(myToolbar);

			/*BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
			navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
			changeDayIcon(navigation.getMenu());
			changePeriodIcon(navigation.getMenu());*/
		}
		catch(IOException ioe) {
			aspenPage();
			Log.i("debugging", ioe.toString());
		}
	}

	public void createSettings() {
		try {
			PrintWriter pw = new PrintWriter(settingsCache);
			pw.println("defaultView:current_view");
			pw.println("notifications:on");
			pw.close();
			Log.i("debugging","M:created settings file");
		}
		catch(Exception e) {
			Log.e("debugging", "error creating settings");
		}
	}

	public void checkSettings() {
		errorLogs = new File(new ContextWrapper(this).getFilesDir() + "/logs.txt");
		try {
			Scanner scan = new Scanner(settingsCache);
			String line;
			String opt;
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				opt = line.substring(0, line.indexOf(":") + 1);
				switch(opt) {
					case "defaultView:":
						setDefaultView(line.substring(line.indexOf(":") + 1));
						break;
					case "notifications:":
						notifications = !(line.substring(line.indexOf(":")).equals("off"));
						break;
					default:
						Log.i("debugging", "settings file corrupted or missing");
						createSettings();
				}
			}
		}
		catch(IOException ioe) {}
	}

	public void setDefaultView(String str) {
		errorLogs = new File(new ContextWrapper(this).getFilesDir() + "/logs.txt");
		Log.e("debugging", str);
		try {
			switch(str) {
				case "day_view":
					defaultScreen = new Intent(MainActivity.this, DayViewActivity.class);
					break;
				case "current_view":
					defaultScreen = new Intent(MainActivity.this, CurrentViewActivity.class);
					break;
				default:
					defaultScreen = new Intent(MainActivity.this, MainActivity.class);
					Log.e("debugging", "error");
					Scanner scan = new Scanner(errorLogs);
					PrintWriter pw = new PrintWriter(errorLogs);
					while(scan.hasNextLine())
						pw.println(scan.nextLine());
					pw.println("error: unable to get default view");
					pw.close();
					break;
			}
		}
		catch(Exception e) {}
	}

}
