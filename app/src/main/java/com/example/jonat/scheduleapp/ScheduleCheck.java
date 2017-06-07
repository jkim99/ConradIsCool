package com.example.jonat.scheduleapp;

import android.content.*;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScheduleCheck {
	private String[] classes;
	private char[][] schedule;
    private Calendar cal;
    private Context context;
	public ScheduleCheck(Context con, ArrayList<String> c) {
        context = con;
		cal = new Calendar(con);
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
	public int getDay() {
		String date = new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
        String[] x = date.substring(0, 6).split("-");
        int[] y = {Integer.valueOf(x[0]), Integer.valueOf(x[1])};
        return cal.getDay(y[0], y[1]);
	}
	public String getNextClass() {
		return getRoom(1);
	}
	public String getCurrentClass() {
		return getRoom(0);
	}
	public String getRoom(int buff){
		String time = getTheTime();
		//Log.i("debugging", time);
		int day = getDay() - 1;
		int minutes = Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3));
		//Log.i("debugging", minutes + "");
		try {
			if(day < 0) {
				if(day == -1)
					return "No School";
				else if(day == -2)
					return "Half day";
				else if(day == -3)
					return "Snow Day";
				else if(day == -10)
					return "Error";
			}
			else {
				if(minutes >= 464 && minutes < 524)
					return getSchedule(schedule[day][buff]);
				else if(minutes >= 524 && minutes < 603)
					return getSchedule(schedule[day][1 + buff]);
				else if(minutes >= 603 && minutes < 667)
					return getSchedule(schedule[day][2 + buff]);
				else if(minutes >= 667 && minutes < 781)
					return getSchedule(schedule[day][3 + buff]);
				else if(minutes >= 781 && minutes < 845)
					return getSchedule(schedule[day][4 + buff]);
				else
					return "Hopefully you're not in school";
			}
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			return "No Class!";
		}
		return "null";
	}
	public String getSchedule(char c) {
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

}
