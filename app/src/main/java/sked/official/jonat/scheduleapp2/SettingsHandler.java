package sked.official.jonat.scheduleapp2;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class SettingsHandler {
	private final String SETTINGS_TAG = "settings";
	private boolean pNotifications;
	private boolean dNotifications;

	public SettingsHandler(File file) {
		readFile(file);
		MainActivity.periodicNotifications = pNotifications;
		MainActivity.dailyNotifications = dNotifications;
	}


	public boolean getPNotification() {
		return pNotifications;
	}

	public boolean getDNotification() {
		return dNotifications;
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
			pw.println("periodicNotifications:" + (pNotifications ? "on" : "off"));
			pw.println("dailyNotifications:" + (dNotifications ? "on" : "off"));
			pw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private boolean verifyFile(File file) {
		try {
			Scanner scan = new Scanner(file);
			return (scan.nextLine()).equals(Utility.SETTINGS_FILE_VERIFICATION_TAG);
		}
		catch(NoSuchElementException nsee) {
			return false;
		}
//		catch(FileNotFoundException fnfe) {
//			return false;
//		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void readFile(File file) {
		try {
			if(!verifyFile(file)) {
				Log.i(SETTINGS_TAG, "settings file corrupt or does not exist");
				writeFile(file);
			}
			Scanner scan = new Scanner(file);
			for(int i = 0; i < 4; i++) {
				String line = scan.nextLine();
				switch(line.substring(0, line.indexOf(":") + 1)) {
					case "dailyNotifications:":
						dNotifications = !(line.substring(line.indexOf(":") + 1).equals("off"));
						break;
					case "periodicNotifications:":
						pNotifications = !(line.substring(line.indexOf(":") + 1).equals("off"));
						break;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
