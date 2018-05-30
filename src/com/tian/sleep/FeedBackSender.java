package com.tian.sleep;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

public class FeedBackSender 
{
	String emailUploadAddress_feedback = "upload.feedbac.1ixq6pngh5@u.box.com";
	
	String emailUploadAddress_psqi = "upload.psqi.63glbu15jb@u.box.com";
	
	String emailUploadAddress_data = "upload.sleep_d.fsilo7bjl0@u.box.com";
	
	String content = null;
	
	public void setContent(String str)
	{
		content = str;
	}
	
	public void send(String type)
	{
		if(content==null)
			return;
		
		String emailUploadAddress = null;

		
		if(type.equals("data"))
			emailUploadAddress = emailUploadAddress_data;
		else if(type.equals("psqi"))
			emailUploadAddress = emailUploadAddress_psqi;
		else if(type.equals("feedback"))
			emailUploadAddress = emailUploadAddress_feedback;
		else 
			return;
		
		
		//add gps info in the first line
		double latitude = 0;
		double longitude = 0;
		String gpsStr = "";
		
		if(AppUI.bestLastLocation!=null)
		{
			latitude = AppUI.bestLastLocation.getLatitude();
			longitude = AppUI.bestLastLocation.getLongitude();
			gpsStr = gpsStr + "#gps\n" + latitude + "\n" + longitude + "\n";
			content = gpsStr + content;
		}

		
		Mail m = new Mail("sleep.quality.monitor@gmail.com", "haotiansleep"); 
		
		m.setTo(emailUploadAddress);
		 
	    m.setBody("Email body."); 
	   
	    //user id and time as feedback file name
	    String fileName = AppUI.USER_ID;
	    fileName += "_"+getTimeString();
	    
	   
	    File logFile = createLogFile(fileName, content);
        
        try {
			m.addAttachment(logFile.getPath(), fileName+".txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
        new AsyncMailSender().execute(m);

	}
	
	private File createLogFile(String fileName, String str)
	{
		File ReportFile = null;
	    File directory = new File 
	    (Environment.getExternalStorageDirectory().getPath()+"/iSleepFeedBackFiles");
	    if (!directory.exists()) { 
              	directory.mkdir(); 
	    } 

        try {
            ReportFile = new File(directory.getPath()+"/"+fileName+".txt");
            ReportFile.createNewFile();

            FileOutputStream fOut = new FileOutputStream(ReportFile);                   
            OutputStreamWriter OutWriter = new OutputStreamWriter(fOut);

            OutWriter.write(str);

            OutWriter.close();
            fOut.close();
            
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
