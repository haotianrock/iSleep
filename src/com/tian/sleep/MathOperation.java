package com.tian.sleep;

import java.util.LinkedList;

import android.util.Log;

public class MathOperation {
	
	public static short CalMax (short[] data, int s, int e)
	{
		short max = 0;
		short tmp;
		
		for(int i=s; i<=e; i++)
		{
			tmp = data[i];
			
			if(tmp>max)
				max = tmp;
		}
		
		return max;
	}
	
	public static double getRMS (short[] data, int s, int e)
	{
		double sum = 0;
		double tmp;
		double len = e-s+1;
		
		for(int i=s; i<e; i++)
		{
			tmp = data[i];
			//Log.e("ENG", Short.toString(data[i]));
			sum = sum + (tmp*tmp);
		}
		
		double rms = Math.sqrt(sum/len);
		return rms;
	}
	
	public static double getEnergy (short[] data, int s, int e)
	{
		double eng = getRMS(data, s, e);
		
		//double result = (double)eng/(double)Short.MAX_VALUE;
		
		return eng;
	}
	
	public static double  getRMSDouble (double[] data, int s, int e)
	{
		double sum = 0;
		double tmp;
		int len = e-s+1;
		
		for(int i=s; i<e; i++)
		{
			tmp = data[i];
			sum+=tmp*tmp;
		}
		
		
		double rms = Math.sqrt((double)(sum/(double)len));
		return rms;
	}
	
	
	public static double getRLH (short[] data, int s, int e)
	{
		int len = e-s+1;
		double[] tmp = new double[len];	
		
		double ratio = 0.2;
		
		//low pass
		tmp[0] = data[s+0];
		for(int i=1; i<len; i++)
		{
			tmp[i] = tmp[i-1] + ratio*((double)data[s+i]-tmp[i-1]); 
		}
		double lowEnergy = getRMSDouble (tmp, 0, len-1);
		
		
		//low pass
		tmp[0] = data[s+0];
		for(int i=1; i<len; i++)
		{
			tmp[i] = ratio*(tmp[i-1] + (double)data[s+i] - (double)data[s+i-1]);
		}
		double highEnergy = getRMSDouble (tmp, 0, len-1);
			
		return lowEnergy/highEnergy;
	}
	
	
	public static int getMean (short[] data, int s, int e)
	{
		int mean = 0;
		
		for(int i=s; i<=e; i++)
		{
			mean+=data[i];
		}
		
		mean = mean/(e-s+1);
		
		return mean;
	}
	
	public static double CalStd (short[] data, int s, int e)
	{
		int num = e-s+1;
		
		//mean value
		int sum = 0;
		for(int i=s; i<=e; i++)
		{
			sum+=data[i];
		}
		double mean = (double)sum/(double)num;
		
		//variance
		double dsum = 0;
		double tmp;
		for(int i=s; i<=e; i++)
		{
			tmp = (double)data[i]-mean;
			dsum+=tmp*tmp;
		}
		double var = dsum/(double)num;
		
		double std = Math.sqrt(var);
		
		std = std/(double)Short.MAX_VALUE;
		
		return std;
	}
	
	
	public static double getVar (short[] data, int s, int e)
	{
		int num = e-s+1;
		
		//mean value
		int sum = 0;
		for(int i=s; i<=e; i++)
		{
			sum+=data[i];
		}
		double mean = (double)sum/(double)num;
		
		//variance
		double dsum = 0;
		double tmp;
		for(int i=s; i<=e; i++)
		{
			tmp = (double)data[i]-mean;
			dsum+=tmp*tmp;
		}
		double var = dsum/(double)num;
		
		//double std = Math.sqrt(var);
		
		//std = std/(double)Short.MAX_VALUE;
		
		return var;
	}
	
	
	public static double[] CalStdInt (int[] data)
	{
		int num = data.length;
		
		//mean value
		int sum = 0;
		for(int i=0; i<num; i++)
		{
			sum+=data[i];
		}
		double mean = (double)sum/(double)num;
		
		//variance
		double dsum = 0;
		double tmp;
		for(int i=0; i<num; i++)
		{
			tmp = (double)data[i]-mean;
			dsum+=tmp*tmp;
		}
		double var = dsum/(double)num;
		
		double std = Math.sqrt(var);
		
		double[] result = new double[2];
		result[0] = mean;
		result[1] = std;
		
		//std = std/(double)Short.MAX_VALUE;
		
		return result;
	}
	
