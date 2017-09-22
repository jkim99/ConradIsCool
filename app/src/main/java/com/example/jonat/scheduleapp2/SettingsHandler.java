package com.example.jonat.scheduleapp2;

import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class SettingsHandler {
	private final String SETTINGS_TAG = "settings";
	private String defaultView;
	private boolean pNotifications;
	private boolean dNotifications;

	public SettingsHandler() {
		this("current_view", true, true);
		//Log.i(SETTINGS_TAG, "creating settings file");
	}

	public SettingsHandler(String defaultView, boolean pNotifications, boolean dNotifications) {
		this.defaultView = defaultView;
		this.pNotifications = pNotifications;
		this.dNotifications = dNotifications;
	}

	public SettingsHandler(File file) {
		readFile(file);
	}

	public String getDefaultView() {
		return defaultView;
	}

	public boolean getPNotification() {
		return pNotifications;
	}

	public boolean getDNotification() {
		return dNotifications;
	}

	public void setDefaultView(String view) {
		Log.d(SETTINGS_TAG, view);
		defaultView = view;
	}

	public void setPNotification(boolean b) {
		pNotifications = b;
	}

	public void setDNotification(boolean b) {
		dNotifications = b;
	}

	public void writeFile(File file) {
		if(defaultView == null)
			defaultView = "current_view";
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.println(Utility.SETTINGS_FILE_VERIFICATION_TAG);
			pw.println("defaultView:" + defaultView);
			pw.println("periodicNotifications:" + (pNotifications ? "on" : "off"));
			pw.println("dailyNotifications:" + (dNotifications ? "on" : "off"));
			pw.close();
		}
		catch(Exception e) {}
	}

	public boolean verifyFile(File file) {
		try {
			Scanner scan = new Scanner(file);
			return scan.nextLine().equals(Utility.SETTINGS_FILE_VERIFICATION_TAG);
		}
		catch(NoSuchElementException nsee) {
			return false;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void readFile(File file) {
		try {
			Scanner scan = new Scanner(file);
			if(!verifyFile(file)) {
				writeFile(file);
			}
			else {
				for(int i = 0; i < 4; i++) {
					String line = scan.nextLine();
					switch(line.substring(0, line.indexOf(":") + 1)) {
						case "defaultView:":
							defaultView = line.substring(line.indexOf(":") + 1);
							break;
						case "dailyNotifications:":
							dNotifications = !(line.substring(line.indexOf(":") + 1).equals("off"));
							break;
						case "periodicNotifications:":
							pNotifications = !(line.substring(line.indexOf(":") + 1).equals("off"));
							break;
						default:
							Log.i("checkSettings", "settings file corrupted or missing");
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
