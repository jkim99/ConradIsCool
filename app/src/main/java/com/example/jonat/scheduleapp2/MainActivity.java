package com.example.jonat.scheduleapp2;

import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
	private WebView aspenLogin = null;
	private File settingsCache;
	private int timesSwiped = 0;
	private ScheduleChecker scheduleChecker;
	private int defaultScreen;

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
				case R.id.navigation_notifications:

					return true;
			}
			return false;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//aspenPage();
		//mainUI();
		/*setContentView(R.layout.activity_main);
		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
		changeDayIcon(navigation.getMenu());
		changePeriodIcon(navigation.getMenu());*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);
		return true;
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

	public void aspenPage() {
		Log.i("debugging", "1");
		aspenLogin = new WebView(this);
		aspenLogin.getSettings().setJavaScriptEnabled(true);
		aspenLogin.getSettings().setUserAgentString("Mozilla/5.0 (Windows; U; Windows NT 6.2; en-US; rv:1.9) Gecko/2008062901 IceWeasel/3.0");
		Log.i("debugging", "2");
		aspenLogin.loadUrl("https://ma-andover.myfollett.com/aspen/logon.do");
		aspenLogin.addJavascriptInterface(new JSInterface(this), "HTMLOUT");
		Log.i("debugging", "3");
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
					mainUI();
				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
	}

	public void mainUI() {
		File scheduleFile = new File((new ContextWrapper(this)).getFilesDir() + "/schedule.txt");
		if(!scheduleFile.exists())
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
		}
		catch(IOException ioe) {
			aspenPage();
			Log.i("debugging", ioe.toString());
		}

	}

	public void checkSettings() {
		try {
			Scanner scan = new Scanner(settingsCache);
			//TODO: get settings
		}
		catch(IOException ioe) {}
	}
}
