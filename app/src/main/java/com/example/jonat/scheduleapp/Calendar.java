package com.example.jonat.scheduleapp;

import android.content.*;
import android.util.Log;
import java.io.*;
import java.util.*;

public class Calendar {
	private static boolean root;
	private File f;
	private Context con;
	private ArrayList<String> snowDays;
	private ArrayList<String> daysOff;
	//private ArrayList<String> manualOverride;
	//private ArrayList<String> halfdays;
	public Calendar(Context con) {
		this.con = con;
		snowDays = new ArrayList<String>();
		daysOff = new ArrayList<String>();
		addDayOff(21, 9);
		addDayOff(9, 10);
		addDayOff(9, 11);
		addDayOff(10, 11);
		addDayOff(23, 11);
		addDayOff(24, 11);
		addDayOff(25, 12);
		addDayOff(15, 1);
		addDayOff(19, 2);
		addDayOff(20, 2);
		addDayOff(21, 2);
		addDayOff(22, 2);
		addDayOff(23, 2);
		addDayOff(30, 3);
		addDayOff(16, 4);
		addDayOff(17, 4);
		addDayOff(18, 4);
		addDayOff(19, 4);
		addDayOff(20, 4);
		addDayOff(38, 5);
		createCalendar();
		//Admin a = new Admin(con);
		//a.login("jon", "kim");
		try {
			Scanner scan = new Scanner(f);
			//while(scan.hasNextLine())
			//	Log.i("debugging", scan.nextLine());
		}
		catch(IOException ioe) {
			Log.i("debugging", ioe.toString());
		}
	}
	public void createCalendar() {
		f = new File((new ContextWrapper(con)).getFilesDir() + "calendar.txt");
		if(!f.exists() && f.isDirectory()) {
			Log.i("debugging", "Creating file...");
			updateCalendar();
		}
	}
	public void updateCalendar() {
		try {
			f = new File((new ContextWrapper(con)).getFilesDir() + "calendar.txt");
			Log.i("debugging", "Updating file...");
			PrintWriter pw = new PrintWriter(f);
			int x = -3;
			int sat = 2;
			int sun = 3;
			int days;
			boolean halfday;
			for(int i = 9; i <= 12; i++) {
				days = 30 + (i + 1) % 2;
				for(int j = 1; j <= days; j++) {
					halfday = (i == 10 && j == 20) || (i == 12 && j == 8) || (i == 11 && j == 22);
					if(x > 8)
						x = 1;
					if(snowDays.contains(i + "/" + j))
						pw.println(i + "/" + j + " = " + "SSSSS");
					if(snowDays.contains(i + "/" + (j - 1))) {
						pw.println(i + "/" + j + " = " + x);
						x++;
					}
					else if(halfday)
						pw.println(i + "/" + j + " = " + "HHHHH");
					else if(j % 7 == sat || j % 7 == sun)
						pw.println(i + "/" + j + " = " + "XXXXX");
					else if(daysOff.contains(i + "/" + j))
						pw.println(i + "/" + j + " = " + "XXXXX");
					else {
						pw.println(i + "/" + j + " = " + x);
						x++;
					}
				}
				sat -= (i + 1) % 2 + 2;
				sun -= (i + 1) % 2 + 2;
				if(sat < 1 && sat != 0)
					sat += 7;
				if(sun < 1 && sun != 0)
					sun += 7;
			}
			for(int i = 1; i <= 6; i++) {
				days = 30 + i % 2;
				if(i == 2) {
					days = 28;
				}
				if(i == 3) {
					sat = 3;
					sun = 4;
				}
				if(i == 6)
					days = 27;
				for(int j = 1; j <= days; j++) {
					halfday = (i == 2 && j == 2) || (i == 3 && j == 16) || (i == 5 && j == 4) || (i == 6 && j == 1);
					if(x > 8)
						x = 1;
					if(snowDays.contains(i + "/" + j))
						pw.println(i + "/" + j + " = " + "SSSSS");
					if(snowDays.contains(i + "/" + (j - 1))) {
						pw.println(i + "/" + j + " = " + x);
						x++;
					}
					else if(halfday)
						pw.println(i + "/" + j + " = " + "HHHHH");
					else if(j % 7 == sat || j % 7 == sun)
						pw.println(i + "/" + j + " = " + "XXXXX");

					else if(daysOff.contains(i + "/" + j))
						pw.println(i + "/" + j + " = " + "XXXXX");
					else {
						pw.println(i + "/" + j + " = " + x);
						x++;
					}
				}
				sat -= i % 2 + 2;
				sun -= i % 2 + 2;
				if(sat < 1 && sat != 0)
					sat += 7;
				if(sun < 1 && sun != 0)
					sun += 7;
			}
			pw.close();
		}
		catch(IOException ioe) {
			Log.i("debugging", ioe.toString());
		}
	}

	public int getDay(int d, int m) {
		String date = m + "/" + (Integer.valueOf(d)).toString();
		String line = "";
		int x;
		try {
			Scanner scan = new Scanner(f);
			while(scan.hasNextLine()) {
				line = scan.nextLine();
				if(line.contains(date))
					break;
			}
			String ret = line.substring(line.indexOf(" = ") + 3);
			if(ret.contains("X"))
				return -1;
			if(ret.contains("H"))
				return -2;
			if(ret.contains("S"))
				return -3;
			else {
				x = Integer.valueOf(ret);
				if(x < 1)
					x = -10;
			}
			return x;
		}
		catch(IOException ioe) {
			Log.i("debugging", ioe.toString());
		}
		return -10;
	}
	public void addSnowDay(int d, int m) {
		snowDays.add(m + "/" + d);
		updateCalendar();
	}
	public void addDayOff(int d, int m) {
		daysOff.add(m + "/" + d);
	}
	public void addManualOverride(String username, String password) {
		if(!root) {
			root = (new Admin(con)).login(username, password);
		}
		if(root) {

		}
		//TODO: add admin privs
	}

}
