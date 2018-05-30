package com.tian.sleep;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.tian.sleep.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class MonitorScreen extends Screen
{
	/********* Fonts **********/
	//font initialization is required 	
	Paint.FontMetricsInt fontTimeInfo = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt fontMonitor = AppUI.FONT_LARGE;
	Paint.FontMetricsInt fontBtn = AppUI.FONT_MID;
	Paint.FontMetricsInt fontDetailInfo = AppUI.FONT_SMALL;
	

	/********** Btn Images ***********/
	/*int imageResID = R.drawable.btn_red;
	int imageHitResID = R.drawable.btn_grey;
	BitmapDrawable image = null;
	BitmapDrawable imageHit = null;*/
	
	
	/********* Images *********/	
	MyImage imgClock = null;
	BitmapDrawable bdClock;
	int idClockImg = R.drawable.clock;
	Rect rClockImg;
	
	BitmapDrawable bdBackground;
	
	
	/********* Paints **********/
	Paint ptTimeInfo;
	Paint ptMonitor;
	Paint ptStopInfo;
	Paint ptBtnText;
	
	
	/********** Color ***********/
	int cSoundMeterLow = AppUI.COLOR_BLUE; //Color.rgb(126,187,18);
	int cSoundMeterMid = Color.rgb(249,157,15);
	int cSoundMeterHigh = Color.rgb(249,82,13);
	
	final static int[] cMonitor = {Color.rgb(0, 255, 0), Color.rgb(0, 240, 0), 
									Color.rgb(0, 225, 0), Color.rgb(0, 210, 0),
									Color.rgb(0, 195, 0), Color.rgb(0, 180, 0),
									Color.rgb(0, 165, 0), Color.rgb(0, 150, 0),
									Color.rgb(0, 135, 0), Color.rgb(0, 120, 0)};
	int curColorIndex = 0;
	int numColorIndex = cMonitor.length;
	
	int cTimeInfo = Color.WHITE;
	int cBtnText = Color.WHITE;
	int cDetailInfo = Color.WHITE;
	
	
	/********* Texts **********/
	String tMonitor = "Monitoring";
	
	String tStartTime = "You got on bed at ";
	String tStartTimeHrMin = " 00:00 ";
	String tDurTime = "Duration:";
	String tDurTimeHrMinSec = " 00:00:00 ";
	
	String tInfoUnderSoundMeter = "Real-time sound intensity";
	
	String tStopBtn = "Stop";
	String tInfoUnderStopBtn_1 = "Click the stop button to see the results";
	String tInfoUnderStopBtn_2 = "when you wake up in the morning";
	
	
	/********* Locations **********/
	Point pMonitor;
	Point pTimeInfo1;
	Point pTimeInfo2;
	
	Point pStopBtn;
	Point pStopInfo1;
	Point pStopInfo2;
	
	
	/********** buttons ***********/
	Rect rStopBtn;
	
	
	/********** Sound Meter ***********/
	Rect rSoundMeter;
	Paint ptSoundMeter;
	
	float radius = AppUI.SIZE_SMALL_TEXT/4;
	double curMax = 0;
	
    float[] meterValue = null;
    float[] meterX = null;
    float[] meterY = null;
    
    //cough display
    int coughFrameCnt = 0;
    boolean isDrawCough = false;
    int numFrameCoughDisp = 20;
    int cntFrameCoughDisp = 0;
	
	
	/********** Time Info ***********/
	int totalH, totalM;
    int durH = 0;
    int durM = 0;
    int durS = 0;


    
	public void initialize()
	{
		//set spacing info
		int smallSpc = (int) AppUI.SIZE_SMALL_TEXT;
		int normalSpc = (int) AppUI.SIZE_NORMAL_TEXT;
		int midSpc = (int) AppUI.SIZE_MID_TEXT;
		
		
		/************* Boundaries of Each Area ************/
		//monitor area
		int hMonitorText = fontMonitor.descent - fontMonitor.ascent;
		int spcMonitor1 = midSpc;	//the space btw monitor logo and the screen top
		int spcMonitor2 = normalSpc;	//the space btw the monitor logo and the time info
		int btmMonitorArea = hMonitorText + spcMonitor1 + spcMonitor2 + y;
		int topMonitorArea = y;
			
		//time info area
		int hTimeInfoText = fontTimeInfo.descent - fontTimeInfo.ascent;
		int spcTime1 = smallSpc;	//spc btw lines of time info
		int spcTime2 = normalSpc;	//spc btw time info and sound meter area
		int topTimeInfoArea = btmMonitorArea;
		int btnTimeInfoArea = topTimeInfoArea + hTimeInfoText*2 + spcTime1 + spcTime2;
		
		
		
		
		/************* Initialize Paints and Locations **************/
		//-----------Monitor area------------		
		ptMonitor = new Paint();
		ptMonitor.setAntiAlias(true);
		ptMonitor.setColor(cMonitor[0]); 	//color will be set when upon draw
		ptMonitor.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		ptMonitor.setTextSize(hMonitorText);

		pMonitor = new Point();
		pMonitor.y = topMonitorArea + spcMonitor1 - fontMonitor.ascent;
		pMonitor.x = (screenW - (int) ptMonitor.measureText(tMonitor))/2 + x;
		
		//-----------Time Info area------------		
		ptTimeInfo = new Paint();
		ptTimeInfo.setAntiAlias(true);
		ptTimeInfo.setColor(cTimeInfo); 
		ptTimeInfo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptTimeInfo.setTextSize(hTimeInfoText);
		
		rClockImg = new Rect();
		int sizeClockImg = hTimeInfoText*2 + spcTime1;
		rClockImg.left = x + normalSpc;
		rClockImg.top = topTimeInfoArea;
		rClockImg.right = rClockImg.left + sizeClockImg;
		rClockImg.bottom = rClockImg.top + sizeClockImg;		
		int spcTime3 = smallSpc;	//the horizontal spc btn clock image and time info

		pTimeInfo1 = new Point();
		pTimeInfo1.y = topTimeInfoArea - fontTimeInfo.ascent;
		pTimeInfo1.x = rClockImg.right + spcTime3;
		
		pTimeInfo2 = new Point();
		pTimeInfo2.y = pTimeInfo1.y + spcTime1 + hTimeInfoText;
		pTimeInfo2.x = rClockImg.right + spcTime3;
				
		//-----------Stop-btn area------------		
		//stop btn
		
		//stop-btn area
		ptBtnText = new Paint();
		ptBtnText.setAntiAlias(true);
		ptBtnText.setColor(cBtnText); 
		ptBtnText.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		
		int hStopBtnText = fontBtn.descent - fontBtn.ascent;
		ptBtnText.setTextSize(hStopBtnText);
			
		int btnTextMargin = hStopBtnText;
		//int hStopBtn = hStopBtnText + btnTextMargin*2;	//twice the height of btn text
		int hStopBtn = (int) (ptBtnText.measureText(tStopBtn)+2*btnTextMargin);
		int hStopInfoText = fontDetailInfo.descent - fontDetailInfo.ascent;
		int hStopInfo = hStopInfoText*2;	//two lines of detailed info under stop btn
		int spcStop1 = smallSpc;	//the space btw stop button and info under it.
		int spcStop2 = midSpc;	//the space btw info and the bottom of the screen
		int topStopArea = screenH - (hStopBtn + hStopInfo + spcStop1 + spcStop2) + y;
				
		//int btnWidth = screenW/2;
		int btnWidth = hStopBtn;
		
		rStopBtn = new Rect();
		rStopBtn.left = x+(screenW-btnWidth)/2;
		rStopBtn.right = rStopBtn.left + btnWidth;
		rStopBtn.top = topStopArea;
		rStopBtn.bottom = rStopBtn.top + hStopBtn;
		
		
		
		pStopBtn = new Point();
		pStopBtn.y = rStopBtn.top + btnWidth/2 + ((fontBtn.descent-fontBtn.ascent)/2-fontBtn.descent);
		pStopBtn.x = rStopBtn.left + (btnWidth-(int) ptBtnText.measureText(tStopBtn))/2;
		
		ptStopInfo = new Paint();
		ptStopInfo.setAntiAlias(true);
		ptStopInfo.setColor(cDetailInfo); 
		ptStopInfo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptStopInfo.setTextSize(hStopInfoText);
		
		pStopInfo1 = new Point();
		pStopInfo1.y = rStopBtn.bottom + spcStop1 - fontDetailInfo.ascent;
		pStopInfo1.x = (screenW - (int)ptStopInfo.measureText(tInfoUnderStopBtn_1))/2 + x;
		
		pStopInfo2 = new Point();
		pStopInfo2.y = pStopInfo1.y + hStopInfoText;
		pStopInfo2.x = (screenW - (int)ptStopInfo.measureText(tInfoUnderStopBtn_2))/2 + x;
		
		
		/************* Initialize Buttons **************/
		MyButton btn = new MyButton(ButtonID.MONITOR_STOP, rStopBtn);
		//btn.setImage(image, imageHit);
		btn.setColor(AppUI.COLOR_ORANGE, AppUI.COLOR_RED);
		btn.setText(tStopBtn, pStopBtn, ptBtnText);
		btn.setLineWidth(20);
		btnList.add(btn);
		
		
		/************* Initialize Images **************/
		imgClock = new MyImage(rClockImg, bdClock);
		
		
		/************* Sound Meter Area **************/
		int verMargin = midSpc;
		int horMargin = smallSpc;
		
		rSoundMeter = new Rect();
		rSoundMeter.top = btnTimeInfoArea + verMargin;
		rSoundMeter.bottom = topStopArea - verMargin;
		rSoundMeter.left = x + horMargin;
		rSoundMeter.right = x + (screenW-horMargin);
		
		ptSoundMeter = new Paint();
		
		/*Log.e("rSound", rSoundMeter.toString());
		Log.e("SSS", " "+horMargin);*/
		
	}
	
	
	
	
	public void paintScreen(Canvas cvs)
	{
		if(bdBackground==null)
			cvs.drawColor(AppUI.COLOR_SCREEN_BACKGROUND);
		else
			cvs.drawBitmap(bdBackground.getBitmap(), new Rect(0,0,bdBackground.getIntrinsicWidth(), bdBackground.getIntrinsicHeight()), rScreen, null);
			//cvs.drawBitmap(bdBackground.getBitmap(), 0, 0, null);
		
		
		
		/***************** Paint button images ******************/
		Iterator<MyButton> iterator = btnList.iterator();
		MyButton btn;
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			btn.drawButton(cvs);
		}
		
		
		/***************** Paint other Images ******************/
		imgClock.paintImage(cvs);
		
		
		/***************** Paint Text ******************/
		cvs.drawText(tMonitor, pMonitor.x, pMonitor.y, ptMonitor);	
		cvs.drawText(tStartTime+tStartTimeHrMin, pTimeInfo1.x, pTimeInfo1.y, ptTimeInfo);	
		cvs.drawText(tDurTime+tDurTimeHrMinSec, pTimeInfo2.x, pTimeInfo2.y, ptTimeInfo);	
		
		cvs.drawText(tInfoUnderStopBtn_1, pStopInfo1.x, pStopInfo1.y, ptStopInfo);	
		cvs.drawText(tInfoUnderStopBtn_2, pStopInfo2.x, pStopInfo2.y, ptStopInfo);
		
		cvs.drawText(tStopBtn, pStopBtn.x, pStopBtn.y, ptBtnText);	
		
		
		/*************** Paint Sound Meter *****************/
		paintSoundMeter(cvs);
		
	}
	

	private void paintSoundMeter(Canvas cvs)
	{
		if(meterValue == null)
		{
			int len = (int) Math.floor((rSoundMeter.width())/(radius*2));
			
			meterX = new float[len];
			meterY = new float[len];
			meterValue = new float[len];
			
			for(int i=0; i<len; i++)
			{
				meterX[i] = rSoundMeter.left + i*(radius*2);
				meterY[i] = rSoundMeter.bottom;
				meterValue[i] = 0;
			}
		}
		
		//update sound meter display
		int len = meterValue.length;
		int i;
		for(i = 1; i<len; i++)
		{
			meterY[i-1] = meterY[i];
			meterValue[i-1] = meterValue[i];
		}
		
		meterY[len-1] = (float) (rSoundMeter.bottom - curMax*rSoundMeter.height());
		meterValue[len-1] = (float) curMax;
		
		if(meterY[len-1]>rSoundMeter.bottom)
			meterY[len-1]=rSoundMeter.bottom;
		if(meterY[len-1]<rSoundMeter.top)
			meterY[len-1]=rSoundMeter.top;
		
		float x,y,v, r;
		ptSoundMeter.setStrokeWidth(radius);
		for(i=0; i<len; i++)
		{
			x = meterX[i];
			y = meterY[i];
			v = meterValue[i];
			
			if(v<0.08)
				ptSoundMeter.setColor(cSoundMeterLow);
			else if(v>=0.08 && v<0.3)
				ptSoundMeter.setColor(cSoundMeterMid);
			else
				ptSoundMeter.setColor(cSoundMeterHigh);
			
			r = radius*(1+2*v);	// enlarge the radius for louder frames
			
			cvs.drawLine(x, y, x, rSoundMeter.bottom, ptSoundMeter);
			cvs.drawCircle(x, y, r, ptSoundMeter);				
		}
		
		
		
		if(AppUI.isShowCoughRealTime)
		{
			if(isDrawCough || cntFrameCoughDisp>0)
			{
				cntFrameCoughDisp++;
				
				if(cntFrameCoughDisp<=numFrameCoughDisp)
				{
					Paint ptCough = new Paint();
					ptCough.setColor(AppUI.COLOR_ORANGE);
					ptCough.setAntiAlias(true);
					
					int alpha = 100+cntFrameCoughDisp*15;
					if(alpha>255)
						alpha = 255-(alpha-255);
					
					ptCough.setAlpha(alpha);
					ptCough.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));
					ptCough.setTextSize(AppUI.SIZE_LARGE_TEXT);
					
					String textCough = "Cough Detected";
					
					
					int yCough = rSoundMeter.centerY();
					int xCough = (rSoundMeter.width() - (int)ptCough.measureText(textCough))/2 + rSoundMeter.left;
					
					cvs.drawText(textCough, xCough, yCough, ptCough);
				}
				else
				{
					cntFrameCoughDisp = 0;
				}				
					
			}
				
		}//end of DRAW COUGH
		
		
	}
	
	
	public void setStartTime()
	{
		AppUI.startTime = Calendar.getInstance();
		Calendar startTime = AppUI.startTime;
        int year = startTime.get(Calendar.YEAR);
        int month = startTime.get(Calendar.MONTH);
        int date = startTime.get(Calendar.DATE);
        int hour = startTime.get(Calendar.HOUR_OF_DAY);
        int min = startTime.get(Calendar.MINUTE);
        int sec = startTime.get(Calendar.SECOND);
        
        tStartTimeHrMin = "";
        //tStartTimeHrMin += Integer.toString(date)+"-";
        tStartTimeHrMin += Integer.toString(hour)+":";
        tStartTimeHrMin += Integer.toString(min);
        //tStartTimeHrMin += Integer.toString(sec);     
	}
	
	
	public void addSecondDuration()
	{
		durS++;
		
		if(durS>59)
		{
			durS -=60;
			durM++;
		}
		
		if(durM>59)
		{
			durM-=60;
			durH++;
		}
		
		//update the string of duration
		tDurTimeHrMinSec = " ";
		tDurTimeHrMinSec+=Integer.toString(durH)+":";
		tDurTimeHrMinSec+=Integer.toString(durM)+":";
		tDurTimeHrMinSec+=Integer.toString(durS);
	}
	

	
	/**
	 * set the color of text Monitoring 
	 * called from the timer every 100ms
	 */
	public void changeMonitorColor()
	{
		curColorIndex++;
		
		if(curColorIndex == numColorIndex)
			curColorIndex = 0;
		
		ptMonitor.setColor(cMonitor[curColorIndex]);
	}
	
	
	public MonitorScreen(Rect r, Context context, BitmapDrawable bdBkgrd)
	{
		//set screen size and location
		rScreen = new Rect(r);
		screenH = r.height();
		screenW = r.width();
		x = r.left;
		y = r.top;
		
		//load btn images
		//image = (BitmapDrawable) context.getResources().getDrawable(imageResID);
		//imageHit = (BitmapDrawable) context.getResources().getDrawable(imageHitResID);
		
		bdClock = (BitmapDrawable) context.getResources().getDrawable(R.drawable.clock);

		//create button list
		btnList = new LinkedList<MyButton>();
		
		bdBackground = bdBkgrd;
	}
	
	
	/*private void setEndTime()
	{
		endTime = Calendar.getInstance();
        int year = endTime.get(Calendar.YEAR);
        int month = endTime.get(Calendar.MONTH);
        int date = endTime.get(Calendar.DATE);
        int hour = endTime.get(Calendar.HOUR_OF_DAY);
        int min = endTime.get(Calendar.MINUTE);
        int sec = endTime.get(Calendar.SECOND);
        
        strEndTime = "";
        strEndTime += Integer.toString(date)+"-";
        strEndTime += Integer.toString(hour)+":";
        strEndTime += Integer.toString(min)+":";
        strEndTime += Integer.toString(sec);
        
        //compute total on-bed time
        totalM = min-startTime.get(Calendar.MINUTE);
        if(totalM<0)
        {
        	totalM+=60;
        	hour--;
        }
        totalH = hour-startTime.get(Calendar.HOUR_OF_DAY);
        strTotalTime = "";
        strTotalTime += Integer.toString(totalH)+" hrs, "
        				+ Integer.toString(totalM)+" mins";
        
        timer.cancel();
        timer = null;
        timerTask = null;
	}*/
	
}
