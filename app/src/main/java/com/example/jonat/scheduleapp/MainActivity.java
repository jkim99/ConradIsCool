package com.example.jonat.scheduleapp;

import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.*;
import android.widget.TextView;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

	private WebView mWebView = null;
	private ScheduleCheck sc;
	private File f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

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
		String day = "Day: " + sc.getDay();
		String period = sc.getRoom(0, 0);
		Log.i("debugging", period);
		setContentView(R.layout.activity_main);
		//TextView date = (TextView)findViewById(R.id.date);
		//date.setText(new SimpleDateFormat("MM-dd").format(new java.util.Date()));
		TextView dayRotation = (TextView)findViewById(R.id.day);
		dayRotation.setText(day);
		TextView currentClass = (TextView)findViewById(R.id.currentClass);
		currentClass.setText(period);
		TextView nextClass = (TextView)findViewById(R.id.nextClass);
		nextClass.setText(sc.getRoom(0, 1));
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
}
