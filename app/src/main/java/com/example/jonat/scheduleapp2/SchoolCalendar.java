package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.content.ContextWrapper;

import java.io.File;
import java.io.IOException;
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
		//TODO: grab calendar file from FTP server
	}

	public int getDayRotation(int day, int month) {
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

