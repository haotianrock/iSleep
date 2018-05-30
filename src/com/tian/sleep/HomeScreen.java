package com.tian.sleep;

import java.util.Iterator;
import java.util.LinkedList;

import com.tian.sleep.R;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
//import android.util.Log;
import android.util.Log;

public class HomeScreen extends Screen
{		
	/*********Fonts**********/
	//font initialization is required 	
	Paint.FontMetricsInt logoFont = AppUI.FONT_LARGE;
	Paint.FontMetricsInt subtextFont = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt btnFont = AppUI.FONT_MID;
	
	
	/*********Paints**********/
	Paint ptLogo;
	Paint ptSubtext;
	Paint ptBtnText;
	
	int cLogo = AppUI.COLOR_BLUE;
	int cSubtext = AppUI.COLOR_ORANGE;
	int cBtnText = Color.WHITE;
	
	
	/*********Texts**********/
	String tLogo = "iSleep";
	String tSubtext = "How well did you sleep?";
	String tHomeBtn = "Start";
	String tRecentBtn = "Recent";
	String tHistoryBtn = "History";
	String tSettingBtn = "Setting";
	String tAboutBtn = "About";
	
	String tPSQIBtn = "Questionnaire";
	
	
	/*********Locations**********/
	//text location
	Point pLogo;	
	Point pSubtext;
	
	//button text location
	Point pHome;
	Point pHistory;
	Point pSetting;
	Point pAbout;
	Point pPSQI;
	Point pRecent;
	
	//button location
	int numBtns = 6;
	Rect rHomeBtn;
	Rect rRecentBtn;
	Rect rHistoryBtn;
	Rect rSettingBtn;
	Rect rAboutBtn;
	Rect rPSQIBtn;
	
	
	/**********btn images***********/
	//int imageResID = R.drawable.btn_green;
	//int imageHitResID = R.drawable.btn_grey;
	
	/*int imageResID = R.drawable.btn_green;
	int imageHitResID = R.drawable.btn_green_hit;
	
	BitmapDrawable image = null;
	BitmapDrawable imageHit = null;*/
	
	
	/********* image ***********/
	BitmapDrawable bdWelcomePic = null;
	MyImage imgWelcomePic;
	Rect rWelcomePic;
	
	BitmapDrawable bdBackground;
		
	
	public HomeScreen(Rect r, Context context, BitmapDrawable welcomePic, BitmapDrawable bdBkgrd)
	{
		//set screen size and location
		rScreen = new Rect(r);
		screenH = r.height();
		screenW = r.width();
		x = r.left;
		y = r.top;
		
		//load btn images
		/*image = (BitmapDrawable) context.getResources().getDrawable(imageResID);
		imageHit = (BitmapDrawable) context.getResources().getDrawable(imageHitResID);*/
		
		bdWelcomePic = welcomePic;
		
		bdBackground = bdBkgrd;
		
		//create button list
		btnList = new LinkedList<MyButton>();
	}
	
	
	/**
	 * draw the screen on the canvas
	 * @param cvs
	 */
	public void PaintScreen(Canvas cvs)
	{
		
		if(bdBackground==null)
			cvs.drawColor(AppUI.COLOR_SCREEN_BACKGROUND);
		else
			cvs.drawBitmap(bdBackground.getBitmap(), new Rect(0,0,bdBackground.getIntrinsicWidth(), bdBackground.getIntrinsicHeight()), rScreen, null);
			//cvs.drawBitmap(bdBackground.getBitmap(), 0, 0, null);
		
		/*****************paint button image******************/
		Iterator<MyButton> iterator = btnList.iterator();
		MyButton btn;
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			//btn.paintButton(cvs);
			btn.drawButtonCircle(cvs);
		}
		
		imgWelcomePic.paintImage(cvs);
		
		/*****************Paint Text******************/
		//cvs.drawText(tLogo, pLogo.x, pLogo.y, ptLogo);	
		//cvs.drawText(tSubtext, pSubtext.x, pSubtext.y, ptSubtext);	
		
