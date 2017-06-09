package com.example.jonat.scheduleapp;

import android.content.*;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScheduleCheck {
	private String[] classes;
	private char[][] schedule;
    private SchoolCalendar cal;
    private Context context;
	public ScheduleCheck(Context con, ArrayList<String> c) {
        context = con;
		cal = new SchoolCalendar(con);
        schedule = new char[8][5];
		schedule[0][0] = 'A';
		schedule[0][1] = 'C';
		schedule[0][2] = 'H';
		schedule[0][3] = 'E';
		schedule[0][4] = 'G';
		schedule[1][0] = 'B';
		schedule[1][1] = 'D';
		schedule[1][2] = 'F';
		schedule[1][3] = 'G';
		schedule[1][4] = 'E';
		schedule[2][0] = 'A';
		schedule[2][1] = 'H';
		schedule[2][2] = 'D';
		schedule[2][3] = 'C';
		schedule[2][4] = 'F';
		schedule[3][0] = 'B';
		schedule[3][1] = 'A';
		schedule[3][2] = 'H';
		schedule[3][3] = 'G';
		schedule[3][4] = 'E';
		schedule[4][0] = 'C';
		schedule[4][1] = 'B';
		schedule[4][2] = 'F';
		schedule[4][3] = 'D';
		schedule[4][4] = 'G';
		schedule[5][0] = 'A';
		schedule[5][1] = 'H';
		schedule[5][2] = 'E';
		schedule[5][3] = 'F';
		schedule[5][4] = 'C';
		schedule[6][0] = 'B';
		schedule[6][1] = 'A';
		schedule[6][2] = 'D';
		schedule[6][3] = 'E';
		schedule[6][4] = 'G';
		schedule[7][0] = 'C';
		schedule[7][1] = 'B';
		schedule[7][2] = 'H';
		schedule[7][3] = 'F';
		schedule[7][4] = 'D';
		classes = new String[8];
		for(int i = 0; i < c.size(); i++)
			classes[i] = c.get(i);
	}
	public String getTheTime() {
		java.sql.Time t = new java.sql.Time(System.currentTimeMillis());
		String time = t.toString();
		return time.substring(0, time.length() - 3);
	}
	public int getDay(int off) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, off);
		String date = new SimpleDateFormat("MM dd").format(c.getTime());
		int[] y = {Integer.valueOf(date.substring(0, 2)), Integer.valueOf(date.substring(3, 5))};
		return cal.getDay(y[1], y[0]);
	}
	public int getDay() {
		return getDay(0);
	}
	public int getPeriod(int minutes) {
		if(minutes >= 464 && minutes < 524)
			return 0;
		else if(minutes >= 524 && minutes < 603)
			return 1;
		else if(minutes >= 603 && minutes < 667)
			return 2;
		else if(minutes >= 667 && minutes < 781)
			return 3;
		else if(minutes >= 781 && minutes < 845)
			return 4;
		else
			return -10;
	}
	public String getRoom(int dayAfter, int periodAfter){
		String time = getTheTime();
		int day = getDay(dayAfter);
		int minutes = Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3));
		try {
			if(day < 0) {
				return exceptions(day);
			}
			else {
				return getSchedule(day - 1, getPeriod(minutes) + periodAfter);
			}
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			return "No Class!";
		}
	}
	public String getSchedule(int day, int p) {
		if(day < 0)
			return exceptions(day);
		char c = schedule[day - 1][p];
		try {
			return classes[(int) c - 65];
		}
		catch(Exception e) {
			return "null";
		}
	}
	public void addSnowDay() {
		String date = new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
		String[] x = date.substring(0, 6).split("-");
		int[] y = {Integer.valueOf(x[0]), Integer.valueOf(x[1])};
		cal.addSnowDay(y[0], y[1]);
	}
	public String exceptions(int day) {
		if(day == -1)
			return "No School";
		else if(day == -2)
			return "Half day";
		else if(day == -3)
			return "Snow Day";
		else if(day == -10)
			return "Error";
		else
			return "null";
	}
}
