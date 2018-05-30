package com.tian.sleep;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class MyButton 
{
	//button location
	Rect area = null;	//indicates the painting area on the screen
	 
	
	//button text
	Point pText;
	Paint ptText;
	String text = null;

	//background image
	BitmapDrawable bkgrdImage = null;
	BitmapDrawable bkgrdImageHit = null;
	Rect src = null;	//indicate the actual size of the image
	
	//button id
	public ButtonID id;
	
	//button status
	public boolean isSelected = false;
	public boolean isVisible = true;
	
	
	//button color
	int btnColor = -1;
	int btnColorHit = -1;
	
	int lineWidth = 5;
	
	int alpha = 255;
	
	boolean isFill = false;
	
	
	//locations (for animation)
	Rect[] rLocations  = null;
	Rect[] rSrc = null;
	int menuIndex = -1;	//indicate the order of this button in menu list, -1 if unVisible
		
	MyButton(ButtonID bid, Rect rect)
	{
		area = rect;
		id = bid;
		
		btnColor = AppUI.COLOR_LIME;
		btnColorHit = AppUI.COLOR_ORANGE;
	}
	
	public void setColor(int c, int ch)
	{
		btnColor = c;
		btnColorHit = ch;
	}
	
	public void setLineWidth(int l)
	{
		lineWidth = l;
	}
	
	public void setImage(BitmapDrawable image, BitmapDrawable imageHit)
	{
		bkgrdImage = image;
		bkgrdImageHit = imageHit;
		src = new Rect(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
	}
	
	public void setText(String t, Point pT, Paint ptT)
	{
		text = t;
		pText = pT;
		ptText = ptText;
	}
	
	MyButton(RectF rect, ButtonID bid)
	{
		area = new Rect();
		area.set((int)rect.left, (int)rect.top, (int)rect.right, (int)rect.bottom);
		id = bid;
	}
	
	
	public void setAlpha(int a)
	{
		if(a<0 || a>255)
			return;
		alpha = a;
	}
	
	public void paintButton(Canvas cvs)
	{
		//check btn image
		if(bkgrdImage==null || bkgrdImageHit==null)
			return;
		
		//paint button background
		if(isSelected)
		{
			cvs.drawBitmap(bkgrdImageHit.getBitmap(), src, area, null);
		}
		else
		{
			cvs.drawBitmap(bkgrdImage.getBitmap(), src, area, null);
		}
	}
	
	
	public void drawButton(Canvas cvs)
	{	
		if(id==ButtonID.MONITOR_STOP)
		{
			drawButtonCircle(cvs);
			return;
		}
		else if (id==ButtonID.EVENTRESULT_INFO)
		{
			paintButton(cvs);
			return;
		}
		else if (id==ButtonID.HISTORY_INFO)
		{
			paintButton(cvs);
			return;
		}
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(lineWidth);
		
		if(isFill)
			paint.setStyle(Paint.Style.FILL);
		else
			paint.setStyle(Paint.Style.STROKE);
		
		//paint button background
		if(isSelected)
		{
			paint.setColor(btnColorHit);
			paint.setAlpha(alpha);
			cvs.drawRect(area, paint);
			
		}
		else
		{
			paint.setColor(btnColor);
			paint.setAlpha(alpha);
			cvs.drawRect(area, paint);
		}
	}
	
	public void drawButtonCircle(Canvas cvs)
	{	
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(lineWidth);
		

		/*if(isFill)
			paint.setStyle(Paint.Style.FILL);
		else
			paint.setStyle(Paint.Style.STROKE);*/
		
		
		alpha = 150;
		
		float cx = area.centerX();
		float cy = area.centerY();
		float r = area.width()/2;
		
		//paint button background
		if(isSelected)
		{
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(btnColor);
			paint.setAlpha(alpha);
			cvs.drawCircle(cx,cy,r-lineWidth/2, paint);
			
		}
		else
		{
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(btnColor);
			paint.setAlpha(alpha);
			cvs.drawCircle(cx,cy,r, paint);
			
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(btnColorHit);
			paint.setAlpha(alpha);
			cvs.drawCircle(cx,cy,r-lineWidth/2, paint);
		}
	}
	
	
	
	
	/**
	 * call this function to paint btn with curFrameIndex to make animation
	 * @param cvs
	 */
	public void paintButton(Canvas cvs, int curFrameIndex)
	{
		//check btn image
		if(bkgrdImage==null || bkgrdImageHit==null)
			return;
		
		//is set not visible
		if(!isVisible)
			return;
		
		//not supposed to be shown
		if(area.width()==0)
			return;
		
		//paint button background
		if(isSelected)
		{
			cvs.drawBitmap(bkgrdImageHit.getBitmap(), rSrc[curFrameIndex], rLocations[curFrameIndex], null);
		}
		else
		{
			cvs.drawBitmap(bkgrdImage.getBitmap(), rSrc[curFrameIndex], rLocations[curFrameIndex], null);
		}
	}
	
	public void setStatus(boolean b)
	{
		isSelected = b;
	}
	
	public boolean getStatus()
	{
		return isSelected;
	}
	
	public Rect getArea()
	{
		return area;
	}
	
	public void setVisible(boolean v)
	{
		isVisible = v;
		
		if(!v)
			menuIndex = -1;
	}
}
