package com.tian.sleep;

import java.util.Iterator;
import java.util.LinkedList;

import com.tian.sleep.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.widget.TextView;

public class IntroScreen extends Screen
{
	Context parentContext;
	
	boolean isStillLoading = false;
	
	int curScreenIndex = 0;
	int numScreen = 5;
	
	/********** btn ***********/
	Paint.FontMetricsInt fontBtn = AppUI.FONT_MID;
	Paint ptBtn;
	
	Point pPrevBtn;
	Point pNextBtn;
	
	Rect rPrevBtn;
	Rect rNextBtn;
	
	String tPrevBtn = "Prev";
	String tNextBtn = "Next";
	
	
	/********** Btn Images ***********/
	int imageBtnID = R.drawable.btn_blue;
	int imageBtnHitResID = R.drawable.btn_grey;
	BitmapDrawable imagePrev = null;
	BitmapDrawable imagePrevHit = null;
	BitmapDrawable imageNext = null;
	BitmapDrawable imageNextHit = null;
	
	
	int picW = 1008;
	int picH = 591;
	
	int tPicW = 1078;
	int tPicH = 540;
	
	
	/********* Images *********/	
	//MyImage imgWelcomeText = null;
	//BitmapDrawable bdWelcomeText;
	int idWelcomeText = R.drawable.isleep_welcome;
	Rect rWelcomeText;
	
	//MyImage imgWorkText = null;
	//BitmapDrawable bdWorkText;
	int idWorkText = R.drawable.isleep_work;
	Rect rWorkText;
	
	//MyImage imgUse1Text = null;
	//BitmapDrawable bdUse1Text;
	int idUse1Text = R.drawable.isleep_use1;
	Rect rUse1Text;
	
	//MyImage imgUse2Text = null;
	//BitmapDrawable bdUse2Text;
	int idUse2Text = R.drawable.isleep_use2;
	Rect rUse2Text;
	
	//MyImage imgUse3Text = null;
	//BitmapDrawable bdUse3Text;
	int idUse3Text = R.drawable.isleep_use3;
	Rect rUse3Text;
	
	//MyImage imgWelcomePic = null;
	BitmapDrawable bdWelcomePic;
	int idWelcomePic = R.drawable.sleep_intro_img;
	Rect rPic;
	
	
	Bitmap bmpBkgrd = null;
	
	public void initialize()
	{
		
		
		
		
		//set spacing info
		int smallSpc = (int) AppUI.SIZE_SMALL_TEXT;
		int normalSpc = (int) AppUI.SIZE_NORMAL_TEXT;
		int midSpc = (int) AppUI.SIZE_MID_TEXT;
		
		int sideMargin = normalSpc;
		
		
		int spcPicText = midSpc;
		int topMargin = midSpc;
		
		int spcBtnBtm = normalSpc;
		int spcBtnSide = normalSpc;
		int spcBtnPic = normalSpc;
		

		
		/************* btns ****************/
		int hBtnText = fontBtn.descent - fontBtn.ascent; 
		ptBtn = new Paint();
		ptBtn.setColor(Color.WHITE); 
		ptBtn.setAntiAlias(true);
		ptBtn.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptBtn.setTextSize(hBtnText);
		
		int marginBtnText = hBtnText/3;
		int hBtn = hBtnText + 2*marginBtnText;
		int topBtnArea = y + screenH - spcBtnBtm - hBtn;
		
		int btnWidth = (int) (ptBtn.measureText(tNextBtn)*2);
		
		//btn rect
		rPrevBtn = new Rect();
		rPrevBtn.left = x+spcBtnSide;
		rPrevBtn.right = rPrevBtn.left + btnWidth;
		rPrevBtn.top = topBtnArea;
		rPrevBtn.bottom = rPrevBtn.top + hBtn;
		
		rNextBtn = new Rect();		
		rNextBtn.right = x+screenW-spcBtnSide;
		rNextBtn.left = rNextBtn.right - btnWidth;
		rNextBtn.top = topBtnArea;
		rNextBtn.bottom = rNextBtn.top + hBtn;
		
		//btn text loc
		pPrevBtn = new Point();
		pPrevBtn.y = rPrevBtn.top + marginBtnText - fontBtn.ascent;
		pPrevBtn.x = rPrevBtn.left + (btnWidth-(int) ptBtn.measureText(tPrevBtn))/2;
		
		pNextBtn = new Point();
		pNextBtn.y = rNextBtn.top + marginBtnText - fontBtn.ascent;
		pNextBtn.x = rNextBtn.left + (btnWidth-(int) ptBtn.measureText(tNextBtn))/2;
		
		
		
		int btmMargin = spcBtnBtm + spcBtnPic + hBtn;
		int picTextWidth = screenW - 2*sideMargin;
		
		//keep ratio
		int picTextHeight = (int)(picTextWidth*((double)tPicH/(double)tPicW));
		
		int topTextImg = y + screenH - btmMargin - picTextHeight;
		
		Rect rTextImg = new Rect();
		rTextImg.left = y+sideMargin;
		rTextImg.right = rTextImg.left + picTextWidth;
		rTextImg.top = topTextImg;
		rTextImg.bottom = rTextImg.top + picTextHeight;
		
		rWelcomeText = rTextImg;
		rWorkText = rTextImg;
		rUse1Text = rTextImg;
		rUse2Text = rTextImg;
		rUse3Text = rTextImg;		
		

		
		
		/************* Initialize Pic Images **************/
		rPic = new Rect();
		rPic.left = x+sideMargin;
		rPic.right = x + screenW - sideMargin;
		rPic.top = y+topMargin;
		rPic.bottom = topTextImg - spcPicText;
		
		rPic = MyImage.fitInRect(rPic, picW, picH);
		
		
		
		/************* Initialize Buttons **************/
		MyButton btn = new MyButton(ButtonID.INTRO_NEXT, rNextBtn);
		btn.setImage(imageNext, imageNextHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.INTRO_PREV, rPrevBtn);
		btn.setImage(imagePrev, imagePrevHit);
		btnList.add(btn);
  
	}
	
