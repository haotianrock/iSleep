package com.tian.sleep;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

import com.tian.sleep.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class ResultScreen extends Screen
{
	boolean isMenuExpanded = false;
	boolean hasMenu = true;
	
	boolean isShowTime = true;
	
	/********* Fonts **********/
	//font initialization is required 	
	Paint.FontMetricsInt fontTimeInfo = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt fontEffInfo = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt fontEffNum = AppUI.FONT_LARGE;
	Paint.FontMetricsInt fontBtn = AppUI.FONT_MID;
	Paint.FontMetricsInt fontDetailInfo = AppUI.FONT_SMALL;
	Paint.FontMetricsInt fontActLabel = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt fontEval = AppUI.FONT_SMALL;
	

	/********** Btn Images ***********/
	int imageResID = R.drawable.btn_orange;
	int imageHitResID = R.drawable.btn_grey;
	BitmapDrawable image = null;
	BitmapDrawable imageHit = null;
	
	
	/********** Touchable Area ***********/
	Rect touchableActBar;
	int xActBar = -1;
	int yActBar = -1;
	
	Rect touchableEvalBar;
	
	
	/********* Images *********/	
	
	
	
	/********* Paints **********/
	Paint ptTimeInfo;
	Paint ptTime;
	Paint ptEffNum;
	Paint ptEffInfo;
	Paint ptDetailInfo;
	Paint ptBtnText;
	Paint ptActLabelSleep;
	Paint ptActLabelWake;
	Paint ptEvalText;
	Paint ptEvalBar;
	
	/********** Color ***********/
	int cTimeInfo = AppUI.COLOR_BLUE; //Color.rgb(126,187,18);
	int cEffInfo = Color.WHITE;
	int cEffNum = AppUI.COLOR_PINK;
	int cDetailInfo = Color.WHITE;
	int cBtnText = Color.WHITE;
	
	int cEvalText = cEffNum;
	int cEvalInfo = cDetailInfo;
	
	int cActLabelSleep = Color.rgb(126,187,18);
	int cActLabelWake = Color.rgb(249,82,13);
	
	int cActDetailText = Color.WHITE;
	int cActDetailBkgd = Color.rgb(87, 96, 105);
	
	int[] cEvalBars = null;
	
	
	/********* Texts **********/
	String tStartTime = "You got on bed at ";
	String tStartTimeHrMin = "00h:00m";
	
	String tDurTime = "Total sleep time: ";
	String tDurTimeHrMin = "00h:00m";
	
	String tActTime = "Actual sleep time: ";
	String tActTimeHrMin = "00h:00m";
	
	String tEffInfo = "Your sleep efficiency last night is";
	String tEffNum = "00%";
	
	String tEventBtn = "Sleep Events";
	String tBtnInfo1 = "Click the sleep events button to see";
	String tBtnInfo2 = "detailed sleep events";
	
	String tActSleep = "Sleeping";
	String tActWake = "Awake";
	
	String tEvaluation = "Does the sleep efficiency reflect your sleep quality accurately?";
	String tEvaluationInfo = "Slide the bar above to give us feedback.";
	
	/********* Locations **********/
	Point pStartTime;
	Point pStartTimeHrMin;
	
	Point pDurTime;
	Point pDurTimeHrMin;
	
	Point pActTime;
	Point pActTimeHrMin;
	
	Point pEffInfo;
	Point pEffNum;
	
	Point pEventBtn;
	Point pBtnInfo1;
	Point pBtnInfo2;
	
	Point pActLabelSleep;
	Point pActLabelWake;
	
	Point pEvalText;
	Point pEvalTextAcc;
	Point pEvalTextNAcc;
	Point pEvalInfo;
	
	
	/********** evaluation bar ************/
	Rect rEvalBar;
	float evalScore = 0.5f;
	
	/********** buttons ***********/
	Rect rEventBtn;
	
	
	/********** Actigraph ***********/
	Rect rActBarArea;
	Paint ptActi;
	
	float barWidth = AppUI.SIZE_SMALL_TEXT/4;
    float[] valActi = null;
    
	
	BitmapDrawable bdBackground = null;

    
    
    public void initialize()
    {
    	//set spacing info
		int smallSpc = (int) AppUI.SIZE_SMALL_TEXT;
		int normalSpc = (int) AppUI.SIZE_NORMAL_TEXT;
		int midSpc = (int) AppUI.SIZE_MID_TEXT;
		
		/************* Boundaries of Each Area ************/
		//Time Info area
		int hTimeInfoText = fontTimeInfo.descent - fontTimeInfo.ascent;
		int spcTime1 = normalSpc;	//the spc btw area and screen top
		int spcTime2 = smallSpc/2;	//the spc btw two times
		int spcTime3 = normalSpc;	//the spc btw time area and eff area
		int hTimeArea = 4*hTimeInfoText + spcTime1 + 1*spcTime2 + spcTime3;
		int topTimeArea = y + spcTime1;
		int btmTimeArea = y + hTimeArea;
		
		//Efficiency area
		int hEffInfoText = fontEffInfo.descent - fontEffInfo.ascent;
		int hEffNumText = fontEffNum.descent - fontEffNum.ascent;
		int spcEff1 = smallSpc/2;	//spc btw eff info and num
		int spcEff2 = smallSpc;	//spc btw eff area and eval bar
		int btmEffArea = btmTimeArea + hEffInfoText + hEffNumText + spcEff1 + spcEff2;
		
		//user evaluation area
		int hEvalText = fontEval.descent - fontEval.ascent;
		int hEvalInfoText = fontDetailInfo.descent - fontDetailInfo.ascent;
		int hEvalBar = normalSpc;
		int hEvalBarHandle = (int) (hEvalBar*1.5);
		int spcEval1 = normalSpc;	//spc btw eval bar and acti
		int btmEvalBarArea = btmEffArea + hEvalText + 2*hEvalInfoText + hEvalBarHandle + spcEval1;
				
		
		//event btn area
		int hBtnText = fontBtn.descent - fontBtn.ascent;
		int marginBtnText = hBtnText/2;
		int hBtn = hBtnText + 2*marginBtnText;
		int spcBtn1 = smallSpc/2;	//spc btw btn and detailed info
		
		int hDetailInfoText = fontDetailInfo.descent - fontDetailInfo.ascent;
		int spcBtn2 = midSpc;	//spc btw btn and screen btm
		int hBtnArea = hBtn + spcBtn1 + 2*hDetailInfoText + spcBtn2;
		int topBtnArea = screenH - hBtnArea + y;
		
		//actigraph area
		int topActArea = btmEvalBarArea;
		int spcAct1 = smallSpc;	//spc btw actigraph and event button
		int btmActArea = topBtnArea - spcAct1;
		int sideMgn = smallSpc; 	//the spc btw actigraph and the screen side
		int hActLabelText = fontActLabel.descent - fontActLabel.ascent;
		
		Rect rActArea = new Rect(sideMgn+x, topActArea, screenW-sideMgn+x, btmActArea);
		rActBarArea = new Rect(sideMgn+x, topActArea+hActLabelText, screenW-sideMgn+x, btmActArea-hActLabelText);
		
		/**************** Location and Paint ********************/
		//-----------Time Info area------------		
		ptTimeInfo = new Paint();
		ptTimeInfo.setAntiAlias(true);
		ptTimeInfo.setColor(cTimeInfo); 	
		ptTimeInfo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptTimeInfo.setTextSize(hTimeInfoText);
		
		ptTime = new Paint();
		ptTime.setAntiAlias(true);
		ptTime.setColor(cTimeInfo); 	
		ptTime.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		ptTime.setTextSize(hTimeInfoText);

		pStartTime = new Point();
		pStartTime.y = topTimeArea - fontTimeInfo.ascent;
		pStartTime.x = (screenW - (int) ptTimeInfo.measureText(tStartTime))/2 + x;
		
		pStartTimeHrMin = new Point();
		pStartTimeHrMin.y = topTimeArea + hTimeInfoText - fontTimeInfo.ascent;
		pStartTimeHrMin.x = (screenW - (int) ptTime.measureText(tStartTimeHrMin))/2 + x;
		
		pDurTime = new Point();
		pDurTime.y = topTimeArea + 2*hTimeInfoText + spcTime2 - fontTimeInfo.ascent;
		pDurTime.x = (screenW - (int) ptTimeInfo.measureText(tDurTime))/2 + x;
		
		pDurTimeHrMin = new Point();
		pDurTimeHrMin.y = topTimeArea + 3*hTimeInfoText + spcTime2 - fontTimeInfo.ascent;
		pDurTimeHrMin.x = (screenW - (int) ptTime.measureText(tDurTimeHrMin))/2 + x;
		
		/*pActTime = new Point();
		pActTime.y = topTimeArea + 4*hTimeInfoText + 2*spcTime2 - fontTimeInfo.ascent;
		pActTime.x = (screenW - (int) ptTimeInfo.measureText(tActTime))/2 + x;
		
		pActTimeHrMin = new Point();
		pActTimeHrMin.y = topTimeArea + 5*hTimeInfoText + 2*spcTime2 - fontTimeInfo.ascent;
		pActTimeHrMin.x = (screenW - (int) ptTime.measureText(tActTimeHrMin))/2 + x;*/
				
		//-------------Efficiency Info area--------------	
		ptEffInfo = new Paint();
		ptEffInfo.setAntiAlias(true);
		ptEffInfo.setColor(cEffInfo); 	
		ptEffInfo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptEffInfo.setTextSize(hEffInfoText);
		
		pEffInfo = new Point();
		pEffInfo.y = btmTimeArea - fontEffInfo.ascent;
		pEffInfo.x = (screenW - (int) ptEffInfo.measureText(tEffInfo))/2 + x;
		
		ptEffNum = new Paint();
		ptEffNum.setAntiAlias(true);
		ptEffNum.setColor(cEffNum); 	
		ptEffNum.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		ptEffNum.setTextSize(hEffNumText);
		
		pEffNum = new Point();
		pEffNum.y = btmTimeArea + hEffInfoText + spcEff1 - fontEffNum.ascent;
		pEffNum.x = (screenW - (int) ptEffNum.measureText(tEffNum))/2 + x;
		
		
		//-----------Event-btn area------------		
		int btnWidth = screenW/2;
		
		rEventBtn = new Rect();
		rEventBtn.left = x+(screenW-btnWidth)/2;
		rEventBtn.right = rEventBtn.left + btnWidth;
		rEventBtn.top = topBtnArea;
		rEventBtn.bottom = rEventBtn.top + hBtn;
		
		ptBtnText = new Paint();
		ptBtnText.setAntiAlias(true);
		ptBtnText.setColor(cBtnText); 
		ptBtnText.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptBtnText.setTextSize(hBtnText);
		
		pEventBtn = new Point();
		pEventBtn.y = rEventBtn.top + marginBtnText - fontBtn.ascent;
		pEventBtn.x = rEventBtn.left + (btnWidth-(int) ptBtnText.measureText(tEventBtn))/2;
		
		ptDetailInfo = new Paint();
		ptDetailInfo.setAntiAlias(true);
		ptDetailInfo.setColor(cDetailInfo); 
		ptDetailInfo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptDetailInfo.setTextSize(hDetailInfoText);
		
		pBtnInfo1 = new Point();
		pBtnInfo1.y = rEventBtn.bottom + spcBtn1 - fontDetailInfo.ascent;
		pBtnInfo1.x = (screenW - (int)ptDetailInfo.measureText(tBtnInfo1))/2 + x;
		
		pBtnInfo2 = new Point();
		pBtnInfo2.y = rEventBtn.bottom + hDetailInfoText + spcBtn1 - fontDetailInfo.ascent;
		pBtnInfo2.x = (screenW - (int)ptDetailInfo.measureText(tBtnInfo2))/2 + x;
		
		
		//--------------Eval area---------------		
		ptEvalText = new Paint();
		ptEvalText.setAntiAlias(true);
		ptEvalText.setColor(cEvalText); 
		ptEvalText.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptEvalText.setTextSize(hEvalText);
		
		pEvalText = new Point();
		pEvalText.y = btmEffArea - fontEval.ascent;
		pEvalText.x = (screenW - (int)ptEvalText.measureText(tEvaluation))/2 + x;
		
		int sideMgnEvalBar = midSpc;
		rEvalBar = new Rect();
		rEvalBar.left = sideMgnEvalBar+x;
		rEvalBar.right = screenW-sideMgnEvalBar+x;
		rEvalBar.top = btmEffArea + hEvalText + hDetailInfoText;
		rEvalBar.bottom = rEvalBar.top + hEvalBarHandle;
		
		int numBars = 100;
		float dR = Color.red(AppUI.COLOR_GREEN)-Color.red(AppUI.COLOR_RED);
		float dG = Color.green(AppUI.COLOR_GREEN)-Color.green(AppUI.COLOR_RED);
		float dB = Color.blue(AppUI.COLOR_GREEN)-Color.blue(AppUI.COLOR_RED);
		dR = dR/(numBars-1);
		dG = dG/(numBars-1);
		dB = dB/(numBars-1);
		
		cEvalBars = new int[numBars];
		cEvalBars[0] = AppUI.COLOR_RED;
		for(int i=1; i<numBars; i++)
		{
			int red = (int) (Color.red(AppUI.COLOR_RED) + dR*i);
			int green = (int) (Color.green(AppUI.COLOR_RED) + dG*i);
			int blue = (int) (Color.blue(AppUI.COLOR_RED) + dB*i);
			cEvalBars[i] = Color.rgb(red, green, blue);
		}
		
		
		pEvalTextAcc = new Point();
		pEvalTextAcc.y = rEvalBar.top - fontDetailInfo.descent;
		pEvalTextAcc.x = rEvalBar.left;
		
		pEvalTextNAcc = new Point();
		pEvalTextNAcc.y = pEvalTextAcc.y;
		pEvalTextNAcc.x = rEvalBar.right - (int) ptDetailInfo.measureText("accurate");
		
		pEvalInfo = new Point();
		pEvalInfo.y = rEvalBar.bottom - fontDetailInfo.ascent;
		pEvalInfo.x = (screenW - (int)ptDetailInfo.measureText(tEvaluationInfo))/2 + x;
		
		
		//-----------Acti area------------	
		ptActLabelSleep = new Paint();
		ptActLabelSleep.setAntiAlias(true);
		ptActLabelSleep.setColor(cActLabelSleep); 	
		ptActLabelSleep.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptActLabelSleep.setTextSize(hActLabelText);
		
		pActLabelSleep = new Point();
		pActLabelSleep.y = rActArea.top - fontActLabel.ascent;
		pActLabelSleep.x = (screenW - (int) ptActLabelSleep.measureText(tActSleep))/2 + x;
		
		ptActLabelWake = new Paint();
		ptActLabelWake.setAntiAlias(true);
		ptActLabelWake.setColor(cActLabelWake); 	
		ptActLabelWake.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptActLabelWake.setTextSize(hActLabelText);
		
		pActLabelWake = new Point();
		pActLabelWake.y = rActArea.bottom - fontActLabel.descent;
		pActLabelWake.x = (screenW - (int) ptActLabelWake.measureText(tActWake))/2 + x;
		
		
		/************* Initialize Buttons **************/
		MyButton btn = new MyButton(ButtonID.RESULT_EVENT, rEventBtn);
		//btn.setImage(image, imageHit);
		btn.setText(tEventBtn, pEventBtn, ptBtnText);
		btn.setColor(AppUI.COLOR_PINK, AppUI.COLOR_GREY_BKG);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btnList.add(btn);
		
		
		/************* Initialize Touchable Area **************/
		touchableActBar = rActBarArea;
		touchableEvalBar = new Rect();
		touchableEvalBar.left = rEvalBar.left;
		touchableEvalBar.right = rEvalBar.right;
		touchableEvalBar.top = rEvalBar.top - rEvalBar.height()/3;
		touchableEvalBar.bottom = rEvalBar.bottom + rEvalBar.height()/3;
		
		
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
		if(isShowTime)
		{
			cvs.drawText(tStartTime, pStartTime.x, pStartTime.y, ptTimeInfo);	
			cvs.drawText(tDurTime, pDurTime.x, pDurTime.y, ptTimeInfo);	
			cvs.drawText(tStartTimeHrMin, pStartTimeHrMin.x, pStartTimeHrMin.y, ptTime);
			cvs.drawText(tDurTimeHrMin, pDurTimeHrMin.x, pDurTimeHrMin.y, ptTime);
			
			drawEvalBar(cvs, evalScore);
			cvs.drawText(tEvaluation, pEvalText.x, pEvalText.y, ptEvalText);
			cvs.drawText("inaccurate", pEvalTextAcc.x, pEvalTextAcc.y, ptDetailInfo);
			cvs.drawText("accurate", pEvalTextNAcc.x, pEvalTextNAcc.y, ptDetailInfo);
			cvs.drawText(tEvaluationInfo, pEvalInfo.x, pEvalInfo.y, ptDetailInfo);
		}
		
		
		//cvs.drawText(tActTime, pActTime.x,  pActTime.y, ptTimeInfo);	
		
		
		//cvs.drawText(tActTimeHrMin, pActTimeHrMin.x, pActTimeHrMin.y, ptTime);
		
		cvs.drawText(tBtnInfo1, pBtnInfo1.x, pBtnInfo1.y, ptDetailInfo);	
		cvs.drawText(tBtnInfo2, pBtnInfo2.x, pBtnInfo2.y, ptDetailInfo);
		
		cvs.drawText(tEventBtn, pEventBtn.x, pEventBtn.y, ptBtnText);
		
		double effnum = HistoryData.overallEff;
		int intEff = (int)Math.ceil(effnum*100);
		tEffNum = Integer.toString(intEff)+"%";
		cvs.drawText(tEffInfo, pEffInfo.x, pEffInfo.y, ptEffInfo);
		cvs.drawText(tEffNum, pEffNum.x, pEffNum.y, ptEffNum);
		
		cvs.drawText(tActSleep, pActLabelSleep.x, pActLabelSleep.y, ptActLabelSleep);
		cvs.drawText(tActWake, pActLabelWake.x, pActLabelWake.y, ptActLabelWake);
		
		
		
		drawActBar(cvs);
		
    }
    
    public void setEndTime()
    {
    	if(!isShowTime)
    		return;
    	
		AppUI.endTime = Calendar.getInstance();
		Calendar endTime = AppUI.endTime;
        

		tDurTimeHrMin = MyTime.getTimeHrMin(AppUI.startTime, AppUI.endTime);
		//tDurTimeHrMin = "08h:30m";
		tStartTimeHrMin = MyTime.getTimeHrMin(AppUI.startTime);
        
        
    }
    
    private void drawEvalBar(Canvas cvs, float score)
    {
    	int numBars = cEvalBars.length;
    	
    	Paint ptEvalBar = new Paint();
    	
    	
    	float barWidth = (float)rEvalBar.width()/(float)numBars;
    	float l, t, b, r;
    	t = rEvalBar.top + rEvalBar.height()/5;
    	b = rEvalBar.bottom - rEvalBar.height()/5;
    	
    	for(int i=0; i<numBars; i++)
		{
    		l = rEvalBar.left + i*barWidth;
    		r = rEvalBar.left + (i+1)*barWidth;
    		ptEvalBar.setColor(cEvalBars[i]);
    		
    		ptEvalBar.setAlpha(100);
    		cvs.drawRect(l, t, r, b, ptEvalBar);
		}
    	
		float radius = rEvalBar.height()/2;
		ptEvalBar.setAntiAlias(true);
		ptEvalBar.setColor(Color.YELLOW);
		ptEvalBar.setAlpha(100);
		 
		float cx = rEvalBar.left + score*rEvalBar.width();
		float cy = rEvalBar.top + radius;
    	
		//ptEvalBar.setAlpha(120);
		cvs.drawCircle(cx, cy, radius, ptEvalBar);
    	
    }
    
    private void drawActBar(Canvas cvs)
    {
    	Rect rectActigraph = rActBarArea;	
    	
    	int barHeight = rectActigraph.height()/2;
		int yBaseLine = barHeight + rectActigraph.top;
		
		Paint actiPaint = new Paint();
		actiPaint.setStyle(Paint.Style.FILL);
		
		double[] valueBars = HistoryData.stateSleepLN;
		int num_bars = valueBars.length;
		
		float barWidth = rectActigraph.width()/num_bars;
		int barInterval = 0;
		
		float l, t, b, r;
		float f;
		
		//peak points
		float [] px = new float[num_bars];
		float [] py = new float[num_bars];
		
		float rCircle = barWidth/2;
		
		/************** draw bars ***************/
		for(int i=0; i<num_bars; i++)
		{	
			f = (float) valueBars[i];
			
			px[i] = rectActigraph.left + barWidth/2+barWidth*i;
			py[i] = yBaseLine-barHeight*f;
			
			if(f<0)
				actiPaint.setColor(AppUI.COLOR_RESULT_ACTI_WAKE);
			else
				actiPaint.setColor(AppUI.COLOR_RESULT_ACTI_SLEEP);
				
			
			actiPaint.setAlpha(AppUI.ALPHA_PEAKPOINT);
			
			cvs.drawCircle(px[i], py[i], rCircle, actiPaint);
			
			/*l = rectActigraph.left + i*barWidth;
			r = l+barWidth;
			
			if(f<0)
			{
				f = Math.abs(f);
				actiPaint.setColor(AppUI.COLOR_RESULT_ACTI_WAKE);
				t = yBaseLine;
				b = yBaseLine+barHeight*f;				
			}
			else
			{
				actiPaint.setColor(AppUI.COLOR_RESULT_ACTI_SLEEP);
				t = yBaseLine-barHeight*f;
				b = yBaseLine;
			}
			actiPaint.setAlpha(120);
			cvs.drawRect(l, t, r, b, actiPaint);*/
		}
		
		actiPaint.setStyle(Paint.Style.STROKE);
		actiPaint.setColor(Color.WHITE);
		actiPaint.setAlpha(AppUI.ALPHA_DOTLINE);
		actiPaint.setStrokeWidth(2);
		actiPaint.setPathEffect(new DashPathEffect(new float[] {6,3}, 0));
		for(int i=1; i<num_bars; i++)
		{
			cvs.drawLine(px[i-1], py[i-1], px[i], py[i], actiPaint);
		}
		
		//draw base line btw sleep and wake bars
		//actiPaint.setStrokeWidth(1);
		//actiPaint.setColor(Color.WHITE);
		cvs.drawLine(rectActigraph.left, yBaseLine, rectActigraph.right, yBaseLine, actiPaint);
		
		
		/*********** display detailed info of acticigraphy bars *************/
		if(xActBar>0 && yActBar>0)
		{
			int x = xActBar;
			int y = yActBar;
			
			//get value
			int curBarIndex = (int)((xActBar-rectActigraph.left)/barWidth);
			if(curBarIndex>=valueBars.length)
				curBarIndex = valueBars.length-1;
			float percent = (float) valueBars[curBarIndex];
			float curTime  = (float)(curBarIndex+1)/(float)6;	//data interval is 10 min
			
			//
			//percent = (float) ((percent-0.5)*2);
			int intPercent = (int) Math.ceil(percent*100);
			//
			
			
			// draw selection bar
			Paint ptSelection = new Paint();
			
			ptSelection.setStyle(Paint.Style.FILL);
			ptSelection.setColor(AppUI.COLOR_BANANA);
			ptSelection.setAlpha(AppUI.ALPHA_DETAILRECT/2);
			
			Rect rSelectBar = new Rect();
			rSelectBar.left = (int) (rectActigraph.left+curBarIndex*barWidth);
			rSelectBar.right = (int) (rSelectBar.left + barWidth);
			rSelectBar.top = rectActigraph.top;
			rSelectBar.bottom = rectActigraph.bottom;
			
			cvs.drawRect(rSelectBar, ptSelection);
			
			ptSelection.setStyle(Paint.Style.STROKE);
			ptSelection.setColor(AppUI.COLOR_PINK);
			ptSelection.setAlpha(120);
			ptSelection.setStrokeWidth((float)(rCircle*1.5));
			float rSelect = rCircle + ptSelection.getStrokeWidth()/2;
			
			cvs.drawCircle(px[curBarIndex], py[curBarIndex], rSelect, ptSelection);
			
			
			//draw line
			ptSelection.setStrokeWidth(rCircle*2);
			ptSelection.setAlpha(AppUI.ALPHA_DETAILRECT);
			
			if(py[curBarIndex]>yBaseLine)
				cvs.drawLine(px[curBarIndex], yBaseLine, px[curBarIndex], py[curBarIndex]-rCircle, ptSelection);
			else
				cvs.drawLine(px[curBarIndex], yBaseLine, px[curBarIndex], py[curBarIndex]+rCircle, ptSelection);
			
			
			Paint paint = new Paint();
			paint.setColor(cActDetailText);
			
			String strStatus;
			if(percent>0)
				strStatus = "Sleeping";
			else
				strStatus = "Awake";
			
			String strTime = MyTime.getTimeHrMin(curTime); 
			String strPercent = Integer.toString(intPercent)+"%";
			
			if(percent>0)
				strPercent = "+"+strPercent;
			
			//measure detail text
			paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			paint.setTextSize(AppUI.SIZE_NORMAL_TEXT);
			
			int txtHeight = AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent;
			int txtWidth = (int)paint.measureText(strStatus);
			
			int maxTextWidth = (int)paint.measureText("Sleeping");
			
			//detail rect
			Rect rBarDetail = new Rect();
			rBarDetail.left = x - maxTextWidth;
			if(rBarDetail.left<=0)
				rBarDetail.left = 0;
			rBarDetail.right = rBarDetail.left + maxTextWidth;
			rBarDetail.bottom = rectActigraph.top;
			rBarDetail.top = rBarDetail.bottom - 3*txtHeight;
			
			actiPaint.setStyle(Paint.Style.FILL);
			actiPaint.setColor(AppUI.COLOR_GREY_BKG);
			actiPaint.setAlpha(AppUI.ALPHA_DETAILRECT);
			cvs.drawRect(rBarDetail, actiPaint);
			
			//draw dotted line
			/*actiPaint.setStrokeWidth(3f);
			actiPaint.setStyle(Style.STROKE);
			actiPaint.setPathEffect(new DashPathEffect(new float[] {10,5}, 0));
			cvs.drawLine(x, rectActigraph.top, x, rectActigraph.bottom, actiPaint);*/
			
			//print detail text
			float xLine = rBarDetail.left + (rBarDetail.width()-paint.measureText(strTime))/2;
			float yLine = rBarDetail.top - AppUI.FONT_NORMAL.ascent;
			cvs.drawText(strTime, xLine, yLine, paint);
			xLine = rBarDetail.left + (rBarDetail.width()-paint.measureText(strStatus))/2;
			yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
			cvs.drawText(strStatus, xLine, yLine, paint);
			
			if(strStatus == "Sleeping")
				paint.setColor(Color.GREEN);
			else if(strStatus == "Awake")
				paint.setColor(Color.RED);
			xLine = rBarDetail.left + (rBarDetail.width()-paint.measureText(strPercent))/2;
			yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
			cvs.drawText(strPercent, xLine, yLine, paint);								
		}
		
		px = null;
		py = null;
    }
    
    public void setActFingerPoint(int x, int y)
    {
    	xActBar = x;
    	yActBar = y;
    }
    
    public void setEvalScore(int x, int y)
    {
    	evalScore = (float)(x-rEvalBar.left)/(float)rEvalBar.width();
    	HistoryData.evalScore = evalScore;
    }
    
    public ResultScreen(Rect r, Context context, BitmapDrawable bdBkgrd)
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

		bdBackground = bdBkgrd;
		
		//create button list
		btnList = new LinkedList<MyButton>();
	}
}
