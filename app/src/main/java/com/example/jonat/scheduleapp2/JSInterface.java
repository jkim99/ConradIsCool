package com.example.jonat.scheduleapp2;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class JSInterface {
	private Context context;

	public JSInterface(Context c) {
		context = c;
	}

	@JavascriptInterface
	public void processHTML(String html) {
		/*TEST DATA*/
		//String testData = " Period A\n MA722-002\n INTRODUCTION TO CALCULUS\n Iascone, Florinda\n 339\n Period B\n PE410-006\n COMPET TEAM ACT\n Fazio, David\n Field3\n Period C\n MA947-001\n AP COMP SCIENCE JAVA\n DiBenedetto, Alison\n 354\n Period D\n EN222-002\n DRAMATIC LITERATURE\n Pellerin, Eric\n 112\n Period E\n MA951-001\n STATISTICS\n Ragucci, Stephanie\n 343\n Period F\n SC651-003\n HUMAN ANATOMY & PHYSIOLOGY\n Marx, Robert\n 229\n Period G\n SS032-002\n PSYCH & MNTL HLT\n Chachus, Michelle\n 253\n Period H\n HBlock\n H Block Enrichment\n Reidy, Minda\n 348\n";
		ArrayList<String> classes = new ArrayList<String>();
		//String[] temp = testData.split("\n");
		//for(int i = 0; i < temp.length - 4; i += 5)
			//classes.add(temp[i] + "\n" + temp[i + 1] + "\n" + temp[i + 2] + "\n" + temp[i + 3] + "\n");
		/*TEST DATA*/
		classes = parse(html);
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
		validateHTML(html);
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

	public boolean validateHTML(String html) {
		try {
			String[] lines = html.split("\n");
			return lines.length > 1000;
		}
		catch(Exception e) {
			Log.e("HTML PARSING", "Invalid html. Possibly due to user internet failure");
			return false;
		}
	}
}
