/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */
package com.example.jonat.scheduleapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.io.File;

/*
 * SettingsActivity page handles user custom settingsHandler and saves them onto settingsHandler file
 */
public class SettingsActivity extends AppCompatActivity {

	private File settingsFile;
	private SettingsHandler settingsHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settingsFile = new File(this.getFilesDir(), "settings.txt");
		settingsHandler = new SettingsHandler(settingsFile);


		setContentView(R.layout.settings);
		Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		Switch periodicNotification = (Switch)findViewById(R.id.every_class_notification);
		periodicNotification.setChecked(settingsHandler.getPNotification());
		periodicNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				settingsHandler.setPNotification(isChecked);
			}
		});

		Switch dailyNotification = (Switch)findViewById(R.id.daily_notification);
		dailyNotification.setChecked(settingsHandler.getDNotification());
		dailyNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				settingsHandler.setDNotification(isChecked);
			}
		});

		Button sendLogs = (Button)findViewById(R.id.send_logs);
		sendLogs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendLogs();
			}
		});

		Button clear = (Button)findViewById(R.id.clear_button);
		final File scheduleFile = new File(this.getFilesDir(), "schedule.txt");
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File[] files = {scheduleFile};
				Utility.purge(files);
				startActivity(new Intent(SettingsActivity.this, MainActivity.class));
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		settingsHandler.writeFile(settingsFile);
	}

	public void sendLogs() {
		try {
			File logs = File.createTempFile("logs.txt", ".tmp", this.getExternalCacheDir());
			Runtime.getRuntime().exec("logcat -f " + logs.getAbsolutePath());

			String[] addressTo = {"jkim2018@k12.andoverma.us"};

			Intent mail = new Intent(Intent.ACTION_SEND);
			mail.setType("vnd.android.cursor.dir/email");
			mail.putExtra(Intent.EXTRA_EMAIL, addressTo);
			mail.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logs));
			mail.putExtra(Intent.EXTRA_SUBJECT, "Seven H App Version: " + MainActivity.version);
			startActivityForResult(Intent.createChooser(mail, "Send Logs..."), 0);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
