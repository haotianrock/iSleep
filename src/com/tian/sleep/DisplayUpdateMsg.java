package com.tian.sleep;

public class DisplayUpdateMsg {
	DisplayStatus ds;
	DisplayStatus update;
	DisplayStatus update1;
	int index;	//for display regulated by timer
	double data1;	//used for passing sound intensity info displayed in sound meter
	
	int x,y;	//used for passing the cordinater of finger move 
	
	String toastStr = null;
}
