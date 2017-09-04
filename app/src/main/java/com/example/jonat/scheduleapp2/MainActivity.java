/**
 * what it does: saves you 10 minutes of time at a specific high school in a specific town for the cost of 1 hour learning this app
 * who to get mad at: josh krinsky (the jew kid in my AP physics 1 class
 * @author Jonathan S. Kim
 * @version whatever the current version of Minecraft is minus 16
 * @since 7/19/2017
 **/

package com.example.jonat.scheduleapp2;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
	private WebView aspenLogin;
	private File settingsCache;
	private File scheduleFile;
	private File errorLogs;
	public static int swipeDirectionOffset = 0;
	private ScheduleChecker scheduleChecker;
	private Intent defaultScreen;
	public static boolean dailyNotifications;
	public static boolean periodicNotifications;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		scheduleFile = new File(new ContextWrapper(this).getFilesDir() + "/schedule.txt");
		settingsCache = new File(new ContextWrapper(this).getFilesDir() + "/settings.txt");

		if(!settingsCache.exists())
			createSettings();
		checkSettings();

		if(!Utility.verifyScheduleFile(scheduleFile)) {
			aspenPage();
		}
		else {
			scheduleChecker = Utility.initializeScheduleChecker(this);
			try {
				startActivity(defaultScreen);
			}
			catch(NullPointerException npe) {
				startActivity(new Intent(MainActivity.this, CurrentViewActivity.class));
			}
			Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
			setSupportActionBar(myToolbar);
		}
		try {
			Scanner scan = new Scanner(errorLogs);
			while(scan.hasNextLine())
				Log.e("debugging", scan.nextLine());
		}
		catch(Exception e) {}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Utility.initializeCalendar(this);
		if((dailyNotifications || periodicNotifications)&& Utility.getSchoolDayRotation(0) >= 0)
			startService(new Intent(this, Notify.class));
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
			default:
				return super.onOptionsItemSelected(item);
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
					try { Thread.sleep(250); }
					catch(Exception e) {}
					finishAndRemoveTask();
				}
			}
		});
		(Toast.makeText(this, "App will close after login, please relaunch app to continue", Toast.LENGTH_SHORT)).show();
	}

	public void createSettings() { //consider using JSON or using a .properties file for each property
		try {
			PrintWriter pw = new PrintWriter(settingsCache);
			pw.println("--Settings--");
			pw.println("defaultView:current_view"); //consider equals
			pw.println("dailyNotifications:on");
			pw.println("periodicNotifications:on");
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
			scan.nextLine();
			String line;
			String opt;
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				opt = line.substring(0, line.indexOf(":") + 1);
				switch(opt) {
					case "defaultView:":
						setDefaultView(line.substring(line.indexOf(":") + 1));
						break;
					case "dailyNotifications:":
						dailyNotifications = !(line.substring(line.indexOf(":")).equals("off"));
						break;
					case "periodicNotifications:":
						periodicNotifications = !(line.substring(line.indexOf(":")).equals("off"));
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
		try {
			switch(str) {
				case "day_view":
					defaultScreen = new Intent(MainActivity.this, DayViewActivity.class);
					break;
				case "current_view":
					defaultScreen = new Intent(MainActivity.this, CurrentViewActivity.class);
					break;
				case "month_view":
					defaultScreen = new Intent(MainActivity.this, MonthViewActivity.class);
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
