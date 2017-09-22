package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EnrichingStudents extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String TAG = "gAuth";
		final Context context = this;

		final FirebaseAuth auth;
		FirebaseAuth.AuthStateListener authStateListener;

		auth = FirebaseAuth.getInstance();
		authStateListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if(user != null) {
					Log.d(TAG, "logged in: " + user.getUid());
				}
				else {
					Log.d(TAG, "#naw");
				}
			}
		};

//		final WebView enrichingStudents = new WebView(this);
//		enrichingStudents.getSettings().setJavaScriptEnabled(true);
//		enrichingStudents.loadUrl("https://app.enrichingstudents.com/Login/index");
//		enrichingStudents.addJavascriptInterface(new JSInterface(this), "HTMLOUT");
//		setContentView(enrichingStudents);
//		enrichingStudents.setWebViewClient(new WebViewClient() {
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				super.onPageFinished(view, url);
//				//if(url.contains("StudentDashboard"))
//					view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//			}
//		});
	}

	private class JSInterface {
		private Context context;
		private String html;

		JSInterface(Context c) {
			context = c;
		}

		@JavascriptInterface
		public void processHTML(String html) {
			Log.i("html", html);
		}
	}
}
