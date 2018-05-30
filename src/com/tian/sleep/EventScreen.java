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
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;


public class EventScreen extends Screen
{
	boolean isMenuExpanded = false;
	boolean hasMenu = true;
	boolean isInfoHit = false;
	
	Context parentContext;
	
	DisplayStatus ds = DisplayStatus.SCREEN_RESULT_EVENT_MOVE;
	
	/********* Fonts **********/
	//font initialization is required 	
	Paint.FontMetricsInt fontBtn = AppUI.FONT_MID;
	Paint.FontMetricsInt fontDetailInfo = AppUI.FONT_SMALL;

	

	/********** Btn Images ***********/
	int imageMoveResID = R.drawable.btn_blue;
	int imageMoveHitResID = R.drawable.btn_grey;
	BitmapDrawable imageMove = null;
	BitmapDrawable imageMoveHit = null;
	
	int imageSnoreResID = R.drawable.btn_orange;
	int imageSnoreHitResID = R.drawable.btn_grey;
	BitmapDrawable imageSnore = null;
	BitmapDrawable imageSnoreHit = null;
	
	int imageCoughResID = R.drawable.btn_red;
	int imageCoughHitResID = R.drawable.btn_grey;
	BitmapDrawable imageCough = null;
	BitmapDrawable imageCoughHit = null;
	
	int imageInfoResID = R.drawable.icon_info;
	int imageInfoHitResID = R.drawable.icon_info_hit;
	BitmapDrawable imageInfo = null;
	BitmapDrawable imageInfoHit = null;
	
	
	BitmapDrawable bdBackground;
	
	
	/********** Touchable Area ***********/
	Rect touchableEventBar;
	int xEventBar = -1;
	int yEventBar = -1;
	
	
	/********* Images *********/	
	
	
	
	/********* Paints **********/
	Paint ptBtnText;
	Paint ptDetailInfo;
	
	
	/********** Color ***********/
	int cBtnText = Color.WHITE;
	int cDetailInfo = Color.WHITE;
	
	int cMoveBar = AppUI.COLOR_BLUE;
	int cSnoreBar = AppUI.COLOR_ORANGE;
	int cCoughBar = AppUI.COLOR_RED;
	
	
	/********* Texts **********/
	String tMoveBtn = "Move";
	String tSnoreBtn = "Snore";
	String tCoughBtn = "Cough";
	String tDetailInfo = "Click buttons to see different types of events.";
	
	
	/********* Locations **********/
	Point pMoveBtn;
	Point pSnoreBtn;
	Point pCoughBtn;
	Point pDetailInfo;
	
	
	
	/********** buttons ***********/
	Rect rMoveBtn;
	Rect rSnoreBtn;
	Rect rCoughBtn;
	Rect rInfoBtn;
	
	
	/********** Event Bar ***********/
	Rect rEventBarArea;
	
	float barWidth = AppUI.SIZE_SMALL_TEXT/4;
    float[] valActi = null;
    

    
    
