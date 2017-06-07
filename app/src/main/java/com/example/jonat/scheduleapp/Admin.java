package com.example.jonat.scheduleapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.StrictMode;
import android.util.Log;
import java.io.*;
import java.util.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class Admin {
	private Context context;
	public Admin(Context c) {
		context = c;
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	public File getHash() {
		FTPClient con = null;
		try {
			File hashes = new File((new ContextWrapper(context)).getFilesDir() + "/hashes.txt");
			con = new FTPClient();
			con.connect("ftp.ahswork.info");
			if(con.login("jkim@ahswork.info", "java987")) {
				con.enterLocalPassiveMode();
				con.setFileType(FTP.BINARY_FILE_TYPE);
				OutputStream out = new FileOutputStream(hashes);
				Log.i("debugging", con.retrieveFile("hash.txt", out) ? "Verification started..." : "Error");
				out.close();
				return hashes;
			}
			else {
				Log.i("debugging", "error");
			}
		}
		catch(Exception e) {
			Log.i("debugging", e.toString());
		}
		return null;
	}
	public boolean login(String user, String pass) {
		ArrayList<String> list = new ArrayList<String>();
		File f = getHash();
		try {
			Scanner scan = new Scanner(f);
			while(scan.hasNextLine())
				list.add(scan.nextLine());
		}
		catch(IOException ioe) {
			Log.i("debugging", ioe.toString());
		}
		String str = user + pass;
		for(String x : list) {
			if(x.contains("" + str.hashCode())) {
				Log.i("debugging", "Login Success");
				return true;
			}
		}
		return false;
	}

}
