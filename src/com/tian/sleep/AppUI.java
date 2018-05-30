package com.tian.sleep;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

public class AppUI {
	
	final static String LOGO_TEXT = "iSleeps";
	
	final static boolean isBlockEnabled = false;
	
	final static boolean isShowConsentForm = false;
	
	final static boolean isFakeUI = true;
	
	static boolean isUploadDataWhenStop = false;
	
	final static boolean isUploadAllData = false;	//inside paint About Screen
	
	final static boolean isShowCoughRealTime = true;
	
	
	
	
	static String USER_ID = null;
	
	static Location bestLastLocation;
	
	static Calendar startTime = null;
	static Calendar endTime = null;
	
	static Context context = null;
	
	final static String CONFIG_DOWNLOAD_ADDR = "https://dl.dropbox.com/u/10208615/configUpdate.txt";
	
	//textview
	static float SIZE_ORIG_TEXTVIEW;
	static float SIZE_QUESTION_TEXTVIEW;
	//menu
	final static float SIZE_MENU_BTN = 0.2f;
	final static double TIME_MENU_BTN = 0.2;
	
	
	//alpha
	final static int ALPHA_DETAILRECT = 80;
	final static int ALPHA_PEAKPOINT = 128;
	final static int ALPHA_DOTLINE = 100;
	
	//color
	final static int COLOR_SCREEN_BACKGROUND = Color.WHITE;
	
	final static int COLOR_RED = Color.rgb(192,15,0);
	final static int COLOR_PINK = Color.rgb(253,0,100);
	final static int COLOR_GREEN = Color.rgb(50,116,44);
	final static int COLOR_BLUE = Color.rgb(0,161,203);
	final static int COLOR_ORANGE = Color.rgb(255,114,0);
	final static int COLOR_GREY = Color.rgb(87, 96, 105);
	final static int COLOR_GREY_BKG = Color.rgb(47, 48, 48);
	final static int COLOR_LIME = Color.rgb(153, 255, 0);
	final static int COLOR_BANANA = Color.rgb(255, 255, 102);
	
	
	//general

	//final static int COLOR_BTN_BKGRD = Color.rgb(248, 147, 29);
	final static int COLOR_BTN_BKGRD = Color.rgb(128, 188, 22);
	
	//Font Info
	final static float SMALL_TEXT_INCH = 0.06f;
	final static float NORMAL_TEXT_INCH = 0.08f;
	final static float MID_TEXT_INCH = 0.12f;
	final static float LARGE_TEXT_INCH = 0.18f;
	final static float LOGO_TEXT_INCH = 0.6f;
	
	static Paint.FontMetricsInt FONT_SMALL;
	static Paint.FontMetricsInt FONT_NORMAL;
	static Paint.FontMetricsInt FONT_MID;
	static Paint.FontMetricsInt FONT_LARGE;
	static Paint.FontMetricsInt FONT_LOGO;
	
	static float SIZE_NORMAL_TEXT;	//the size of normal text in font size
	static float SIZE_LOGO_TEXT;
	static float SIZE_LARGE_TEXT;
	static float SIZE_MID_TEXT;
	static float SIZE_SMALL_TEXT;
	
	//welcome screen
	final static int COLOR_WELCOME_BACKGROUND = Color.rgb(147, 224, 255);
	final static int COLOR_WELCOME_TEXT = Color.rgb(87, 96, 105);
	final static int COLOR_WELCOME_BUTTON_TEXT = Color.WHITE;
	final static int COLOR_WELCOME_BUTTON_TEXT_HOVER = Color.rgb(255, 94, 72);
	final static String WELCOME_TEXT = "iSleep";
	final static String WELCOME_TEXT_SUB = "How well did you sleep?";
	final static String WELCOME_TEXT_BUTTON_START = "START";
	final static String WELCOME_TEXT_BUTTON_HISTORY = "HISTORY";
	final static String WELCOME_TEXT_BUTTON_ABOUT = "ABOUT";
	
	//monitor screen
	final static int COLOR_MONITOR_BACKGROUND = COLOR_WELCOME_BACKGROUND;
	final static String TXT_MONITOR_MONI = "Monitoring";
	final static int COLOR_MONITOR_MONI = Color.rgb(0, 255, 0);
													
	final static String TXT_MONITOR_START = "Start Time: ";
	final static String TXT_MONITOR_DUR = "Duration: ";
	final static int COLOR_MONITOR_TEXT = Color.WHITE;
	final static String TXT_MONITOR_STOP = "STOP";
	final static int COLOR_MONITOR_STOP_BKG = Color.RED;
	final static int COLOR_MONITOR_STOP = Color.WHITE;
	final static String TXT_MONITOR_INFO1 = "Click stop after you wake up"; 
	final static String TXT_MONITOR_INFO2 = "in the morning to see result.";
	final static int COLOR_MONITOR_INFO = Color.rgb(87, 96, 105);
	
	final static int COLOR_MONITOR_METER_BKGRD = Color.rgb(23,50,7);
	final static int COLOR_MONITOR_METER_LOW = Color.rgb(126,187,18);
	final static int COLOR_MONITOR_METER_MID = Color.rgb(249,157,15);
	final static int COLOR_MONITOR_METER_HIGH = Color.rgb(249,82,13);
	
