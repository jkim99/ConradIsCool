package com.example.jonat.scheduleapp2;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/*
 * Created by jonat on 9/13/2017.
 */

public class LunchChecker {
	private static String[][] lunchSchedule;

	public LunchChecker(File file) {
		setLunchSchedule(file);
	}

	private void setLunchSchedule(File file) {
		String[] lunchGroups;
		String lunchCode;
		String lastUpdated;

		try {
			Scanner scan = new Scanner(file);

			String verify = scan.nextLine();
			if(!verify.substring(0, 8).equals("CONFIRM:"))
				Log.e("lunch", "lunch schedule file invalid");
			lunchCode = verify.substring(8, verify.indexOf('-'));
			lastUpdated = verify.substring(verify.indexOf("-LU") + 3, verify.lastIndexOf('-'));

			Log.d("lunch", "Lunch Code: " + lunchCode);
			Log.d("lunch", "Last Updated: " + lastUpdated);

			int groups = Integer.valueOf(verify.substring(verify.lastIndexOf('-') + 1, verify.lastIndexOf('x')));
			int teachersPerGroup = Integer.valueOf(verify.substring(verify.lastIndexOf('x') + 1));
			lunchGroups = new String[groups];
			lunchSchedule = new String[groups][teachersPerGroup];

			String line = scan.nextLine();
			int x = 0;
			int y = 0;
			while(scan.hasNextLine()) {
				if(line.substring(0, 7).equals("Group: ")) {
					lunchGroups[x] = line.substring(7);
					x++;
					y = 0;
				}
				else {
					lunchSchedule[x - 1][y] = line;
					y++;
				}
				line = scan.nextLine();
			}
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {
			Log.e("lunch", aioobe.toString());
		}
		catch(IOException ioe) {
			Log.e("lunch", ioe.toString());
		}
	}

	public int getLunchBlock(String teacher) {
		for(int i = 0; i < lunchSchedule.length; i++) {
			for(int j = 0; j < lunchSchedule[i].length; j++) {
				if(lunchSchedule[i][j] != null && lunchSchedule[i][j].equals(teacher))
					return i + 1;
			}
		}
		return -1;
	}

}
