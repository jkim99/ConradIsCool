package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.StrictMode;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class SchoolCalendar {
	public static File calendar;
	private Context context;
	private ArrayList<String> snowDays;
	private ArrayList<String> daysOff;

	public SchoolCalendar(Context con) {
		context = con;
		snowDays = new ArrayList<>();
		daysOff = new ArrayList<>();

		calendar = new File((new ContextWrapper(con)).getFilesDir() + "/calendar.txt");
		if(!calendar.exists())
			updateCalendar();
	}
	public void updateCalendar() {
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //check what this does exactly
			StrictMode.setThreadPolicy(policy);
			FileUtils.copyURLToFile(new URL("https://pastebin.com/raw/BJQR6gHx"), calendar);
			Scanner scan = new Scanner(calendar);
			//while(scan.hasNextLine())
				//Log.i("debugging", scan.nextLine());
		}
		catch(Exception e) {
			Log.i("debugging", e.toString());
		}
	}

	public static int getDayRotation(int month, int day) {
		String searchDate = month + "/" + day;
		String returnValue;
		try {
			Scanner scan = new Scanner(calendar);
			String line = "";
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				if(line.contains(searchDate))
					break;
			}
			returnValue = line.substring(line.indexOf(" = ") + 3);
			if(returnValue.contains("HHHH"))
				return -20 - Integer.valueOf(line.substring(line.indexOf(" = ") + 7));
			switch(returnValue) {
				case "XXXXX":
					return -1; //const the error values
				case "EEEEE":
					return -2;
				case "SSSSS":
					return -3;
				case "A":
				case "B":
				case "C":
					return -4;
				default:
					return Integer.valueOf(returnValue) >= 1 ? Integer.valueOf(returnValue) : -10; //consider logging if error in calendar
			}
		}
		catch(IOException ioe) {
			return -10;
		}
	}

}

