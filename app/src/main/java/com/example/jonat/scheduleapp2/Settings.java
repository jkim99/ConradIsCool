package com.example.jonat.scheduleapp2;

import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;


public class Settings {
	private final String SETTINGS_TAG = "settings";
	private final String CURRENT_VIEW_STRING = "current_view";
	private final String DAY_VIEW_STRING = "day_view";
	private final String MONTH_VIEW_STRING = "month_view";
	private String defaultView;
	private boolean pNotifications;
	private boolean dNotifications;

	public Settings() {
		this("current_view", true, true);
		Log.i(SETTINGS_TAG, "creating settings file");
	}

	public Settings(String defaultView, boolean pNotifications, boolean dNotifications) {
		this.defaultView = defaultView;
		this.pNotifications = pNotifications;
		this.dNotifications = dNotifications;
	}

	public Settings(File file) {
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
		defaultView = view;
	}

	public void setPNotification(boolean b) {
		pNotifications = b;
	}

	public void setDNotification(boolean b) {
		dNotifications = b;
	}

	public void writeFile(File file) {
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
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void readFile(File file) {
		try {
			Scanner scan = new Scanner(file);
			if(!verifyFile(file)) {
				return;
			}
			else {
				for(int i = 0; i < 5; i++) {
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