		cvs.drawText(tHomeBtn, pHome.x, pHome.y, ptBtnText);	
		cvs.drawText(tHistoryBtn, pHistory.x, pHistory.y, ptBtnText);	
		//cvs.drawText(tSettingBtn, pSetting.x, pSetting.y, ptBtnText);	
		cvs.drawText(tAboutBtn, pAbout.x, pAbout.y, ptBtnText);	
		//cvs.drawText(tPSQIBtn, pPSQI.x, pPSQI.y, ptBtnText);
		cvs.drawText(tRecentBtn, pRecent.x, pRecent.y, ptBtnText);
	}



	/**
	 * initiliaze the locations and paints used to paint screen
	 */
	public void initialize()
	{	
		//set spacing info
		int smallSpc = (int) AppUI.SIZE_SMALL_TEXT;
		int normalSpc = (int) AppUI.SIZE_NORMAL_TEXT;
		int midSpc = (int) AppUI.SIZE_MID_TEXT;
				
		/*************Calculate Spacing**************/
		//size of each text
		int sBtnText = btnFont.descent-btnFont.ascent;
		
		
		

		
		/*************Initialize Paints and Locations**************/
		//int yBtnTop = screenH - numBtns*btnHeight - (numBtns-1)*spcBtn - spcBtnBtm;
		
		//Logo		
		/*ptLogo = new Paint();
		ptLogo.setAntiAlias(true);
		ptLogo.setColor(cLogo);
		ptLogo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		ptLogo.setTextSize(sLogoText);*/

		/*pLogo = new Point();
		//pLogo.y = (screenH - yBtnTop)/2  + y;
		pLogo.y = y+spcTopLogo-logoFont.ascent;
		pLogo.x = (screenW - (int) ptLogo.measureText(tLogo))/2 + x;*/

		//subtext
		/*ptSubtext = new Paint();
		ptSubtext.setAntiAlias(true);
		ptSubtext.setColor(cSubtext);
		ptSubtext.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		ptSubtext.setTextSize(sSubText);
		
		pSubtext = new Point();
		pSubtext.y = pLogo.y + logoFont.bottom - subtextFont.ascent;
		pSubtext.x = (screenW - (int) ptSubtext.measureText(tSubtext))/2 + x;
		
		int btmLogoArea = pSubtext.y + subtextFont.descent;*/
		
		
		//welcome pic
		int marginPic = normalSpc;
		//int hPic = yBtnTop-btmLogoArea-2*marginPic;
		int hPic = (int) (screenH*0.4-2*marginPic);
		int wPic = screenW-2*marginPic;
		Rect rWelcomePicArea = new Rect();
		rWelcomePicArea.left = x+marginPic;
		rWelcomePicArea.right = rWelcomePicArea.left+wPic;
		rWelcomePicArea.top = marginPic;
		rWelcomePicArea.bottom = rWelcomePicArea.top + hPic;
		
		rWelcomePic = Screen.calMaxImageLoc(bdWelcomePic, rWelcomePicArea);
		imgWelcomePic = new MyImage(rWelcomePic, bdWelcomePic);
		
		
		
		int yBtnTop = rWelcomePicArea.bottom + marginPic;
		
		
		//size of each button
		int btnTextMargin = sBtnText;
				
		//button text Paint
		ptBtnText = new Paint();
		ptBtnText.setAntiAlias(true);
		ptBtnText.setColor(cBtnText);
		ptBtnText.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptBtnText.setTextSize(sBtnText);
		
		
		//calculate button radius
		int txtLen = (int) ptBtnText.measureText(tHomeBtn);
		if((int) ptBtnText.measureText(tRecentBtn)>txtLen)
			txtLen = (int) ptBtnText.measureText(tRecentBtn);
		if((int) ptBtnText.measureText(tHistoryBtn)>txtLen)
			txtLen = (int) ptBtnText.measureText(tHistoryBtn);
		if((int) ptBtnText.measureText(tAboutBtn)>txtLen)
			txtLen = (int) ptBtnText.measureText(tAboutBtn);

		int btnWidth = txtLen+smallSpc*2;
		int btnHeight = btnWidth;
		
		int hBtnArea = screenH-yBtnTop;
		int wBtnArea = screenW;
		
		int spcBtn;
		
		if(hBtnArea>=wBtnArea)
			spcBtn = (wBtnArea-btnWidth*2)/3;
		else
			spcBtn = (hBtnArea-btnWidth*2)/3;
			
		
		
		int hBtnMargin = (hBtnArea-btnWidth*2-spcBtn)/2;
		int wBtnMargin = (wBtnArea-btnWidth*2-spcBtn)/2;
		
		
		
		//Home button
		rHomeBtn = new Rect(x+wBtnMargin, 
							yBtnTop+hBtnMargin, 
							x+wBtnMargin+btnWidth,
							yBtnTop+hBtnMargin+btnHeight);
		pHome = new Point();
		pHome.y = rHomeBtn.top + btnWidth/2 + ((btnFont.descent-btnFont.ascent)/2-btnFont.descent);
		pHome.x = rHomeBtn.left + (btnWidth-(int) ptBtnText.measureText(tHomeBtn))/2;
		
		//Recent button
		rRecentBtn = new Rect(rHomeBtn.right+spcBtn,
								rHomeBtn.top,
								screenW-wBtnMargin,
								rHomeBtn.bottom);
		pRecent = new Point();
		pRecent.y = rRecentBtn.top + btnWidth/2 + ((btnFont.descent-btnFont.ascent)/2-btnFont.descent);
		pRecent.x = rRecentBtn.left + (btnWidth-(int) ptBtnText.measureText(tRecentBtn))/2;
		
		
		//History button
		rHistoryBtn = new Rect(rHomeBtn.left,
								rHomeBtn.bottom+spcBtn,
								rHomeBtn.right,
								rHomeBtn.bottom+spcBtn+btnHeight);
		pHistory = new Point();
		pHistory.y = rHistoryBtn.top + btnWidth/2 + ((btnFont.descent-btnFont.ascent)/2-btnFont.descent);
		pHistory.x = rHistoryBtn.left + (btnWidth-(int) ptBtnText.measureText(tHistoryBtn))/2;
		
		//About button
		rAboutBtn = new Rect(rRecentBtn.left,
							rHistoryBtn.top,
							rRecentBtn.right,
							rHistoryBtn.bottom);
		pAbout = new Point();	
		pAbout.y = rAboutBtn.top + btnWidth/2 + ((btnFont.descent-btnFont.ascent)/2-btnFont.descent);
		pAbout.x = rAboutBtn.left + (btnWidth-(int) ptBtnText.measureText(tAboutBtn))/2;

		
		//Home button
		/*rHomeBtn = new Rect(x+(screenW-btnWidth)/2, 
							yBtnTop+y, 
							x+(screenW-btnWidth)/2+btnWidth,
							yBtnTop+y+btnHeight);
		pHome = new Point();
		pHome.y = rHomeBtn.top + btnTextMargin - btnFont.ascent;
		pHome.x = rHomeBtn.left + (btnWidth-(int) ptBtnText.measureText(tHomeBtn))/2;
		
		//Recent button
		rRecentBtn = new Rect(rHomeBtn.left,
								rHomeBtn.bottom+spcBtn,
								rHomeBtn.right,
								rHomeBtn.bottom+spcBtn+btnHeight);
		pRecent = new Point();
		pRecent.y = rRecentBtn.top + btnTextMargin - btnFont.ascent;
		pRecent.x = rRecentBtn.left + (btnWidth-(int) ptBtnText.measureText(tRecentBtn))/2;
		
		
		//History button
		rHistoryBtn = new Rect(rRecentBtn.left,
								rRecentBtn.bottom+spcBtn,
								rRecentBtn.right,
								rRecentBtn.bottom+spcBtn+btnHeight);
		pHistory = new Point();
		pHistory.y = rHistoryBtn.top + btnTextMargin - btnFont.ascent;
		pHistory.x = rHistoryBtn.left + (btnWidth-(int) ptBtnText.measureText(tHistoryBtn))/2;
		
		
		//Setting button
		rSettingBtn = new Rect(rHistoryBtn.left,
								rHistoryBtn.bottom+spcBtn,
								rHistoryBtn.right,
								rHistoryBtn.bottom+spcBtn+btnHeight);
		pSetting = new Point();
		pSetting.y = rSettingBtn.top + btnTextMargin - btnFont.ascent;
		pSetting.x = rSettingBtn.left + (btnWidth-(int) ptBtnText.measureText(tSettingBtn))/2;
		
		
		//About button
		rAboutBtn = new Rect(rSettingBtn.left,
							rSettingBtn.bottom+spcBtn,
							rSettingBtn.right,
							rSettingBtn.bottom+spcBtn+btnHeight);
		pAbout = new Point();
		pAbout.y = rAboutBtn.top + btnTextMargin - btnFont.ascent;
		pAbout.x = rAboutBtn.left + (btnWidth-(int) ptBtnText.measureText(tAboutBtn))/2;
		
		//psqi button
		rPSQIBtn = new Rect(rAboutBtn.left,
							rAboutBtn.bottom+spcBtn,
							rAboutBtn.right,
							rAboutBtn.bottom+spcBtn+btnHeight);
		pPSQI = new Point();
		pPSQI.y = rPSQIBtn.top + btnTextMargin - btnFont.ascent;
		pPSQI.x = rPSQIBtn.left + (btnWidth-(int) ptBtnText.measureText(tPSQIBtn))/2;*/
		
		
		/*********************Initialize Buttons List*********************/
		MyButton btn = new MyButton(ButtonID.HOME_START, rHomeBtn);
		//btn.setImage(image, imageHit);
		btn.setText(tHomeBtn, pHome, ptBtnText);
		btn.setColor(AppUI.COLOR_LIME, AppUI.COLOR_GREY_BKG);
		btn.isFill = true;
		btn.setLineWidth(20);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.HOME_RECENT, rRecentBtn);
		//btn.setImage(image, imageHit);
		btn.setText(tRecentBtn, pRecent, ptBtnText);
		btn.setColor(AppUI.COLOR_RED, AppUI.COLOR_GREY_BKG);
		btn.setLineWidth(20);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.HOME_HISTORY, rHistoryBtn);
		//btn.setImage(image, imageHit);
		btn.setText(tHistoryBtn, pHistory, ptBtnText);
		btn.setColor(AppUI.COLOR_PINK, AppUI.COLOR_GREY_BKG);
		btn.setLineWidth(20);
		btnList.add(btn);
		
		/*btn = new MyButton(ButtonID.HOME_SETTING, rSettingBtn);
		btn.setImage(image, imageHit);
		btn.setText(tSettingBtn, pSetting, ptBtnText);
		btnList.add(btn);*/
		
		btn = new MyButton(ButtonID.HOME_ABOUT, rAboutBtn);
		//btn.setImage(image, imageHit);
		btn.setText(tAboutBtn, pAbout, ptBtnText);
		btn.setColor(AppUI.COLOR_BLUE, AppUI.COLOR_GREY_BKG);
		btn.setLineWidth(20);
		btnList.add(btn);
		
		/*btn = new MyButton(ButtonID.HOME_PSQI, rPSQIBtn);
		btn.setImage(image, imageHit);
		btn.setText(tPSQIBtn, pPSQI, ptBtnText);
		btnList.add(btn);*/
				
	}//EOF initialization()
}
