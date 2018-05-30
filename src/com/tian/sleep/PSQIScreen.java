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
import android.text.method.ScrollingMovementMethod;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

public class PSQIScreen extends Screen
{
	DisplayStatus curScreen = DisplayStatus.SCREEN_PSQI;
	
	Context parentContext;
	
	
	/********* views **********/
	TextView mTV;	
	
	
	/********* Fonts **********/
	//font initialization is required 	
	Paint.FontMetricsInt fontDetailInfo = AppUI.FONT_SMALL;
	Paint.FontMetricsInt fontHeader = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt fontBtn = AppUI.FONT_MID;
	
	
	/********* text **********/
	String tFirstScreen = "Thanks for using iSleep. You can help us improve the performance of iSleep by telling us how well you sleep on a weekly basis. The only thing you need to do is answering several simple questions. It will only take you about 30 seconds. The questions are selected from Pittsburgh Sleep Quality Index (Buysse,D.J. et al., Psychiatry Research, 1989), which is a commonly used self-rated questionnaire. Your answers will be used to assess your sleep quality and distrubance.";
	String tAgreeBtnText = "OK";
	String tNoBtnText = "Later";
	
	
	/*************** btn **************/
	int imageAgreeResID = R.drawable.btn_green;
	int imageAgreeHitResID = R.drawable.btn_grey;
	BitmapDrawable imageAgree = null;
	BitmapDrawable imageAgreeHit = null;
	
	int imageNoResID = R.drawable.btn_red;
	int imageNoHitResID = R.drawable.btn_grey;
	BitmapDrawable imageNo = null;
	BitmapDrawable imageNoHit = null;
	
	
	/********* Paints **********/
	Paint ptDetailInfo;
	Paint ptHeader;
	Paint ptBtn;
	
	
	/********** Color ***********/
	int cDetailInfo = Color.WHITE;
	int cBtnText = Color.WHITE;	
	int cBkgrd = AppUI.COLOR_GREY_BKG;
	
	
	/********* Locations **********/	
	Point pAgreeBtn; 
	Point pNoBtn; 

	
	/********** buttons ***********/
	Rect rAgreeBtn;
	Rect rNoBtn;
	
