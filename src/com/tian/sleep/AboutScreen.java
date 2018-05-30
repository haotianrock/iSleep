package com.tian.sleep;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;


import com.tian.sleep.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

@SuppressLint("NewApi") public class AboutScreen extends Screen
{
	boolean isMenuExpanded = false;
	boolean hasMenu = false;
	
	boolean isShowEditText = false;
	
	Context parentContext;
	
	
	/********* views **********/
	TextView mTV;
	EditText mET;
	
	
	/********* Fonts **********/
	//font initialization is required 	
	Paint.FontMetricsInt fontDetailInfo = AppUI.FONT_SMALL;
	Paint.FontMetricsInt fontHeader = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt fontBtn = AppUI.FONT_MID;
	

	/********** Btn Images ***********/
	int imageResID = R.drawable.btn_orange;
	int imageHitResID = R.drawable.btn_grey;
	BitmapDrawable imageContact = null;
	BitmapDrawable imageContactHit = null;
	
	int imageIntroResID = R.drawable.btn_blue;
	int imageIntroHitResID = R.drawable.btn_grey;
	BitmapDrawable imageIntro = null;
	BitmapDrawable imageIntroHit = null;
	
	/********* Paints **********/
	Paint ptDetailInfo;
	Paint ptHeader;
	Paint ptBtn;
	
	/********** Color ***********/
	int cDetailInfo = Color.WHITE;
	int cBtnText = Color.WHITE;	
	int cBkgrd = AppUI.COLOR_GREY_BKG;
	int cEditBkgrd = Color.rgb(200,231,234);
	int cEditText = AppUI.COLOR_GREY_BKG;

	/********* Texts **********/
	String tWelcomeMsg = "- Thank you for using "+AppUI.LOGO_TEXT+".";
	String tAboutAppHeader = "- About This App";
	String tAboutAppContent = "This application is built to measure your sleep quality non-intrusively while your are sleeping at night. "
								+"It automatically monitors the sound through the built-in microphone and analyzes the acoustic samples to measure your sleep quality. "
								+"When you wake up, you will be provided a report that shows your sleep efficiency, and the detected activities such as snoring and coughing over night.";
	String tResearchHeader = "- Helping Our Research";
	String tResearchContent = "By allowing iSleep to upload ANONYMOUS everyday sleep information (e.g., snoring at 3am),"
								+" you are helping us on improving the performance of sleep quality measurement. "
								+"Our research has been approved by Human Research and Protection Program at Michigan State University. "
								+"The reviewing committee has found that our research protects the rights and welfare of human subjects, "
								+"and meets the requirements of MSU's Federal Wide Assurance and the Federal Guidelines (45 CFR 46 and 21 CFR Part 50).";
	
	String tConsentBtnText = "Upload";
	String tBtnText = "Feedback";
	String tIntroText = "Intro";
	String tSendBtnText = "Send";
	String tBtnDetailInfo1 = "If you have any questions or comments,";
	String tBtnDetailInfo2 = "leave your message by clicking the 'Feedback' button.";
	
	
	/********* Paragraph **********/
	Paragraph paraWelcome;
	Paragraph paraAbout;
	Paragraph paraResearch;
	
	
	/********* Locations **********/
	Point pWelcomeMsg;
	Point pAboutAppHeader;
	Point[] pAboutAppPara;
	Point pResearchHeader;
	Point[] pResearchPara;
	
	Point pContactBtn; 
	Point pConsentBtn;
	Point pIntroBtn;
	Point pSendBtn; 
	Point pBtnDetailInfo1;
	Point pBtnDetailInfo2;
	
	/********** buttons ***********/
	Rect rContactBtn;
	Rect rIntroBtn;
	Rect rConsent;
	
	Rect rText;
	Rect rEdit;
	
	BitmapDrawable bdBackground;
	
