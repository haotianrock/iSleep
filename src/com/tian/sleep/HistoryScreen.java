package com.tian.sleep;

import java.util.Iterator;
import java.util.LinkedList;

import com.tian.sleep.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.widget.Toast;

public class HistoryScreen extends Screen
{
	boolean isMenuExpanded = false;
	boolean hasMenu = true;
	
	boolean isInfoHit = false;
	
	DisplayStatus curScreen = DisplayStatus.SCREEN_HISTORY_QUALITY;
	DisplayStatus subScreen = DisplayStatus.UPDATE_HISTORY_WEEK;
	
	Context parentContext;
	
	
	/********* Fonts **********/
	//font initialization is required 	
	Paint.FontMetricsInt fontEffInfo = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt fontEffNum = AppUI.FONT_LARGE;
	Paint.FontMetricsInt fontBtn = AppUI.FONT_MID;
	Paint.FontMetricsInt fontDetailInfo = AppUI.FONT_SMALL;
	
	Paint.FontMetricsInt fontBarDetailInfo = AppUI.FONT_NORMAL;
	

	/********** Btn Images ***********/
	/*int imageWeekResID = R.drawable.btn_blue;
	int imageWeekHitResID = R.drawable.btn_grey;
	BitmapDrawable imageWeek = null;
	BitmapDrawable imageWeekHit = null;
	
	int imageMonthResID = R.drawable.btn_orange;
	int imageMonthHitResID = R.drawable.btn_grey;
	BitmapDrawable imageMonth = null;
	BitmapDrawable imageMonthHit = null;
	
	int imageYearResID = R.drawable.btn_red;
	int imageYearHitResID = R.drawable.btn_grey;
	BitmapDrawable imageYear = null;
	BitmapDrawable imageYearHit = null;*/
	
	int imageInfoResID = R.drawable.icon_info;
	int imageInfoHitResID = R.drawable.icon_info_hit;
	BitmapDrawable imageInfo = null;
	BitmapDrawable imageInfoHit = null;
	
	/*int imageQualityResID = R.drawable.btn_green;
	int imageQualityHitResID = R.drawable.btn_grey;
	BitmapDrawable imageQuality = null;
	BitmapDrawable imageQualityHit = null;
	
	int imageEventResID = R.drawable.btn_green;
	int imageEventHitResID = R.drawable.btn_grey;
	BitmapDrawable imageEvent = null;
	BitmapDrawable imageEventHit = null;*/
	
	
	/********** Touchable Area ***********/
	Rect touchableQualityBar;
	Rect touchableEventBar;
	int xQualityBar = -1;
	int yQualityBar = -1;
	int xEventBar = -1;
	int yEventBar = -1;
	
	
	/********* Images *********/	
	
	
	
	/********* Paints **********/
	Paint ptNormalInfo;
	Paint ptEffNum;
	Paint ptEffInfo;
	Paint ptDetailInfo;
	Paint ptBtnText;
	
	
	/********** Color ***********/
	int cEffInfo = Color.WHITE;
	int cEffNum = AppUI.COLOR_PINK;
	int cDetailInfo = Color.WHITE;
	int cNormalInfo = Color.WHITE;
	int cBtnText = Color.WHITE;
	
	int cQualityBarWeek = AppUI.COLOR_PINK;
	int cQualityBarMonth = AppUI.COLOR_PINK;
	int cQualityBarYear = AppUI.COLOR_PINK;
	
	int cEventBarMove = AppUI.COLOR_BLUE;
	int cEventBarSnore = AppUI.COLOR_ORANGE;
	int cEventBarCough = AppUI.COLOR_RED;
	
	int cBarDetailBkg = AppUI.COLOR_GREY_BKG;
	
	/********* Texts **********/
	String tEffInfo = "Overall sleep efficiency over the past week:";
	String tEffNum = "00%";
	
	String tMonthBtn = "Month";
	String tWeekBtn = "Week";
	String tYearBtn = "Year";
	String tQualityBtn = "Quality";
	String tEventBtn = "Event";
	
	String tBtnTimeInfo = "Click to switch between time periods.";
	String tBtnTypeInfo = "Click to switch between history types.";

	
	
	/********* Locations **********/
	Point pEffInfo;
	Point pEffNum;
	
	Point pWeekBtn;
	Point pMonthBtn;
	Point pYearBtn;
	Point pQualityBtn;
	Point pEventBtn;
	
	Point pBtnTimeInfo;
	Point pBtnTypeInfo;
	
