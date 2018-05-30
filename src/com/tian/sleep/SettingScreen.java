package com.tian.sleep;

import java.util.Iterator;
import java.util.LinkedList;

import com.tian.sleep.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;


public class SettingScreen extends Screen
{
	Context parentContext;
	
	boolean isSwitchOn = false;
	
	/********* Fonts **********/
	//font initialization is required 	
	Paint.FontMetricsInt fontDetailInfo = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt fontBtn = AppUI.FONT_MID;
	
	/********** Btn Images ***********/
	int imageAgreeResID = R.drawable.btn_green;
	int imageAgreeHitResID = R.drawable.btn_grey;
	BitmapDrawable imageAgree = null;
	BitmapDrawable imageAgreeHit = null;
	
	/********* Paints **********/
	Paint ptDetailInfo;
	Paint ptBtn;
	
	/********* points ************/
	Point pBtnInfo;
	
	Point pAgreeBtn; 
	Point pNoBtn; 
	
	/********** Color ***********/
	int cText = Color.WHITE;	
	int cBtnText = Color.WHITE;	
	int cBkgrd = AppUI.COLOR_GREY_BKG;
	

	/********* Texts **********/
	String tOn = "On"; 
	String tOff = "Off"; 
	String tSwitch = "Uploading sleep event data";
	
	/********** buttons ***********/
	Rect rAgreeBtn;
	
	
	
	public void initialize()
    {
    	//set spacing info
		int smallSpc = (int) AppUI.SIZE_SMALL_TEXT;
		int normalSpc = (int) AppUI.SIZE_NORMAL_TEXT;
		int midSpc = (int) AppUI.SIZE_MID_TEXT;
		int largeSpc = (int) AppUI.SIZE_LARGE_TEXT;
		
		/************* Spc of Each Area ************/
		int topMargin = screenH/3;	//the margin btw top screen and first paragraph	
		int spcTextBtn = smallSpc;

		
		/************* Paint ************/
		int hText = fontDetailInfo.descent - fontDetailInfo.ascent; 
		ptDetailInfo = new Paint();
		ptDetailInfo.setAntiAlias(true);
		ptDetailInfo.setColor(cText); 	
		ptDetailInfo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptDetailInfo.setTextSize(hText);
		
		int hBtnText = fontBtn.descent - fontBtn.ascent; 
		ptBtn = new Paint();
		ptBtn.setAntiAlias(true);
		ptBtn.setColor(cBtnText); 	
		ptBtn.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptBtn.setTextSize(hBtnText);
		
		/************ loc **************/
		//detail btn info
		pBtnInfo = new Point();
		pBtnInfo.y = y + topMargin - fontDetailInfo.ascent;
		pBtnInfo.x = (screenW - (int)ptDetailInfo.measureText(tSwitch))/2 + x;
		
		//btn
		int btnWidth = screenW/3;
		int marginBtnText = hBtnText/3;
		int hBtn = hBtnText + 2*marginBtnText;
		
		rAgreeBtn = new Rect();
		rAgreeBtn.left = x+(screenW-btnWidth)/2;
		rAgreeBtn.right = rAgreeBtn.left + btnWidth;
		rAgreeBtn.top = y + topMargin + spcTextBtn + hText;
		rAgreeBtn.bottom = rAgreeBtn.top + hBtn;
		
		//btn text loc
		pAgreeBtn = new Point();
		pAgreeBtn.y = rAgreeBtn.top + marginBtnText - fontBtn.ascent;
		pAgreeBtn.x = rAgreeBtn.left + (btnWidth-(int) ptBtn.measureText(tOn))/2;
		
		pNoBtn = new Point();
		pNoBtn.y = rAgreeBtn.top + marginBtnText - fontBtn.ascent;
		pNoBtn.x = rAgreeBtn.left + (btnWidth-(int) ptBtn.measureText(tOff))/2;
		
		
		/************* Initialize Buttons **************/
		MyButton btn = new MyButton(ButtonID.CONSENT_AGREE, rAgreeBtn);
		btn.setImage(imageAgree, imageAgreeHit);
		btnList.add(btn);
    }
	
	
	public void paintScreen(Canvas cvs)
    {
		cvs.drawColor(cBkgrd);
		
		
			
		/***************** Paint button images ******************/
		Iterator<MyButton> iterator = btnList.iterator();
		MyButton btn;
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			
			/*if(isSwitchOn)
				btn.isSelected = false;
			else
				btn.isSelected = true;*/
			
			btn.isSelected = true;
			btn.paintButton(cvs);
		}
		
		
		/***************** Paint Text ******************/
		//btn text	
		cvs.drawText(tSwitch, pBtnInfo.x, pBtnInfo.y, ptDetailInfo);
		cvs.drawText(tOff, pNoBtn.x, pNoBtn.y, ptBtn);
		/*if(isSwitchOn)	
			cvs.drawText(tOn, pAgreeBtn.x, pAgreeBtn.y, ptBtn);
		else
			cvs.drawText(tOff, pNoBtn.x, pNoBtn.y, ptBtn);*/
		

    }
	
	
	public SettingScreen(Rect r, Context context)
	{
		//set screen size and location
		rScreen = new Rect(r);
		screenH = r.height();
		screenW = r.width();
		x = r.left;
		y = r.top;
		
		parentContext = context;
		
		//load btn images
		imageAgree = (BitmapDrawable) context.getResources().getDrawable(imageAgreeResID);
		imageAgreeHit = (BitmapDrawable) context.getResources().getDrawable(imageAgreeHitResID);
		
		
		//create button list
		btnList = new LinkedList<MyButton>();
	}
}
