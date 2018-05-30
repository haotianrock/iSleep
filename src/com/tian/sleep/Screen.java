package com.tian.sleep;

import java.util.LinkedList;


import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class Screen 
{
	LinkedList<MyButton> btnList = null;
	
	boolean showToast = false;
	
	/*********screen info***********/
	int screenH, screenW;
	int x,y; //original left top corner of the screen
	Rect rScreen;
	
	public static Rect calMaxImageLoc(BitmapDrawable img, Rect area)
	{
		Rect loc = new Rect();
		int locH, locW;
		
		float ratioImg = (float)img.getIntrinsicHeight()/(float)img.getIntrinsicWidth();
		float ratioArea = (float)area.height()/(float)area.width();
		
		if(ratioImg>ratioArea)
		{
			locH = area.height();
			locW = (int) (locH*(1/ratioImg));
		}
		else
		{
			locW = area.width();
			locH = (int) (locW*ratioImg);
		}
		
		loc.left = area.left+(area.width()-locW)/2;
		loc.right = area.right-(area.width()-locW)/2;
		loc.top = area.top+(area.height()-locH)/2;
		loc.bottom = area.bottom-(area.height()-locH)/2;
		
		return loc;
	}
}
