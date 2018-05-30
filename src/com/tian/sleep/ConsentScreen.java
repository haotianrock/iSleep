package com.tian.sleep;

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
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

@SuppressLint("NewApi") public class ConsentScreen extends Screen
{	
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
	int cEditBkgrd = Color.rgb(200,231,234);
	int cEditText = AppUI.COLOR_GREY_BKG;

	/********* Texts **********/
	String tWelcomeMsg = "- Thank you for using "+AppUI.LOGO_TEXT+"." 
								+ "To use this app, you need to sign the following consent form by clicking 'Agree'.";
	String t1H = "1.Purpose of Research";
	String t1 = "You are being asked to participate in a research study of sleep quality monitoring, by anonymously uploading your sleep events information detected by the application to our data server. From this study, the researchers hope to explore methods of monitoring sleep quality in a nonintrusive manner (using the microphone and accelerometer on smartphones). You must be at least 18 years old to qualify for this study.";
	
	String t2H = "2. What You Will Do";
	String t2 = "You will not be asked to do anything for the research. After you click ‘OK’ on the agreement, your anonymous sleep event information will be uploaded to the server periodically on the background. You are also able to turn on and off the upload at any time in setting. At the end of data collection period (7 days), you will also be asked to complete a questionnaire regarding your sleep quality (see attachment).";
	
	String t3H = "3. Potential Benefits";
	String t3 = "Based on the analysis of the collected sleep events, we will be able to improve the performance of our application, so that users will get a more accurate estimation of their sleep quality.";
	
	String t4H = "4. Potential Risks";
	String t4 = "To the best of our knowledge, there are no potential risks of participating in this study. The data collection is conducted in the background, which will not cause any interruptions to the normal usage of phone. The amount of data to be uploaded is very small (around 1KB/day).";
	
	String t5H = "5. Privacy and Confidentiality";
	String t5 = "iSleep will not collect any data that may reveal your identity. When you open the application for the first time, this consent page will be shown in a dialog. By clicking the ‘OK’ button, we consider you allow us to upload sleep event data and other information to the server (see a sample of data to be uploaded at the end of this paragraph). By clicking the ‘No, thanks’ button, you are able to disable any data upload.\n  The data we collect includes your sleep events that are detected by the application while you are sleeping over night, along with your hashed MAC address as your ID and your location. The sleep event information is composed of the type of the event, its time, and duration. The current version of this application is able to detect 4 types of events, which are body movement, snoring, coughing and get-up. The hashed MAC address only allows us to distinguish the data uploaded by different users without knowing your real MAC address or your identity.";
	String t5data = "move 10:10-10:20pm\n" +
					"move 11:10-11:20pm\n" + 
					"move 12:30-1:40am\n" + 
					"snore 1:10-1:20am\n" + 
					"move 2:40-2:50am\n" + 
					"snore 4:10-4:20am\n" + 
					"...\n" + 
					"move 7:20-7:30am\n" + 
					"cough 8:30-8:40am";
			
	String t6H = "6. Your Rights";
	String t6 = "Participation in this research project is completely voluntary. You have the right to say no. The data upload can be turned on and off in the application setting at any time. You may change your mind at any time and withdraw with no consequences. You will be told of any significant findings that develop during the course of the study that may influence your willingness to continue to participate in the research.";
	
	String t7H = "7. Contact Information";
	String t7 = "If you have concerns or questions about this study, such as scientific issues, how to do any part of it, please send us feedback by clicking the 'About' button on the Home screen.";
	
	String tAgreeBtnText = "Agree";
	String tNoBtnText = "No,thanks";
	String tBtnDetailInfo1 = "click 'Agree' to sign the consent form.";
	String tBtnDetailInfo2 = "click 'No,thanks' to exit.";

	
	
	/********* Locations **********/	
	Point pAgreeBtn; 
	Point pNoBtn; 
	Point pBtnDetailInfo1;
	Point pBtnDetailInfo2;
	
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
		
		//detail btn info
		pBtnDetailInfo1 = new Point();
		pBtnDetailInfo1.y = rAgreeBtn.bottom + spcBtn1 - fontDetailInfo.ascent;
		pBtnDetailInfo1.x = (screenW - (int)ptDetailInfo.measureText(tBtnDetailInfo1))/2 + x;
		
		pBtnDetailInfo2 = new Point();
		pBtnDetailInfo2.y = rNoBtn.bottom + spcBtn1 - fontDetailInfo.ascent;
		pBtnDetailInfo2.x = (screenW - (int)ptDetailInfo.measureText(tBtnDetailInfo2))/2 + x;
		
		
		/************* set up text view *****************/
		rText = new Rect();
		rText.left = sideMargin + x;
		rText.right = x+screenW-sideMargin;
		rText.top = topMargin + y;
		rText.bottom = topBtnArea - topMargin;
		
	
		
		/************* Initialize Buttons **************/
		MyButton btn = new MyButton(ButtonID.CONSENT_AGREE, rAgreeBtn);
		btn.setImage(imageAgree, imageAgreeHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.CONSENT_NO, rNoBtn);
		btn.setImage(imageNo, imageNoHit);
		btnList.add(btn);
		
    }
	
	
	public void paintScreen(Canvas cvs)
    {
		cvs.drawColor(cBkgrd);
		
		@SuppressWarnings("deprecation")
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(rText.width(), rText.height(), rText.left, rText.top);
		//LayoutParams lp  = new LayoutParams(rText.width(), rText.height());
		mTV.setLayoutParams(lp);
		mTV.setTextColor(cDetailInfo);
		
		
		mTV.setScroller(new Scroller(parentContext)); 
		//mET.setMaxLines(rEdit.height()/midSpc); 
		mTV.setVerticalScrollBarEnabled(true); 
		mTV.setMovementMethod(new ScrollingMovementMethod()); 
		
		mTV.setText(t1H + "\n" + t1 + "\n\n"
				+ t2H + "\n" + t2 + "\n\n"
				+ t3H + "\n" + t3 + "\n\n"
				+ t4H + "\n" + t4 + "\n\n"
				+ t5H + "\n" + t5 + "\n" + t5data + "\n\n"
				+ t6H + "\n" + t6 + "\n\n"
				+ t7H + "\n" + t7 );
		
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
		cvs.drawText(tBtnDetailInfo1, pBtnDetailInfo1.x, pBtnDetailInfo1.y, ptDetailInfo);	
		cvs.drawText(tBtnDetailInfo2, pBtnDetailInfo2.x, pBtnDetailInfo2.y, ptDetailInfo);
		

    }
	
	
	
	
	public ConsentScreen(Rect r, Context context, TextView textView)
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
