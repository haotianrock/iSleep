package com.tian.sleep;

import java.util.Calendar;

public class MyTime 
{
	final static int DaysPerWeek = 7;
	final static int DaysPerMonth = 30;
	final static int WeeksPerYear = 52;
	final static int DaysPerYear = DaysPerWeek*WeeksPerYear;
	
	public static String getTimeHrMin (int hr, int min)
	{
		String strHr = String.format("%2dh:%2dm", hr, min);
		return strHr;
	}
	
	public static String getTimeHrMin (float hr)
	{
		int intHr = (int) Math.floor(hr);
		int intMin = (int) Math.floor((hr-intHr)*60);
		
		return getTimeHrMin(intHr, intMin);		
	}
	
	public static String getTimeHrMin (Calendar from, Calendar to)
	{
        int fdate = from.get(Calendar.DATE);
        int fhour = from.get(Calendar.HOUR_OF_DAY);
        int fmin = from.get(Calendar.MINUTE);
        
        int tdate = to.get(Calendar.DATE);
        int thour = to.get(Calendar.HOUR_OF_DAY);
        int tmin = to.get(Calendar.MINUTE);
        
        thour = thour + (tdate-fdate)*24;
        
        if(tmin<fmin)
        {
        	tmin = tmin+60;
        	thour--;
        }
        
        int min = tmin-fmin;       
        int hour = thour-fhour;
        
       return getTimeHrMin(hour, min);
        
	}
	
	public static int[] getTimeDifHrMin (Calendar from, Calendar to)
	{
        int fdate = from.get(Calendar.DATE);
        int fhour = from.get(Calendar.HOUR_OF_DAY);
        int fmin = from.get(Calendar.MINUTE);
        
        int tdate = to.get(Calendar.DATE);
        int thour = to.get(Calendar.HOUR_OF_DAY);
        int tmin = to.get(Calendar.MINUTE);
        
        thour = thour + (tdate-fdate)*24;
        
        if(tmin<fmin)
        {
        	tmin = tmin+60;
        	thour--;
        }
        
        int min = tmin-fmin;       
        int hour = thour-fhour;
        
       int[] HrMin = new int[2];
       HrMin[0] = hour;
       HrMin[1] = min;
       
       return HrMin;
        
	}
	
	public static String getTimeHrMin (Calendar c)
	{
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        
        return getTimeHrMin(hour, min);
	}
}
