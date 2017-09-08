package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
				if(url.contains("home")) {
					aspenLogin.loadUrl("https://ma-andover.myfollett.com/aspen/studentScheduleContextList.do?navkey=myInfo.sch.list");
				}
				else if(url.contains("studentScheduleMatrix")) {
					aspenLogin.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
					(Toast.makeText(context, "Schedule downloaded successfully", Toast.LENGTH_SHORT)).show();
					startActivity(new Intent(AspenPage.this, MainActivity.class));
				}
			}
		};
		aspenLogin.setWebViewClient(webViewClient);
	}


}