	//result screen
	final static int COLOR_RESULT_BACKGROUND = COLOR_WELCOME_BACKGROUND;
	final static String TXT_RESULT_TIMEONBED = "Total Time on Bed: ";
	final static String TXT_RESULT_SLEEPTIME = "Actual Sleep Time: ";
	final static String TXT_RESULT_EFFICIENCY = "Sleep Efficiency: ";
	final static int COLOR_RESULT_SLEEPINFO = Color.WHITE;
	final static int COLOR_RESULT_EFFICIENCY_NUM = Color.rgb(247, 68, 97);
	final static String TXT_RESULT_EVENTS = "SLEEP EVENTS";
	final static int COLOR_RESULT_EVENTS_BTN = Color.rgb(248, 147, 29);
	final static int COLOR_RESULT_EVENTS_TXT = Color.WHITE;
	final static String TXT_RESULT_INFO1 = "Click to see detailed events";
	final static String TXT_RESULT_INFO2 = "e.g., snore and cough.";
	final static int COLOR_RESULT_INFO = Color.rgb(87, 96, 105);
	final static String TXT_RESULT_HOMEBTN = "< Home";
	final static int COLOR_RESULT_HOMEBTNBKG = Color.rgb(173, 195, 192);
	final static int COLOR_RESULT_HOMEBTN = Color.BLACK;
	
	final static int COLOR_ACTIBAR_DETAIL_BKGRD = Color.rgb(87, 96, 105);
	final static int COLOR_ACTIBAR_DETAIL_TXT = Color.WHITE;
	
	final static int COLOR_RESULT_FIGBKG = Color.rgb(173, 195, 192);
	final static int COLOR_RESULT_ACTI_SLEEP = AppUI.COLOR_LIME;
	final static int COLOR_RESULT_ACTI_WAKE = COLOR_MONITOR_METER_HIGH;
		
	
	//event result screen	
	final static int COLOR_EVENTRESULT_COUGH = Color.rgb(119, 52, 96);
	final static int COLOR_EVENTRESULT_SNORE = Color.rgb(6, 128, 67);
	final static int COLOR_EVENTRESULT_GETUP = Color.rgb(255, 222, 0);
	final static int COLOR_EVENTRESULT_MOVE = Color.rgb(254, 67, 101);
	final static int COLOR_EVENTRESULT_TIME = Color.BLACK;
	final static int COLOR_EVENTRESULT_LINE = Color.rgb(87, 96, 105);
	final static float SIZE_EVENTRESULT_LINE = 2;
	final static float SIZE_EVENTRESULT_EVENT_ROUND_RADIUS = 5;
	
	final static int COLOR_EVENTRESULT_BTN = Color.rgb(248, 147, 29);
	final static int COLOR_EVENTRESULT_BTN_SELECT = Color.rgb(128, 188, 22);
	final static int COLOR_EVENTRESULT_BAKBTN = Color.rgb(173, 195, 192);
	final static int COLOR_EVENTRESULT_BTN_TXT = Color.WHITE;
	final static int COLOR_EVENTRESULT_BAKBTN_TXT = Color.BLACK;
	
	final static String TXT_EVENTRESULT_SNOREBTN = "Snore";
	final static String TXT_EVENTRESULT_MOVEBTN = "Move";
	final static String TXT_EVENTRESULT_COUGHBTN = "Cough";
	final static String TXT_EVENTRESULT_GETUPBTN = "Getup";
	final static String TXT_EVENTRESULT_BACKBTN = "< BACK";
	
	final static int COLOR_EVENTRESULT_TIMELABEL = Color.BLACK;
	
	final static int COLOR_EVENTBAR_DETAIL_BKGRD = Color.rgb(87, 96, 105);
	final static int COLOR_EVENTBAR_DETAIL_TXT = Color.WHITE;
	
	
	//history screen
	final static String TXT_HISTORY_SLEEPQUALITY = "Quality";
	final static String TXT_HISTORY_SLEEPEVENT = "Events";
	final static String TXT_HISTORY_HOMEBTN = "< HOME";
	
	final static int COLOR_HISTORY_BTN = Color.rgb(248, 147, 29);
	final static int COLOR_HISTORY_BTN_SELECT = Color.rgb(128, 188, 22);
	final static int COLOR_HISTORY_HOMEBTN = Color.rgb(173, 195, 192);
	final static int COLOR_HISTORY_BTN_TXT = Color.WHITE;
	final static int COLOR_HISTORY_HOMEBTN_TXT = Color.BLACK;
	
	final static int COLOR_HISTORYBAR_DETAIL_BKGRD = Color.rgb(87, 96, 105);
	final static int COLOR_HISTORY_DETAIL_TXT = Color.WHITE;
	
	//history quality screen
	final static String TXT_HISTORY_QUALITY_LINE1 = "Overall sleep quality";
	final static String TXT_HISTORY_QUALITY_LINE2 = "over this Month is:";
	final static int COLOR_HISTORY_QUALITY_LINE = Color.WHITE;
	final static int COLOR_HISTORY_QUALITY_EFFICIENCY = Color.rgb(255, 66, 93);
	final static String TXT_HISTORY_QUALITY_INFO_LINE1 = "Sleep efficiency over";
	final static String TXT_HISTORY_QUALITY_INFO_LINE2 = "selected period of time";
	final static int COLOR_HISTORY_QUALITY_BAR = Color.rgb(255, 66, 93);
	final static int COLOR_HISTORY_QUALITY_BARINFO = Color.rgb(87, 96, 105);
	
	//history event screen
	final static int COLOR_HISTORY_EVENT_COUGH = Color.rgb(119, 52, 96);
	final static int COLOR_HISTORY_EVENT_SNORE = Color.rgb(6, 128, 67);
	final static int COLOR_HISTORY_EVENT_GETUP = Color.rgb(255, 222, 0);
	final static int COLOR_HISTORY_EVENT_MOVE = Color.rgb(254, 67, 101);
}
