package com.tian.sleep;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class MyImage 
{
	Rect area;
	Rect src;
	
	BitmapDrawable img;
	
	MyImage(Rect rect, BitmapDrawable image)
	{
		area = rect;
		img = image;
		
		src = new Rect(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
	}

	
	public void paintImage(Canvas cvs)
	{
		//check btn image
		if(img==null)
			return;
		
		//paint button background
		cvs.drawBitmap(img.getBitmap(), src, area, null);
		
	}
	
	public static Rect fitInRect(Rect r, int w, int h)
	{
		Rect fit = new Rect();
		
		//try fit width first
		int rw = r.width();
		int rh = r.height();
		
		int fh = (int) (rw*((double)h/(double)w));
		
		if(fh>rh)
		{
			//try fit height
			int fw = (int) (rh*((double)w/(double)h));
			fit.top = r.top;
			fit.bottom = r.bottom;
			fit.left = r.left + (rw-fw)/2;
			fit.right = fit.left + fw;
			
		}
		else
		{
			fit.left = r.left;
			fit.right = r.right;
			fit.top = r.top + (rh-fh)/2;
			fit.bottom = fit.top + fh;
		}
		
		return fit;
	}
}
