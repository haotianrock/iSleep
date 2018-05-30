package com.tian.sleep;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


public class DownloadFile extends AsyncTask<String, Integer, String>
{
    String folderName = "iSleepConfigurationFiles";
	String fileName = "configUpdate.txt";
	
	@Override
	protected String doInBackground(String... sUrl) 
	{
	    try 
	    {
	        URL url = new URL(sUrl[0]);
	        URLConnection connection = url.openConnection();
	        connection.connect();
	        
	        // this will be useful so that you can show a typical 0-100% progress bar
	        int fileLength = connection.getContentLength();    	
	    	
	        String directory = Environment.getExternalStorageDirectory().getPath()+"/"+folderName;
	        String downloadFile = directory + "/" + fileName;
	
	        // download the file
	        InputStream input = new BufferedInputStream(url.openStream());
	        OutputStream output = new FileOutputStream(downloadFile);
	        
	        InputStreamReader inReader = new InputStreamReader(input);
			LineNumberReader lnReader = new LineNumberReader(inReader);
	
			//read lines in config file
			String line;
			
			while(true)
			{
				line = lnReader.readLine();
			
				if(line==null)
					break;
				
				Log.e("UPDATE", line);
				
				if(line.charAt(0) == '#')
				{
					if(line.equals("#version"))
					{
						line = lnReader.readLine();
						Log.e("UPDATE", line);
						
						UpdateConfigInfo.newVersion = Integer.valueOf(line);
					}
				
				}
			}
			
			UpdateConfigInfo.isReady = true;

	        output.flush();
	        output.close();
	        input.close();
	    } 
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    }
	    
	    return null;
	}
}


