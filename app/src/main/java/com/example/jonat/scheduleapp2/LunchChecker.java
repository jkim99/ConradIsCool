package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.util.Log;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/*
 * Created by jonat on 9/13/2017.
 */

public class LunchChecker {
	private String[][] lunchSchedule;

	public LunchChecker(Context context, char block) {
		Log.i("lunch", "const");
		File file = new File(context.getFilesDir(), "lunch" + block + ".csv");
		setLunchSchedule(file);
	}

	private void setLunchSchedule(File file) {
		try {
			Reader in = new FileReader(file);
			CSVParser parser = new CSVParser(in, CSVFormat.RFC4180);
			List<CSVRecord> records = parser.getRecords();
			lunchSchedule = new String[records.size()][];
			for(int i = 0; i < records.size(); i++) {
				CSVRecord record = records.get(i);
				lunchSchedule[i] = new String[record.size()];
				for(int j = 0; j < record.size(); j++)
					lunchSchedule[i][j] = record.get(j);
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public int getLunchBlock(String teacher) {
		try {
			for(int i = 0; i < lunchSchedule.length; i++) {
				for(int j = 0; j < lunchSchedule[i].length; j++) {
					if(lunchSchedule[i][j] != null && lunchSchedule[i][j].equals(teacher))
						return j + 1;
				}
			}
			return -1;
		}
		catch(NullPointerException npe) {
			return -1;
		}
	}

}