	Rect rText;
	
	
	public void initialize()
    {		
    	//set spacing info
		int smallSpc = (int) AppUI.SIZE_SMALL_TEXT;
		int normalSpc = (int) AppUI.SIZE_NORMAL_TEXT;
		int midSpc = (int) AppUI.SIZE_MID_TEXT;
		int largeSpc = (int) AppUI.SIZE_LARGE_TEXT;
		
		/************* Spc of Each Area ************/
		int topMargin = smallSpc;	//the margin btw top screen and first paragraph	
		int sideMargin = midSpc;
		int spcParaBtn = midSpc;	//spc btw paragraph and btn
		
		int paraWidth = screenW-2*sideMargin;
		
		
		
		/************* Paint ************/
		int hDetailInfoText = fontDetailInfo.descent - fontDetailInfo.ascent; 
		ptDetailInfo = new Paint();
		ptDetailInfo.setAntiAlias(true);
		ptDetailInfo.setColor(cDetailInfo); 	
		ptDetailInfo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptDetailInfo.setTextSize(hDetailInfoText);
		
		int hBtnText = fontBtn.descent - fontBtn.ascent; 
		ptBtn = new Paint();
		ptBtn.setAntiAlias(true);
		ptBtn.setColor(cBtnText); 	
		ptBtn.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptBtn.setTextSize(hBtnText);
		
		
		/************* Button Area ************/
		int marginBtnText = hBtnText/3;
		int hBtn = hBtnText + 2*marginBtnText;
		int spcBtn1 = smallSpc/2;	//spc btw btn and detailed info	
		int spcBtn2 = normalSpc;	//spc btw btn detailinfo and other btn
		
		int hBtnArea = 2*hBtn + 2*spcBtn1 + 2*hDetailInfoText + 2*spcBtn2;
		int topBtnArea = screenH - hBtnArea + y;
		
		int btnWidth = screenW/2;
		
		//btn rect
		rAgreeBtn = new Rect();
		rAgreeBtn.left = x+(screenW-btnWidth)/2;
		rAgreeBtn.right = rAgreeBtn.left + btnWidth;
		rAgreeBtn.top = topBtnArea;
		rAgreeBtn.bottom = rAgreeBtn.top + hBtn;
		
		rNoBtn = new Rect();
		rNoBtn.left = x+(screenW-btnWidth)/2;
		rNoBtn.right = rNoBtn.left + btnWidth;
		rNoBtn.top = topBtnArea+hBtnArea/2;
		rNoBtn.bottom = rNoBtn.top + hBtn;
		
		//btn text loc
		pAgreeBtn = new Point();
		pAgreeBtn.y = rAgreeBtn.top + marginBtnText - fontBtn.ascent;
		pAgreeBtn.x = rAgreeBtn.left + (btnWidth-(int) ptBtn.measureText(tAgreeBtnText))/2;
		
		pNoBtn = new Point();
		pNoBtn.y = rNoBtn.top + marginBtnText - fontBtn.ascent;
		pNoBtn.x = rNoBtn.left + (btnWidth-(int) ptBtn.measureText(tNoBtnText))/2;
		
		
		
		
		/************* set up text view *****************/
		rText = new Rect();
		rText.left = sideMargin + x;
		rText.right = x+screenW-sideMargin;
		rText.top = topMargin + y;
		rText.bottom = topBtnArea - topMargin;
		
		
		/************* Initialize Buttons **************/
		MyButton btn = new MyButton(ButtonID.PSQI_OK, rAgreeBtn);
		btn.setImage(imageAgree, imageAgreeHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.PSQI_NO, rNoBtn);
		btn.setImage(imageNo, imageNoHit);
		btnList.add(btn);
		
    }
	
	
	public void paintScreen(Canvas cvs)
    {
		cvs.drawColor(cBkgrd);
		
		mTV.setText(tFirstScreen);
		
		@SuppressWarnings("deprecation")
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(rText.width(), rText.height(), rText.left, rText.top);
		//LayoutParams lp  = new LayoutParams(rText.width(), rText.height());
		mTV.setLayoutParams(lp);
		mTV.setTextColor(cDetailInfo);
		
		
		mTV.setScroller(new Scroller(parentContext)); 
		//mET.setMaxLines(rEdit.height()/midSpc); 
		mTV.setVerticalScrollBarEnabled(true); 
		mTV.setMovementMethod(new ScrollingMovementMethod()); 
		
		
		/***************** Paint button images ******************/
		Iterator<MyButton> iterator = btnList.iterator();
		MyButton btn;
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			btn.paintButton(cvs);
		}
		
		
		/***************** Paint Text ******************/
		//btn text	
		cvs.drawText(tAgreeBtnText, pAgreeBtn.x, pAgreeBtn.y, ptBtn);
		cvs.drawText(tNoBtnText, pNoBtn.x, pNoBtn.y, ptBtn);		

    }
	
	
	public PSQIScreen(Rect r, Context context, TextView textView)
	{
		//set screen size and location
		rScreen = new Rect(r);
		screenH = r.height();
		screenW = r.width();
		x = r.left;
		y = r.top;
		
		mTV = textView;
		
		parentContext = context;
		
		//load btn images
		imageAgree = (BitmapDrawable) context.getResources().getDrawable(imageAgreeResID);
		imageAgreeHit = (BitmapDrawable) context.getResources().getDrawable(imageAgreeHitResID);
		
		imageNo = (BitmapDrawable) context.getResources().getDrawable(imageNoResID);
		imageNoHit = (BitmapDrawable) context.getResources().getDrawable(imageNoHitResID);

		

		//create button list
		btnList = new LinkedList<MyButton>();
	}
}
