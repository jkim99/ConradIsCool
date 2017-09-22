/*
 * Copyright (C) 2017 copyright things
 *
 * @author Jonathan S. Kim
 * @version Beta 1.1
 * @since 7/19/2017
 */
package com.example.jonat.scheduleapp2;

import android.util.Log;

import java.util.ArrayList;

/*
 * ScheduleChecker is an object that handles the information within the saved
 * schedule file created by the JSInterface. All methods such
 */
public class ScheduleChecker {
	private String[] classes;
	private char[][] schedule;
	private String[] lunches;

	public ScheduleChecker(ArrayList<String> classes) {
		schedule = new char[8][5];
		this.classes = new String[8];
		lunches = new String[5];
		for(int i = 0; i < classes.size(); i++) {
			String[] lines = classes.get(i).split("\n");
			char block = lines[1].charAt(0);
			this.classes[block - 65] = lines[0] + "\n" + lines[3] + "\n" + lines[2] + "\n";
			if(block > 66 && block < 72)
				lunches[block - 67] = lines[4].replace("Lunch ", "Lunch: ");
		}
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

	public int getCurrentPeriod(int minutes, int periodAfter) {
		if(minutes >= Utility.SEVEN_O_CLOCK && minutes <= Utility.PERIOD_1_BELL)
			return 5 - periodAfter * 5;
		else if(minutes >= Utility.PERIOD_1_BELL && minutes < Utility.PERIOD_2_BELL)
			return periodAfter;
		else if(minutes >= Utility.PERIOD_2_BELL && minutes < Utility.PERIOD_3_BELL)
			return 1 + periodAfter;
		else if(minutes >= Utility.PERIOD_3_BELL && minutes < Utility.PERIOD_4_BELL)
			return 2 + periodAfter;
		else if(minutes >= Utility.PERIOD_4_BELL && minutes < Utility.PERIOD_5_BELL)
			return 3 + periodAfter;
		else if(minutes >= Utility.PERIOD_5_BELL && minutes < 845)
			return 4 + periodAfter;
		else
			return 5;
	}

	/** @param day is the day rotation of the week
	 *  @param period is the period of the day
	 *  @return explanation for exception within the schedule */
	public String exceptions(int day, int period) {
		if(day < -20 && day > -30)
			return halfDayHandler(Math.abs(day % 10), period);
		switch(day) {
			case -1: //convert to enum classes
				return "No School";
			case -2:
				return "Exam";
			case -3:
				return "Snow Day";
			case -10:
				return "Error";
			default:
				return "null";
		}
	}

	public String getClass(int off, int period) {
		int day = Utility.getSchoolDayRotation(off);
		return period == 5 ? "No Class!" : (day < 0 ? exceptions(day, period) : classes[(int)(getBlock(day - 1, period)) - 65]); //make more readable
	}

	public String getClassName(int off, int period) {
		String course = getClass(off, period);
		return course.split("\n")[0];
	}

	public String getLunchBlock(int off, int period) {
		return lunches[getBlock(Utility.getSchoolDayRotation(off), period) - 67];
	}


	public char getBlock(int day, int period) {
		return period == -10 ? 'X' : schedule[day][period];
	}

	public String halfDayHandler(int day, int period) {
		char[][] halfDay = new char[7][3];
		halfDay[0][0] = 'A';
		halfDay[0][1] = 'B';
		halfDay[0][2] = 'C';
		halfDay[1][0] = 'A';
		halfDay[1][1] = 'E';
		halfDay[1][2] = 'P'; //pep rally
		halfDay[2][0] = 'C';
		halfDay[2][1] = 'B';
		halfDay[2][2] = 'F';
		halfDay[3][0] = 'A';
		halfDay[3][1] = 'B';
		halfDay[3][2] = 'C';
		halfDay[4][0] = 'D';
		halfDay[4][1] = 'E';
		halfDay[4][2] = 'A';
		halfDay[5][0] = 'B';
		halfDay[5][1] = 'D';
		halfDay[5][2] = 'F';
		halfDay[6][0] = 'G';
		halfDay[6][1] = 'B';
		halfDay[6][2] = 'D';
		try {
			int classIndex = (int)halfDay[day - 1][period] - 65;
			return period > 2 ? "No Class!" : (classIndex != 15 ? classes[classIndex] : "Pep Rally");
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			return "No Class";
		}
	}

}
