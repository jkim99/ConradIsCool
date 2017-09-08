package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.content.ContextWrapper;

import android.util.Log;
import android.webkit.JavascriptInterface;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class JSInterface {
	private Context context;
	private String html;

	public JSInterface(Context c) {
		context = c;
	}

	@JavascriptInterface
	public void processHTML(String html) {
		this.html = html;
		ArrayList<String> classes = parse(html);
		try {
			PrintWriter pw = new PrintWriter(new File(new ContextWrapper(context).getFilesDir() + "/schedule.txt"));
			pw.println("--Schedule--");
			for(String s : classes) {
				pw.println(s);
				Log.i("debugging", s);
			}
			Log.i("debugging", "Schedule File created");
			pw.close();
		}
		catch(Exception e) {
			Log.e("debugging", e.toString());
		}
	}

	public ArrayList<String> parse(String html) { //do a null and length check VALIDATION
		Utility.validateHTML(html);
		String[] lines = html.split("<");
		String line, str;
		ArrayList<String> ret = new ArrayList<String>(); //rename variables and allocate memory needed
		int x = -1;
		for(int i = 0; i < lines.length; i++) {
			line = lines[i];
			if(line.contains("1-1 Period")) {
				x = i;
				break;
			}
		}//VALIDATE
		for(int j = x + 21; j < lines.length; j++) {
			str = lines[j].replace("br>", "").replace("!--", "").replace("List view", "").replace("amp;", "");
			if(!str.contains(">"))
				ret.add(str);
		}
		ArrayList<String> end = new ArrayList<String>();
		String course;
		for(int i = 0; i < ret.size() - 4; i+=5) {
			course = ret.get(i) + "\n" + ret.get(i + 1) + "\n" + ret.get(i + 2) + "\n" + ret.get(i + 3) + "\n";
			if(!end.contains(course))
				end.add(course);
		}

		//correcting order
		String a, h;
		a = end.get(1);
		h = end.get(4);
		end.set(1, end.get(0));
		end.set(0, a);
		end.set(4, end.get(6));
		end.set(6, h);
		end.set(6, end.get(7));
		end.set(7, h);
		return end;
	}
}
