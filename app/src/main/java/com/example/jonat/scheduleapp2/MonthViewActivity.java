package com.example.jonat.scheduleapp2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.ContextWrapper;

public class MonthViewActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		displayRotationDay();
	}
	
	public boolean displayRotationDay() {
		ScheduleChecker check = new ScheduleChecker (new Context(), 
		//use schedulechecker to go through the instances
		
		int rotateDay = -1;
		for (int m=1; m<=12; m++){
			if (m==1 || m==3 || m==5 || m==8 || m==10 || m==12){
				for (int d=1; d<=31; d++){
					rotateDay = getDay(m, d);			
				}
			}
			else if (m==2){
				for (int d=1; d<=28; d++){ //needs to account for leap years
					rotateDay = getDay(m, d);
			}
			else {
				for (int d=1; d<=30; d++){
					rotateDay = getDay (m, d);
				}
			}
		}
	}
		
	private int getDay(int m, int d) {
		return cal.getDayRotation (m, d);
	}
		
	private Color getColor (int n){
		switch (n){
			case 1:
				
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case -1:
			case -2:
			case -3:
		}
	}

}
