package com.example.jonat.scheduleapp;

import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
<<<<<<< HEAD
import android.view.View;
import android.webkit.*;
import android.widget.Button;
=======
import android.webkit.*;
>>>>>>> 30134e2900a127bff6f37dae529bd1518fd839a8
import android.widget.TextView;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

	private WebView mWebView = null;
	private ScheduleCheck sc;
	private File f;
<<<<<<< HEAD
	private int timesSwiped;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
=======

	@Override
	protected void onCreate(Bundle savedInstanceState) {

>>>>>>> 30134e2900a127bff6f37dae529bd1518fd839a8
		super.onCreate(savedInstanceState);
		//getGrades();
		f = new File((new ContextWrapper(this)).getFilesDir() + "/schedule.txt");
		if(!f.exists())
			getSchedule();
		else
			mainUI();
	}

	public void mainUI() {
		String file = "";
		Log.i("debugging", "Checking for file...");
		try {
			Scanner scan = new Scanner(f);
			String line = scan.nextLine();
			if(!(line.equals("--Schedule--"))) { //VERIFICATION
				Log.i("debugging", "File not found or corrupted. Creating new schedule file...");
				getSchedule();
			}
			else {
				Log.i("debugging", "File verified");
				while(scan.hasNextLine()) {
					line = scan.nextLine();
					file += line + "\n";
				}
			}
		}
		catch(IOException ioe) {
			Log.i("debugging", ioe.toString());
		}
		ArrayList<String> classes = new ArrayList<String>();
		String[] temp = file.split("\n");
		for(int i = 0; i < temp.length; i += 5)
			classes.add(temp[i] + "\n" + temp[i + 1] + "\n" + temp[i + 2] + "\n" + temp[i + 3] + "\n");
		sc = new ScheduleCheck(this, classes);
<<<<<<< HEAD
		mainView(null);
=======
		String day = "Day: " + sc.getDay();
		String period = sc.getCurrentClass();
		Log.i("debugging", period);
		setContentView(R.layout.activity_main);
		TextView date = (TextView)findViewById(R.id.date);
		date.setText(new SimpleDateFormat("MM-dd").format(new java.util.Date()));
		TextView textView = (TextView)findViewById(R.id.day);
		textView.setText(day);
		TextView textView2 = (TextView)findViewById(R.id.currentClass);
		textView2.setText(period);
		TextView textView3 = (TextView)findViewById(R.id.nextClass);
		textView3.setText(sc.getNextClass());
>>>>>>> 30134e2900a127bff6f37dae529bd1518fd839a8
	}

	public void getSchedule() {

		Log.i("debugging", "Creating file...");
		Log.i("debugging", "Please Login");

		mWebView = new WebView(this);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows; U; Windows NT 6.2; en-US; rv:1.9) Gecko/2008062901 IceWeasel/3.0");
		mWebView.loadUrl("https://ma-andover.myfollett.com/aspen/logon.do");
		mWebView.addJavascriptInterface(new JavaScriptInterface(this), "HTMLOUT");
		setContentView(mWebView);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				String ht = "javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
				mWebView.loadUrl(ht);
				if(url.contains("home")) {
					Log.i("debugging", "Logged in");
					mWebView.loadUrl("https://ma-andover.myfollett.com/aspen/studentScheduleContextList.do?navkey=myInfo.sch.list");
					ht = "javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
					mWebView.loadUrl(ht);
					mWebView.destroy();
					mainUI();
				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
	}
<<<<<<< HEAD