	/********** buttons ***********/	
	Rect rQualityBtn;
	Rect rEventBtn;
	Rect rWeekBtn;
	Rect rMonthBtn;
	Rect rYearBtn;
	Rect rInfoBtn;
		
	
	/********** graph ***********/
	float barMargin = 3;	// bar margin for week and month
	Rect rQualityGraph;
	Rect rMoveGraph;
	Rect rSnoreGraph;
	Rect rCoughGraph;
	
	
	BitmapDrawable bdBackground;
	
	
	public void initialize()
	{
    	//set spacing info
		int smallSpc = (int) AppUI.SIZE_SMALL_TEXT;
		int normalSpc = (int) AppUI.SIZE_NORMAL_TEXT;
		int midSpc = (int) AppUI.SIZE_MID_TEXT;
		int largeSpc = (int) AppUI.SIZE_LARGE_TEXT;
		
		//load text
		double effnum = HistoryData.overallEffWeek;
		if(effnum==-2)
			tEffNum = "N/A";
		else
		{
			int effPercent = (int)Math.floor(effnum*100);
			tEffNum = Integer.toString(effPercent) + "%";
		}
		
		//------------------ Btn area Location ------------------//
		int hBtnText = fontBtn.descent - fontBtn.ascent;
		int hBtnDetailText = fontDetailInfo.descent - fontDetailInfo.ascent;
		int marginBtnText = hBtnText/3;
		int hBtn = hBtnText + 2*marginBtnText;
		int spcBtn1 = smallSpc/4;	//spc btw btn and detailed info 
		int spcBtn2 = smallSpc;	//spc btw btns
		int spcBtn3 = smallSpc/2;		//spc btw btn and screen side
		int spcBtn4 = smallSpc;		//spc btw detail info and screen btm
		int spcBtn5 = smallSpc/2;		//spc btw time and type buttons
		
		int wTimeBtn = (screenW - 2*spcBtn2 - 2*spcBtn3)/3;
		int wTypeBtn = (screenW - spcBtn2 - 2*spcBtn3)/2;
		
		int topBtnArea = y + screenH - (2*hBtn + 2*hBtnDetailText + 2*spcBtn1 + spcBtn4 + spcBtn5);
		
		//buttons
		rWeekBtn = new Rect();
		rWeekBtn.left = x+ spcBtn3;
		rWeekBtn.right = rWeekBtn.left + wTimeBtn;
		rWeekBtn.top = topBtnArea;
		rWeekBtn.bottom = rWeekBtn.top + hBtn;
		
		rMonthBtn = new Rect();
		rMonthBtn.left = rWeekBtn.right + spcBtn2;
		rMonthBtn.right = rMonthBtn.left + wTimeBtn;
		rMonthBtn.top = topBtnArea;
		rMonthBtn.bottom = rMonthBtn.top + hBtn;
		
		rYearBtn = new Rect();
		rYearBtn.left = rMonthBtn.right + spcBtn2;
		rYearBtn.right = rYearBtn.left + wTimeBtn;
		rYearBtn.top = topBtnArea;
		rYearBtn.bottom = rYearBtn.top + hBtn;
		
		rQualityBtn = new Rect();
		rQualityBtn.left = x+ spcBtn3;
		rQualityBtn.right = rQualityBtn.left + wTypeBtn;
		rQualityBtn.top = topBtnArea + hBtn + hBtnDetailText + spcBtn1 + spcBtn4;
		rQualityBtn.bottom = rQualityBtn.top + hBtn;
		
		rEventBtn = new Rect();
		rEventBtn.left = rQualityBtn.right + spcBtn2;
		rEventBtn.right = rEventBtn.left + wTypeBtn;
		rEventBtn.top = rQualityBtn.top;
		rEventBtn.bottom = rQualityBtn.bottom;
		
		//button text
		ptBtnText = new Paint();
		ptBtnText.setAntiAlias(true);
		ptBtnText.setColor(cBtnText); 
		ptBtnText.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptBtnText.setTextSize(hBtnText);
		
		pWeekBtn = new Point();
		pWeekBtn.y = rWeekBtn.top + marginBtnText - fontBtn.ascent;
		pWeekBtn.x = rWeekBtn.left + (wTimeBtn-(int) ptBtnText.measureText(tWeekBtn))/2;
		
		pMonthBtn = new Point();
		pMonthBtn.y = rMonthBtn.top + marginBtnText - fontBtn.ascent;
		pMonthBtn.x = rMonthBtn.left + (wTimeBtn-(int) ptBtnText.measureText(tMonthBtn))/2;
		
		pYearBtn = new Point();
		pYearBtn.y = rYearBtn.top + marginBtnText - fontBtn.ascent;
		pYearBtn.x = rYearBtn.left + (wTimeBtn-(int) ptBtnText.measureText(tYearBtn))/2;
		
		pQualityBtn = new Point();
		pQualityBtn.y = rQualityBtn.top + marginBtnText - fontBtn.ascent;
		pQualityBtn.x = rQualityBtn.left + (wTypeBtn-(int) ptBtnText.measureText(tQualityBtn))/2;
		
		pEventBtn = new Point();
		pEventBtn.y = rEventBtn.top + marginBtnText - fontBtn.ascent;
		pEventBtn.x = rEventBtn.left + (wTypeBtn-(int) ptBtnText.measureText(tEventBtn))/2;
		
		//button info text
		ptDetailInfo = new Paint();
		ptDetailInfo.setAntiAlias(true);
		ptDetailInfo.setColor(cDetailInfo); 
		ptDetailInfo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptDetailInfo.setTextSize(hBtnDetailText);
		
		pBtnTimeInfo = new Point();
		pBtnTimeInfo.y = rWeekBtn.bottom + spcBtn1 - fontDetailInfo.ascent;
		pBtnTimeInfo.x = (screenW - (int)ptDetailInfo.measureText(tBtnTimeInfo))/2 + x;
		
		pBtnTypeInfo = new Point();
		pBtnTypeInfo.y = rQualityBtn.bottom + spcBtn1 - fontDetailInfo.ascent;
		pBtnTypeInfo.x = (screenW - (int)ptDetailInfo.measureText(tBtnTypeInfo))/2 + x;
		
		
		//------------------ Eff info ------------------//
		int spcEffTop = largeSpc;
		int hEffInfo = fontEffInfo.descent - fontEffInfo.ascent;
		int hEffNum = fontEffNum.descent - fontEffNum.ascent;
		int spcEff1 = midSpc;	//spc btw eff num and info btn
		int spcEff2 = smallSpc/2;	//spc btw effinfo and effnum
		int btmEffArea = y + hEffInfo + hEffNum + spcEff1 + spcEff2;
		
		//Eff paint
		ptEffNum = new Paint();
		ptEffNum.setAntiAlias(true);
		ptEffNum.setColor(cEffNum); 
		ptEffNum.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptEffNum.setTextSize(hEffNum);
		
		ptEffInfo = new Paint();
		ptEffInfo.setAntiAlias(true);
		ptEffInfo.setColor(cEffInfo); 
		ptEffInfo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptEffInfo.setTextSize(hEffInfo);
		
		//Eff loc
		pEffInfo = new Point();
		pEffInfo.y = y+spcEffTop-fontEffInfo.ascent;
		pEffInfo.x = (screenW - (int)ptEffInfo.measureText(tEffInfo))/2 + x;
		
		pEffNum = new Point();
		pEffNum.y = y+spcEffTop+hEffInfo+spcEff2-fontEffNum.ascent;
		pEffNum.x = (screenW - (int)ptEffNum.measureText(tEffNum))/2 + x;
		
		
		//------------- info btn ---------------//
		int sizeInfoBtn = (int) AppUI.SIZE_LARGE_TEXT;
		int spcInfo1 = smallSpc/2;	//spc btw info btn and other btns
		int spcInfo2 = smallSpc/2;	//spc btw info btn and graph
		
		rInfoBtn = new Rect();
		rInfoBtn.left = x + (screenW - sizeInfoBtn)/2;
		rInfoBtn.right = rInfoBtn.left + sizeInfoBtn;		
		rInfoBtn.bottom = topBtnArea - spcInfo1;
		rInfoBtn.top = rInfoBtn.bottom - sizeInfoBtn;
		
		
		//------------------- graph area -----------------------//
		int graphSideMargin = smallSpc;
		int graphTopMargin = smallSpc;	//spc btw the graph and the element on its top
		int spcBtwGraph = normalSpc;	//spc between event graphs
		
		rQualityGraph = new Rect();
		rQualityGraph.left = graphSideMargin+x;
		rQualityGraph.right = x+screenW-graphSideMargin;
		rQualityGraph.top = graphTopMargin+btmEffArea;
		rQualityGraph.bottom = rInfoBtn.top - spcInfo2;
		
		touchableQualityBar = rQualityGraph;
		
		
		touchableEventBar = new Rect();
		touchableEventBar.left = touchableQualityBar.left;
		touchableEventBar.right = touchableQualityBar.right;
		touchableEventBar.top = (int)AppUI.SIZE_LARGE_TEXT+ spcBtwGraph +y;//avoid blocking the menu btn
		touchableEventBar.bottom = rQualityGraph.bottom;
		
		int eventGraphHeight = (touchableEventBar.height()-2*spcBtwGraph)/3;
		
		rMoveGraph = new Rect();
		rMoveGraph.left = graphSideMargin+x;
		rMoveGraph.right = x+screenW-graphSideMargin;
		rMoveGraph.top = touchableEventBar.top;	
		rMoveGraph.bottom = rMoveGraph.top + eventGraphHeight;
		
		rSnoreGraph = new Rect();
		rSnoreGraph.left = graphSideMargin+x;
		rSnoreGraph.right = x+screenW-graphSideMargin;
		rSnoreGraph.top = rMoveGraph.bottom + spcBtwGraph;
		rSnoreGraph.bottom = rSnoreGraph.top + eventGraphHeight;
		
		rCoughGraph = new Rect();
		rCoughGraph.left = graphSideMargin+x;
		rCoughGraph.right = x+screenW-graphSideMargin;
		rCoughGraph.top = rSnoreGraph.bottom + spcBtwGraph;
		rCoughGraph.bottom = rCoughGraph.top + eventGraphHeight;
		
		
		
		/************* Initialize Buttons **************/
		MyButton btn = new MyButton(ButtonID.HISTORY_INFO, rInfoBtn);
		btn.setImage(imageInfo, imageInfoHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.HISTORY_WEEK, rWeekBtn);
		//btn.setImage(imageWeek, imageWeekHit);
		btn.setColor(AppUI.COLOR_GREY_BKG, AppUI.COLOR_BLUE);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btn.isSelected = true;
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.HISTORY_MONTH, rMonthBtn);
		//btn.setImage(imageMonth, imageMonthHit);
		btn.setColor(AppUI.COLOR_GREY_BKG, AppUI.COLOR_BLUE);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.HISTORY_YEAR, rYearBtn);
		//btn.setImage(imageYear, imageYearHit);
		btn.setColor(AppUI.COLOR_GREY_BKG, AppUI.COLOR_BLUE);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.HISTORY_QUALITY, rQualityBtn);
		//btn.setImage(imageQuality, imageQualityHit);
		btn.setColor(AppUI.COLOR_GREY_BKG, AppUI.COLOR_PINK);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btn.isSelected = true;
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.HISTORY_EVENT, rEventBtn);
		//btn.setImage(imageEvent, imageEventHit);
		btn.setColor(AppUI.COLOR_GREY_BKG, AppUI.COLOR_PINK);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btnList.add(btn);

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
		

		/***************** Paint Text ******************/		
		cvs.drawText(tWeekBtn, pWeekBtn.x,  pWeekBtn.y, ptBtnText);	
		cvs.drawText(tMonthBtn, pMonthBtn.x,  pMonthBtn.y, ptBtnText);	
		cvs.drawText(tYearBtn, pYearBtn.x,  pYearBtn.y, ptBtnText);	
		cvs.drawText(tQualityBtn, pQualityBtn.x,  pQualityBtn.y, ptBtnText);	
		cvs.drawText(tEventBtn, pEventBtn.x,  pEventBtn.y, ptBtnText);	
		
		cvs.drawText(tBtnTimeInfo, pBtnTimeInfo.x, pBtnTimeInfo.y, ptDetailInfo);	
		cvs.drawText(tBtnTypeInfo, pBtnTypeInfo.x, pBtnTypeInfo.y, ptDetailInfo);	
		
		
		//--------------- display QUALITY HISTORY ------------------//
		if(curScreen==DisplayStatus.SCREEN_HISTORY_QUALITY)
		{
			cvs.drawText(tEffInfo, pEffInfo.x, pEffInfo.y, ptEffInfo);	
			cvs.drawText(tEffNum, pEffNum.x, pEffNum.y, ptEffNum);
			
			if(subScreen==DisplayStatus.UPDATE_HISTORY_WEEK)
			{
				drawQualityBars(rQualityGraph, HistoryData.effWeek, cvs, cQualityBarWeek);
			}
			
			else if(subScreen==DisplayStatus.UPDATE_HISTORY_MONTH)
			{
				drawQualityBars(rQualityGraph, HistoryData.effMonth, cvs, cQualityBarMonth);
			}
			
			else if(subScreen==DisplayStatus.UPDATE_HISTORY_YEAR)
			{
				drawQualityBars(rQualityGraph, HistoryData.effYear, cvs, cQualityBarYear);
			}
			
			if(xQualityBar>0 && yQualityBar>0)
			{
				displayQualityBarDetail(cvs);
			}
		}
		
		//--------------- display EVENTS HISTORY ------------------//
		else if(curScreen==DisplayStatus.SCREEN_HISTORY_EVENT)
		{
			if(subScreen==DisplayStatus.UPDATE_HISTORY_WEEK)
			{
				drawQualityBars(rMoveGraph, HistoryData.evtMoveWeek, cvs, cEventBarMove);
				drawQualityBars(rSnoreGraph, HistoryData.evtSnoreWeek, cvs, cEventBarSnore);
				drawQualityBars(rCoughGraph, HistoryData.evtCoughWeek, cvs, cEventBarCough);
			}
			
			else if(subScreen==DisplayStatus.UPDATE_HISTORY_MONTH)
			{
				drawQualityBars(rMoveGraph, HistoryData.evtMoveMonth, cvs, cEventBarMove);
				drawQualityBars(rSnoreGraph, HistoryData.evtSnoreMonth, cvs, cEventBarSnore);
				drawQualityBars(rCoughGraph, HistoryData.evtCoughMonth, cvs, cEventBarCough);
			}
			
			else if(subScreen==DisplayStatus.UPDATE_HISTORY_YEAR)
			{
				drawQualityBars(rMoveGraph, HistoryData.evtMoveYear, cvs, cEventBarMove);
				drawQualityBars(rSnoreGraph, HistoryData.evtSnoreYear, cvs, cEventBarSnore);
				drawQualityBars(rCoughGraph, HistoryData.evtCoughYear, cvs, cEventBarCough);
			}
			
			if(xEventBar>0 && yEventBar>0)
			{
				displayQualityBarDetail(cvs);
			}
		}
			
		
		/***************** display info in toast ********************/
		if(isInfoHit)
		{
			CharSequence text = null;
			CharSequence textEvent = "Slide your Finger on the bar area to reveal detail.\n\nThe height of the bar indicates the intensity of the events.";
			CharSequence textQuality = "Slide your Finger on the bar area to reveal detail.\n\nThe height of the bar indicates the degree of your sleep efficiency."; 
			int duration = Toast.LENGTH_LONG;

			if(curScreen==DisplayStatus.SCREEN_HISTORY_EVENT)
				text = textEvent;
			else if(curScreen==DisplayStatus.SCREEN_HISTORY_QUALITY)
				text = textQuality;
			Toast toast = Toast.makeText(parentContext, text, duration);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.show();
			
			isInfoHit = false;
		}
	}
	
	
	private void displayQualityBarDetail(Canvas cvs)
	{
		int sizeText = fontBarDetailInfo.descent - fontBarDetailInfo.ascent;
		
		if(curScreen==DisplayStatus.SCREEN_HISTORY_QUALITY)
		{
			int x = xQualityBar;
			int y = yQualityBar;
			
			Rect barArea = rQualityGraph;
			int barWidth=1;
			double[] valueBars = null;
			String strTime = null;
			
			switch(subScreen)
			{
			case UPDATE_HISTORY_WEEK:
				valueBars = HistoryData.effWeek;
				barWidth = barArea.width()/MyTime.DaysPerWeek;
				strTime = "Day ";
				break;
			case UPDATE_HISTORY_MONTH:
				valueBars = HistoryData.effMonth;
				barWidth = barArea.width()/MyTime.DaysPerMonth;
				strTime = "Day ";
				break;
			case UPDATE_HISTORY_YEAR:
				valueBars = HistoryData.effYear;
				barWidth = barArea.width()/MyTime.WeeksPerYear;
				strTime = "Week ";
				break;
			}	
			
			//get value
			int curBarIndex = (int)((xQualityBar-barArea.left)/barWidth);
			if(curBarIndex>=valueBars.length)
				curBarIndex = valueBars.length-1;
			else if(curBarIndex<0)
				curBarIndex = 0;
			float value = (float) valueBars[curBarIndex];
			if(value==-2)
				value = 0;
			int intPercent = (int) Math.ceil(value*100);
			
			//prepare detail text
			String strStatus = "Sleep Eff.";
			String strPercent = Integer.toString(intPercent) + "%";
			strTime = strTime+Integer.toString(curBarIndex+1);
			
			//paint and measure
			int txtHeight = fontBarDetailInfo.descent - fontBarDetailInfo.ascent;		
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			paint.setTextSize(sizeText);
			
			int maxTextWidth = (int)paint.measureText(strStatus);
			int areaHeight = 3*sizeText;
			
			//draw detail rect area
			Rect rBarDetail = new Rect();
			rBarDetail.left = x - maxTextWidth;
			if(rBarDetail.left<=0)
				rBarDetail.left = 0;
			rBarDetail.right = rBarDetail.left + maxTextWidth;		
			rBarDetail.top = barArea.top;
			rBarDetail.bottom = (int) barArea.top + areaHeight;
			
			Paint actiPaint = new Paint();
			actiPaint.setStyle(Paint.Style.FILL);
			actiPaint.setColor(AppUI.COLOR_GREY_BKG);
			actiPaint.setAlpha(AppUI.ALPHA_DETAILRECT);
			cvs.drawRect(rBarDetail, actiPaint);
			
			//draw dotted line
			/*actiPaint.setStrokeWidth(3f);
			actiPaint.setStyle(Style.STROKE);
			actiPaint.setPathEffect(new DashPathEffect(new float[] {10,5}, 0));
			cvs.drawLine(x, barArea.top, x, barArea.bottom, actiPaint);*/
			
			//print text of the detail info
			float xLine = rBarDetail.left + (rBarDetail.width()-paint.measureText(strTime))/2;
			float yLine = rBarDetail.top - fontBarDetailInfo.ascent;
			cvs.drawText(strTime, xLine, yLine, paint);
			
			paint.setColor(AppUI.COLOR_PINK);
			xLine = rBarDetail.left + (rBarDetail.width()-paint.measureText(strStatus))/2;
			yLine = yLine + sizeText;
			cvs.drawText(strStatus, xLine, yLine, paint);
			paint.setColor(Color.WHITE);
			
			paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
			xLine = rBarDetail.left + (rBarDetail.width()-paint.measureText(strPercent))/2;
			yLine = yLine + sizeText;
			cvs.drawText(strPercent, xLine, yLine, paint);	
		}
		
		else if(curScreen == DisplayStatus.SCREEN_HISTORY_EVENT)
		{
			int x = xEventBar;
			int y = yEventBar;
			
			Rect areaMove = rMoveGraph;
			Rect areaSnore = rSnoreGraph;
			Rect areaCough = rCoughGraph;
			
			int spcBtwArea = rSnoreGraph.top - rMoveGraph.bottom;
			
			////////////
			int barWidth=1;
			double[][] valueBars = null;
			valueBars = new double[3][];	//three bar areas
			String strTime = null;
			String[] strType = {"Move", "Snore", "Cough"};
			
			switch(subScreen)
			{
			case UPDATE_HISTORY_WEEK:
				valueBars[0] = HistoryData.evtMoveWeek;
				valueBars[1] = HistoryData.evtSnoreWeek;
				valueBars[2] = HistoryData.evtCoughWeek;
				
				barWidth = areaMove.width()/MyTime.DaysPerWeek;
				strTime = "Day ";
				break;
			case UPDATE_HISTORY_MONTH:
				valueBars[0] = HistoryData.evtMoveMonth;
				valueBars[1] = HistoryData.evtSnoreMonth;
				valueBars[2] = HistoryData.evtCoughMonth;
			
				barWidth = areaMove.width()/MyTime.DaysPerMonth;
				strTime = "Day ";
				break;
			case UPDATE_HISTORY_YEAR:
				valueBars[0] = HistoryData.evtMoveYear;
				valueBars[1] = HistoryData.evtSnoreYear;
				valueBars[2] = HistoryData.evtCoughYear;
				
				barWidth = areaMove.width()/MyTime.WeeksPerYear;
				strTime = "Week ";
				break;
			}	
			
			//get value
			int curBarIndex = (int)((xEventBar-areaMove.left)/barWidth);
			if(curBarIndex>=valueBars[0].length)
				curBarIndex = valueBars[0].length-1;
			else if(curBarIndex<0)
				curBarIndex = 0;
			double[] value = {valueBars[0][curBarIndex], valueBars[1][curBarIndex], valueBars[2][curBarIndex]};
			if(value[0]==-2)
				value[0] = 0;
			if(value[1]==-2)
				value[1] = 0;
			if(value[2]==-2)
				value[2] = 0;
			strTime = strTime+Integer.toString(curBarIndex+1);
			String[] strLevel = new String[3];
			int[] intPercent = new int[3];
			
			for(int i=0; i<3; i++)
			{
				intPercent[i] = (int)Math.ceil(value[i]*10);
				strLevel[i] = Integer.toString(intPercent[i])+"/10";
			}
			
			//paint and measure	
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			paint.setTextSize(sizeText);			
			int maxTextWidth = (int)paint.measureText("Week  52");
			
			//Draw detail rect
			int rectHeight = 3*sizeText;		
			Rect rMoveDetail = new Rect();
			rMoveDetail.left = x - maxTextWidth;
			if(rMoveDetail.left<=0)
				rMoveDetail.left = 0;
			rMoveDetail.right = rMoveDetail.left + maxTextWidth;				
			rMoveDetail.top = (int) areaMove.top - spcBtwArea;
			//rMoveDetail.top = this.y;
			rMoveDetail.bottom = rMoveDetail.top + rectHeight;
			
			Rect rSnoreDetail = new Rect();
			rSnoreDetail.left = x - maxTextWidth;
			if(rSnoreDetail.left<=0)
				rSnoreDetail.left = 0;
			rSnoreDetail.right = rSnoreDetail.left + maxTextWidth;				
			rSnoreDetail.top = (int) areaSnore.top - spcBtwArea;				
			rSnoreDetail.bottom = rSnoreDetail.top + rectHeight;
			
			Rect rCoughDetail = new Rect();
			rCoughDetail.left = x - maxTextWidth;
			if(rCoughDetail.left<=0)
				rCoughDetail.left = 0;
			rCoughDetail.right = rCoughDetail.left + maxTextWidth;				
			rCoughDetail.top = (int) areaCough.top - spcBtwArea;				
			rCoughDetail.bottom = rCoughDetail.top + rectHeight;
			
			Paint ptEventDetail = new Paint();
			ptEventDetail.setStyle(Paint.Style.FILL);
			ptEventDetail.setColor(AppUI.COLOR_GREY_BKG);
			ptEventDetail.setAlpha(AppUI.ALPHA_DETAILRECT);
			cvs.drawRect(rMoveDetail, ptEventDetail);
			cvs.drawRect(rSnoreDetail, ptEventDetail);
			cvs.drawRect(rCoughDetail, ptEventDetail);
		
			//draw dotted line
			/*ptEventDetail.setStrokeWidth(3f);
			ptEventDetail.setStyle(Style.STROKE);
			ptEventDetail.setPathEffect(new DashPathEffect(new float[] {10,5}, 0));
			cvs.drawLine(x, 0, x, touchableEventBar.bottom, ptEventDetail);*/
			
			//draw detailed text
			float xLine, yLine;
			
			//move text
			xLine = rMoveDetail.left + (rMoveDetail.width()-paint.measureText(strTime))/2;
			yLine = rMoveDetail.top - fontBarDetailInfo.ascent;
			cvs.drawText(strTime, xLine, yLine, paint);	
			
			paint.setColor(cEventBarMove);
			xLine = rMoveDetail.left + (rMoveDetail.width()-paint.measureText(strType[0]))/2;
			yLine = yLine + sizeText;
			cvs.drawText(strType[0], xLine, yLine, paint);
			paint.setColor(Color.WHITE);
			
			paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
			xLine = rMoveDetail.left + (rMoveDetail.width()-paint.measureText(strLevel[0]))/2;
			yLine = yLine + sizeText;
			cvs.drawText(strLevel[0], xLine, yLine, paint);		
			paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			
			//snore text
			xLine = rSnoreDetail.left + (rMoveDetail.width()-paint.measureText(strTime))/2;
			yLine = rSnoreDetail.top - fontBarDetailInfo.ascent;
			cvs.drawText(strTime, xLine, yLine, paint);		
			
			paint.setColor(cEventBarSnore);
			xLine = rSnoreDetail.left + (rMoveDetail.width()-paint.measureText(strType[1]))/2;
			yLine = yLine + sizeText;
			cvs.drawText(strType[1], xLine, yLine, paint);		
			paint.setColor(Color.WHITE);
			
			paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
			xLine = rSnoreDetail.left + (rMoveDetail.width()-paint.measureText(strLevel[1]))/2;
			yLine = yLine + sizeText;
			cvs.drawText(strLevel[1], xLine, yLine, paint);	
			paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			
			//cough text
			xLine = rCoughDetail.left + (rMoveDetail.width()-paint.measureText(strTime))/2;
			yLine = rCoughDetail.top - fontBarDetailInfo.ascent;
			cvs.drawText(strTime, xLine, yLine, paint);	
			
			paint.setColor(cEventBarCough);
			xLine = rCoughDetail.left + (rMoveDetail.width()-paint.measureText(strType[2]))/2;
			yLine = yLine + sizeText;
			cvs.drawText(strType[2], xLine, yLine, paint);	
			paint.setColor(Color.WHITE);
			
			paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
			xLine = rCoughDetail.left + (rMoveDetail.width()-paint.measureText(strLevel[2]))/2;
			yLine = yLine + sizeText;
			cvs.drawText(strLevel[2], xLine, yLine, paint);	
			paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			
				
		}//EOF draw bar details
	}
	
	
	public HistoryScreen(Rect r, Context context, BitmapDrawable bdBkgrd)
	{
		//set screen size and location
		rScreen = new Rect(r);
		screenH = r.height();
		screenW = r.width();
		x = r.left;
		y = r.top;
		
		parentContext = context;
		
		bdBackground = bdBkgrd;
	
		
		//load btn images
		/*imageWeek = (BitmapDrawable) context.getResources().getDrawable(imageWeekResID);
		imageWeekHit = (BitmapDrawable) context.getResources().getDrawable(imageWeekHitResID);
		
		imageMonth = (BitmapDrawable) context.getResources().getDrawable(imageMonthResID);
		imageMonthHit = (BitmapDrawable) context.getResources().getDrawable(imageMonthHitResID);
		
		imageYear = (BitmapDrawable) context.getResources().getDrawable(imageYearResID);
		imageYearHit = (BitmapDrawable) context.getResources().getDrawable(imageYearHitResID);
		
		imageQuality = (BitmapDrawable) context.getResources().getDrawable(imageQualityResID);
		imageQualityHit = (BitmapDrawable) context.getResources().getDrawable(imageQualityHitResID);
		
		imageEvent = (BitmapDrawable) context.getResources().getDrawable(imageEventResID);
		imageEventHit = (BitmapDrawable) context.getResources().getDrawable(imageEventHitResID);*/
		
		imageInfo = (BitmapDrawable) context.getResources().getDrawable(imageInfoResID);
		imageInfoHit = (BitmapDrawable) context.getResources().getDrawable(imageInfoHitResID);

		//create button list
		btnList = new LinkedList<MyButton>();
	}
	
	
	private void drawQualityBars(Rect area, double[] barVals, Canvas cvs, int barColor)
	{
		
		int num_bars = barVals.length;
		
		//barMargin = 3;
		barMargin = 0;
		
		if(num_bars==52)
			barMargin = 0;
		
		float barWidth = (area.width()-(num_bars-1)*barMargin)/num_bars;
		
		Paint paint  = new Paint();
		paint.setColor(barColor);
		
		Paint linePaint  = new Paint();
		linePaint.setColor(Color.RED);
		linePaint.setStrokeWidth(2);
		
		RectF rBar = new RectF();
		
		//peak points
		float [] px = new float[num_bars];
		float [] py = new float[num_bars];
		
		float f;
		
		float rPeakPoint = AppUI.SIZE_SMALL_TEXT/2;
		if(2*rPeakPoint>barWidth)
			rPeakPoint = barWidth/2;
		
		for(int i=0; i<num_bars; i++)
		{	
			f = (float) barVals[i];
			
			px[i] = area.left + barWidth/2+barWidth*i;
			py[i] = area.bottom-area.height()*f;
			
			rBar.left = area.left + i*(barWidth+barMargin);
			rBar.right = rBar.left + barWidth;
			rBar.bottom = area.bottom;
			rBar.top = area.top;
			
			if(f==-2)
			{
				//draw cross at bottom if no value available
				cvs.drawLine(rBar.left, rBar.bottom-barWidth, rBar.right, rBar.bottom, linePaint);
				cvs.drawLine(rBar.left, rBar.bottom, rBar.right, rBar.bottom-barWidth, linePaint);
				f = 0;
			
				if(i==0)
				{
					Paint txtPaint = new Paint();
					txtPaint.setColor(AppUI.COLOR_GREY_BKG);
					txtPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
					txtPaint.setTextSize(AppUI.SIZE_NORMAL_TEXT);
					cvs.drawText("More data is required to display.", area.left, area.top+area.height()/2, txtPaint);
				}
			}
			else
			{		
				paint.setAlpha(AppUI.ALPHA_PEAKPOINT);
				cvs.drawCircle(px[i], py[i], rPeakPoint, paint);
			}	
				
			
		}
		
		/*for(int i = 0; i<num_bars; i++)
		{
			//barVals[i] = (float)Math.random();
			
			rBar.left = area.left + i*(barWidth+barMargin);
			rBar.right = rBar.left + barWidth;
			rBar.bottom = area.bottom;
			
			value = barVals[i];
			if(value==-2)
			{
				//draw cross at bottom if no value available
				cvs.drawLine(rBar.left, rBar.bottom-barWidth, rBar.right, rBar.bottom, linePaint);
				cvs.drawLine(rBar.left, rBar.bottom, rBar.right, rBar.bottom-barWidth, linePaint);
				value = 0;
			
				if(i==0)
				{
					Paint txtPaint = new Paint();
					txtPaint.setColor(AppUI.COLOR_GREY_BKG);
					txtPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
					txtPaint.setTextSize(AppUI.SIZE_NORMAL_TEXT);
					cvs.drawText("More data is required to display.", area.left, area.top+area.height()/2, txtPaint);
				}
			}
			else
			{
				rBar.top = (float) (area.bottom - area.height()*value);		
				paint.setAlpha(160);
				cvs.drawRect(rBar, paint);
			}
		}*/
		
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setAlpha(100);
		paint.setStrokeWidth(2);
		paint.setPathEffect(new DashPathEffect(new float[] {6,3}, 0));
		for(int i=1; i<num_bars; i++)
		{
			if(py[i]>area.bottom)
				break;
			
			cvs.drawLine(px[i-1], py[i-1], px[i], py[i], paint);
		}
		
		
		
		int x = -1;
		int y = -1;
		
		if(curScreen == DisplayStatus.SCREEN_HISTORY_EVENT)
		{
			x = xEventBar;
			y = yEventBar;
		}
		else if(curScreen == DisplayStatus.SCREEN_HISTORY_QUALITY)
		{
			x = xQualityBar;
			y = yQualityBar;
		}
		
			
		if(x==-1||y==-1)
			return;
			
		
		int curBarIndex = (int)((x-area.left)/barWidth);		
		if(curBarIndex>=barVals.length)
			curBarIndex = barVals.length-1;
		else if(curBarIndex<0)
			curBarIndex = 0;
		
		// draw selection bar
		Paint ptSelection = new Paint();
		
		ptSelection.setStyle(Paint.Style.FILL);
		ptSelection.setColor(AppUI.COLOR_BANANA);
		ptSelection.setAlpha(AppUI.ALPHA_DETAILRECT/2);	
		
		rBar.left = area.left + curBarIndex*(barWidth+barMargin);
		rBar.right = rBar.left + barWidth;
		rBar.bottom = area.bottom;
		rBar.top = area.top;
		
		float margin = barWidth/2-rPeakPoint;
		rBar.left = rBar.left + margin;
		rBar.right = rBar.right - margin;
		
		cvs.drawRect(rBar, ptSelection);
		
		ptSelection.setStyle(Paint.Style.STROKE);
		ptSelection.setColor(AppUI.COLOR_LIME);
		ptSelection.setAlpha(120);
		
		float strokeW = (float) (rPeakPoint*1.5);
		if(strokeW>AppUI.SIZE_SMALL_TEXT)
			strokeW = AppUI.SIZE_SMALL_TEXT;
		ptSelection.setStrokeWidth(strokeW);
		float rSelect = rPeakPoint + ptSelection.getStrokeWidth()/2;
	
		cvs.drawCircle(px[curBarIndex], py[curBarIndex], rSelect, ptSelection);
		
		//draw line
		ptSelection.setStrokeWidth(rPeakPoint*2);
		ptSelection.setAlpha(AppUI.ALPHA_DETAILRECT);
		cvs.drawLine(px[curBarIndex], rBar.bottom, px[curBarIndex], py[curBarIndex]+rPeakPoint, ptSelection);
					
		
		px = null;
		py = null;
	}
	
	
	public void clearTimeBtnSelect()
    {
		Iterator<MyButton> iterator = btnList.iterator();
		MyButton btn;
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			
			if(btn.id!=ButtonID.HISTORY_QUALITY && btn.id!=ButtonID.HISTORY_EVENT)
				btn.isSelected = false;
		}
    }
	
	public void clearTypeBtnSelect()
    {
		Iterator<MyButton> iterator = btnList.iterator();
		MyButton btn;
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			
			if(btn.id==ButtonID.HISTORY_QUALITY || btn.id==ButtonID.HISTORY_EVENT)
				btn.isSelected = false;
		}
    }
	
	public void clearTouchPoint()
	{
		xQualityBar = -1;
		yQualityBar = -1;
		xEventBar = -1;
		yEventBar = -1;
	}
	
}
