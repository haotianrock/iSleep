package com.tian.sleep;

import java.io.File;
import java.util.LinkedList;

public class HistoryData 
{
	public static double overallEff;
	
	public static double overallEffWeek;
	
	
	/********* sleep efficiency ***********/
	public static double[] effWeek = null;
	public static double[] effMonth = null;
	public static double[] effYear = null;
	
	/********* sleep Events ***********/
	public static double[] evtMoveWeek = null;
	public static double[] evtSnoreWeek = null;
	public static double[] evtCoughWeek = null;
	
	public static double[] evtMoveMonth = null;
	public static double[] evtSnoreMonth = null;
	public static double[] evtCoughMonth = null;
	
	public static double[] evtMoveYear = null;
	public static double[] evtSnoreYear = null;
	public static double[] evtCoughYear = null;
	
	/********* back bone arr ***********/
	public static double[] evtCough = null;
	public static double[] evtSnore = null;
	public static double[] evtMove = null;
	public static double[] eff = null;

	
	/***** last night ****/
	public static double[] evtMoveLN = null;
	public static double[] evtSnoreLN = null;
	public static double[] evtCoughLN = null;
	
	public static double[] stateSleepLN = null;
	
	public static float evalScore = 0.5f;	
	
	public static String uploadString = null;
	
	
	public static void setLastNight(double[] moveList, double[] snoreList, double[] coughList, double[] stateList)
	{
		evtMoveLN = moveList;
		evtSnoreLN = snoreList;
		evtCoughLN = coughList;
		stateSleepLN = stateList;
		
		
		/******** prepare upload string ***********/
		int len;
		String str = "";
		
		//#TIME
		/*String tDurTimeHrMin = MyTime.getTimeHrMin(AppUI.startTime, AppUI.endTime);
		String tStartTimeHrMin = MyTime.getTimeHrMin(AppUI.startTime);
		str+="#time"+"\n";
		str+=tStartTimeHrMin+"\n";
		str+=tDurTimeHrMin+"\n";*/
		
		//#state
		str+="#state"+"\n";
		len = stateSleepLN.length;
		str+=len+"\n";
		for(int i=0; i<len; i++)
			str+=stateSleepLN[i]+"\n";
		
		//#move
		str+="#move"+"\n";
		len = evtMoveLN.length;
		str+=len+"\n";
		for(int i=0; i<len; i++)
			str+=evtMoveLN[i]+"\n";
		//#snore
		str+="#snore"+"\n";
		len = evtSnoreLN.length;
		str+=len+"\n";
		for(int i=0; i<len; i++)
			str+=evtSnoreLN[i]+"\n";
		
		//#cough
		str+="#cough"+"\n";
		len = evtCoughLN.length;
		str+=len+"\n";
		for(int i=0; i<len; i++)
			str+=evtCoughLN[i]+"\n";
		
		
		
		
		uploadString = str;
		
		
		if(uploadString!=null && AppUI.isUploadDataWhenStop==true)
		{
			FileUploader fu = new FileUploader();
			File f = fu.createLogFile(uploadString);
			fu.send(f, "data");
		}
		
		
		/******** compute overall eff *********/
		len = stateSleepLN.length;
		int sleepCnt = 0;
		for(int i=0; i<len; i++)
		{
			if(stateList[i]>0)
				sleepCnt++;
		}
		overallEff = (double)sleepCnt/(double)len;
		
		
		/******** update the back-bone arr *********/
		double dayScoreMove = 0;
		double dayScoreCough = 0;
		double dayScoreSnore = 0;
		
		len = moveList.length;
		for(int i=0; i<len-1; i++)
		{
			if(evtMoveLN[i]>0.1)
				dayScoreMove++;
			
			if(evtCoughLN[i]>0.1)
				dayScoreCough++;
			
			if(evtSnoreLN[i]>0.1)
				dayScoreSnore++;
		}	
		
		dayScoreMove = dayScoreMove/(len/3);
		if(dayScoreMove>1)
			dayScoreMove = 1;
		
		dayScoreCough = dayScoreCough/(len/10);
		if(dayScoreCough>1)
			dayScoreCough = 1;
		
		dayScoreSnore = dayScoreSnore/(len/5);
		if(dayScoreSnore>1)
			dayScoreSnore = 1;
		
		len = MyTime.DaysPerYear;
		for(int i=0; i<len-1; i++)
		{
			evtCough[i] = evtCough[i+1];
			evtSnore[i] = evtSnore[i+1];
			evtMove[i] = evtMove[i+1];
			eff[i] = eff[i+1];
		}		
		evtCough[len-1] = dayScoreCough;
		evtSnore[len-1] = dayScoreSnore;
		evtMove[len-1] = dayScoreMove;
		eff[len-1] = overallEff;
		
	}
	
	
	public static void groupData(double[] data, double[] week, double[] month, double[] year)
	{
		int lenData = data.length;
		int s = lenData;
		
		//find start index
		for(int i=0; i<lenData; i++)
		{
			if(data[i]!=-2)
			{
				s = i;
				break;
			}
		}
		
		
		//week
		for(int i=0; i<MyTime.DaysPerWeek; i++)
		{
			if(s+i<lenData)
				week[i] = data[s+i];
			else
				week[i] = -2;
		}
		
		//month
		for(int i=0; i<MyTime.DaysPerMonth; i++)
		{
			if(s+i<lenData)
				month[i] = data[s+i];
			else
				month[i] = -2;
		}
		
		//year
		int st, end;
		double weekValue;
		for(int i=0; i<MyTime.WeeksPerYear; i++)
		{
			st = s+i*MyTime.DaysPerWeek;
			end = st+MyTime.DaysPerWeek-1;
			
			if(st>=lenData || end>=lenData)
				weekValue = -2;
			else
			{
				weekValue = 0;
				for(int j = st; j<=end; j++)
				{
					weekValue+=data[j];
				}
				weekValue/=MyTime.DaysPerWeek;
			}
			
			year[i] = weekValue;
		}
	}
	
	
	public static void loadHistoryData(ConfigurationManager cm)
	{
		evtCough = cm.coughList;
		evtSnore = cm.snoreList;
		evtMove = cm.moveList;
		eff = cm.effList;
		
		if(evtCough==null||evtSnore==null||evtMove==null||eff==null)
			return;
		
		//EFF
		effWeek = new double[MyTime.DaysPerWeek];
		effMonth = new double[MyTime.DaysPerMonth];		
		effYear = new double[MyTime.WeeksPerYear];	
		groupData(eff, effWeek, effMonth, effYear);
		
		//EVT MOVE
		evtMoveWeek = new double[MyTime.DaysPerWeek];
		evtMoveMonth = new double[MyTime.DaysPerMonth];		
		evtMoveYear = new double[MyTime.WeeksPerYear];
		groupData(evtMove, evtMoveWeek, evtMoveMonth, evtMoveYear);
			
		//EVT SNORE
		evtSnoreWeek = new double[MyTime.DaysPerWeek];
		evtSnoreMonth = new double[MyTime.DaysPerMonth];		
		evtSnoreYear = new double[MyTime.WeeksPerYear];	
		groupData(evtSnore, evtSnoreWeek, evtSnoreMonth, evtSnoreYear);
		
		//EVT COUGH
		evtCoughWeek = new double[MyTime.DaysPerWeek];
		evtCoughMonth = new double[MyTime.DaysPerMonth];		
		evtCoughYear = new double[MyTime.WeeksPerYear];
		groupData(evtCough, evtCoughWeek, evtCoughMonth, evtCoughYear);
		
		//overall eff
		overallEffWeek = 0;
		int validDays = 0;
		for(int i=0; i<MyTime.DaysPerWeek; i++)
		{
			double e = effWeek[i];
			if(e!=-2)
			{
				overallEffWeek+=e;
				validDays++;
			}
		}
		if(validDays==0)
			overallEffWeek = -2;
		else
			overallEffWeek/=validDays;
			

	}
	
}
