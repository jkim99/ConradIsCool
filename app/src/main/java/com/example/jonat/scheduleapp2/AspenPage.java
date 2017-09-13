/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */

package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * AspenPage is used for the user to login to Aspen for the application
 * to download the schedule data to the phone. This is so the user does
 * not have to login to Aspen every time they want to check their schedule.
 */
public class AspenPage extends AppCompatActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context context = this;

		final WebView aspenLogin = new WebView(this);
		aspenLogin.getSettings().setJavaScriptEnabled(true);
		aspenLogin.getSettings().setUserAgentString("Mozilla/5.0 (Windows; U; Windows NT 6.2; en-US; rv:1.9) Gecko/2008062901 IceWeasel/3.0");
		aspenLogin.loadUrl("https://ma-andover.myfollett.com/aspen/logon.do");
		aspenLogin.addJavascriptInterface(new JSInterface(this), "HTMLOUT");
		setContentView(aspenLogin);
		WebViewClient webViewClient = new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				//redirects webview to schedule info page
				if(url.contains("home")) {
					aspenLogin.loadUrl("https://ma-andover.myfollett.com/aspen/studentScheduleContextList.do?navkey=myInfo.sch.list");
				}

				//starts downloading info
				else if(url.contains("studentScheduleMatrix")) {
					aspenLogin.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
					(Toast.makeText(context, "Schedule downloaded successfully", Toast.LENGTH_SHORT)).show();
					startActivity(new Intent(AspenPage.this, MainActivity.class));
				}
			}
		};
		aspenLogin.setWebViewClient(webViewClient);
	}

	/*
	 * JSInterface is the JavascriptInterface class that processes
	 * the HTML from Aspen and parses for the schedule information
	 */
	private class JSInterface {
		private Context context;
		private String html;

		JSInterface(Context c) {
			context = c;
		}

		@JavascriptInterface
		public void processHTML(String html) {
			this.html = html;
			ArrayList<String> classes = parse(html);
			try {
				PrintWriter pw = new PrintWriter(new File(new ContextWrapper(context).getFilesDir() + "/schedule.txt"));
				pw.println("--Schedule--");
				for(String s : classes) {
					pw.println(s);
					Log.i("debugging", s);
				}
				Log.i("debugging", "Schedule File created");
				pw.close();
			}
			catch(Exception e) {
				Log.e("debugging", e.toString());
			}
		}

		private ArrayList<String> parse(String html) {
			Utility.validateHTML(html);
			String[] lines = html.split("<");
			String line, course;
			ArrayList<String> scheduleInfoLines = new ArrayList<String>(); //rename variables and allocate memory needed
			ArrayList<String> scheduleInfoGrouped = new ArrayList<String>();
			int x = -1;
			for(int i = 0; i < lines.length; i++) {
				line = lines[i];
				if(line.contains("1-1 Period")) {
					x = i;
					break;
				}
			}
			for(int j = x + 21; j < lines.length; j++) {
				line = lines[j].replace("br>", "").replace("!--", "").replace("List view", "").replace("amp;", "");
				if(!line.contains(">"))
					scheduleInfoLines.add(line);
			}
			for(int i = 0; i < scheduleInfoLines.size() - 4; i+=5) {
				course = scheduleInfoLines.get(i) + "\n" + scheduleInfoLines.get(i + 1) + "\n" + scheduleInfoLines.get(i + 2) + "\n" + scheduleInfoLines.get(i + 3) + "\n";
				if(!scheduleInfoGrouped.contains(course))
					scheduleInfoGrouped.add(course);
			}

			//correcting order
			String a, h;
			a = scheduleInfoGrouped.get(1);
			h = scheduleInfoGrouped.get(4);
			scheduleInfoGrouped.set(1, scheduleInfoGrouped.get(0));
			scheduleInfoGrouped.set(0, a);
			scheduleInfoGrouped.set(4, scheduleInfoGrouped.get(6));
			scheduleInfoGrouped.set(6, h);
			scheduleInfoGrouped.set(6, scheduleInfoGrouped.get(7));
			scheduleInfoGrouped.set(7, h);
			return scheduleInfoGrouped;
		}
	}

}
