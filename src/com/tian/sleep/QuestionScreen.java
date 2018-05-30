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

public class QuestionScreen extends Screen
{
	
	
	Context parentContext;
	
	int curQuestion = 0;
	int[] btnNum = {8,6,8,8,4,4,4,4,4,4,4,4,4};
	
	float psqiScore = 0;
	float psqiFullScore = 6;
	
	int numScreen = 13;
	int numButtons = 8;
	
	String psqiAns = "";	//store the answer that will send to data server;
	
	/********* views **********/
	TextView mTV;	
	
	
	/********* Fonts **********/
	//font initialization is required 	
	Paint.FontMetricsInt fontDetailInfo = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt fontHeader = AppUI.FONT_NORMAL;
	Paint.FontMetricsInt fontBtn = AppUI.FONT_NORMAL;
	
	
	/********* text **********/
	String[] texts = null;
	String[][] btnTexts = null;
	float[][] psqiScores = null;
	
	String tThanks = "Thanks! Based on your answers, your sleep quality score is ";

	
	
	/*************** btn **************/	
	int imageResID = R.drawable.btn_blue;
	int imageHitResID = R.drawable.btn_grey;
	BitmapDrawable imageBtn = null;
	BitmapDrawable imageBtnHit = null;
	
	
	/********* Paints **********/
	Paint ptDetailInfo;
	Paint ptHeader;
	Paint ptBtn;
	
	
	/********** Color ***********/
	int cDetailInfo = Color.WHITE;
	int cBtnText = Color.WHITE;	
	int cBkgrd = AppUI.COLOR_GREY_BKG;
	
	
	/********* Locations **********/	
	Point[][] pBtnText;

	
	/********** buttons ***********/
	Rect[] rBtns;
	Rect rText;

	
	public void initialize()
    {
		//create button and text arrays	
		btnTexts = new String[numScreen][numButtons];
		texts = new String[numScreen];
		pBtnText = new Point[numScreen][numButtons];
		rBtns = new Rect[numButtons];
		psqiScores = new float[numScreen][numButtons];
		
		for(int i=0; i<numButtons; i++)
			rBtns[i] = new Rect();
		
		for(int i=0; i<numScreen; i++)
			for(int j=0; j<numButtons; j++)
				pBtnText[i][j] = new Point();
		
		texts[0] = "1 of 13. During the past week, what time have you usually gone to bed at night?";
		btnTexts[0][0] = "before 8pm";
		btnTexts[0][1] = "8~9pm";
		btnTexts[0][2] = "9~10pm";
		btnTexts[0][3] = "10~11pm";
		btnTexts[0][4] = "11pm~12am";
		btnTexts[0][5] = "12~1am";
		btnTexts[0][6] = "1~2am";
		btnTexts[0][7] = "after 2am";
		
		psqiScores[0][0] = 0;
		psqiScores[0][1] = 0;
		psqiScores[0][2] = 0;
		psqiScores[0][3] = 0;
		psqiScores[0][4] = 0;
		psqiScores[0][5] = 0;
		psqiScores[0][6] = 0;
		psqiScores[0][7] = 0;
		
		texts[1] = "2 of 13. During the past week, how long has it usually taken you to fall asleep each night?";
		btnTexts[1][0] = "< 10 min";
		btnTexts[1][1] = "10~30 min";
		btnTexts[1][2] = "30~60 min";
		btnTexts[1][3] = "60~90 min";
		btnTexts[1][4] = "90~120 min";
		btnTexts[1][5] = "> 120 min";
		btnTexts[1][6] = null;
		btnTexts[1][7] = null;
		
		psqiScores[1][0] = 0;
		psqiScores[1][1] = 0;
		psqiScores[1][2] = 0;
		psqiScores[1][3] = 0;
		psqiScores[1][4] = 0;
		psqiScores[1][5] = 0;
		psqiScores[1][6] = 0;
		psqiScores[1][7] = 0;
		
		texts[2] = "3 of 13. During the past week, what time have you usually gotten up in the morning?";
		btnTexts[2][0] = "before 5am";
		btnTexts[2][1] = "5~6am";
		btnTexts[2][2] = "6~7am";
		btnTexts[2][3] = "7~8am";
		btnTexts[2][4] = "8~9am";
		btnTexts[2][5] = "9~10am";
		btnTexts[2][6] = "10~11am";
		btnTexts[2][7] = "after 11am";
		
		psqiScores[2][0] = 0;
		psqiScores[2][1] = 0;
		psqiScores[2][2] = 0;
		psqiScores[2][3] = 0;
		psqiScores[2][4] = 0;
		psqiScores[2][5] = 0;
		psqiScores[2][6] = 0;
		psqiScores[2][7] = 0;
		
		texts[3] = "4 of 13. During the past week, how many hours of actual sleep did you get at night? (This may be different than the number of hours you spent in bed.)";
		btnTexts[3][0] = "< 2hr";
		btnTexts[3][1] = "2~3hr";
		btnTexts[3][2] = "3~4hr";
		btnTexts[3][3] = "4~5hr";
		btnTexts[3][4] = "5~6hr";
		btnTexts[3][5] = "6~7hr";
		btnTexts[3][6] = "7~8hr";
		btnTexts[3][7] = "> 8hr";
		
		psqiScores[3][0] = 3;
		psqiScores[3][1] = 3;
		psqiScores[3][2] = 3;
		psqiScores[3][3] = 3;
		psqiScores[3][4] = 2;
		psqiScores[3][5] = 1;
		psqiScores[3][6] = 0;
		psqiScores[3][7] = 0;
		
		texts[4] = "5 of 13. During the past week, how many times have you had trouble sleeping because you:" +
				"\n\nCannot get to sleep within 30 minutes";
		btnTexts[4][0] = "None";
		btnTexts[4][1] = "less than 1";
		btnTexts[4][2] = "1 or 2";
		btnTexts[4][3] = "3 or more";
		btnTexts[4][4] = null;
		btnTexts[4][5] = null;
		btnTexts[4][6] = null;
		btnTexts[4][7] = null;
		
		psqiScores[4][0] = 0;
		psqiScores[4][1] = (float)1/(float)9;
		psqiScores[4][2] = (float)2/(float)9;
		psqiScores[4][3] = (float)3/(float)9;
		psqiScores[4][4] = 0;
		psqiScores[4][5] = 0;
		psqiScores[4][6] = 0;
		psqiScores[4][7] = 0;
		
		texts[5] = "6 of 13. During the past week, how many times have you had trouble sleeping because you:" +
				"\n\nWake up in the middle of the night or early morning";
		btnTexts[5][0] = "None";
		btnTexts[5][1] = "less than 1";
		btnTexts[5][2] = "1 or 2";
		btnTexts[5][3] = "3 or more";
		btnTexts[5][4] = null;
		btnTexts[5][5] = null;
		btnTexts[5][6] = null;
		btnTexts[5][7] = null;
		
		psqiScores[5][0] = 0;
		psqiScores[5][1] = (float)1/(float)9;
		psqiScores[5][2] = (float)2/(float)9;
		psqiScores[5][3] = (float)3/(float)9;
		psqiScores[5][4] = 0;
		psqiScores[5][5] = 0;
		psqiScores[5][6] = 0;
		psqiScores[5][7] = 0;
		
		texts[6] = "7 of 13. During the past week, how many times have you had trouble sleeping because you:" +
				"\n\nHave to get up to use the bathroom";
		btnTexts[6][0] = "None";
		btnTexts[6][1] = "less than 1";
		btnTexts[6][2] = "1 or 2";
		btnTexts[6][3] = "3 or more";
		btnTexts[6][4] = null;
		btnTexts[6][5] = null;
		btnTexts[6][6] = null;
		btnTexts[6][7] = null;
		
		psqiScores[6][0] = 0;
		psqiScores[6][1] = (float)1/(float)9;
		psqiScores[6][2] = (float)2/(float)9;
		psqiScores[6][3] = (float)3/(float)9;
		psqiScores[6][4] = 0;
		psqiScores[6][5] = 0;
		psqiScores[6][6] = 0;
		psqiScores[6][7] = 0;
		
		texts[7] = "8 of 13. During the past week, how many times have you had trouble sleeping because you:" +
				"\n\nCannot breathe comfortably";
		btnTexts[7][0] = "None";
		btnTexts[7][1] = "less than 1";
		btnTexts[7][2] = "1 or 2";
		btnTexts[7][3] = "3 or more";
		btnTexts[7][4] = null;
		btnTexts[7][5] = null;
		btnTexts[7][6] = null;
		btnTexts[7][7] = null;
		
		psqiScores[7][0] = 0;
		psqiScores[7][1] = (float)1/(float)9;
		psqiScores[7][2] = (float)2/(float)9;
		psqiScores[7][3] = (float)3/(float)9;
		psqiScores[7][4] = 0;
		psqiScores[7][5] = 0;
		psqiScores[7][6] = 0;
		psqiScores[7][7] = 0;
		
		texts[8] = "9 of 13. During the past week, how many times have you had trouble sleeping because you:" +
				"\n\nCough or snore loudly";
		btnTexts[8][0] = "None";
		btnTexts[8][1] = "less than 1";
		btnTexts[8][2] = "1 or 2";
		btnTexts[8][3] = "3 or more";
		btnTexts[8][4] = null;
		btnTexts[8][5] = null;
		btnTexts[8][6] = null;
		btnTexts[8][7] = null;
		
		psqiScores[8][0] = 0;
		psqiScores[8][1] = (float)1/(float)9;
		psqiScores[8][2] = (float)2/(float)9;
		psqiScores[8][3] = (float)3/(float)9;
		psqiScores[8][4] = 0;
		psqiScores[8][5] = 0;
		psqiScores[8][6] = 0;
		psqiScores[8][7] = 0;
		
		texts[9] = "10 of 13. During the past week, how would you rate your sleep quality overall?";
		btnTexts[9][0] = "Very Good";
		btnTexts[9][1] = "Fairly Good";
		btnTexts[9][2] = "Fairly Bad";
		btnTexts[9][3] = "Very Bad";
		btnTexts[9][4] = null;
		btnTexts[9][5] = null;
		btnTexts[9][6] = null;
		btnTexts[9][7] = null;
		
		psqiScores[9][0] = 0;
		psqiScores[9][1] = (float)1/(float)9;
		psqiScores[9][2] = (float)2/(float)9;
		psqiScores[9][3] = (float)3/(float)9;
		psqiScores[9][4] = 0;
		psqiScores[9][5] = 0;
		psqiScores[9][6] = 0;
		psqiScores[9][7] = 0;
		
		texts[10] = "11 of 13. During the past week, how many times have you taken medicine to help you sleep (prescribed or \"over the counter\")?";
		btnTexts[10][0] = "None";
		btnTexts[10][1] = "less than 1";
		btnTexts[10][2] = "1 or 2";
		btnTexts[10][3] = "3 or more";
		btnTexts[10][4] = null;
		btnTexts[10][5] = null;
		btnTexts[10][6] = null;
		btnTexts[10][7] = null;
		
		psqiScores[10][0] = 0;
		psqiScores[10][1] = (float)1/(float)9;
		psqiScores[10][2] = (float)2/(float)9;
		psqiScores[10][3] = (float)3/(float)9;
		psqiScores[10][4] = 0;
		psqiScores[10][5] = 0;
		psqiScores[10][6] = 0;
		psqiScores[10][7] = 0;
		
		texts[11] = "12 of 13. During the past week, how many times have you had trouble staying awake while driving, eating meals, or engaging in social activity?";
		btnTexts[11][0] = "None";
		btnTexts[11][1] = "less than 1";
		btnTexts[11][2] = "1 or 2";
		btnTexts[11][3] = "3 or more";
		btnTexts[11][4] = null;
		btnTexts[11][5] = null;
		btnTexts[11][6] = null;
		btnTexts[11][7] = null;
		
		psqiScores[11][0] = 0;
		psqiScores[11][1] = (float)1/(float)9;
		psqiScores[11][2] = (float)2/(float)9;
		psqiScores[11][3] = (float)3/(float)9;
		psqiScores[11][4] = 0;
		psqiScores[11][5] = 0;
		psqiScores[11][6] = 0;
		psqiScores[11][7] = 0;
		
		texts[12] = "13 of 13. During the past week, how much of a problem has it been for you to keep up enough enthusiasm to get things done?";
		btnTexts[12][0] = "No problem";
		btnTexts[12][1] = "Very slight";
		btnTexts[12][2] = "Somewhat";
		btnTexts[12][3] = "Very big";
		btnTexts[12][4] = null;
		btnTexts[12][5] = null;
		btnTexts[12][6] = null;
		btnTexts[12][7] = null;
		
		psqiScores[12][0] = 0;
		psqiScores[12][1] = (float)1/(float)9;
		psqiScores[12][2] = (float)2/(float)9;
		psqiScores[12][3] = (float)3/(float)9;
		psqiScores[12][4] = 0;
		psqiScores[12][5] = 0;
		psqiScores[12][6] = 0;
		psqiScores[12][7] = 0;
		
		
    	//set spacing info
		int smallSpc = (int) AppUI.SIZE_SMALL_TEXT;
		int normalSpc = (int) AppUI.SIZE_NORMAL_TEXT;
		int midSpc = (int) AppUI.SIZE_MID_TEXT;
		int largeSpc = (int) AppUI.SIZE_LARGE_TEXT;
		
		/************* Spc of Each Area ************/
		int sideMargin = normalSpc;
		int btmMargin = normalSpc;	//spc btw btm and btn
		
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
		ptBtn.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		ptBtn.setTextSize(hBtnText);
		
		
		/************* Button Area ************/
		int marginBtnText = hBtnText/2;
		int hBtn = hBtnText + 2*marginBtnText;
		int spcBtn = smallSpc; //spc btw btn, ver and hor
		
		int hBtnArea = 4*hBtn + 3*spcBtn + btmMargin;
		int topBtnArea = screenH - hBtnArea + y;
		
		int btnWidth = (screenW-spcBtn-2*sideMargin)/2;
		
		rBtns[0].left = sideMargin + x;
		rBtns[0].right = rBtns[0].left + btnWidth;
		rBtns[0].top = topBtnArea;
		rBtns[0].bottom = rBtns[0].top + hBtn;
		
		rBtns[1].left = rBtns[0].right + spcBtn;
		rBtns[1].right = rBtns[1].left + btnWidth;
		rBtns[1].top = topBtnArea;
		rBtns[1].bottom = rBtns[0].top + hBtn;
		
		rBtns[2].left = sideMargin + x;
		rBtns[2].right = rBtns[0].left + btnWidth;
		rBtns[2].top = rBtns[0].bottom + spcBtn;
		rBtns[2].bottom = rBtns[2].top + hBtn;
		
		rBtns[3].left = rBtns[0].right + spcBtn;
		rBtns[3].right = rBtns[1].left + btnWidth;
		rBtns[3].top = rBtns[1].bottom + spcBtn;
		rBtns[3].bottom = rBtns[3].top + hBtn;
		
		rBtns[4].left = sideMargin + x;
		rBtns[4].right = rBtns[0].left + btnWidth;
		rBtns[4].top = rBtns[2].bottom + spcBtn;
		rBtns[4].bottom = rBtns[4].top + hBtn;
		
		rBtns[5].left = rBtns[0].right + spcBtn;
		rBtns[5].right = rBtns[1].left + btnWidth;
		rBtns[5].top = rBtns[3].bottom + spcBtn;
		rBtns[5].bottom = rBtns[5].top + hBtn;
		
		rBtns[6].left = sideMargin + x;
		rBtns[6].right = rBtns[0].left + btnWidth;
		rBtns[6].top = rBtns[4].bottom + spcBtn;
		rBtns[6].bottom = rBtns[6].top + hBtn;
		
		rBtns[7].left = rBtns[0].right + spcBtn;
		rBtns[7].right = rBtns[1].left + btnWidth;
		rBtns[7].top = rBtns[5].bottom + spcBtn;
		rBtns[7].bottom = rBtns[7].top + hBtn;
		
		
		/********** set btn text **************/
		for(int p = 0; p<numScreen; p++)
			for(int i=0; i<numButtons; i++)
			{
				String str = btnTexts[p][i];
				Rect rect = rBtns[i];
				
				if(str==null)
					continue;
				
				pBtnText[p][i].y = rect.top + marginBtnText - fontBtn.ascent;
				pBtnText[p][i].x = rect.left + (btnWidth-(int) ptBtn.measureText(str))/2;
			}
		
		
		/************* set up text view *****************/
		int verMargin = smallSpc;	//the ver margin btw top and btw btn area
		rText = new Rect();
		rText.left = sideMargin + x;
		rText.right = x+screenW-sideMargin;
		rText.top = verMargin + y;
		rText.bottom = topBtnArea - verMargin;

	
		
		/************* Initialize Buttons **************/
		MyButton btn = new MyButton(ButtonID.Q0, rBtns[0]);
		btn.setImage(imageBtn, imageBtnHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.Q1, rBtns[1]);
		btn.setImage(imageBtn, imageBtnHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.Q2, rBtns[2]);
		btn.setImage(imageBtn, imageBtnHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.Q3, rBtns[3]);
		btn.setImage(imageBtn, imageBtnHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.Q4, rBtns[4]);
		btn.setImage(imageBtn, imageBtnHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.Q5, rBtns[5]);
		btn.setImage(imageBtn, imageBtnHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.Q6, rBtns[6]);
		btn.setImage(imageBtn, imageBtnHit);
		btnList.add(btn);
		
		btn = new MyButton(ButtonID.Q7, rBtns[7]);
		btn.setImage(imageBtn, imageBtnHit);
		btnList.add(btn);

		
    }
	
	
	public void paintScreen(Canvas cvs)
    {
		cvs.drawColor(cBkgrd);
		
		//mTV.setTextSize((float) (mTV.getTextSize()*1.25));

		
		@SuppressWarnings("deprecation")
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(rText.width(), rText.height(), rText.left, rText.top);
		//LayoutParams lp  = new LayoutParams(rText.width(), rText.height());
		mTV.setLayoutParams(lp);
		mTV.setTextColor(cDetailInfo);
		
		mTV.setText(texts[curQuestion]);

		
		mTV.setScroller(new Scroller(parentContext)); 
		//mET.setMaxLines(rEdit.height()/midSpc); 
		mTV.setVerticalScrollBarEnabled(true); 
		mTV.setMovementMethod(new ScrollingMovementMethod()); 
		
		
		/***************** Paint button images ******************/
		Iterator<MyButton> iterator = btnList.iterator();
		MyButton btn;
		int index = 0;
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			
			if(index>=btnNum[curQuestion])
				break;
			
			btn.paintButton(cvs);
			cvs.drawText(btnTexts[curQuestion][index],pBtnText[curQuestion][index].x, pBtnText[curQuestion][index].y, ptBtn);
			index++;
		}		

    }
	
	
	public QuestionScreen(Rect r, Context context, TextView textView)
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
		imageBtn = (BitmapDrawable) context.getResources().getDrawable(imageResID);
		imageBtnHit = (BitmapDrawable) context.getResources().getDrawable(imageHitResID);
		

		//create button list
		btnList = new LinkedList<MyButton>();
	}
}
