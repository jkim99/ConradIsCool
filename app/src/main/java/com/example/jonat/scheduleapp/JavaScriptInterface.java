package com.example.jonat.scheduleapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.webkit.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class JavaScriptInterface{

	private Context context;

	public JavaScriptInterface(Context c) {
		context = c;
	}

	@JavascriptInterface
	public void processHTML(String html) {
        String str = " Period A\n MA722-002\n INTRODUCTION TO CALCULUS\n Iascone, Florinda\n 339\n Period B\n PE410-006\n COMPET TEAM ACT\n Fazio, David\n Field3\n Period C\n MA947-001\n AP COMP SCIENCE JAVA\n DiBenedetto, Alison\n 354\n Period D\n EN222-002\n DRAMATIC LITERATURE\n Pellerin, Eric\n 112\n Period E\n MA951-001\n STATISTICS\n Ragucci, Stephanie\n 343\n Period F\n SC651-003\n HUMAN ANATOMY & PHYSIOLOGY\n Marx, Robert\n 229\n Period G\n SS032-002\n PSYCH & MNTL HLT\n Chachus, Michelle\n 253\n Period H\n HBlock\n H Block Enrichment\n Reidy, Minda\n 348\n";
        ArrayList<String> classes = new ArrayList<String>();

        /*JUST FOR TESTING*/
        String[] temp = str.split("\n");
        ArrayList<String> ret = new ArrayList<String>();
        for(String s : temp)
            ret.add(s);
        for(int i = 0; i < ret.size() - 4; i += 5)
            classes.add(ret.get(i) + "\n" + ret.get(i + 1) + "\n" + ret.get(i + 2) + "\n" + ret.get(i + 3) + "\n");

        /*JUST FOR TESTING*/

        Log.i("debugging", "Extracting data from aspen...");
        //classes = parse(html);
		try {
			Log.i("debugging", "Creating schedule file...");
			String path = (new ContextWrapper(context)).getFilesDir() + "/schedule.txt";
			File f = new File(path);
			PrintWriter pw = new PrintWriter(f);
			pw.println("--Schedule--");
			for(String s : classes) {
				pw.println(s);
			}
			pw.close();
		}
		catch(IOException ioe) {
			Log.i("debugging", ioe.toString());
		}
    }
    @JavascriptInterface
	public void doThingsHTML(String html) {
		try {
			String str = "";
			String[] lines = html.split("\n");
			for(String m : lines)
				if(m.contains("Performance"))
					Log.i("debugging", m);
			String line;
			int x = 0;
			for(int i = 0; i < lines.length; i++)
				if(lines[i].contains("Performance"))
					x = i;
			for(int j = x; j < lines.length; j++) {
				line = lines[j];
				if(!line.equals("") && !line.contains("<") && !line.contains(">") && line.length() != 1 && !line.contains("setInitial"))
					str += line + "\n";
			}
			Log.i("debugging", str);
		}
		catch(Exception e) {}
	}
	public ArrayList<String> parse(String html) {
		String[] lines = html.split("<");
		String line, str;
		ArrayList<String> ret = new ArrayList<String>();
		int x = -1;
		for(int i = 0; i < lines.length; i++) {
			line = lines[i];
			if(line.contains("Blue")) {
				x = i;
				break;
			}
		}
		for(int j = x + 21; j < lines.length; j++) {
			str = lines[j].replace("br>", "");
			if(!str.contains(">"))
				ret.add(str);
		}
		ArrayList<String> end = new ArrayList<String>();
		for(int i = 0; i < ret.size() - 4; i+=5) {
			end.add(ret.get(i) + "\n" + ret.get(i + 1) + "\n" + ret.get(i + 2) + "\n" + ret.get(i + 3) + "\n");
		}
		return end;
	}
}