package com.tian.sleep;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import android.os.Environment;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class FileUploader 
{
	final static String hostname = "arctic.cse.msu.edu";
	final static String username = "haotian";
	final static String password = "Haotian1004";
	final static int portnum = 22;
	
	JSch jsch = null;
	
	File f;
	String folder;
	
	
	
	public void send(File file, String folderName)
	{
		folder=folderName;
		f = file;
		
		new AsyncFileUploader().execute(this);
	}
	
	public void send()
	{
		jsch = new JSch();
		Session session;
		try 
		{
			session = jsch.getSession(username, hostname, portnum);
			session.setPassword(password);
		    session.setConfig("StrictHostKeyChecking", "no");
			session.setTimeout(5000);
			
			Log.e("UPLOAD", "Connecting to sftp server...");
		    session.connect();
		    
		    Channel channel = session.openChannel("sftp");
		    channel.connect();
		    
		    ChannelSftp channelSftp = (ChannelSftp)channel;
		    String dstFolder = "/user/haotian/iSleep/" + folder + "/";
		    channelSftp.cd(dstFolder);
		    
		    Log.e("UPLOAD", "isConnected:"+channelSftp.isConnected());
		    
		    try {
		    	Log.e("UPLOAD", f.getName());
		    	Log.e("UPLOAD","#channel: "+channel+"#sftp :"+channelSftp);
		    	FileInputStream in = new FileInputStream(f);
				channelSftp.put(in, dstFolder+f.getName(), null);
				
				/*if(in!=null)
				{
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}*/
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    /*if(channel!=null)
		    	channel.disconnect();
		    
		    if(session!=null)
		    	session.disconnect();*/

		    
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	   
	}
	
	public File createLogFile(String str)
	{
		File ReportFile = null;
	    File directory = new File 
	    (Environment.getExternalStorageDirectory().getPath()+"/iSleepFeedBackFiles");
	    if (!directory.exists()) { 
              	directory.mkdir(); 
	    } 

        try {
        	//user id and time as feedback file name
    	    String fileName = AppUI.USER_ID;
    	    fileName += "_"+getTimeString();
    	    
            ReportFile = new File(directory.getPath()+"/"+fileName+".txt");
            ReportFile.createNewFile();

            FileOutputStream fOut = new FileOutputStream(ReportFile);                   
            OutputStreamWriter OutWriter = new OutputStreamWriter(fOut);

            OutWriter.write(str);

            OutWriter.close();
            fOut.close();
            
            Log.e("UPLOAD", "File created"+ReportFile.toString());
            return ReportFile;

        } catch (Exception e) {
            e.printStackTrace();
        }//End of try/catch
        
        return null;
	}
	
	private String getTimeString()
	{
		Calendar curTime = Calendar.getInstance();
        int year = curTime.get(Calendar.YEAR);
        int month = curTime.get(Calendar.MONTH);
        int date = curTime.get(Calendar.DATE);
        int hour = curTime.get(Calendar.HOUR_OF_DAY);
        int min = curTime.get(Calendar.MINUTE);
        int sec = curTime.get(Calendar.SECOND);
        
        String timeStr = "";
        timeStr += Integer.toString(year)+"-";
        timeStr += Integer.toString(month)+"-";
        timeStr += Integer.toString(date)+"-";
        timeStr += Integer.toString(hour)+"-";
        timeStr += Integer.toString(min)+"-";
        timeStr += Integer.toString(sec);
        
        return timeStr;
	}
}
