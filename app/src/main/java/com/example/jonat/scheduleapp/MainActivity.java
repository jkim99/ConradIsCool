package com.example.jonat.scheduleapp;

import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.*;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.Button;
import android.widget.TextView;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.R.attr.slideEdge;

public class MainActivity extends AppCompatActivity {

	private WebView mWebView = null;
	private ScheduleCheck sc;
	private File f;
	private int timesSwiped;
	private DateFormat df;
	private OnSwipeTouchListener on;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		timesSwiped = 0;
		df = new SimpleDateFormat("MMM dd");
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
		mainView(null);
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

	public void constantView(View view) {
		final ViewGroup transitionsContainer = (ViewGroup)this.findViewById(android.R.id.content);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, timesSwiped + 365);
		TextView date = (TextView)findViewById(R.id.date);
		String monthDay = df.format(cal.getTime());
		date.setText(monthDay);
		TextView dayweek = (TextView)findViewById(R.id.dayweek);
		dayweek.setText((new SimpleDateFormat("EEE")).format(cal.getTime()).toUpperCase());
		TextView dayRotation = (TextView)findViewById(R.id.day);
		int d = sc.getDay(timesSwiped);
		dayRotation.setText(d > 0 ? " Day:  " + d : sc.exceptions(d));
		Button search = (Button)findViewById(R.id.search);
		search.setText("Search");
		Button toggle = (Button)findViewById(R.id.toggle);
		toggle.setText("Toggle");
		Button settings = (Button)findViewById(R.id.settings);
		settings.setText("Settings");
	}
	public void mainView(View view) {
		setContentView(R.layout.activity_main);
		final ViewGroup transitionsContainer = (ViewGroup)this.findViewById(android.R.id.content);
		on = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {
				timesSwiped++;
				Transition transition = new Slide(3);
				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
				mainView(null);
			}
			public void onSwipeRight() {
				timesSwiped--;
				Transition transition = new Slide(5);
				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
				mainView(null);
			}
		};
		constantView(view);
		Button currentClass = (Button)findViewById(R.id.currentClass);
		currentClass.setText("Current Class: " + sc.getRoom(timesSwiped, 0));
		currentClass.setOnTouchListener(on);
		Button nextClass = (Button)findViewById(R.id.nextClass);
		nextClass.setText("Next Class: " + sc.getRoom(timesSwiped, 1));
		nextClass.setOnTouchListener(on);
	}
	public void dayView(View view) {
		setContentView(R.layout.day_view);
		final ViewGroup transitionsContainer = (ViewGroup)this.findViewById(android.R.id.content);
		on = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {
				timesSwiped++;
				Transition transition = new Slide(3);
				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
				dayView(null);
			}
			public void onSwipeRight() {
				timesSwiped--;
				Transition transition = new Slide(5);
				TransitionManager.beginDelayedTransition(transitionsContainer, transition);
				dayView(null);
			}
		};
		constantView(view);
		Button p1 = (Button)findViewById(R.id.p1);
		Button p2 = (Button)findViewById(R.id.p2);
		Button p3 = (Button)findViewById(R.id.p3);
		Button p4 = (Button)findViewById(R.id.p4);
		Button p5 = (Button)findViewById(R.id.p5);
		p1.setText(sc.getSchedule(sc.getDay(timesSwiped), 0));
		p2.setText(sc.getSchedule(sc.getDay(timesSwiped), 1));
		p3.setText(sc.getSchedule(sc.getDay(timesSwiped), 2));
		p4.setText(sc.getSchedule(sc.getDay(timesSwiped), 3));
		p5.setText(sc.getSchedule(sc.getDay(timesSwiped), 4));
		p1.setOnTouchListener(on);
		p2.setOnTouchListener(on);
		p3.setOnTouchListener(on);
		p4.setOnTouchListener(on);
		p5.setOnTouchListener(on);

	}
}