=======
>>>>>>> 30134e2900a127bff6f37dae529bd1518fd839a8
	/*public void getGrades() {
		mWebView = new WebView(this);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows; U; Windows NT 6.2; en-US; rv:1.9) Gecko/2008062901 IceWeasel/3.0");
		mWebView.loadUrl("https://ma-andover.myfollett.com/aspen/logon.do");
		mWebView.addJavascriptInterface(new JavaScriptInterface(this), "HTMLOUT");
		setContentView(mWebView);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				String ht = "javascript:window.HTMLOUT.doThingsHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
				mWebView.loadUrl(ht);
				if(url.contains("home")) {
					Log.i("debugging", "Logged in");
					mWebView.loadUrl("https://ma-andover.myfollett.com/aspen/portalClassList.do");
					ht = "javascript:window.HTMLOUT.doThingsHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
					mWebView.loadUrl(ht);
					mWebView.destroy();
				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
	}*/
<<<<<<< HEAD
	public void mainView(View view) {
		setContentView(R.layout.activity_main);
		OnSwipeTouchListener on = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {timesSwiped--; mainView(null);}
			public void onSwipeRight() {timesSwiped++; mainView(null);}
		};
		TextView date = (TextView)findViewById(R.id.date);
		String monthDay = (new SimpleDateFormat("MMM dd").format(new java.util.Date()));
		date.setText(monthDay.substring(0, monthDay.length() - 2) + (Integer.valueOf(monthDay.substring(monthDay.length() - 2)) + timesSwiped));
		TextView dayRotation = (TextView)findViewById(R.id.day);
		dayRotation.setText("Day: " + sc.getDay());
		Button currentClass = (Button)findViewById(R.id.currentClass);
		currentClass.setText("Current Class: " + sc.getRoom(timesSwiped, 0));
		currentClass.setOnTouchListener(on);
		Button nextClass = (Button)findViewById(R.id.nextClass);
		nextClass.setText("Next Class: " + sc.getRoom(timesSwiped, 1));
		nextClass.setOnTouchListener(on);
		Button search = (Button)findViewById(R.id.search);
		search.setText("Search");
		Button toggle = (Button)findViewById(R.id.toggle);
		toggle.setText("Toggle");
		Button settings = (Button)findViewById(R.id.settings);
		settings.setText("Settings");
	}
	public void dayView(View view) {
		setContentView(R.layout.day_view);
		OnSwipeTouchListener on = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {timesSwiped--; dayView(null);}
			public void onSwipeRight() {timesSwiped++; dayView(null);}
		};
		TextView date = (TextView)findViewById(R.id.date);
		String monthDay = (new SimpleDateFormat("MMM dd").format(new java.util.Date()));
		date.setText(monthDay.substring(0, monthDay.length() - 2) + (Integer.valueOf(monthDay.substring(monthDay.length() - 2)) + timesSwiped));
		TextView dayRotation = (TextView)findViewById(R.id.day);
		dayRotation.setText("Day: " + (sc.getDay() + timesSwiped));
		Button p1 = (Button)findViewById(R.id.p1);
		Button p2 = (Button)findViewById(R.id.p2);
		Button p3 = (Button)findViewById(R.id.p3);
		Button p4 = (Button)findViewById(R.id.p4);
		Button p5 = (Button)findViewById(R.id.p5);
		p1.setText(sc.getSchedule(sc.getDay() + timesSwiped, 0));
		p2.setText(sc.getSchedule(sc.getDay() + timesSwiped, 1));
		p3.setText(sc.getSchedule(sc.getDay() + timesSwiped, 2));
		p4.setText(sc.getSchedule(sc.getDay() + timesSwiped, 3));
		p5.setText(sc.getSchedule(sc.getDay() + timesSwiped, 4));
		p1.setOnTouchListener(on);
		p2.setOnTouchListener(on);
		p3.setOnTouchListener(on);
		p4.setOnTouchListener(on);
		p5.setOnTouchListener(on);
		Button search = (Button)findViewById(R.id.search);
		search.setText("Search");
		Button toggle = (Button)findViewById(R.id.toggle);
		toggle.setText("Toggle");
		Button settings = (Button)findViewById(R.id.settings);
		settings.setText("Settings");

	}
=======
>>>>>>> 30134e2900a127bff6f37dae529bd1518fd839a8
}