	public static double[] CalStdDouble (double[] data)
	{
		int num = data.length;
		
		//mean value
		double sum = 0;
		for(int i=0; i<num; i++)
		{
			sum+=data[i];
		}
		double mean = sum/(double)num;
		
		//variance
		double dsum = 0;
		double tmp;
		for(int i=0; i<num; i++)
		{
			tmp = data[i]-mean;
			dsum+=tmp*tmp;
		}
		double var = dsum/(double)num;
		
		double std = Math.sqrt(var);
		
		double[] result = new double[2];
		result[0] = mean;
		result[1] = std;
		
		//std = std/(double)Short.MAX_VALUE;
		
		return result;
	}
	
	public static double CalVarDouble (double[] data)
	{
		int len = data.length;
		
		//mean value
		double sum = 0;
		for(int i=0; i<len; i++)
		{
			sum+=data[i];
		}
		double mean = sum/(double)len;
		
		//variance
		double dsum = 0;
		double tmp;
		for(int i=0; i<len; i++)
		{
			tmp = data[i]-mean;
			dsum+=tmp*tmp;
		}
		double var = dsum/(double)len;
		
		//double std = Math.sqrt(var);
		
		return var;
	}
	
	
	public static void saveSleepEvent(LinkedList<SleepEvent> list, boolean[] flags, int sf)
	{
		int len = flags.length;
		
		int s, e;
		s = -1;
		e = -1;
		
		int ind = 0;
		
		if(flags[0]==true)
			s = 0;
		
		while(true)
		{
			ind++;
			
			if(ind>=len)
				break;
			
			if(flags[ind]==true)
			{
				if(s<0)
					s = ind;	//mark start pos
			}
			
			else
			{
				if(s>=0)
				{
					e = ind-1;	//mark end pos
					
					//save event
					list.add(new SleepEvent(s+sf, e+sf));
					
					Log.e("MathOper", Integer.toString(s+sf)+" "+Integer.toString(e+sf));
					
					//reset
					s = -1;
					e = -1;
				}
			}
		}
	}
	
	 
	public static void openningOperation(boolean[] fbuf, int l)
	{
	
		//remove the non-env-noise frames if their duration is less than 5 frames
		int minDur = 5;
		if(l>0)
			minDur = l;
		
		int i = 0;
		int len = fbuf.length;
		
		int s = -1;
		int e = -1;
		
		boolean cur, next;
		
		boolean isInsideArea = false;
		
		if(fbuf[i])
		{
			isInsideArea = true;
			s = 0;
		}

		
		while(true)
		{
			if(i+1>=len)
			{
				//i is the last element
				if(isInsideArea)
				{
					e = i;
					if(e-s+1<minDur)
					{
						//remove short-duration frames
						for(int j=s; j<=e; j++)
						{
							/*if(j<0||j>=len)
								logBooleanArr(fbuf, 0, fbuf.length-1, "i="+i);*/
							
							fbuf[j] = false;
						}
					}
				}
				break;
			}
			
			//i+1-th element exist, check the transition
			cur = fbuf[i];
			next = fbuf[i+1];
			
			
			
			if(cur==true && next==false)
			{
				//exiting the event area
				isInsideArea = false;
				e = i;
				if(e-s+1<minDur)
				{
					//remove short-duration frames
					for(int j=s; j<=e; j++)
					{
						/*if(j<0||j>=len)
							logBooleanArr(fbuf, 0, fbuf.length-1, "i="+i);*/
						
						fbuf[j] = false;
					}
				}
			}
			else if(cur==false && next==true) 
			{
				//entering the event area
				isInsideArea = true;
				s = i+1;
			}
			
			i++;
		}
		
	}
	
	
	public static void closingOperation(boolean[] fbuf, int l)
	{
		
		//fill the short gap between event areas that is less than 5 frames
		int minDur = 5;
		if(l>0)
			minDur = l;
		
		int i = 0;
		int len = fbuf.length;
		
		int s = -1;
		int e = -1;
		
		boolean cur, next;
		
		boolean isInsideGap = false;
		
		if(!fbuf[i])
		{
			isInsideGap = true;
			s = 0;
		}

		
		while(true)
		{
			if(i+1>=len)
			{
				//i is the last element
				if(isInsideGap)
				{
					e = i;
					if(e-s+1<=minDur)
					{
						//set short-duration frames within gap as event-frame
						for(int j=s; j<=e; j++)
						{
							/*if(j<0||j>=len)
								logBooleanArr(fbuf, 0, fbuf.length-1, "i="+i);*/
							
							fbuf[j] = true;
						}
					}
				}
				break;
			}
			
			//i+1-th element exist, check the transition
			cur = fbuf[i];
			next = fbuf[i+1];
			
			
			if(cur==true && next==false)
			{
				//entering the gap area
				isInsideGap = true;
				s = i+1;
			}
			else if(cur==false && next==true) 
			{
				//exiting the gap area
				isInsideGap = false;
				e = i;
				
				if(e-s+1<=minDur)
				{
					//set short-duration frames within gap as event-frame
					for(int j=s; j<=e; j++)
					{
						/*if(j<0||j>=len)
							logBooleanArr(fbuf, 0, fbuf.length-1, "i="+i);*/
						
						fbuf[j] = true;
					}
				}
			}
			
			i++;
		}
	}
	
	
	public static void dilationOperation(boolean[] fbuf, int l)
	{
		//expand the edge of event area by 10 frames
		int diaLen = 10;
		if(l>0)
			diaLen = l;


		int i = 0;
		int len = fbuf.length;
		
		int s = -1;
		int e = -1;
		
		boolean cur, next;

		while(true)
		{
			if(i+1>=len)
				break;
			
			//i+1-th element exist, check the transition
			cur = fbuf[i];
			next = fbuf[i+1];
			
			
			if(cur==true && next==false)
			{
				//exiting the event area
				for(int j=i+1; j<=(i+diaLen); j++)
				{
					if(j>=len)
						break;
					
					fbuf[j] = true;
				}
				
				i = i+diaLen;
				
			}
			else if(cur==false && next==true) 
			{
				//entering the event area
				for(int j=i; j>(i-diaLen); j--)
				{
					if(j<0)
						break;
					
					fbuf[j] = true;
				}
			}
			
			i++;
		}
	}
	
	
	public static boolean isInCurrentArea(int areaHead, int areaEnd, int s, int e)
	{
		
		//Log.e("Checked Area", areaHead+" "+areaEnd+" "+s+" "+e);
		
		if(areaHead>=s && areaHead<=e)
			return true;
		
		if(areaEnd>=s && areaEnd<=e)
			return true;
		
		if(areaHead<=s && areaEnd>=e)
			return true;
		
		return false;
	}
	
	
	public static void limitDuration(boolean[] flags, int maxLen)
	{
		//filter out continuous cough frames of more than MAX_COUGH_DURATION frames
		 int duration = 0;
		 int size = flags.length;
		 
		 for(int i=0; i<size; i++)
		 {
			 if(flags[i]==true)
			 {
				 duration++;
				 
				 if(i==(size-1))
				 {
					 i++;
					 if(duration>=maxLen)
					 {
						 //remove flags
						 for(int j=1; j<=duration; j++)
						 {
							 flags[i-j] = false;
						 }
					 }
				 }
			 }
			 else
			 {
				 if(duration>=maxLen)
				 {
					 //remove flags
					 for(int j=1; j<=duration; j++)
					 {
						 flags[i-j] = false;
					 }
				 }
				 duration=0;
			 }
		 }
	}
		

}
