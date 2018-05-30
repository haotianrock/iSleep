package com.tian.sleep;

public class SleepEvent {
	
	int startFrame;
	int endFrame;
	int duration;
	
	EventType type;
	
	SleepEvent(int s, int e, EventType t)
	{
		startFrame = s;
		endFrame = e;
		duration = e-s+1;
		type = t;
	}
	
	SleepEvent(int s, int e)
	{
		startFrame = s;
		endFrame = e;
		duration = e-s+1;
		//type = t;
	}
}
