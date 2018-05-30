package com.tian.sleep;

public class EventArea 
{
	//global frame index since start-off
	int startFrame;
	int endFrame;
	int len;
	
	//frame index according to current audio-buffer
	int locStart;
	int locEnd;
	
	//features
	public int[] maxArr;
	public int[] zcArr;
	public int[] lmmrArr;
	
	//flags for event detection
	public EventType[] typeArr;
	
	EventArea(int s, int e, int locs, int loce)
	{	
		startFrame = s;
		endFrame = e;
		len = e-s+1;
		
		locStart = locs;
		locEnd = loce;
	}
	
	public String toString()
	{
		String str = "["+startFrame+","+endFrame+"] len="+len;
		return str;
	}
	
	public void allocate()
	{
		maxArr = new int[len];
		zcArr = new int[len];
		lmmrArr = new int[len];
		
		typeArr = new EventType[len];
		
		for(int i=0; i<len; i++)
			typeArr[i] = EventType.NONE;
	}
	
	public void setFeature(Feature f, int locInd)
	{
		int pos = locInd-locStart;
		
		maxArr[pos] = f.max;
		zcArr[pos] = f.zc;
		lmmrArr[pos] = f.lmmr;
	}
}
