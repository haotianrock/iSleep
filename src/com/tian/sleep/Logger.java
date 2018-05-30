package com.tian.sleep;

import java.util.LinkedList;

public class Logger {


	public static void logBooleanArr(boolean[] arr, int s, int e, int FRAMES_PER_READ, String header)
	{
		String logStr = "";
		
		int ColNum = 10;	//display 10 numbers per line
		int col_cnt = 0;
		
		int RowNum = FRAMES_PER_READ/ColNum;
		int row_cnt = 0;
		
		String sep = " ";
		
		if(s>e || s<0 || e<0)
			return;
		
		//print header
		System.out.print("----------"+header+"----------"+"\n");
		
		for(int i=s; i<=e; i++)
		{
			if(arr[i])
				logStr+=1;
			else
				logStr+=0;
			
			col_cnt++;
			
			if(col_cnt==ColNum)
			{
				System.out.print("["+row_cnt+"] "+logStr+"\n");
				
				row_cnt++;
				if(row_cnt==RowNum)
					row_cnt = 0;
				
				col_cnt=0;
				logStr = "";
			}
			else
				logStr+=sep;
		}
				
		if(col_cnt>0)
		{
			System.out.print(logStr+"\n");
		}

	}
	
	public static void logEventArea(LinkedList<EventArea> list, String header)
	{
		int size = list.size();
		
		//System.out.print("EA:"+size+"\n");
		
		if(size==0)
			return;

		EventArea ea = null;
		
		//print header
		System.out.print("----------"+header+"----------"+"\n");
		
		for(int i=0; i<size; i++)
		{
			ea = list.get(i);
			System.out.print("["+i+"] "+ea.toString()+"\n");
		}		
	}
	
	public static void logEventFeatures(EventArea ea, String header)
	{
		int[] max = ea.maxArr;
		int[] zc = ea.zcArr;
		int[] lmmr = ea.lmmrArr;
		
		if(header!=null)
			System.out.print("*************"+header+"*************"+"\n");
		
		logIntArr(max, 0, max.length-1, "MAX");
		logIntArr(zc, 0, zc.length-1, "ZC");
		logIntArr(lmmr, 0, lmmr.length-1, "LMMR");
	}
	
	public static void logIntArr(int[] arr, int s, int e, String header)
	{
		String logStr = "";
		
		int ColNum = 10;	//display 10 numbers per line
		int col_cnt = 0;
		
		int row_cnt = 0;
		
		String sep = ",";
		
		if(s>e || s<0 || e<0)
			return;
		
		//print header
		System.out.print("----------"+header+"----------"+"\n");
		
		for(int i=s; i<=e; i++)
		{

			//logStr+=Integer.toString(arr[i]);
			logStr+=String.format("%4d", arr[i]);
					
			col_cnt++;
			
			if(col_cnt==ColNum)
			{
				System.out.print(String.format("%2d", row_cnt)+"] "+logStr+"\n");
				
				row_cnt++;
				
				col_cnt=0;
				logStr = "";
			}
			else
				logStr+=sep;
		}
				
		if(col_cnt>0)
		{
			//print last line
			System.out.print(String.format("%2d", row_cnt)+"] "+logStr+"\n");
		}

	}
}
