/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */

package com.example.jonat.scheduleapp2;

import android.content.Context;
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

				else if(url.contains("studentScheduleMatrix")) {
					aspenLogin.loadUrl("javascript:doParamSubmit(360, document.forms['scheduleMatrixForm'], '')");
				}

				//starts downloading info
				else if(url.contains("studentScheduleContext")) {
					setContentView(R.layout.logo_page);
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

		JSInterface(Context c) {
			context = c;
		}

		@JavascriptInterface
		public void processHTML(String html) {
			ArrayList<String> classes = parse(html);
			try {
				PrintWriter pw = new PrintWriter(new File(context.getFilesDir(), "schedule.txt"));
				pw.println(Utility.SCHEDULE_FILE_VERIFICATION_TAG);
				for(String s : classes) {
					pw.println(s);
					Log.i("debugging", s);
				}
				Log.i("debugging", "Schedule File created");
				pw.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		private ArrayList<String> parse(String html) {
			String[] lines = html.split("<");
			String line;
			int x = -1;
			ArrayList<String> scheduleInfoLines = new ArrayList<>();
			ArrayList<String> classes = new ArrayList<>();

			for(int i = 0; i < lines.length; i++) {
				if(lines[i].contains("Lunch")) {
					x = i;
					break;
				}
			}
			for(int i = x; i < lines.length; i++) {
				line = lines[i].replaceAll("\n", "").replaceAll("td>", "").replaceAll("/", "");
				if(line.contains("td nowrap=\"\">")) {
					scheduleInfoLines.add(line.replaceAll("td nowrap=\"\">", "").replaceAll("&nbsp;", "").replaceAll("&amp;", ""));
				}
			}
			for(int j = 0; j < scheduleInfoLines.size(); j+=7) {
				if(scheduleInfoLines.get(j + 1).equals(""))
					scheduleInfoLines.set(j + 1, "Lunch 0");
				if(!scheduleInfoLines.get(j + 3).equals("2"))
					classes.add(scheduleInfoLines.get(j) + "\n" + scheduleInfoLines.get(j + 2) + "\n" + scheduleInfoLines.get(j + 6) + "\n" + scheduleInfoLines.get(j + 5) + "\n" + scheduleInfoLines.get(j + 1) + "\n");
			}

			return classes;
		}
	}

}