    public void initialize()
    {
    	//set spacing info
		int smallSpc = (int) AppUI.SIZE_SMALL_TEXT;
		int normalSpc = (int) AppUI.SIZE_NORMAL_TEXT;
		int midSpc = (int) AppUI.SIZE_MID_TEXT;
		
		/************* Boundaries of Each Area ************/
		//info and menu btn area
		int sizeInfoBtn = (int) AppUI.SIZE_LARGE_TEXT;
		int spcInfo1 = smallSpc;	//spc btw top and info btn
		int spcInfo2 = midSpc;	//spc btw info btn and event bars
		int btmInfoBtnArea = y + sizeInfoBtn + spcInfo1 + spcInfo2;
		
		//btn area
		int hBtnText = fontBtn.descent - fontBtn.ascent;
		int hBtnDetailText = fontDetailInfo.descent - fontDetailInfo.ascent;
		int marginBtnText = hBtnText/2;
		int hBtn = hBtnText + 2*marginBtnText;
		int spcBtn1 = smallSpc/2;	//spc btw btn and detailed info 
		int spcBtn2 = smallSpc/2;	//spc btw btns
		int spcBtn3 = smallSpc;		//spc btw btn and screen side
		int spcBtn4 = smallSpc;		//spc btw detail info and screen btm
		
		int wBtn = (screenW - 2*spcBtn2 - 2*spcBtn3)/3;
		int topBtnArea = y + screenH - (hBtn + hBtnDetailText + spcBtn1 + spcBtn4);
		
		//event bar area
		rEventBarArea = new Rect();
		rEventBarArea.left = x;
		rEventBarArea.right = x+screenW;
		rEventBarArea.top = btmInfoBtnArea;
		rEventBarArea.bottom = topBtnArea - spcInfo2;
		
		
		
		/**************** Location and Paint ********************/
		//----------- info btn area --------------
		rInfoBtn = new Rect();
		rInfoBtn.left = x + (screenW - sizeInfoBtn)/2;
		rInfoBtn.right = rInfoBtn.left + sizeInfoBtn;
		rInfoBtn.top = y + spcInfo1;
		rInfoBtn.bottom = rInfoBtn.top + sizeInfoBtn;
		
		//----------- btn area ------------		
		rMoveBtn = new Rect();
		rMoveBtn.left = x+ spcBtn3;
		rMoveBtn.right = rMoveBtn.left + wBtn;
		rMoveBtn.top = topBtnArea;
		rMoveBtn.bottom = rMoveBtn.top + hBtn;
		
		rSnoreBtn = new Rect();
		rSnoreBtn.left = rMoveBtn.right + spcBtn2;
		rSnoreBtn.right = rSnoreBtn.left + wBtn;
		rSnoreBtn.top = topBtnArea;
		rSnoreBtn.bottom = rSnoreBtn.top + hBtn;
		
		rCoughBtn = new Rect();
		rCoughBtn.left = rSnoreBtn.right + spcBtn2;
		rCoughBtn.right = rCoughBtn.left + wBtn;
		rCoughBtn.top = topBtnArea;
		rCoughBtn.bottom = rCoughBtn.top + hBtn;
		
		ptBtnText = new Paint();
		ptBtnText.setAntiAlias(true);
		ptBtnText.setColor(cBtnText); 
		//ptBtnText.setShadowLayer(1, 0, 0, Color.BLACK);	//set shadow layer
		ptBtnText.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptBtnText.setTextSize(hBtnText);
		
		pMoveBtn = new Point();
		pMoveBtn.y = rMoveBtn.top + marginBtnText - fontBtn.ascent;
		pMoveBtn.x = rMoveBtn.left + (wBtn-(int) ptBtnText.measureText(tMoveBtn))/2;
		
		pSnoreBtn = new Point();
		pSnoreBtn.y = rSnoreBtn.top + marginBtnText - fontBtn.ascent;
		pSnoreBtn.x = rSnoreBtn.left + (wBtn-(int) ptBtnText.measureText(tSnoreBtn))/2;
		
		pCoughBtn = new Point();
		pCoughBtn.y = rCoughBtn.top + marginBtnText - fontBtn.ascent;
		pCoughBtn.x = rCoughBtn.left + (wBtn-(int) ptBtnText.measureText(tCoughBtn))/2;
		
		ptDetailInfo = new Paint();
		ptDetailInfo.setAntiAlias(true);
		ptDetailInfo.setColor(cDetailInfo); 
		ptDetailInfo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptDetailInfo.setTextSize(hBtnDetailText);
		
		pDetailInfo = new Point();
		pDetailInfo.y = rMoveBtn.bottom + spcBtn1 - fontDetailInfo.ascent;
		pDetailInfo.x = (screenW - (int)ptDetailInfo.measureText(tDetailInfo))/2 + x;
		
		
		/************* Initialize Buttons **************/
		MyButton btn = new MyButton(ButtonID.EVENTRESULT_INFO, rInfoBtn);
		btn.setImage(imageInfo, imageInfoHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.EVENTRESULT_MOVE, rMoveBtn);
		//btn.setImage(imageMove, imageMoveHit);
		btn.setColor(AppUI.COLOR_GREY_BKG, AppUI.COLOR_BLUE);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btn.isSelected = true;
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.EVENTRESULT_SNORE, rSnoreBtn);
		//btn.setImage(imageSnore, imageSnoreHit);
		btn.setColor(AppUI.COLOR_GREY_BKG, AppUI.COLOR_ORANGE);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.EVENTRESULT_COUGH, rCoughBtn);
		//btn.setImage(imageCough, imageCoughHit);
		btn.setColor(AppUI.COLOR_GREY_BKG, AppUI.COLOR_RED);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btnList.add(btn);
		
		
		/************* Initialize Touchable Area **************/
		touchableEventBar = rEventBarArea;
		
		
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
		cvs.drawText(tCoughBtn, pCoughBtn.x, pCoughBtn.y, ptBtnText);	
		cvs.drawText(tMoveBtn, pMoveBtn.x, pMoveBtn.y, ptBtnText);	
		cvs.drawText(tSnoreBtn, pSnoreBtn.x,  pSnoreBtn.y, ptBtnText);	
		
		cvs.drawText(tDetailInfo, pDetailInfo.x, pDetailInfo.y, ptDetailInfo);	
		
		if(ds==DisplayStatus.SCREEN_RESULT_EVENT_MOVE)
			drawEventBars(cvs, HistoryData.evtMoveLN);
		else if(ds==DisplayStatus.SCREEN_RESULT_EVENT_SNORE)
			drawEventBars(cvs, HistoryData.evtSnoreLN);
		else if(ds==DisplayStatus.SCREEN_RESULT_EVENT_COUGH)
			drawEventBars(cvs, HistoryData.evtCoughLN);
		
		
		/***************** display info in toast ********************/
		if(isInfoHit)
		{
			CharSequence text = "Slide your Finger on the bar area to reveal detail.\n\nThe length of the bar indicates the intensity of the events."; 
			
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(parentContext, text, duration);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.show();
			
			isInfoHit = false;
		}
    }

    
    public void setActFingerPoint(int x, int y)
    {
    	xEventBar = x;
    	yEventBar = y;
    }
    
    public EventScreen(Rect r, Context context, BitmapDrawable bdBkgrd)
	{
		//set screen size and location
		rScreen = new Rect(r);
		screenH = r.height();
		screenW = r.width();
		x = r.left;
		y = r.top;
		
		parentContext = context;
		
		//load btn images
		/*imageMove = (BitmapDrawable) context.getResources().getDrawable(imageMoveResID);
		imageMoveHit = (BitmapDrawable) context.getResources().getDrawable(imageMoveHitResID);
		
		imageSnore = (BitmapDrawable) context.getResources().getDrawable(imageSnoreResID);
		imageSnoreHit = (BitmapDrawable) context.getResources().getDrawable(imageSnoreHitResID);
		
		imageCough = (BitmapDrawable) context.getResources().getDrawable(imageCoughResID);
		imageCoughHit = (BitmapDrawable) context.getResources().getDrawable(imageCoughHitResID);*/
		
		imageInfo = (BitmapDrawable) context.getResources().getDrawable(imageInfoResID);
		imageInfoHit = (BitmapDrawable) context.getResources().getDrawable(imageInfoHitResID);
		
		bdBackground = bdBkgrd;

		//create button list
		btnList = new LinkedList<MyButton>();
	}
    
    
    private void drawEventBars(Canvas cvs, double[] val_bars)
	{	
    	Rect area = rEventBarArea;
    	
		//int verMgn = AppUI.FONT_NORMAL.descent;
		//int horMgn = AppUI.FONT_NORMAL.descent;
		int verMgn = 0;
		int horMgn = (int) AppUI.SIZE_SMALL_TEXT;
		
		//real drawing area considering margins
		RectF r = new RectF(area.left+horMgn, area.top+verMgn, area.right-horMgn, area.bottom-verMgn);
		
		int num_bars = val_bars.length;
		

		Paint labelPaint = new Paint();
		labelPaint.setColor(AppUI.COLOR_EVENTRESULT_TIMELABEL);
		labelPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		labelPaint.setTextSize(AppUI.SIZE_NORMAL_TEXT);
		
		//-------------------draw bars---------------------//
		RectF rectBar = new RectF(r);
		float maxBarLen = r.width();
		
		//float barWidth = (r.height()-barSpc*(num_bars-1))/num_bars;
		float barWidth = r.height()/num_bars;
		int color = 0;;
		
		Paint paintBar = new Paint();
		switch(ds)
		{
		case SCREEN_RESULT_EVENT_COUGH:
			color = cCoughBar;
			break;
		case SCREEN_RESULT_EVENT_MOVE:
		case SCREEN_RESULT_EVENT:
			color = cMoveBar;
			break;
		case SCREEN_RESULT_EVENT_SNORE:
			color = cSnoreBar;
			break;
		}
		paintBar.setColor(color);
		paintBar.setStrokeWidth(2);
		
		
		//peak points
		float [] px = new float[num_bars];
		float [] py = new float[num_bars];
		
		float f;
		
		float rCircle = barWidth/2;
		if(rCircle>AppUI.SIZE_SMALL_TEXT/2)
			rCircle = AppUI.SIZE_SMALL_TEXT/2;
		
		/************** draw bars ***************/
		for(int i=0; i<num_bars; i++)
		{	
			f = (float) val_bars[i];
			
			if(f<0.1)
				f=0;
						
			py[i] = r.top + barWidth/2+barWidth*i;
			px[i] = r.left+r.width()*f;			
			
			paintBar.setAlpha(AppUI.ALPHA_PEAKPOINT);
			
			cvs.drawCircle(px[i], py[i], rCircle, paintBar);
		}
		
		paintBar.setStyle(Paint.Style.STROKE);
		paintBar.setColor(Color.WHITE);
		paintBar.setAlpha(AppUI.ALPHA_DOTLINE);
		paintBar.setStrokeWidth(2);
		paintBar.setPathEffect(new DashPathEffect(new float[] {6,3}, 0));
		for(int i=1; i<num_bars; i++)
		{
			cvs.drawLine(px[i-1], py[i-1], px[i], py[i], paintBar);
		}
		
		/*for(int i = 0; i<num_bars; i++)
		{
			//do not draw weak events if value<0.1
			if(val_bars[i]<0.1)
				continue;
			
			rectBar.right = (float) (rectBar.left + maxBarLen*val_bars[i]);
			rectBar.top = r.top + i*barWidth;
			rectBar.bottom = rectBar.top + barWidth;
			
			paintBar.setAlpha(150);
			cvs.drawRect(rectBar, paintBar);
		}*/
		
		//display detailed info of acticigraphy bars
		if(xEventBar>0 && yEventBar>0)
		{
			int x = xEventBar;
			int y = yEventBar;
			
			
			//get value
			int curBarIndex = (int)((yEventBar-area.top)/barWidth);
			float percent = (float) val_bars[curBarIndex];
			float curTime  = (float)(curBarIndex+1)/(float)6;
			int intLevel = (int) Math.ceil(percent*10);
			
			String strTime = MyTime.getTimeHrMin(curTime); 
			String strLevel = Integer.toString(intLevel)+"/10";
			//
			
			// draw selection bar
			Paint ptSelection = new Paint();
			
			ptSelection.setStyle(Paint.Style.FILL);
			ptSelection.setColor(AppUI.COLOR_BANANA);
			ptSelection.setAlpha(AppUI.ALPHA_DETAILRECT/2);
			
			Rect rSelectBar = new Rect();
			rSelectBar.left = (int) (r.left);
			rSelectBar.right = (int) (r.right);
			rSelectBar.top = (int) (r.top+curBarIndex*barWidth);
			rSelectBar.bottom = (int) (rSelectBar.top+barWidth);
			
			cvs.drawRect(rSelectBar, ptSelection);
			
			ptSelection.setStyle(Paint.Style.STROKE);
			ptSelection.setColor(AppUI.COLOR_PINK);
			ptSelection.setAlpha(100);
			
			float strokeW = (float) (rCircle*1.5);
			if(strokeW>AppUI.SIZE_SMALL_TEXT)
				strokeW = AppUI.SIZE_SMALL_TEXT;
			ptSelection.setStrokeWidth(strokeW);
			float rSelect = rCircle + ptSelection.getStrokeWidth()/2;
			
			cvs.drawCircle(px[curBarIndex], py[curBarIndex], rSelect, ptSelection);
			
			//draw line
			ptSelection.setStrokeWidth(rCircle*2);
			ptSelection.setAlpha(AppUI.ALPHA_DETAILRECT);
			cvs.drawLine(r.left, py[curBarIndex], px[curBarIndex]-rCircle, py[curBarIndex], ptSelection);
			
			//prepare detail text
			String strStatus = null;
			switch(ds)
			{
			case SCREEN_RESULT_EVENT_COUGH:
				strStatus = "Cough";
				break;
			case SCREEN_RESULT_EVENT_MOVE:
				strStatus = "Move";
				break;
			case SCREEN_RESULT_EVENT_SNORE:
				strStatus = "Snore";
				break;
			}
			
			//measure detail text
			int txtHeight = fontDetailInfo.descent - fontDetailInfo.ascent;
			
			Paint ptEventDetailTxt = new Paint();
			ptEventDetailTxt.setColor(Color.WHITE);
			ptEventDetailTxt.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			ptEventDetailTxt.setTextSize(txtHeight);
			
			int maxTextWidth = (int)ptEventDetailTxt.measureText("00h:00m");
			
			int txtWidth = (int)labelPaint.measureText(strStatus);
					
			//draw detail text rect
			Rect rEventDetail = new Rect();
			rEventDetail.left = 0;
			rEventDetail.right = rEventDetail.left + maxTextWidth;
			
			rEventDetail.top = y - 3*txtHeight;
			if(rEventDetail.top<=0)
				rEventDetail.top = 0;
			rEventDetail.bottom = rEventDetail.top + 3*txtHeight;
			
			Paint ptEventDetail = new Paint();
			ptEventDetail.setColor(AppUI.COLOR_GREY_BKG);
			ptEventDetail.setAlpha(AppUI.ALPHA_DETAILRECT);
			cvs.drawRect(rEventDetail, ptEventDetail);
			
			//draw dotted line
			/*ptEventDetail.setStrokeWidth(3f);
			ptEventDetail.setStyle(Style.STROKE);
			ptEventDetail.setPathEffect(new DashPathEffect(new float[] {10,5}, 0));
			cvs.drawLine(0, y, screenW, y, ptEventDetail);*/
			
			//draw detail text
			float xLine = rEventDetail.left + (rEventDetail.width()-ptEventDetailTxt.measureText(strTime))/2;
			float yLine = rEventDetail.top - fontDetailInfo.ascent;
			cvs.drawText(strTime, xLine, yLine, ptEventDetailTxt);
			
			ptEventDetailTxt.setColor(color);
			xLine = rEventDetail.left + (rEventDetail.width()-ptEventDetailTxt.measureText(strStatus))/2;
			yLine = yLine + txtHeight;
			cvs.drawText(strStatus, xLine, yLine, ptEventDetailTxt);
			ptEventDetailTxt.setColor(Color.WHITE);
			
			ptEventDetailTxt.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
			xLine = rEventDetail.left + (rEventDetail.width()-ptEventDetailTxt.measureText(strLevel))/2;
			yLine = yLine + (fontDetailInfo.descent - fontDetailInfo.ascent);
			cvs.drawText(strLevel, xLine, yLine, ptEventDetailTxt);								
		}
	
	}
    
    
    
    
    
    public void clearBtnSelect()
    {
		Iterator<MyButton> iterator = btnList.iterator();
		MyButton btn;
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			btn.isSelected = false;
		}
    }
     
}