	public void paintScreen(Canvas cvs)
	{
		isStillLoading = true;
		
		cvs.drawColor(Color.WHITE);
		
		
		/***************** Paint button images ******************/
		Iterator<MyButton> iterator = btnList.iterator();
		MyButton btn;
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			btn.paintButton(cvs);
		}
		
		
		if(curScreenIndex==0)
		{
			BitmapDrawable bdWelcomeText = (BitmapDrawable) parentContext.getResources().getDrawable(idWelcomeText);
			MyImage imgWelcomeText = new MyImage(rWelcomeText, bdWelcomeText);
			MyImage imgWelcomePic = new MyImage(rPic, bdWelcomePic);
			imgWelcomeText.paintImage(cvs);
			imgWelcomePic.paintImage(cvs);	

		}
		else if(curScreenIndex==1)
		{
			BitmapDrawable bdWorkPic = (BitmapDrawable) parentContext.getResources().getDrawable(R.drawable.work_img);
			MyImage imgWorkPic = new MyImage(rPic, bdWorkPic);
			imgWorkPic.paintImage(cvs);
			
			BitmapDrawable bdWorkText = (BitmapDrawable) parentContext.getResources().getDrawable(idWorkText);
			MyImage imgWorkText = new MyImage(rWorkText, bdWorkText);
			imgWorkText.paintImage(cvs);
		}
		else if(curScreenIndex==2)
		{
			BitmapDrawable bdUse1Pic = (BitmapDrawable) parentContext.getResources().getDrawable(R.drawable.use1_img);
			MyImage imgUse1Pic = new MyImage(rPic, bdUse1Pic);
			imgUse1Pic.paintImage(cvs);
			
			BitmapDrawable bdUse1Text = (BitmapDrawable) parentContext.getResources().getDrawable(idUse1Text);
			MyImage imgUse1Text = new MyImage(rUse1Text, bdUse1Text);
			imgUse1Text.paintImage(cvs);
		}
		else if(curScreenIndex==3)
		{
			BitmapDrawable bdUse2Pic = (BitmapDrawable) parentContext.getResources().getDrawable(R.drawable.use2_img);
			MyImage imgUse2Pic = new MyImage(rPic, bdUse2Pic);
			imgUse2Pic.paintImage(cvs);
			
			BitmapDrawable bdUse2Text = (BitmapDrawable) parentContext.getResources().getDrawable(idUse2Text);
			MyImage imgUse2Text = new MyImage(rUse2Text, bdUse2Text);
			imgUse2Text.paintImage(cvs);
		}
		else if(curScreenIndex==4)
		{
			BitmapDrawable bdUse3Pic = (BitmapDrawable) parentContext.getResources().getDrawable(R.drawable.use3_img);
			MyImage imgUse3Pic = new MyImage(rPic, bdUse3Pic);
			imgUse3Pic.paintImage(cvs);
			
			BitmapDrawable bdUse3Text = (BitmapDrawable) parentContext.getResources().getDrawable(idUse3Text);
			MyImage imgUse3Text = new MyImage(rUse3Text, bdUse3Text);
			imgUse3Text.paintImage(cvs);
		}
		
		//btn text	
		cvs.drawText(tPrevBtn, pPrevBtn.x, pPrevBtn.y, ptBtn);
		cvs.drawText(tNextBtn, pNextBtn.x, pNextBtn.y, ptBtn);
		
		isStillLoading = false;
	}
	
	public IntroScreen(Rect r, Context context, BitmapDrawable welcomePic)
	{
		//set screen size and location
		rScreen = new Rect(r);
		screenH = r.height();
		screenW = r.width();
		x = r.left;
		y = r.top;
		
		parentContext = context;
		
		bdWelcomePic = (BitmapDrawable) context.getResources().getDrawable(idWelcomePic);
		
		//load btn images
		imagePrev = (BitmapDrawable) context.getResources().getDrawable(imageBtnID);
		imagePrevHit = (BitmapDrawable) context.getResources().getDrawable(imageBtnHitResID);
		
		imageNext = (BitmapDrawable) context.getResources().getDrawable(imageBtnID);
		imageNextHit = (BitmapDrawable) context.getResources().getDrawable(imageBtnHitResID);
		
		//load images
		


		//create button list
		btnList = new LinkedList<MyButton>();
	}
}
