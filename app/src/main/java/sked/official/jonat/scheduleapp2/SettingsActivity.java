/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */
package sked.official.jonat.scheduleapp2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
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

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		settingsHandler.writeFile(settingsFile);
	}

}
