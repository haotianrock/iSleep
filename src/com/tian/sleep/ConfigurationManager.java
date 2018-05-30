package com.tian.sleep;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


import android.os.Environment;
import android.util.Log;

public class ConfigurationManager 
{
	String folderName = "iSleepConfigurationFiles";
	String fileName = "configuration";
	
	File configFile = null;
    File directory = null;
    
    public double[] moveList = null;
    public double[] snoreList = null;
    public double[] coughList = null;
    public double[] effList = null;
    
    public double[] stateLNList = null;
    public double[] moveLNList = null;
    public double[] snoreLNList = null;
    public double[] coughLNList = null;
    public double overallLN = 0;
    
    boolean isFirstTimeUse = false;	//change true if no config file exists
    
    boolean isAgreeConsent = false;
    
    boolean isShowIntroScreen = true;
	
	ArrayList<String> configLines;
	
	int curVersion;
	
	public ConfigurationManager()
	{
		//check if there exist folder and configuration.txt in folder
		configFile = null;
	    directory = new File 
	    (Environment.getExternalStorageDirectory().getPath()+"/"+folderName);
	    
	    configLines = new ArrayList<String>();
	    
	    moveList = new double[MyTime.DaysPerYear];
	    coughList = new double[MyTime.DaysPerYear];
	    snoreList = new double[MyTime.DaysPerYear];
	    effList = new double[MyTime.DaysPerYear];
	    
	    //create config folder and file if not exist
	    if (!directory.exists()) 
	    { 
          	directory.mkdir(); 
          	configFile = new File(directory.getPath()+"/"+fileName+".txt");
          	
          	//first time run, initialize config file.
          	/**********************************/
          	isFirstTimeUse = true;
          	/**********************************/
          	
          	try {
				configFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    else
	    {
	    	configFile = new File(directory.getPath()+"/"+fileName+".txt");
	    }
	    
	    if(isFirstTimeUse)
	    	writeFile(getFirstTimeConfigString());

	}
	
	private String getFirstTimeConfigString()
	{
		String str = "";
		
		//#version
		str+="#version"+"\n";
		str+="0"+"\n";
		//#consent
		str+="#consent"+"\n";
		str+="0"+"\n";
		//#eff
		str+="#eff"+"\n";
		for(int i=0; i<MyTime.DaysPerYear; i++)
			str+="-2"+"\n";
		//#move
		str+="#move"+"\n";
		for(int i=0; i<MyTime.DaysPerYear; i++)
			str+="-2"+"\n";
		//#snore
		str+="#snore"+"\n";
		for(int i=0; i<MyTime.DaysPerYear; i++)
			str+="-2"+"\n";
		//#cough
		str+="#cough"+"\n";
		for(int i=0; i<MyTime.DaysPerYear; i++)
			str+="-2"+"\n";
		
		return str;
		
	}
	
	public boolean saveConfigFile()
	{
		String str = "";
		
		//#version
		str+="#version"+"\n";
		str+=curVersion+"\n";
		//#consent
		str+="#consent"+"\n";
		if(isAgreeConsent)
			str+="1"+"\n";
		else
			str+="0"+"\n";
		//#eff
		str+="#eff"+"\n";
		for(int i=0; i<MyTime.DaysPerYear; i++)
			str+=effList[i]+"\n";
		//#move
		str+="#move"+"\n";
		for(int i=0; i<MyTime.DaysPerYear; i++)
			str+=moveList[i]+"\n";
		//#snore
		str+="#snore"+"\n";
		for(int i=0; i<MyTime.DaysPerYear; i++)
			str+=snoreList[i]+"\n";
		//#cough
		str+="#cough"+"\n";
		for(int i=0; i<MyTime.DaysPerYear; i++)
			str+=coughList[i]+"\n";
		
		if(HistoryData.stateSleepLN == null)
		{
			HistoryData.stateSleepLN = stateLNList;
			HistoryData.evtMoveLN = moveLNList;
			HistoryData.evtSnoreLN = snoreLNList;
			HistoryData.evtCoughLN = coughLNList;
			HistoryData.overallEff = overallLN;
		}
		if(HistoryData.stateSleepLN != null)
		{
			//#move
			str+="#overallLN"+"\n";
			str+=HistoryData.overallEff+"\n";
			
			//#state
			str+="#stateLN"+"\n";			
			int num = HistoryData.stateSleepLN.length;
			str+=num+"\n";
			for(int i=0; i<num; i++)
				str+=HistoryData.stateSleepLN[i]+"\n";

			//#move
			str+="#moveLN"+"\n";
			num = HistoryData.evtMoveLN.length;
			str+=num+"\n";
			for(int i=0; i<num; i++)
				str+=HistoryData.evtMoveLN[i]+"\n";

			
			//#snore
			str+="#snoreLN"+"\n";
			num = HistoryData.evtSnoreLN.length;
			str+=num+"\n";
			for(int i=0; i<num; i++)
				str+=HistoryData.evtSnoreLN[i]+"\n";
			
			//#cough
			str+="#coughLN"+"\n";
			num = HistoryData.evtCoughLN.length;
			str+=num+"\n";
			for(int i=0; i<num; i++)
				str+=HistoryData.evtCoughLN[i]+"\n";
			
		}
		//#cough
		
		writeFile(str);
		return true;
	}
	
	public boolean writeFile(String str)
	{
		if(configFile==null)
			return false;
		
		try {
			FileOutputStream fOut = new FileOutputStream(configFile);                   
            OutputStreamWriter OutWriter = new OutputStreamWriter(fOut);
            
            OutWriter.write(str);

            OutWriter.close();
            fOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }//End of try/catch
		
		return true;
	}
	
	
	public boolean checkUpdate()
	{
		if(UpdateConfigInfo.newVersion>curVersion)
			return true;
		else
			return false;
	}
	
	
	public boolean readFile()
	{
		/************ read file *****************/
		try
		{
			if(configFile==null)
				return false;
			
			//open config file
			InputStream in = null;
			in = new BufferedInputStream(new FileInputStream(configFile));
			InputStreamReader inReader = new InputStreamReader(in);
			LineNumberReader lnReader = new LineNumberReader(inReader);
			
			//read lines in config file
			String line;
			while(true)
			{
				line = lnReader.readLine();
				
				Log.e("READ-CONFIG", line);
				
				if(line==null)
					break;
				
				configLines.add(new String(line));
			}
			
			//close file
			if(in != null)
				in.close();
			
		}catch (Exception e) {
            e.printStackTrace();
        }//End of try/catch
		
		
		/************ parse file *****************/
		int len = configLines.size();
		String str = null;
		for(int i=0; i<len; i++)
		{
			//Log.e("CONFIG", configLines.get(i));
			str = configLines.get(i);
			
			if(str.charAt(0) == '#')
			{
				if(str.equals("#version"))
				{
					i++;
					str = configLines.get(i);
					curVersion = Integer.valueOf(str);
				}
				
				else if(str.equals("#consent"))
				{
					i++;
					str = configLines.get(i);
					int n = Integer.valueOf(str);
					
					if(n==0)
						isAgreeConsent = false;
					else
						isAgreeConsent = true;
				}
				
				else if(str.equals("#eff"))
				{
					for(int j=0; j<MyTime.DaysPerYear; j++)
					{
						i++;
						str = configLines.get(i);
						effList[j] = Double.valueOf(str);
					}
				}
				
				else if(str.equals("#move"))
				{
					for(int j=0; j<MyTime.DaysPerYear; j++)
					{
						i++;
						str = configLines.get(i);
						moveList[j] = Double.valueOf(str);
					}
				}
				
				else if(str.equals("#snore"))
				{
					for(int j=0; j<MyTime.DaysPerYear; j++)
					{
						i++;
						str = configLines.get(i);
						snoreList[j] = Double.valueOf(str);
					}				
				}
				
				else if(str.equals("#cough"))
				{
					for(int j=0; j<MyTime.DaysPerYear; j++)
					{
						i++;
						str = configLines.get(i);
						coughList[j] = Double.valueOf(str);
					}
				}
				
				else if(str.equals("#stateLN"))
				{
					i++;
					str = configLines.get(i);
					int num = Integer.valueOf(str);
					
					stateLNList = new double[num];
					
					for(int j=0; j<num; j++)
					{
						i++;
						str = configLines.get(i);
						stateLNList[j] = Double.valueOf(str);
					}
				}
				
				else if(str.equals("#moveLN"))
				{
					i++;
					str = configLines.get(i);
					int num = Integer.valueOf(str);
					
					moveLNList = new double[num];
					
					for(int j=0; j<num; j++)
					{
						i++;
						str = configLines.get(i);
						moveLNList[j] = Double.valueOf(str);
					}
				}
				
				else if(str.equals("#snoreLN"))
				{
					i++;
					str = configLines.get(i);
					int num = Integer.valueOf(str);
					
					snoreLNList = new double[num];
					
					for(int j=0; j<num; j++)
					{
						i++;
						str = configLines.get(i);
						snoreLNList[j] = Double.valueOf(str);
					}
				}
				
				else if(str.equals("#coughLN"))
				{
					i++;
					str = configLines.get(i);
					int num = Integer.valueOf(str);
					
					coughLNList = new double[num];
					
					for(int j=0; j<num; j++)
					{
						i++;
						str = configLines.get(i);
						coughLNList[j] = Double.valueOf(str);
					}
				}
				
				else if(str.equals("#overallLN"))
				{
					i++;
					str = configLines.get(i);
					overallLN = Double.valueOf(str);
				}
			
			}//EOF "#"
			
		}//EOF for (lines)
		
		isShowIntroScreen = isFirstTimeUse;
		
		return true;
	}
}
