package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleChecker {
	private String[] classes;
	private char[][] schedule;
	private SchoolCalendar schoolCalendar;
	private Context context;

	public ScheduleChecker(Context con, ArrayList<String> classes) {
		context = con;
		schoolCalendar = new SchoolCalendar(context);
		schedule = new char[8][5];
		this.classes = new String[8];
		for(int i = 0; i < classes.size(); i++)
			this.classes[i] = classes.get(i);
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
	}

	public String getTime() {
		String time = new java.sql.Time(System.currentTimeMillis()).toString();
		return time.substring(0, time.length() - 3);
	}

	public int getSchoolDayRotation(int off) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, off);
		String date = new SimpleDateFormat("MM dd").format(c.getTime());
		return schoolCalendar.getDayRotation(Integer.valueOf(date.substring(0, 2)), Integer.valueOf(date.substring(3, 5)));
	}

	public int getCurrentPeriod(int minutes) {
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

	public String getClass(int day, int period) {
		return day < 0 ? exceptions(day) : classes[(int)(getBlock(day, period)) - 65];
	}

	public String getRoom(int dayAfter, int periodAfter) {
		String time = getTime();
		int day = getSchoolDayRotation(dayAfter);
		int minutes = Integer.valueOf(time.substring(0, 2)) * 60 + Integer.valueOf(time.substring(3));
		try {
			return day < 0 ? exceptions(day) : classes[(int)(getBlock(day, getCurrentPeriod(minutes) + periodAfter)) - 65];
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			return "No Class!";
		}
	}

	public char getBlock(int day, int period) {
		try {
			return schedule[day][period];
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			return 'X';
		}
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
