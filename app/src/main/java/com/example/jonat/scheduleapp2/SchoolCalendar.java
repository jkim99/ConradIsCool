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
	private File calendar;
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
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			FileUtils.copyURLToFile(new URL("https://pastebin.com/raw/9DWb9b74"), calendar);
			Scanner scan = new Scanner(calendar);
			while(scan.hasNextLine())
				Log.i("debugging", scan.nextLine());
		}
		catch(Exception e) {
			Log.i("debugging", e.toString());
		}
	}

	public int getDayRotation(int month, int day) {
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
			switch(returnValue) {
				case "XXXXX":
					return -1;
				case "HHHHH":
					return -2;
				case "SSSSS":
					return -3;
				default:
					return Integer.valueOf(returnValue) >= 1 ? Integer.valueOf(returnValue) : -10;
			}
		}
		catch(IOException ioe) {
			return -10;
		}
	}

}