	public void initialize()
    {
    	//set spacing info
		int smallSpc = (int) AppUI.SIZE_SMALL_TEXT;
		int normalSpc = (int) AppUI.SIZE_NORMAL_TEXT;
		int midSpc = (int) AppUI.SIZE_MID_TEXT;
		int largeSpc = (int) AppUI.SIZE_LARGE_TEXT;
		
		/************* Spc of Each Area ************/
		int topMargin = smallSpc;	//the margin btw top screen and first paragraph
		int paraSpc = smallSpc;	//spc btw paras
		int spcHeaderDetail = 0;	//spc btw header and content		
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
		
		int hHeaderText = fontHeader.descent - fontHeader.ascent; 
		ptHeader = new Paint();
		ptHeader.setAntiAlias(true);
		ptHeader.setColor(cDetailInfo); 	
		ptHeader.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		ptHeader.setTextSize(hHeaderText);
		
		int hBtnText = fontBtn.descent - fontBtn.ascent; 
		ptBtn = new Paint();
		ptBtn.setAntiAlias(true);
		ptBtn.setColor(cBtnText); 	
		ptBtn.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
		ptBtn.setTextSize(hBtnText);
		
		
		/************* Button Area ************/
		int marginBtnText = hBtnText/2;
		int hBtn = hBtnText + 2*marginBtnText;
		int spcBtn1 = smallSpc/2;	//spc btw btn and detailed info
		
		int spcBtn2 = normalSpc;	//spc btw btn and screen btm
		int spcBtn3 = normalSpc;
		
		int hBtnArea = 3*hBtn + spcBtn1 + 2*hDetailInfoText + spcBtn2 + 2*spcBtn3;
		int topBtnArea = screenH - hBtnArea + y;
		
		int btnWidth = screenW/2;
		
		//btn rect		
		rConsent = new Rect();
		rConsent.left = x+(screenW-btnWidth)/2;
		rConsent.right = rConsent.left + btnWidth;
		rConsent.top = topBtnArea;
		rConsent.bottom = rConsent.top + hBtn;
		
		rIntroBtn = new Rect();
		rIntroBtn.left = x+(screenW-btnWidth)/2;
		rIntroBtn.right = rIntroBtn.left + btnWidth;
		rIntroBtn.top = rConsent.bottom + spcBtn3;
		rIntroBtn.bottom = rIntroBtn.top + hBtn;
		
		rContactBtn = new Rect();
		rContactBtn.left = x+(screenW-btnWidth)/2;
		rContactBtn.right = rContactBtn.left + btnWidth;
		rContactBtn.top = rIntroBtn.bottom + spcBtn3;
		rContactBtn.bottom = rContactBtn.top + hBtn;
		
		
		
		//btn text loc
		pContactBtn = new Point();
		pContactBtn.y = rContactBtn.top + marginBtnText - fontBtn.ascent;
		pContactBtn.x = rContactBtn.left + (btnWidth-(int) ptBtn.measureText(tBtnText))/2;
		
		pIntroBtn = new Point();
		pIntroBtn.y = rIntroBtn.top + marginBtnText - fontBtn.ascent;
		pIntroBtn.x = rIntroBtn.left + (btnWidth-(int) ptBtn.measureText(tIntroText))/2;
		
		pConsentBtn = new Point();
		pConsentBtn.y = rConsent.top + marginBtnText - fontBtn.ascent;
		pConsentBtn.x = rConsent.left + (btnWidth-(int) ptBtn.measureText(tConsentBtnText))/2;
	
		
		pSendBtn = new Point();
		pSendBtn.y = rContactBtn.top + marginBtnText - fontBtn.ascent;
		pSendBtn.x = rContactBtn.left + (btnWidth-(int) ptBtn.measureText(tSendBtnText))/2;
		
		//detail btn info
		pBtnDetailInfo1 = new Point();
		pBtnDetailInfo1.y = rContactBtn.bottom + spcBtn1 - fontDetailInfo.ascent;
		pBtnDetailInfo1.x = (screenW - (int)ptDetailInfo.measureText(tBtnDetailInfo1))/2 + x;
		
		pBtnDetailInfo2 = new Point();
		pBtnDetailInfo2.y = rContactBtn.bottom + hDetailInfoText + spcBtn1 - fontDetailInfo.ascent;
		pBtnDetailInfo2.x = (screenW - (int)ptDetailInfo.measureText(tBtnDetailInfo2))/2 + x;
		
		
		/************* set up text view *****************/
		rText = new Rect();
		rText.left = sideMargin + x;
		rText.right = x+screenW-sideMargin;
		rText.top = topMargin + y;
		rText.bottom = topBtnArea - topMargin;

		
		
		/************* set up Edit Text *****************/
		rEdit = new Rect();
		rEdit.left = sideMargin + x;
		rEdit.right = x+screenW-sideMargin;
		rEdit.top = midSpc + y;
		rEdit.bottom = rEdit.top + largeSpc;
		
	
		
		/************* Initialize Buttons **************/
		MyButton btn = new MyButton(ButtonID.ABOUT_CONTACT, rContactBtn);
		//btn.setImage(imageContact, imageContactHit);
		btn.setColor(AppUI.COLOR_LIME, AppUI.COLOR_GREY_BKG);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.ABOUT_INTRO, rIntroBtn);
		//btn.setImage(imageIntro, imageIntroHit);
		btn.setColor(AppUI.COLOR_BLUE, AppUI.COLOR_GREY_BKG);
		btn.setLineWidth(20);
		btn.isFill = true;
		btn.setAlpha(160);
		btnList.add(btn);
		
		
		if(AppUI.isShowConsentForm)
		{
			btn = new MyButton(ButtonID.CONSENT, rConsent);
			//btn.setImage(imageIntro, imageIntroHit);
			btn.setColor(AppUI.COLOR_PINK, AppUI.COLOR_GREY_BKG);
			btn.setLineWidth(20);
			btn.isFill = true;
			btn.setAlpha(160);
			btnList.add(btn);
		}
		
		
		
    }
	
	
	public void paintScreen(Canvas cvs)
    {
		/**** upload all data ****/
		if(AppUI.isUploadAllData)
		{
			String path = null;	
			File[] fileList = null;
			path = Environment.getExternalStorageDirectory().getPath()+"/iSleepFeedBackFiles";
			
			if(path!=null)
			{
				File dir = new File(path);
				fileList = dir.listFiles();
			}
			
			for(int i=0; i<fileList.length; i++)
			{
				Log.e("sending file", fileList[i].toString());
				FileUploader fu = new FileUploader();
				fu.send(fileList[i], "data");
			}
		}		
		/*************************/
		
		
		if(bdBackground==null)
			cvs.drawColor(AppUI.COLOR_SCREEN_BACKGROUND);
		else
			cvs.drawBitmap(bdBackground.getBitmap(), new Rect(0,0,bdBackground.getIntrinsicWidth(), bdBackground.getIntrinsicHeight()), rScreen, null);
			//cvs.drawBitmap(bdBackground.getBitmap(), 0, 0, null);
		
		
		/***************** set up Text view ******************/
		@SuppressWarnings("deprecation")
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(rText.width(), rText.height(), rText.left, rText.top);
		//LayoutParams lp  = new LayoutParams(rText.width(), rText.height());
		mTV.setLayoutParams(lp);
		mTV.setTextColor(cDetailInfo);
		//mTV.setText(tWelcomeMsg + "\n\n" + tAboutAppHeader + "\n" + tAboutAppContent + "\n\n"
					 //+ tResearchHeader+"\n"+tResearchContent);
		mTV.setText(tWelcomeMsg + "\n\n" + tAboutAppHeader + "\n" + tAboutAppContent);
		mTV.setScroller(new Scroller(parentContext)); 
		//mET.setMaxLines(rEdit.height()/midSpc); 
		mTV.setVerticalScrollBarEnabled(true); 
		mTV.setMovementMethod(new ScrollingMovementMethod()); 
		
		
		/************* set up Edit Text *****************/
		@SuppressWarnings("deprecation")
		AbsoluteLayout.LayoutParams lpEdit = new AbsoluteLayout.LayoutParams(rEdit.width(), ViewGroup.LayoutParams.WRAP_CONTENT, rEdit.left, rEdit.top);
		//LayoutParams lp  = new LayoutParams(rText.width(), rText.height());
		mET.setLayoutParams(lpEdit);
		mET.setMaxHeight(rEdit.height());
		mET.setLines(10);
		mET.setBackgroundColor(cEditBkgrd);
		mET.setTextColor(cEditText);
		//mET.setTextAlignment(View.TEXT_ALIGNMENT_RESOLVED_DEFAULT);
		
		mET.setScroller(new Scroller(parentContext)); 
		//mET.setMaxLines(rEdit.height()/midSpc); 
		mET.setVerticalScrollBarEnabled(true); 
		mET.setMovementMethod(new ScrollingMovementMethod()); 
		
		//btn text	
		if(isShowEditText)
		{
			btnList.get(0).drawButton(cvs);
			cvs.drawText(tSendBtnText, pSendBtn.x, pSendBtn.y, ptBtn);
		}
		else
		{
			btnList.get(0).drawButton(cvs);
			btnList.get(1).drawButton(cvs);
			
			if(AppUI.isShowConsentForm)
			{
				btnList.get(2).drawButton(cvs);
				cvs.drawText(tConsentBtnText, pConsentBtn.x, pConsentBtn.y, ptBtn);
			}
			
			cvs.drawText(tIntroText, pIntroBtn.x, pIntroBtn.y, ptBtn);
			
			cvs.drawText(tBtnText, pContactBtn.x, pContactBtn.y, ptBtn);
			cvs.drawText(tBtnDetailInfo1, pBtnDetailInfo1.x, pBtnDetailInfo1.y, ptDetailInfo);	
			cvs.drawText(tBtnDetailInfo2, pBtnDetailInfo2.x, pBtnDetailInfo2.y, ptDetailInfo);
		}

    }
	
	
	
	
	public AboutScreen(Rect r, Context context, TextView textView, EditText editText, BitmapDrawable bdBkgrd)
	{
		//set screen size and location
		rScreen = new Rect(r);
		screenH = r.height();
		screenW = r.width();
		x = r.left;
		y = r.top;
		
		mTV = textView;
		mET = editText;
		
		parentContext = context;
		
		bdBackground = bdBkgrd;
		
		//load btn images
		imageContact = (BitmapDrawable) context.getResources().getDrawable(imageResID);
		imageContactHit = (BitmapDrawable) context.getResources().getDrawable(imageHitResID);
		
		imageIntro = (BitmapDrawable) context.getResources().getDrawable(imageIntroResID);
		imageIntroHit = (BitmapDrawable) context.getResources().getDrawable(imageIntroHitResID);

		//create button list
		btnList = new LinkedList<MyButton>();
	}
}
