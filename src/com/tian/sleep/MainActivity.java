package com.tian.sleep;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.tian.sleep.R;


import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceView.OnTouchListener 
{
	
	ConfigurationManager configManager = null;
	
	/*********surface**********/
	SurView mSV = null;
	SurfaceHolder mSH = null;
	
	
	/********* frame layout *********/
	FrameLayout frameLayout;
	@SuppressWarnings("deprecation")
	AbsoluteLayout absLayout;
	TextView textView;
	EditText editText;
	
	
	/*************Working Thread************/
	ProcessRunnable pRun = null;
	Thread workThread = null;
	
	
	/************Main Thread Handler***********/
    final UIHandler uiHandler = new UIHandler();
    
    
    /*******************Display**********************/
    DisplayStatus displayStatus = DisplayStatus.SCREEN_HOME;
    DisplayUpdateMsg displayUpdateMsg = null;
    
    
    /*******************app state**********************/
    boolean isOnPause = false;
    boolean isExit = false;
    NotificationManager mNotificationManager = null;
    
    /******************* Screen **********************/
    ConsentScreen consentScreen = null;
    HomeScreen homeScreen = null;
    MonitorScreen monitorScreen = null;
    ResultScreen resultScreen = null;
    EventScreen eventScreen = null;
    AboutScreen aboutScreen = null;
    HistoryScreen historyScreen = null;
    SettingScreen settingScreen = null;
    PSQIScreen psqiScreen = null;
    QuestionScreen questionScreen = null;
    IntroScreen introScreen = null;
    
    
    /************ Menu ***********/
    boolean isExpand = false;
    int totalNumBtns = 4;
    MenuButton menuBtn = null;
    int menuAnimationIndex = 0;
    int menuAnimationFrames = 14;
    int menuDuration = 300;	//the time needed to play menu animation
    int menuDurPerFrame = menuDuration/menuAnimationFrames;
    
    //buttons
	MyButton lastDownBtn = null;
	LinkedList<MyButton> monitorScreenButtons = null;
	LinkedList<MyButton> resultScreenButtons = null;
	LinkedList<MyButton> eventResultScreenButtons = null;
	LinkedList<MyButton> historyQualityScreenButtons = null;
	LinkedList<MyButton> historyEventScreenButtons = null;
	
	
	/*************touchable areas***************/
	Rect touchableResult = null;
	Rect touchableEventResult = null;
	Rect touchableEventHistory = null;
	RectF touchableQualityHistory = null;
    
    
    
    private PowerManager.WakeLock wl = null;
    
    Boolean monitorTouch = true;
    
    /***************Time Info****************/
    String strStartTime;
    String strEndTime;
    String strDurTime = "0:0:0";
    String strTotalTime;
    String strActualTime = "0 hrs, 0 mins";
    int efficiency = 0;
    
    
    /************* UI Timer ***************/
    MonitorTimerTask uiTimerTask = null;
	Timer uiTimer = null;
	
    long timerPeriod = 100;
    int countTick = 0;
    int ticksPerSecond = 10;
    
    
    /****************screen info*******************/
    //handle different screen size and res
    //Initialized in Surview.onSurfaceCreate
    DisplayMetrics dm = null;
    ScreenScale scale = null;
    Context context;
    
    /****************vibration******************/
    // Get instance of Vibrator from current Context
    Vibrator vibrator = null;
    long vibBtn = 20;
    
    
    
    /************ screen status ***************/
    BitmapDrawable bdWelcomePic = null;
    
    
    /************ Preloaded Images ************/
    BitmapDrawable bdBkgrd = null;
    
    
    /**
     * reset variables in the last monitoring
     */
    private void returnToHomeScreen()
    {
    	//monitor screen
    	AppUI.startTime = null;
    	AppUI.endTime = null;
    	
    	
    	//for timer
    	countTick = 0;
    	monitorScreen.durH = 0;
    	monitorScreen.durM = 0;
    	monitorScreen.durS = 0;
        strDurTime = "0:0:0";
        
        
        if(uiTimer!=null)
        {
        	uiTimer.cancel();
        	uiTimer = null;
        }
    }
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		
		Log.e("OnCreate", "OnCreate");
		
		context = getApplicationContext();
		AppUI.context = context;
		
        //keep cpu awake while the screen is off
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "keepCPU");
        wl.acquire();

		//request full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
        //request vibration service
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
   
        //download updated config file
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.execute(AppUI.CONFIG_DOWNLOAD_ADDR);
        
        //configuration
        configManager = new ConfigurationManager();
        configManager.readFile();
        
        //set upload
        if(configManager.isAgreeConsent)
        	AppUI.isUploadDataWhenStop = true;
        else
        	AppUI.isUploadDataWhenStop = false;
        
        //load history info
        HistoryData.loadHistoryData(configManager);

        initializeScreenInfo();
        initializeFontInfo();
        
        loadBkgImg(scale);
        
        
        //get mac address as USER ID
        WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		AppUI.USER_ID = wifiInf.getMacAddress();
		
		
		//get GPS
		//require <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /> 
		//LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		//AppUI.bestLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
        
		//first time display
		displayUpdateMsg = new DisplayUpdateMsg();
		displayUpdateMsg.ds = DisplayStatus.SCREEN_HOME;
		displayStatus = displayUpdateMsg.ds;
        		
		mSV = new SurView(this);
		mSH = mSV.getHolder();	
		//setContentView(mSV);	
		
		frameLayout = new FrameLayout(this);
        absLayout = new AbsoluteLayout (this);
        
        //create text and edit control on top of SurfaceView
        textView = new TextView(this);
        editText = new EditText(this);        
        absLayout.addView(textView);    
        absLayout.addView(editText);
        
        //make other controls invisiable
        textView.setVisibility(View.INVISIBLE);
        textView.setEnabled(false);
        editText.setVisibility(View.INVISIBLE);
        editText.setEnabled(false);

        frameLayout.addView(mSV);
        frameLayout.addView(absLayout);
           
        bdWelcomePic = (BitmapDrawable) context.getResources().getDrawable(R.drawable.sleep_welcome_img);
        
        //initializeMenu();
        initializeHomeScreen();
        initializeMonitorScreen();
        initializeResultScreen();
        initializeEventResultScreen();
        initializeAboutScreen();
        initializeHistoryScreen();
        initializeConsentScreen();
        initializeSettingScreen();
        initializePSQIScreen();
        initializeQuestionScreen();
        initializeIntroScreen();
		
        
        setContentView(frameLayout);
        
        
		//Lock the screen orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);	
        
        //set touch listener to view object
        mSV.setOnTouchListener(this);        
	}
	
	
	void loadBkgImg(ScreenScale ss)
	{
		if(bdBkgrd!=null)
			return;
		
		int sh = (int)ss.hPix;
		int sw = (int)ss.wPix;
		
		//bdBkgrd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bkg_blackmetal_1280x800);
		
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.outHeight = sh;
		opts.outWidth = sw;
		
		
		//Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bkg_blackmetal_1280x800, opts);
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.moon, opts);
		
		
		bdBkgrd  = new BitmapDrawable(context.getResources(), bmp);
		
		//bdBkgrd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bkg_blackmetal_1280x800);
	
	}
	
	
	
	private void startWorkingThread()
	{
		pRun = new ProcessRunnable();
		pRun.setParentUIHandler(uiHandler);
		pRun.setConfigManager(configManager);
        
        workThread = new Thread(pRun);
		pRun.setRunning(true);
        workThread.start();
	}
	
	@Override
	public void onBackPressed() {
		Log.e("BACK", "BACK");
		
		if(displayStatus == DisplayStatus.SCREEN_ABOUT)
		{
			aboutScreen.isShowEditText = false;	//reset
			pRun.goHome();
			return;
		}
		else if(displayStatus == DisplayStatus.SCREEN_SETTING)
		{
			pRun.goHome();
			return;
		}
		else if(displayStatus == DisplayStatus.SCREEN_HISTORY)
		{
			pRun.goHome();
			return;
		}
		else if(displayStatus == DisplayStatus.SCREEN_RESULT)
		{
			returnToHomeScreen();
			pRun.goHome();
			return;
		}
		else if(displayStatus == DisplayStatus.SCREEN_RESULT_EVENT)
		{
			returnToHomeScreen();
			pRun.goHome();
			return;
		}
		else if(displayStatus == DisplayStatus.SCREEN_PSQI)
		{
			pRun.goHome();
			return;
		}
		
		else if(displayStatus == DisplayStatus.SCREEN_PSQI_QUESTION)
		{
			//reset
			questionScreen.curQuestion = 0;
			questionScreen.psqiAns = "";
			pRun.goHome();
			return;
		}
		
		else if(displayStatus == DisplayStatus.SCREEN_INTRO)
		{
			//reset
			introScreen.curScreenIndex = 0;
			pRun.goHome();
			return;
		}
		
		isExit = true;
		
		actionsOnQuit();
		
		if(mNotificationManager!=null)
			mNotificationManager.cancelAll();
		
		CharSequence text = "You have exited iSleep.";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
		
    	
		super.onBackPressed();
		
	    return;
	}
	
	private void actionsOnQuit()
	{
		if(wl!=null)
		{
    		wl.release();
    		wl=null;
		}
    	
    	if(pRun!=null)
    	{   		
    		pRun.close();
    		pRun.releaseResources();
    		pRun = null;
    	}
    	
    	if(uiTimer!=null)
    	{
    		uiTimer.cancel();
    		uiTimer = null;
    	}
    	
    	configManager.saveConfigFile();
    	
    	//send event file to server
    	if(configManager.isAgreeConsent)
    	{
    		if(HistoryData.uploadString!=null)
    		{
    			//#evalscore
    			HistoryData.uploadString = "";
    			HistoryData.uploadString+="#evalscore"+"\n";
    			HistoryData.uploadString+=Float.toString(HistoryData.evalScore)+"\n";
    			
    			String content = HistoryData.uploadString;
    			FileUploader fu = new FileUploader();
    			File f = fu.createLogFile(content);
    			fu.send(f, "data");
    			
				/*FeedBackSender fbs = new FeedBackSender();
				fbs.setContent(content);
				fbs.send("data");*/
    		}
    	}
    	
    	finish();
	}
	
    @Override
    protected void onStop()
    {
    	super.onStop();
    	
    	Log.e("STOP", "STOP");
    }
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	
    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	boolean isScreenOn = pm.isScreenOn();
    	
    	//quit using home and task button
    	if(isScreenOn)
    	{
    		//save states into files if monitoring
    		if(displayStatus == DisplayStatus.SCREEN_MONITOR)
    		{
    			isOnPause = true;
    			//endTimer();
    			Log.e("monitor", "exit");
    		}
    		else
    		{
    			isOnPause = true;
    			Log.e("other", "exit");
    		}
    		//else if(displayStatus == DisplayStatus.SCREEN_HISTORY)
    		/*{
    			isOnPause = true;
    			Log.e("history", "exit");
    		}	
    		else   		
    			actionsOnQuit();*/
    		
    		if(!isExit)
    			createNotification(true);
    	}
    	
    	Log.e("PAUSE", "PAUSE"+"/ScreenOn"+isScreenOn);
    }
    
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	
    	Log.e("onDestroy", "onDestroy");
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	
    	if(isOnPause)
    	{
    		if(displayStatus == DisplayStatus.SCREEN_MONITOR)
    			Log.e("resume","Monitor");
    		else if(displayStatus == DisplayStatus.SCREEN_HISTORY)
    		{
    			Log.e("resume","Monitor");
    		}
    		
    		//isOnPause = false; //changed back in createSurface()
    	}
    	
    	
    	Log.e("resume", "resume");
    }
    
    @Override
    protected void onStart()
    {
    	super.onStart();
    	
    	Log.e("start", "start");
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    //respond to menu item selection
		switch (item.getItemId()) 
		{
		    case R.id.menu_start:
		    	//pRun.displayResult();
		    	return true;
		    
		    case R.id.menu_settings:
		    	
		    	return true;
		    
		    default:
		    	return super.onOptionsItemSelected(item);
		}
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		Log.e("!!!","!!!");
	    if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
	        
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public class SurView extends SurfaceView implements SurfaceHolder.Callback
	{
		private int screenHeight;
		private int screenWidth;
		

		public SurView(Context context) 
		{
			super(context);
	  		
    		getHolder().addCallback(this);
    		//_thread = new DrawThread(getHolder(), this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) 
		{
			
			// TODO Auto-generated method stub
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) 
		{
			//-------compute drawing area-------
    		screenHeight = this.getHeight();
    		screenWidth = this.getWidth();
    		
    		initializeFontInfo();
    		
    		if(!isOnPause)
    		{
    			startWorkingThread();
    		}
    		else
    		{
    			if(displayStatus == DisplayStatus.SCREEN_MONITOR)
    			{
    				pRun.displayMonitorScreen();
        			isOnPause = false;
    			}
    			else
    			{
    				isOnPause = false;
    				refreshScreen();
    			}
    			
    		}
		}
		
		
		@Override
    	public void onDraw (Canvas cvs)
    	{        
			if(cvs==null)
				return;
			
			//change current displaying screen status
			if(displayUpdateMsg!=null)
				displayStatus = displayUpdateMsg.ds;

			
			switch (displayStatus) 
			{
	            case SCREEN_HOME:
	                displayHomeScreen(cvs);
	                break;
	            case SCREEN_RESULT:	       
	            	displayResultScreen(cvs);
	                break;	       
	            case SCREEN_MONITOR:
	            	displayMonitorScreen(cvs);
	            	break;
	            case SCREEN_RESULT_EVENT:
	            	displayEventResultScreen(cvs);
	            	break;
	            case SCREEN_INTRO:
	            	displayIntroScreen(cvs);
	            	break;
	            case SCREEN_HISTORY:
	            	displayHistoryScreen(cvs);
	            	break;
	            case SCREEN_ABOUT:
	            	displayAboutScreen(cvs);
	            	break;
	            case SCREEN_CONSENT:
	            	displayConsentScreen(cvs);
	            	break;
	            case SCREEN_SETTING:
	            	displaySettingScreen(cvs);
	            	break;
	            case SCREEN_PSQI:
	            	displayPSQIScreen(cvs);
	            	break;
	            case SCREEN_PSQI_QUESTION:
	            	displayQuestionScreen(cvs);
	            	break;
	            default:
	            	cvs.drawColor(Color.RED);
	                break;
			}		
    	}
		
		
		private void displayIntroScreen(Canvas cvs)
		{
			disableTextView();
			disableEditText();
			introScreen.paintScreen(cvs);
		}
		
		
		private void displayHomeScreen(Canvas cvs)
		{
			disableTextView();
			disableEditText();
			
			if(displayUpdateMsg.toastStr!=null)
    		{
    			Toast toast = Toast.makeText(context, displayUpdateMsg.toastStr, Toast.LENGTH_SHORT);
    			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
    			toast.show();
    			returnToHomeScreen();
    			displayUpdateMsg.toastStr=null;
    		}
			
			/******** check update **********/
			if(UpdateConfigInfo.isReady && !UpdateConfigInfo.isChecked)
			{
				UpdateConfigInfo.isChecked = true;
				if(configManager.checkUpdate())
				{
					//ask for update
					Toast toast = Toast.makeText(context, "Update is available this iSleep. Update the app in Google Play Store to get better experience!", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.show();
				}
			}
					
			
			homeScreen.PaintScreen(cvs);
			
		}
		
		private void displaySettingScreen(Canvas cvs)
		{
	
			settingScreen.paintScreen(cvs);
			
		}
		
		private void displayPSQIScreen(Canvas cvs)
		{
	
			enableTextView();
			disableEditText();
			
			psqiScreen.paintScreen(cvs);			
		}
		
		private void displayQuestionScreen(Canvas cvs)
		{
	
			enableTextView();
			disableEditText();
			
			//textView.setTextSize(AppUI.SIZE_QUESTION_TEXTVIEW);
			
			questionScreen.paintScreen(cvs);
			
			
		}
		
		
		/**
		 * change the frame index in MenuButton 
		 * and refresh the screen
		 */
		private void displayMenu(Canvas cvs, boolean isExpand)
		{

			if(isExpand)
			{
				if(menuAnimationIndex<menuAnimationFrames-1)
				{
					menuBtn.paintMenu(cvs, menuAnimationIndex);
					menuAnimationIndex++;
					
					//refresh screen
					uiHandler.postDelayed(new UpdateRunnable(), menuDurPerFrame);
				}
				else
				{
					menuBtn.paintMenu(cvs, menuAnimationIndex);
				}
			}
			else
			{
				if(menuAnimationIndex>0)
				{
					menuBtn.paintMenu(cvs, menuAnimationIndex);
					menuAnimationIndex--;
					
					//refresh screen
					uiHandler.postDelayed(new UpdateRunnable(), menuDurPerFrame);
				}
				else
				{
					menuBtn.paintMenu(cvs, menuAnimationIndex);
				}
			}
			
		}
		
		
		private void displayResultScreen(Canvas cvs)
		{
			endTimer();
			
			/*if(!resultScreen.showToast)
			{
				resultScreen.showToast = true;
				Toast toast = Toast.makeText(context, "Touch the bar area to reveal detail.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
			}*/
			
			
			if(AppUI.endTime==null)
				resultScreen.setEndTime();
			
			resultScreen.paintScreen(cvs);
			
			//the menu flag is changed in HandleTouch()
			//displayMenu(cvs, resultScreen.isMenuExpanded);
			
		}
		

		
		
		private void displayEventResultScreen(Canvas cvs)
		{
			if(!eventScreen.showToast)
			{
				eventScreen.showToast = true;
				Toast toast = Toast.makeText(context, "Touch the bar area to reveal detail.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
			}

			
			eventScreen.paintScreen(cvs);
			
			//the menu flag is changed in HandleTouch()
			//displayMenu(cvs, eventScreen.isMenuExpanded);		
						
		}
		
		
		private void displayHistoryScreen(Canvas cvs)
		{
			if(!historyScreen.showToast)
			{
				historyScreen.showToast = true;
				Toast toast = Toast.makeText(context, "Touch the bar area to reveal detail.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
			}

			historyScreen.paintScreen(cvs);
			
			//the menu flag is changed in HandleTouch()
			//displayMenu(cvs, historyScreen.isMenuExpanded);
		}
		
		
		private void displayQualityHistoryScreen(Canvas cvs, DisplayStatus ds, DisplayUpdateMsg updateMsg)
		{
			cvs.drawColor(AppUI.COLOR_SCREEN_BACKGROUND);
			
			Paint.FontMetricsInt btnFont = AppUI.FONT_MID;
			float btnTxtSize = AppUI.SIZE_MID_TEXT;
			
			//------------------draw button background------------------//
			int btnHeight = btnFont.bottom - btnFont.top;
			int btnSpc = AppUI.FONT_NORMAL.descent;			
			int btnAreaHeight = btnHeight*2 + btnSpc*1;
			int eventBtnWidth = (screenWidth-3*btnSpc)/2;
			
			RectF rectQualityBtn = new RectF();
			rectQualityBtn.left = btnSpc;
			rectQualityBtn.right = rectQualityBtn.left + eventBtnWidth;
			rectQualityBtn.top = screenHeight - btnSpc - 2*btnHeight;
			rectQualityBtn.bottom = rectQualityBtn.top + btnHeight;
			
			RectF rectEventBtn = new RectF();
			rectEventBtn.top = rectQualityBtn.top;
			rectEventBtn.bottom = rectQualityBtn.bottom;
			rectEventBtn.left = rectQualityBtn.right + btnSpc;
			rectEventBtn.right = rectEventBtn.left + eventBtnWidth;
			
			RectF rectHomeBtn = new RectF();
			rectHomeBtn.left = 0;
			rectHomeBtn.right = screenWidth;
			rectHomeBtn.top = screenHeight - btnHeight;
			rectHomeBtn.bottom = screenHeight;
			
			Paint paintBtn = new Paint();
			float r = btnSpc;
			paintBtn.setColor(AppUI.COLOR_HISTORY_BTN);
			cvs.drawRoundRect(rectQualityBtn, r, r, paintBtn);
			cvs.drawRoundRect(rectEventBtn, r, r, paintBtn);
			
			paintBtn.setColor(AppUI.COLOR_EVENTRESULT_BTN_SELECT);
			switch(ds)
			{
			case SCREEN_HISTORY_QUALITY:
				cvs.drawRoundRect(rectQualityBtn, r, r, paintBtn);
				break;
			case SCREEN_HISTORY_EVENT:
				cvs.drawRoundRect(rectEventBtn, r, r, paintBtn);
				break;
			}
				
			paintBtn.setColor(AppUI.COLOR_HISTORY_HOMEBTN);
			cvs.drawRoundRect(rectHomeBtn, 0, 0, paintBtn);
			
			
			//-----------------draw Btn text---------------------//
			paintBtn.setColor(AppUI.COLOR_HISTORY_BTN_TXT);
			paintBtn.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			paintBtn.setTextSize(btnTxtSize);
		    float wQuality = paintBtn.measureText(AppUI.TXT_HISTORY_SLEEPQUALITY);
			float xQuality = (eventBtnWidth - wQuality)/2 + rectQualityBtn.left;
			float yQuality = rectQualityBtn.bottom - btnFont.bottom;			
			cvs.drawText(AppUI.TXT_HISTORY_SLEEPQUALITY, xQuality, yQuality, paintBtn);
			
			float wEvent = paintBtn.measureText(AppUI.TXT_HISTORY_SLEEPEVENT);
			float xEvent = (eventBtnWidth - wEvent)/2 + rectEventBtn.left;
			float yEvent = rectEventBtn.bottom - btnFont.bottom;			
			cvs.drawText(AppUI.TXT_HISTORY_SLEEPEVENT, xEvent, yEvent, paintBtn);

			
			paintBtn.setColor(AppUI.COLOR_EVENTRESULT_BAKBTN_TXT);
			float wHome = paintBtn.measureText(AppUI.TXT_HISTORY_HOMEBTN);
			float xHome = (screenWidth - wHome)/2 + rectHomeBtn.left;
			float yHome = rectHomeBtn.bottom - btnFont.bottom;			
			cvs.drawText(AppUI.TXT_HISTORY_HOMEBTN, xHome, yHome, paintBtn);
			
			
			//draw efficiency text on top
			int normalSpc = AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent;
			int yBaseEffInfo = normalSpc + Math.abs(AppUI.FONT_MID.top);
			Paint txtPaint = new Paint();
			
			txtPaint.setColor(AppUI.COLOR_HISTORY_QUALITY_LINE);
			txtPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			txtPaint.setTextSize(AppUI.SIZE_MID_TEXT);
		    float wLine1 = txtPaint.measureText(AppUI.TXT_HISTORY_QUALITY_LINE1);
			float xLine1 = (screenWidth - wLine1)/2;
			float yLine1 = yBaseEffInfo;			
			cvs.drawText(AppUI.TXT_HISTORY_QUALITY_LINE1, xLine1, yLine1, txtPaint);
			
			float wLine2 = txtPaint.measureText(AppUI.TXT_HISTORY_QUALITY_LINE2);
			float xLine2 = (screenWidth - wLine2)/2;
			float yLine2 = yLine1 + (AppUI.FONT_MID.descent - AppUI.FONT_MID.ascent);			
			cvs.drawText(AppUI.TXT_HISTORY_QUALITY_LINE2, xLine2, yLine2, txtPaint);
			
			int yBaseEffNum = (int)yLine2 + AppUI.FONT_MID.descent + normalSpc 
								- AppUI.FONT_LARGE.ascent; 
			txtPaint.setColor(AppUI.COLOR_HISTORY_QUALITY_EFFICIENCY);
			txtPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
			txtPaint.setTextSize(AppUI.SIZE_LARGE_TEXT);
			String strEff = "0/100";
		    float wEffNum = txtPaint.measureText(strEff);
			float xEffNum = (screenWidth - wEffNum)/2;
			float yEffNum = yBaseEffNum;			
			cvs.drawText(strEff, xEffNum, yEffNum, txtPaint);
			
			
			//draw bar graph info text
			int yBaseBarInfo = (int)yEffNum + AppUI.FONT_LARGE.descent + 2*normalSpc
								- AppUI.FONT_NORMAL.ascent;			
			txtPaint.setColor(AppUI.COLOR_HISTORY_QUALITY_BARINFO);
			txtPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			txtPaint.setTextSize(AppUI.SIZE_NORMAL_TEXT);
		    float wInfoLine1 = txtPaint.measureText(AppUI.TXT_HISTORY_QUALITY_INFO_LINE1);
			float xInfoLine1 = (screenWidth - wInfoLine1)/2;
			float yInfoLine1 = yBaseBarInfo;			
			cvs.drawText(AppUI.TXT_HISTORY_QUALITY_INFO_LINE1, xInfoLine1, yInfoLine1, txtPaint);
			
			float wInfoLine2 = txtPaint.measureText(AppUI.TXT_HISTORY_QUALITY_INFO_LINE2);
			float xInfoLine2 = (screenWidth - wInfoLine2)/2;
			float yInfoLine2 = yInfoLine1 + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);		
			cvs.drawText(AppUI.TXT_HISTORY_QUALITY_INFO_LINE2, xInfoLine2, yInfoLine2, txtPaint);
			
			
			//draw time-select buttons
			float btnBtm = rectQualityBtn.top - 2*normalSpc;
			float sideMargin = normalSpc;
			float timeSelectBtnWidth = (screenWidth - 2*sideMargin - 2*btnSpc)/3;
			
			RectF rWeekBtn = new RectF();
			rWeekBtn.left = sideMargin;
			rWeekBtn.right = rWeekBtn.left + timeSelectBtnWidth;
			rWeekBtn.top = btnBtm - btnHeight;
			rWeekBtn.bottom = btnBtm;
			
			RectF rMonthBtn = new RectF();
			rMonthBtn.left = rWeekBtn.right+btnSpc;
			rMonthBtn.right = rMonthBtn.left + timeSelectBtnWidth;
			rMonthBtn.top = btnBtm - btnHeight;
			rMonthBtn.bottom = btnBtm;
			
			RectF rYearBtn = new RectF();
			rYearBtn.left = rMonthBtn.right+btnSpc;
			rYearBtn.right = rYearBtn.left + timeSelectBtnWidth;
			rYearBtn.top = btnBtm - btnHeight;
			rYearBtn.bottom = btnBtm;
			
			paintBtn.setColor(AppUI.COLOR_HISTORY_BTN);
			cvs.drawRoundRect(rWeekBtn, 0, 0, paintBtn);
			cvs.drawRoundRect(rMonthBtn, 0, 0, paintBtn);
			cvs.drawRoundRect(rYearBtn, 0, 0, paintBtn);
			
			RectF barArea = new RectF();
			barArea.bottom = rWeekBtn.top - btnSpc;
			barArea.top = yInfoLine2 + normalSpc;
			barArea.left = sideMargin;
			barArea.right = screenWidth - sideMargin;	
			//cvs.drawRect(barArea, paintBtn);
			
			paintBtn.setColor(AppUI.COLOR_EVENTRESULT_BTN_SELECT);
			switch(updateMsg.update)
			{
			case UPDATE_HISTORY_QUALITY_WEEK:
				cvs.drawRoundRect(rWeekBtn, 0, 0, paintBtn);
				drawQualityBars(barArea, 7, cvs, AppUI.COLOR_HISTORY_QUALITY_BAR);
				break;
			case UPDATE_HISTORY_QUALITY_MONTH:
				cvs.drawRoundRect(rMonthBtn, 0, 0, paintBtn);
				drawQualityBars(barArea, 30, cvs, AppUI.COLOR_HISTORY_QUALITY_BAR);
				break;
			case UPDATE_HISTORY_QUALITY_YEAR:
				cvs.drawRoundRect(rYearBtn, 0, 0, paintBtn);
				drawQualityBars(barArea, 52, cvs, AppUI.COLOR_HISTORY_QUALITY_BAR);
				break;
			}

			
			//-----------------draw time-select Btn text---------------------//
			paintBtn.setColor(AppUI.COLOR_HISTORY_BTN_TXT);
			paintBtn.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			paintBtn.setTextSize(btnTxtSize);
			String sWeekBtn = "Week";
		    float wWeekBtn = paintBtn.measureText(sWeekBtn);
			float xWeekBtn = (timeSelectBtnWidth - wWeekBtn)/2 + rWeekBtn.left;
			float yWeekBtn = rWeekBtn.bottom - btnFont.bottom;			
			cvs.drawText(sWeekBtn, xWeekBtn, yWeekBtn, paintBtn);
			
			String sMonthBtn = "Month";
			float wMonthBtn = paintBtn.measureText(sMonthBtn);
			float xMonthBtn = (timeSelectBtnWidth - wMonthBtn)/2 + rMonthBtn.left;
			float yMonthBtn = rMonthBtn.bottom - btnFont.bottom;			
			cvs.drawText(sMonthBtn, xMonthBtn, yMonthBtn, paintBtn);
			
			String sYearBtn = "Year";
			float wYearBtn = paintBtn.measureText(sYearBtn);
			float xYearBtn = (timeSelectBtnWidth - wYearBtn)/2 + rYearBtn.left;
			float yYearBtn = rYearBtn.bottom - btnFont.bottom;			
			cvs.drawText(sYearBtn, xYearBtn, yYearBtn, paintBtn);
			
			//initialize button list for this screen
			if(historyQualityScreenButtons==null)
			{
				historyQualityScreenButtons = new LinkedList<MyButton>();
				
				MyButton btn1 = new MyButton(rectQualityBtn, ButtonID.HISTORY_QUALITY);
				historyQualityScreenButtons.add(btn1);
				
				MyButton btn2 = new MyButton(rectEventBtn, ButtonID.HISTORY_EVENT);
				historyQualityScreenButtons.add(btn2);
				
				MyButton btn5 = new MyButton(rectHomeBtn, ButtonID.HISTORY_HOME);
				historyQualityScreenButtons.add(btn5);
				
				MyButton btn3 = new MyButton(rWeekBtn, ButtonID.HISTORY_QUALITYWEEK);
				historyQualityScreenButtons.add(btn3);
				
				MyButton btn4 = new MyButton(rMonthBtn, ButtonID.HISTORY_QUALITYMONTH);
				historyQualityScreenButtons.add(btn4);
				
				MyButton btn6 = new MyButton(rYearBtn, ButtonID.HISTORY_QUALITYYEAR);
				historyQualityScreenButtons.add(btn6);
			}//EOF initialize button list
			
			if(touchableQualityHistory == null)
				touchableQualityHistory = barArea;
				
			//display detailed info of acticigraphy bars
			if(displayUpdateMsg.update1 == DisplayStatus.UPDATE_HISTORY_QUALITY_MOVE)
			{
				int x = displayUpdateMsg.x;
				int y = displayUpdateMsg.y;
				
				//measure detail text
				Paint paint = new Paint();
				paint.setColor(AppUI.COLOR_HISTORY_DETAIL_TXT);
				String strStatus = "Sleeping";
				String strTime = "00:00";
				String strPercent = "-100%";
				
				paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
				paint.setTextSize(AppUI.SIZE_NORMAL_TEXT);
				
				int txtHeight = AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent;
				int txtWidth = (int)paint.measureText(strStatus);
				int areaHeight = 3*txtHeight;
				
				Rect rBarDetail = new Rect();
				rBarDetail.left = x - txtWidth;
				if(rBarDetail.left<=0)
					rBarDetail.left = 0;
				rBarDetail.right = rBarDetail.left + txtWidth;
				rBarDetail.bottom = (int) barArea.top;
				rBarDetail.top = rBarDetail.bottom - areaHeight;
				
				Paint actiPaint = new Paint();
				actiPaint.setColor(AppUI.COLOR_HISTORYBAR_DETAIL_BKGRD);
				cvs.drawRect(rBarDetail, actiPaint);
				
				actiPaint.setStrokeWidth(3f);
				actiPaint.setStyle(Style.STROKE);
				actiPaint.setPathEffect(new DashPathEffect(new float[] {10,5}, 0));
				cvs.drawLine(x, barArea.top, x, barArea.bottom, actiPaint);
				
				float xLine = rBarDetail.left + (txtWidth-paint.measureText(strTime))/2;
				float yLine = rBarDetail.top - AppUI.FONT_NORMAL.ascent;
				cvs.drawText(strTime, xLine, yLine, paint);
				xLine = rBarDetail.left + (txtWidth-paint.measureText(strStatus))/2;
				yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
				cvs.drawText(strStatus, xLine, yLine, paint);
				xLine = rBarDetail.left + (txtWidth-paint.measureText(strPercent))/2;
				yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
				cvs.drawText(strPercent, xLine, yLine, paint);								
			}
						
		}
		
		
		private void displayEventHistoryScreen(Canvas cvs, DisplayStatus ds, DisplayUpdateMsg updateMsg)
		{
			cvs.drawColor(AppUI.COLOR_SCREEN_BACKGROUND);
			
			Paint.FontMetricsInt btnFont = AppUI.FONT_MID;
			float btnTxtSize = AppUI.SIZE_MID_TEXT;
			
			//------------------draw button background------------------//
			int btnHeight = btnFont.bottom - btnFont.top;
			int btnSpc = AppUI.FONT_NORMAL.descent;			
			int btnAreaHeight = btnHeight*2 + btnSpc*1;
			int eventBtnWidth = (screenWidth-3*btnSpc)/2;
			
			RectF rectQualityBtn = new RectF();
			rectQualityBtn.left = btnSpc;
			rectQualityBtn.right = rectQualityBtn.left + eventBtnWidth;
			rectQualityBtn.top = screenHeight - btnSpc - 2*btnHeight;
			rectQualityBtn.bottom = rectQualityBtn.top + btnHeight;
			
			RectF rectEventBtn = new RectF();
			rectEventBtn.top = rectQualityBtn.top;
			rectEventBtn.bottom = rectQualityBtn.bottom;
			rectEventBtn.left = rectQualityBtn.right + btnSpc;
			rectEventBtn.right = rectEventBtn.left + eventBtnWidth;
			
			RectF rectHomeBtn = new RectF();
			rectHomeBtn.left = 0;
			rectHomeBtn.right = screenWidth;
			rectHomeBtn.top = screenHeight - btnHeight;
			rectHomeBtn.bottom = screenHeight;
			
			Paint paintBtn = new Paint();
			float r = btnSpc;
			paintBtn.setColor(AppUI.COLOR_HISTORY_BTN);
			cvs.drawRoundRect(rectQualityBtn, r, r, paintBtn);
			cvs.drawRoundRect(rectEventBtn, r, r, paintBtn);
			
			paintBtn.setColor(AppUI.COLOR_EVENTRESULT_BTN_SELECT);
			switch(ds)
			{
			case SCREEN_HISTORY_QUALITY:
				cvs.drawRoundRect(rectQualityBtn, r, r, paintBtn);
				break;
			case SCREEN_HISTORY_EVENT:
				cvs.drawRoundRect(rectEventBtn, r, r, paintBtn);
				break;
			}
				
			paintBtn.setColor(AppUI.COLOR_HISTORY_HOMEBTN);
			cvs.drawRoundRect(rectHomeBtn, 0, 0, paintBtn);			
			
			//-----------------draw Btn text---------------------//
			paintBtn.setColor(AppUI.COLOR_HISTORY_BTN_TXT);
			paintBtn.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			paintBtn.setTextSize(btnTxtSize);
		    float wQuality = paintBtn.measureText(AppUI.TXT_HISTORY_SLEEPQUALITY);
			float xQuality = (eventBtnWidth - wQuality)/2 + rectQualityBtn.left;
			float yQuality = rectQualityBtn.bottom - btnFont.bottom;			
			cvs.drawText(AppUI.TXT_HISTORY_SLEEPQUALITY, xQuality, yQuality, paintBtn);
			
			float wEvent = paintBtn.measureText(AppUI.TXT_HISTORY_SLEEPEVENT);
			float xEvent = (eventBtnWidth - wEvent)/2 + rectEventBtn.left;
			float yEvent = rectEventBtn.bottom - btnFont.bottom;			
			cvs.drawText(AppUI.TXT_HISTORY_SLEEPEVENT, xEvent, yEvent, paintBtn);

			
			paintBtn.setColor(AppUI.COLOR_EVENTRESULT_BAKBTN_TXT);
			float wHome = paintBtn.measureText(AppUI.TXT_HISTORY_HOMEBTN);
			float xHome = (screenWidth - wHome)/2 + rectHomeBtn.left;
			float yHome = rectHomeBtn.bottom - btnFont.bottom;			
			cvs.drawText(AppUI.TXT_HISTORY_HOMEBTN, xHome, yHome, paintBtn);
			
			
			int normalSpc = AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent;	
			
			//draw time-select buttons
			float btnBtm = rectQualityBtn.top - normalSpc;
			float sideMargin = normalSpc;
			float timeSelectBtnWidth = (screenWidth - 2*sideMargin - 2*btnSpc)/3;
			
			RectF rWeekBtn = new RectF();
			rWeekBtn.left = sideMargin;
			rWeekBtn.right = rWeekBtn.left + timeSelectBtnWidth;
			rWeekBtn.top = btnBtm - btnHeight;
			rWeekBtn.bottom = btnBtm;
			
			RectF rMonthBtn = new RectF();
			rMonthBtn.left = rWeekBtn.right+btnSpc;
			rMonthBtn.right = rMonthBtn.left + timeSelectBtnWidth;
			rMonthBtn.top = btnBtm - btnHeight;
			rMonthBtn.bottom = btnBtm;
			
			RectF rYearBtn = new RectF();
			rYearBtn.left = rMonthBtn.right+btnSpc;
			rYearBtn.right = rYearBtn.left + timeSelectBtnWidth;
			rYearBtn.top = btnBtm - btnHeight;
			rYearBtn.bottom = btnBtm;
			
			paintBtn.setColor(AppUI.COLOR_HISTORY_BTN);
			cvs.drawRoundRect(rWeekBtn, 0, 0, paintBtn);
			cvs.drawRoundRect(rMonthBtn, 0, 0, paintBtn);
			cvs.drawRoundRect(rYearBtn, 0, 0, paintBtn);

			paintBtn.setColor(AppUI.COLOR_EVENTRESULT_BTN_SELECT);
			
			switch(updateMsg.update)
			{
			case UPDATE_HISTORY_EVENT_WEEK:
				cvs.drawRoundRect(rWeekBtn, 0, 0, paintBtn);
				break;
			case UPDATE_HISTORY_EVENT_MONTH:
				cvs.drawRoundRect(rMonthBtn, 0, 0, paintBtn);
				break;
			case UPDATE_HISTORY_EVENT_YEAR:
				cvs.drawRoundRect(rYearBtn, 0, 0, paintBtn);
				break;
			}
			
			
			//-----------------draw time-select Btn text---------------------//
			paintBtn.setColor(AppUI.COLOR_HISTORY_BTN_TXT);
			paintBtn.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
			paintBtn.setTextSize(btnTxtSize);
			String sWeekBtn = "Week";
		    float wWeekBtn = paintBtn.measureText(sWeekBtn);
			float xWeekBtn = (timeSelectBtnWidth - wWeekBtn)/2 + rWeekBtn.left;
			float yWeekBtn = rWeekBtn.bottom - btnFont.bottom;			
			cvs.drawText(sWeekBtn, xWeekBtn, yWeekBtn, paintBtn);
			
			String sMonthBtn = "Month";
			float wMonthBtn = paintBtn.measureText(sMonthBtn);
			float xMonthBtn = (timeSelectBtnWidth - wMonthBtn)/2 + rMonthBtn.left;
			float yMonthBtn = rMonthBtn.bottom - btnFont.bottom;			
			cvs.drawText(sMonthBtn, xMonthBtn, yMonthBtn, paintBtn);
			
			String sYearBtn = "Year";
			float wYearBtn = paintBtn.measureText(sYearBtn);
			float xYearBtn = (timeSelectBtnWidth - wYearBtn)/2 + rYearBtn.left;
			float yYearBtn = rYearBtn.bottom - btnFont.bottom;			
			cvs.drawText(sYearBtn, xYearBtn, yYearBtn, paintBtn);
			
			//initialize button list for this screen
			if(historyEventScreenButtons==null)
			{
				historyEventScreenButtons = new LinkedList<MyButton>();
				
				MyButton btn1 = new MyButton(rectQualityBtn, ButtonID.HISTORY_QUALITY);
				historyEventScreenButtons.add(btn1);
				
				MyButton btn2 = new MyButton(rectEventBtn, ButtonID.HISTORY_EVENT);
				historyEventScreenButtons.add(btn2);
				
				MyButton btn5 = new MyButton(rectHomeBtn, ButtonID.HISTORY_HOME);
				historyEventScreenButtons.add(btn5);
				
				MyButton btn3 = new MyButton(rWeekBtn, ButtonID.HISTORY_EVENTWEEK);
				historyEventScreenButtons.add(btn3);
				
				MyButton btn4 = new MyButton(rMonthBtn, ButtonID.HISTORY_EVENTMONTH);
				historyEventScreenButtons.add(btn4);
				
				MyButton btn6 = new MyButton(rYearBtn, ButtonID.HISTORY_EVENTYEAR);
				historyEventScreenButtons.add(btn6);
			}//EOF initialize button list
			
			
			//draw bar areas for 4 kinds of events
			int spcBtwArea = normalSpc;
			int areaHeight = (int) ((rWeekBtn.top - 5*spcBtwArea)/4);
			int areaSideMargin = 0;
			
			RectF areaMove = new RectF();
			areaMove.left = areaSideMargin;
			areaMove.right = screenWidth-areaSideMargin;
			areaMove.top = spcBtwArea;
			areaMove.bottom = areaMove.top + areaHeight;
			
			RectF areaSnore = new RectF();
			areaSnore.left = areaSideMargin;
			areaSnore.right = screenWidth-areaSideMargin;
			areaSnore.top = areaMove.bottom + spcBtwArea;
			areaSnore.bottom = areaSnore.top + areaHeight;
			
			RectF areaCough = new RectF();
			areaCough.left = areaSideMargin;
			areaCough.right = screenWidth-areaSideMargin;
			areaCough.top = areaSnore.bottom + spcBtwArea;
			areaCough.bottom = areaCough.top + areaHeight;
						
			RectF areaGetup = new RectF();
			areaGetup.left = areaSideMargin;
			areaGetup.right = screenWidth-areaSideMargin;
			areaGetup.top = areaCough.bottom + spcBtwArea;
			areaGetup.bottom = areaGetup.top + areaHeight;
			
			switch(updateMsg.update)
			{
			case UPDATE_HISTORY_EVENT_WEEK:
				drawQualityBars(areaMove, 7, cvs, AppUI.COLOR_HISTORY_EVENT_MOVE);
				drawQualityBars(areaSnore, 7, cvs, AppUI.COLOR_HISTORY_EVENT_SNORE);
				drawQualityBars(areaCough, 7, cvs, AppUI.COLOR_HISTORY_EVENT_COUGH);
				drawQualityBars(areaGetup, 7, cvs, AppUI.COLOR_HISTORY_EVENT_GETUP);
				break;
			case UPDATE_HISTORY_EVENT_MONTH:
				drawQualityBars(areaMove, 31, cvs, AppUI.COLOR_HISTORY_EVENT_MOVE);
				drawQualityBars(areaSnore, 31, cvs, AppUI.COLOR_HISTORY_EVENT_SNORE);
				drawQualityBars(areaCough, 31, cvs, AppUI.COLOR_HISTORY_EVENT_COUGH);
				drawQualityBars(areaGetup, 31, cvs, AppUI.COLOR_HISTORY_EVENT_GETUP);
				break;
			case UPDATE_HISTORY_EVENT_YEAR:
				drawQualityBars(areaMove, 52, cvs, AppUI.COLOR_HISTORY_EVENT_MOVE);
				drawQualityBars(areaSnore, 52, cvs, AppUI.COLOR_HISTORY_EVENT_SNORE);
				drawQualityBars(areaCough, 52, cvs, AppUI.COLOR_HISTORY_EVENT_COUGH);
				drawQualityBars(areaGetup, 52, cvs, AppUI.COLOR_HISTORY_EVENT_GETUP);
				break;
			}
			
			//initialize touchable area for move_action detection
			if(touchableEventHistory == null)
				touchableEventHistory = new Rect(areaSideMargin, spcBtwArea, 
											screenWidth-areaSideMargin, 4*spcBtwArea+4*areaHeight);	
			
			if(displayUpdateMsg.update1 == DisplayStatus.UPDATE_HISTORY_EVENT_MOVE)
			{
				int x = displayUpdateMsg.x;
				int y = displayUpdateMsg.y;
				
				//measure detail text
				Paint labelPaint = new Paint();
				labelPaint.setColor(AppUI.COLOR_HISTORY_DETAIL_TXT);
				String strStatus = "Sleeping";
				String strTime = "00:00";
				String strStrength = "-100%";
				
				labelPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
				labelPaint.setTextSize(AppUI.SIZE_NORMAL_TEXT);
				
				int txtHeight = AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent;
				int txtWidth = (int)labelPaint.measureText(strStatus);
				int rectHeight = 3*txtHeight;
				
				Rect rMoveDetail = new Rect();
				rMoveDetail.left = x - txtWidth;
				if(rMoveDetail.left<=0)
					rMoveDetail.left = 0;
				rMoveDetail.right = rMoveDetail.left + txtWidth;				
				rMoveDetail.top = (int) areaMove.top - spcBtwArea;				
				rMoveDetail.bottom = rMoveDetail.top + rectHeight;
				
				Rect rSnoreDetail = new Rect();
				rSnoreDetail.left = x - txtWidth;
				if(rSnoreDetail.left<=0)
					rSnoreDetail.left = 0;
				rSnoreDetail.right = rSnoreDetail.left + txtWidth;				
				rSnoreDetail.top = (int) areaSnore.top - spcBtwArea;				
				rSnoreDetail.bottom = rSnoreDetail.top + rectHeight;
				
				Rect rCoughDetail = new Rect();
				rCoughDetail.left = x - txtWidth;
				if(rCoughDetail.left<=0)
					rCoughDetail.left = 0;
				rCoughDetail.right = rCoughDetail.left + txtWidth;				
				rCoughDetail.top = (int) areaCough.top - spcBtwArea;				
				rCoughDetail.bottom = rCoughDetail.top + rectHeight;
				
				Rect rGetupDetail = new Rect();
				rGetupDetail.left = x - txtWidth;
				if(rGetupDetail.left<=0)
					rGetupDetail.left = 0;
				rGetupDetail.right = rGetupDetail.left + txtWidth;				
				rGetupDetail.top = (int) areaGetup.top - spcBtwArea;				
				rGetupDetail.bottom = rGetupDetail.top + rectHeight;
				
				Paint ptEventDetail = new Paint();
				ptEventDetail.setColor(AppUI.COLOR_HISTORYBAR_DETAIL_BKGRD);
				cvs.drawRect(rMoveDetail, ptEventDetail);
				cvs.drawRect(rSnoreDetail, ptEventDetail);
				cvs.drawRect(rCoughDetail, ptEventDetail);
				cvs.drawRect(rGetupDetail, ptEventDetail);
				
				ptEventDetail.setStrokeWidth(3f);
				ptEventDetail.setStyle(Style.STROKE);
				ptEventDetail.setPathEffect(new DashPathEffect(new float[] {10,5}, 0));
				cvs.drawLine(x, 0, x, touchableEventHistory.bottom, ptEventDetail);
				
				float xLine, yLine;
				
				xLine = rMoveDetail.left + (txtWidth-labelPaint.measureText(strTime))/2;
				yLine = rMoveDetail.top - AppUI.FONT_NORMAL.ascent;
				cvs.drawText(strTime, xLine, yLine, labelPaint);				
				xLine = rMoveDetail.left + (txtWidth-labelPaint.measureText(strStatus))/2;
				yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
				cvs.drawText(strStatus, xLine, yLine, labelPaint);				
				xLine = rMoveDetail.left + (txtWidth-labelPaint.measureText(strStrength))/2;
				yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
				cvs.drawText(strStrength, xLine, yLine, labelPaint);		
				
				xLine = rSnoreDetail.left + (txtWidth-labelPaint.measureText(strTime))/2;
				yLine = rSnoreDetail.top - AppUI.FONT_NORMAL.ascent;
				cvs.drawText(strTime, xLine, yLine, labelPaint);				
				xLine = rSnoreDetail.left + (txtWidth-labelPaint.measureText(strStatus))/2;
				yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
				cvs.drawText(strStatus, xLine, yLine, labelPaint);				
				xLine = rSnoreDetail.left + (txtWidth-labelPaint.measureText(strStrength))/2;
				yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
				cvs.drawText(strStrength, xLine, yLine, labelPaint);		
				
				xLine = rCoughDetail.left + (txtWidth-labelPaint.measureText(strTime))/2;
				yLine = rCoughDetail.top - AppUI.FONT_NORMAL.ascent;
				cvs.drawText(strTime, xLine, yLine, labelPaint);				
				xLine = rCoughDetail.left + (txtWidth-labelPaint.measureText(strStatus))/2;
				yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
				cvs.drawText(strStatus, xLine, yLine, labelPaint);				
				xLine = rCoughDetail.left + (txtWidth-labelPaint.measureText(strStrength))/2;
				yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
				cvs.drawText(strStrength, xLine, yLine, labelPaint);		
				
				xLine = rGetupDetail.left + (txtWidth-labelPaint.measureText(strTime))/2;
				yLine = rGetupDetail.top - AppUI.FONT_NORMAL.ascent;
				cvs.drawText(strTime, xLine, yLine, labelPaint);				
				xLine = rGetupDetail.left + (txtWidth-labelPaint.measureText(strStatus))/2;
				yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
				cvs.drawText(strStatus, xLine, yLine, labelPaint);				
				xLine = rGetupDetail.left + (txtWidth-labelPaint.measureText(strStrength))/2;
				yLine = yLine + (AppUI.FONT_NORMAL.descent - AppUI.FONT_NORMAL.ascent);
				cvs.drawText(strStrength, xLine, yLine, labelPaint);		
			}//EOF draw bar details
			
		}
		
		
		private void drawQualityBars(RectF area, int num_bars, Canvas cvs, int barColor)
		{
			
			float[] barVals = new float[num_bars];
			
			float margin = 3;
			
			if(num_bars==52)
				margin = 0;
			
			float barWidth = (area.width()-(num_bars-1)*margin)/num_bars;
			
			Paint paint  = new Paint();
			paint.setColor(barColor);
			
			RectF rBar = new RectF();
			
			for(int i = 0; i<num_bars; i++)
			{
				barVals[i] = (float)Math.random();
				
				rBar.left = area.left + i*(barWidth+margin);
				rBar.right = rBar.left + barWidth;
				rBar.bottom = area.bottom;
				rBar.top = area.bottom - area.height()*barVals[i];
				
				cvs.drawRect(rBar, paint);
			}
		}

		
		
		private void displayAboutScreen(Canvas cvs)
		{
			
			
			
			if(aboutScreen.isShowEditText)
			{
				disableTextView();
				enableEditText();
			}
			else
			{
				enableTextView();
				disableEditText();
			}
			
			aboutScreen.paintScreen(cvs);
			
		}
		
		
		private void displayConsentScreen(Canvas cvs)
		{			
			enableTextView();
			disableEditText();
			
			consentScreen.paintScreen(cvs);			
		}
		
		
		
		private void displayMonitorScreen(Canvas cvs)
		{
			if(AppUI.startTime==null)
				monitorScreen.setStartTime();
			
			//ask for update
			if(!monitorScreen.showToast)
			{
				monitorScreen.showToast = true;
				Toast toast = Toast.makeText(context, "iSleep is monitoring your sleep quality.\n\nYou can turn off the screen by press the POWER KEY.\n\nPlease use the BACK KEY to exit app.", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
			}
		
			
			monitorScreen.paintScreen(cvs);
			
			if(uiTimer==null)
			{
				startTimer();
			}
		}
		
		
		private void getRealButtonBound(Rect bound, int x, int y, int horizontalMargin, int verticalMargin)
		{
			bound.left = (int) (bound.left + x - horizontalMargin);			
			bound.right = (int) (bound.right + x + horizontalMargin);
			bound.top = (int) (bound.top + y -verticalMargin);
			bound.bottom = (int) (bound.bottom + y + verticalMargin);
		}
		
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) 
		{
			//_thread.setRunning(false);
		}
		
		
	
	}//EOF SurView
	
	
	
	
	
	class UpdateRunnable implements Runnable
	{
		@Override
        public void run() 
		{
			invalidateScreen();
        }
	}
	
	
	class UIHandler extends Handler 
    {	
        @Override
        public void handleMessage(Message msg) 
        {		
        	if(msg==null)
        	{
        		super.handleMessage(msg);
        		return;
        	}
        	
        	Object obj = msg.obj;
        	if(obj!=null)
        	{
        		//DisplayUpdateMsg dum = (DisplayUpdateMsg)obj;
        		
        		displayUpdateMsg = (DisplayUpdateMsg)obj;
        		
        		invalidateScreen();
        	}
        	
            super.handleMessage(msg);
        }
    }//EOF UIHandler
	
	
	@SuppressLint("WrongCall") private void invalidateScreen()
	{
		//Log.e("Display Update Msg", displayUpdateMsg.toString());
		//------------------update screen-----------------------
        Canvas c = null;
        try 
        {
            c = mSH.lockCanvas(null);
            synchronized (mSH) {
                mSV.onDraw(c);
            }
        } 
        finally 
        {
            // do this in a finally so that if an exception is thrown
            // during the above, we don't leave the Surface in an
            // inconsistent state
            if (c != null) 
            {
                mSH.unlockCanvasAndPost(c);
            }
        }
	}
	
	
	private void initializeFontInfo()
	{
		//set font size according to screen size
		Paint paint = new Paint();
		paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));

		int txtPixel = scale.i2p(AppUI.SMALL_TEXT_INCH);	
		//int txtPixel = 12;
		paint.setTextSize(txtPixel);			
		Paint.FontMetricsInt fontMetric = paint.getFontMetricsInt();			
		AppUI.SIZE_SMALL_TEXT = fontMetric.descent-fontMetric.ascent;
		//AppUI.SIZE_SMALL_TEXT = txtPixel;
		AppUI.FONT_SMALL = fontMetric;
		
		txtPixel = scale.i2p(AppUI.NORMAL_TEXT_INCH);	
		//txtPixel = 14;
		paint.setTextSize(txtPixel);			
		fontMetric = paint.getFontMetricsInt();			
		AppUI.SIZE_NORMAL_TEXT = fontMetric.descent-fontMetric.ascent;
		//AppUI.SIZE_NORMAL_TEXT = txtPixel;
		AppUI.FONT_NORMAL = fontMetric;
		
		txtPixel = scale.i2p(AppUI.MID_TEXT_INCH);		
		//txtPixel = 18;
		paint.setTextSize(txtPixel);			
		fontMetric = paint.getFontMetricsInt();			
		AppUI.SIZE_MID_TEXT = fontMetric.descent-fontMetric.ascent;
		//AppUI.SIZE_MID_TEXT = txtPixel;
		AppUI.FONT_MID = fontMetric;
		
		txtPixel = scale.i2p(AppUI.LARGE_TEXT_INCH);	
		//txtPixel = 22;
		paint.setTextSize(txtPixel);			
		fontMetric = paint.getFontMetricsInt();			
		AppUI.SIZE_LARGE_TEXT = fontMetric.descent-fontMetric.ascent;
		//AppUI.SIZE_LARGE_TEXT = txtPixel;
		AppUI.FONT_LARGE = fontMetric;
		
		txtPixel = scale.i2p(AppUI.LOGO_TEXT_INCH);	
		//txtPixel = 40;
		paint.setTextSize(txtPixel);			
		fontMetric = paint.getFontMetricsInt();			
		AppUI.SIZE_LOGO_TEXT = fontMetric.descent-fontMetric.ascent;
		//AppUI.SIZE_LOGO_TEXT = txtPixel;
		AppUI.FONT_LOGO = fontMetric;
		
	}
	
	private void initializeMenu()
	{
		/********* calculate size ************/
		int hBtn = (int) (2*AppUI.SIZE_MID_TEXT);
		int wBtn = 3*hBtn;
		int menuBtnSize = (int) AppUI.SIZE_LARGE_TEXT;
		
		int spcMB = (int) AppUI.SIZE_SMALL_TEXT;
		int spcB = spcMB/2;
		
		int menuHeight = menuBtnSize + spcMB + (totalNumBtns-1)*spcB + totalNumBtns*hBtn;
		
		Rect r = new Rect();
		r.left = (int) (scale.wPix-wBtn);
		r.right = (int) scale.wPix;
		r.top = 0;
		r.bottom = r.top + menuHeight;
		
		MenuLayout ml = new MenuLayout();
		ml.area = r;
		ml.hBtn = hBtn;
		ml.wBtn = wBtn;
		ml.sizeMenuBtn = menuBtnSize;
		ml.vSpcBtn = spcB;
		ml.vSpcMB = spcMB;
		
		menuBtn = new MenuButton(context, ml);
		BitmapDrawable bdBtn, bdBtnHit;
		
		bdBtn = (BitmapDrawable)context.getResources().getDrawable(R.drawable.home_btn);
		bdBtnHit = bdBtn;
		menuBtn.addButton(ButtonID.MENU_HOME, bdBtn, bdBtnHit);
		
		bdBtn = (BitmapDrawable)context.getResources().getDrawable(R.drawable.history_btn);
		bdBtnHit = bdBtn;
		menuBtn.addButton(ButtonID.MENU_HISTORY, bdBtn, bdBtnHit);
		
		bdBtn = (BitmapDrawable)context.getResources().getDrawable(R.drawable.setting_btn);
		bdBtnHit = bdBtn;
		menuBtn.addButton(ButtonID.MENU_SETTING, bdBtn, bdBtnHit);
		
		bdBtn = (BitmapDrawable)context.getResources().getDrawable(R.drawable.about_btn);
		bdBtnHit = bdBtn;
		menuBtn.addButton(ButtonID.MENU_ABOUT, bdBtn, bdBtnHit);
		
		menuBtn.initializeAnimation();
		
	}
	
	private void initializeHomeScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);

		homeScreen = new HomeScreen(rect, context, bdWelcomePic, bdBkgrd);
		homeScreen.initialize();
	}
	
	private void initializeIntroScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);
		introScreen = new IntroScreen(rect, context, bdWelcomePic);
		introScreen.initialize();
	}
	
	private void initializeMonitorScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);
		monitorScreen = new MonitorScreen(rect, context, bdBkgrd);
		monitorScreen.initialize();
	}
	
	private void initializeResultScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);
		resultScreen = new ResultScreen(rect, context, bdBkgrd);
		resultScreen.initialize();
	}
	
	private void initializeQuestionScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);
		questionScreen = new QuestionScreen(rect, context,textView);
		questionScreen.initialize();
	}
	
	private void initializePSQIScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);
		psqiScreen = new PSQIScreen(rect, context, textView);
		psqiScreen.initialize();
	}
	
	
	private void initializeConsentScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);
		consentScreen = new ConsentScreen(rect, context, textView);
		consentScreen.initialize();
	}
	
	
	private void initializeEventResultScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);
		eventScreen = new EventScreen(rect, context, bdBkgrd);
		eventScreen.initialize();
	}
	
	private void initializeSettingScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);
		settingScreen = new SettingScreen(rect, context);
		settingScreen.initialize();
		settingScreen.isSwitchOn = configManager.isAgreeConsent;
	}
	
	
	private void initializeAboutScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);
		aboutScreen = new AboutScreen(rect, context, textView, editText, bdBkgrd);
		aboutScreen.initialize();
	}
	
	private void initializeHistoryScreen()
	{
		Rect rect = new Rect(0,0,(int)scale.wPix, (int)scale.hPix);
		historyScreen = new HistoryScreen(rect, context, bdBkgrd);
		historyScreen.initialize();
	}
	
	
	private void initializeScreenInfo()
	{
		//initialize dm and ss
		
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		scale = new ScreenScale(dm);
				
	}
	
	private class ScreenScale
	{
		float hPix;
		float wPix;
		
		//float xdpi;
		//float ydpi;
		
		float dpi;
		
		float xInch;
		float yInch;
		
		ScreenScale(DisplayMetrics dm)
		{
			hPix = dm.heightPixels;
			wPix = dm.widthPixels;
			
			dpi = (dm.xdpi+dm.ydpi)/2;
			
			xInch = wPix/dpi;
			yInch = hPix/dpi;
			
			Log.e("SCREEN", "Height:"+Float.toString(hPix)+"pix/"+Float.toString(yInch)+"inch");
			Log.e("SCREEN", "Width:"+Float.toString(wPix)+"pix/"+Float.toString(xInch)+"inch");
		}
		
		
		public int i2p(float inch)
		{
			if(inch<=0)
				return -1;
			
			return (int)(inch*dpi);
		}
		
		public float p2i(int pix)
		{
			if(pix<=0)
				return -1;
			
			return ((float)pix)/dpi;
		}
	}//EOF ScreenScale

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		if(monitorTouch)
		{
			switch( event.getAction() )
			{
		        case MotionEvent.ACTION_DOWN:
		        	handleTouch((int)event.getX(), (int)event.getY(), MotionEvent.ACTION_DOWN);			 
		            return true;
		        case MotionEvent.ACTION_UP:
		        	if(lastDownBtn!=null)
		        	{
		        		lastDownBtn.isSelected = false;
						refreshScreen();
			            handleTouch((int)event.getX(), (int)event.getY(), MotionEvent.ACTION_UP);
			            lastDownBtn = null;
		        	}		        			 
		            return true;
		        case MotionEvent.ACTION_MOVE:
		            handleTouch((int)event.getX(), (int)event.getY(), MotionEvent.ACTION_MOVE);			 
		            return true;
			}
		}
		
		return false;
	}
	
	private void handleTouch(int x, int y, int touchType)
	{
		
		/*******************MOVE ACTION*********************/
		if(touchType == MotionEvent.ACTION_MOVE)
		{
			if(displayStatus == DisplayStatus.SCREEN_RESULT)
			{
				if(resultScreen.touchableActBar == null)
					return;
				
				if(!resultScreen.isMenuExpanded && resultScreen.touchableActBar.contains(x, y))
				{
					resultScreen.setActFingerPoint(x,y);
					refreshScreen();
				}
				
				if(!resultScreen.isMenuExpanded && 
						resultScreen.isShowTime && 
						resultScreen.touchableEvalBar.contains(x, y))
				{
					resultScreen.setEvalScore(x,y);
					refreshScreen();
				}
				
			}
			
			else if(displayStatus == DisplayStatus.SCREEN_RESULT_EVENT)
			{
				if(eventScreen.touchableEventBar == null)
					return;
		
				
				if( !eventScreen.isMenuExpanded && eventScreen.touchableEventBar.contains(x, y))
				{
					eventScreen.setActFingerPoint(x,y);
					refreshScreen();
				}
				
			}
			
			else if(displayStatus == DisplayStatus.SCREEN_HISTORY)
			{	
				if(historyScreen.curScreen == DisplayStatus.SCREEN_HISTORY_QUALITY)
				{
					if( !historyScreen.isMenuExpanded && historyScreen.touchableQualityBar.contains(x, y))
					{
						historyScreen.xQualityBar = x;
						historyScreen.yQualityBar = y;
						refreshScreen();
					}
				}
				else if(historyScreen.curScreen == DisplayStatus.SCREEN_HISTORY_EVENT)
				{
					if( !historyScreen.isMenuExpanded && historyScreen.touchableEventBar.contains(x, y))
					{
						historyScreen.xEventBar = x;
						historyScreen.yEventBar = y;
						refreshScreen();
					}
				}
			
			}
					
			//do not run the following code for button push
			return;
		}//EOF handle move
				
		
		/******************* UP and DOWN ACTION *********************/
		LinkedList<MyButton> btnlist = null;
		MyButton btn = null;
		int index = 1;
		
		if(displayStatus==DisplayStatus.SCREEN_HOME)
		{
			if(homeScreen.btnList==null)
				return;
			
			btnlist = homeScreen.btnList;
			
			if(btnlist==null)
				return;
		}
		
		else if(displayStatus==DisplayStatus.SCREEN_SETTING)
		{
			//disable setting screen
			return;
			
			/*if(settingScreen.btnList==null)
				return;
			
			btnlist = settingScreen.btnList;
			
			if(btnlist==null)
				return;*/
		}
		
		else if(displayStatus==DisplayStatus.SCREEN_MONITOR)
		{
			if(monitorScreen.btnList==null)
				return;
			
			btnlist = monitorScreen.btnList;
		}
		
		else if(displayStatus==DisplayStatus.SCREEN_CONSENT)
		{
			if(consentScreen.btnList==null)
				return;
			
			btnlist = consentScreen.btnList;
		}
		
		else if(displayStatus==DisplayStatus.SCREEN_PSQI)
		{
			if(psqiScreen.btnList==null)
				return;
			
			btnlist = psqiScreen.btnList;
		}
		
		else if(displayStatus==DisplayStatus.SCREEN_INTRO)
		{
			if(introScreen.btnList==null)
				return;
			
			btnlist = introScreen.btnList;
		}
		
		else if(displayStatus==DisplayStatus.SCREEN_PSQI_QUESTION)
		{
			if(questionScreen.btnList==null)
				return;
			
			LinkedList<MyButton> bl = new LinkedList<MyButton>();
			int btnNum = questionScreen.btnNum[questionScreen.curQuestion];
			int i = 0;
			while (i<btnNum)
			{
				bl.add(questionScreen.btnList.get(i));
				i++;
			}
			
			btnlist = bl;
		}
		
		else if(displayStatus==DisplayStatus.SCREEN_RESULT)
		{	
			//check if menu is hit
			/*if(menuBtn.menuBtnLoc.contains(x, y))
			{
				resultScreen.isMenuExpanded = !resultScreen.isMenuExpanded;
				refreshScreen();
				return;
			}
			else
			{
				//if menu is expanded, focus only respond to menu buttons
				if(resultScreen.isMenuExpanded)
					btnlist = menuBtn.btnList;
				else
					btnlist = resultScreen.btnList;
			}*/	
			
			btnlist = resultScreen.btnList;
				
			if(btnlist==null)
				return;
		}
		
		else if(displayStatus==DisplayStatus.SCREEN_ABOUT)
		{	
			//check if menu is hit
			/*if(menuBtn.menuBtnLoc.contains(x, y))
			{
				aboutScreen.isMenuExpanded = !resultScreen.isMenuExpanded;
				refreshScreen();
				return;
			}
			else
			{
				//if menu is expanded, focus only respond to menu buttons
				if(aboutScreen.isMenuExpanded)
					btnlist = menuBtn.btnList;
				else
					btnlist = aboutScreen.btnList;
			}*/
			
			btnlist = aboutScreen.btnList;	
			if(btnlist==null)
				return;
		}
		
		else if(displayStatus==DisplayStatus.SCREEN_RESULT_EVENT)
		{	
			//check if menu is hit
			/*if(menuBtn.menuBtnLoc.contains(x, y))
			{
				eventScreen.isMenuExpanded = !eventScreen.isMenuExpanded;
				refreshScreen();
				return;
			}
			else
			{
				//if menu is expanded, focus only respond to menu buttons
				if(eventScreen.isMenuExpanded)
					btnlist = menuBtn.btnList;
				else
					btnlist = eventScreen.btnList;
			}	*/	
				
			btnlist = eventScreen.btnList;
			
			if(btnlist==null)
				return;
		}
		
		
		else if(displayStatus==DisplayStatus.SCREEN_HISTORY)
		{		
			//check if menu is hit
			/*if(menuBtn.menuBtnLoc.contains(x, y))
			{
				historyScreen.isMenuExpanded = !historyScreen.isMenuExpanded;
				refreshScreen();
				return;
			}
			else
			{
				//if menu is expanded, focus only respond to menu buttons
				if(historyScreen.isMenuExpanded)
					btnlist = menuBtn.btnList;
				else
					btnlist = historyScreen.btnList;
			}	*/	
			
			btnlist = historyScreen.btnList;
			if(btnlist==null)
				return;
		}
		
		
		else
			return;
		
		
		/****************** Check Button List *******************/
		Iterator<MyButton> iterator = btnlist.iterator();
		int btnIndex = 0;
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			if(btn.area.contains(x, y))
			{
				if(touchType == MotionEvent.ACTION_DOWN)
				{
					lastDownBtn = btn;
					vibrator.vibrate(vibBtn);
					btn.isSelected = true;
					refreshScreen();
				}
				
				else if(touchType == MotionEvent.ACTION_UP)
				{	
					if(lastDownBtn!=null && lastDownBtn.id==btn.id)
					{
						if(btn.id == ButtonID.RESULT_HOME 
								|| btn.id == ButtonID.HISTORY_HOME
								|| btn.id == ButtonID.MENU_HOME)
								returnToHomeScreen();	//do some resets	
						
						/******* menu btn hit *********/
						/*if(btnlist == menuBtn.btnList)
						{
							menuAnimationIndex = 0;
							
							if(displayStatus==DisplayStatus.SCREEN_RESULT)
								resultScreen.isMenuExpanded = false;
							else if(displayStatus==DisplayStatus.SCREEN_RESULT_EVENT)
								eventScreen.isMenuExpanded = false;
							else if(displayStatus==DisplayStatus.SCREEN_HISTORY)
								historyScreen.isMenuExpanded = false;
							
						}*/
						

						/********** block use if do not agree ***********/
						/*if(!configManager.isAgreeConsent)
						{
							boolean isBlocked = true;
							if(displayStatus==DisplayStatus.SCREEN_HOME && btn.id == ButtonID.HOME_SETTING)
									isBlocked = false;
							else if(displayStatus==DisplayStatus.SCREEN_SETTING)
								isBlocked = false;
							else if(displayStatus==DisplayStatus.SCREEN_CONSENT)
								isBlocked = false;
							
							if(isBlocked)
							{
								CharSequence text = "In order to use this App, you need to turn on the upload in Setting.";
								int duration = Toast.LENGTH_SHORT;

								Toast toast = Toast.makeText(context, text, duration);
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
								toast.show();
								
								return;
							}
						}*/
						
						else if(displayStatus ==  DisplayStatus.SCREEN_HOME)
						{
							//lock home screen if not agree
							if(AppUI.isBlockEnabled)
							{
								if(!configManager.isAgreeConsent && 
										!(btn.id==ButtonID.HOME_SETTING
												|| btn.id==ButtonID.HOME_ABOUT
												|| btn.id==ButtonID.ABOUT_CONTACT
												|| btn.id==ButtonID.ABOUT_INTRO))
								{
									CharSequence text = "In order to use this App, you need to turn on the upload in Setting.";
									int duration = Toast.LENGTH_SHORT;
		
									Toast toast = Toast.makeText(context, text, duration);
									toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
									toast.show();
									return;
								}
								
							}//end if block
							
							if(btn.id==ButtonID.HOME_RECENT)
							{
								
								if(HistoryData.stateSleepLN == null)
								{
									//use data of last night
									HistoryData.stateSleepLN = configManager.stateLNList;
									HistoryData.evtMoveLN = configManager.moveLNList;
									HistoryData.evtSnoreLN = configManager.snoreLNList;
									HistoryData.evtCoughLN = configManager.coughLNList;
									HistoryData.overallEff = configManager.overallLN;
									
									
								}
								
								if(HistoryData.stateSleepLN == null)
								{
									//last night do not have data
									CharSequence text = "Recent data is not available.";
									int duration = Toast.LENGTH_SHORT;
		
									Toast toast = Toast.makeText(context, text, duration);
									toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
									toast.show();
									return;
								}
								else
								{
									
									//show result screen
									resultScreen.isShowTime = false;
									pRun.displayResult();
									return;
								}
							}
							
						}
						
						
						/******* monitor btn hit *********/
						else if(displayStatus ==  DisplayStatus.SCREEN_MONITOR)
						{
							if(btn.id==ButtonID.MONITOR_STOP)
								resultScreen.isShowTime = true;
						}
						
						
						/******* consent btn hit *********/
						else if(displayStatus ==  DisplayStatus.SCREEN_CONSENT)
						{
							if(btn.id==ButtonID.CONSENT_AGREE)
							{
								configManager.isAgreeConsent = true;
								AppUI.isUploadDataWhenStop = true;
							}
							else if (btn.id==ButtonID.CONSENT_NO) 	
							{
								configManager.isAgreeConsent = false;
								AppUI.isUploadDataWhenStop = false;
							}
						}
						
						/******* Intro btn hit *********/
						else if(displayStatus ==  DisplayStatus.SCREEN_INTRO)
						{
							if(introScreen.isStillLoading)
								return;
								
							if(btn.id==ButtonID.INTRO_NEXT)
								introScreen.curScreenIndex++;
							else if (btn.id==ButtonID.INTRO_PREV) 							
								introScreen.curScreenIndex--;
							
							if(introScreen.curScreenIndex>=introScreen.numScreen)
							{	
								//finish display intro
								introScreen.curScreenIndex = 0;	//reset
								
								//check is display consent form
								if(configManager.isFirstTimeUse && !configManager.isAgreeConsent && AppUI.isShowConsentForm)
								{
									pRun.displayConsentScreen();
									return;
								}
								
								//configManager.isAgreeConsent = true;
								pRun.goHome();
								
								return;
									
							}
							else if(introScreen.curScreenIndex<0)
							{
								introScreen.curScreenIndex = 0;
								return;
							}
							
							refreshScreen();
							return;
								
						}
						
						/******* question btn hit *********/
						else if(displayStatus ==  DisplayStatus.SCREEN_PSQI_QUESTION)
						{
							if(questionScreen.curQuestion==0)
								questionScreen.psqiScore = 0;;
								
							questionScreen.psqiScore += questionScreen.psqiScores[questionScreen.curQuestion][btnIndex];
							
							if(questionScreen.curQuestion==questionScreen.numScreen-1)
								questionScreen.psqiScore = questionScreen.psqiScore/questionScreen.psqiFullScore;
							
							questionScreen.curQuestion++;
							
							//Log.e("PSQI", " "+btnIndex);
							
							//save answer
							questionScreen.psqiAns+=(btn.id.toString()+"\n");
							
							if(questionScreen.curQuestion==questionScreen.numScreen)
							{
								//finish all questions
													
								//send feed back to server
								String content = questionScreen.psqiAns;
								/*FeedBackSender fbs = new FeedBackSender();
								fbs.setContent(content);
								fbs.send("psqi");*/
								
				    			FileUploader fu = new FileUploader();
				    			File f = fu.createLogFile(content);
				    			fu.send(f, "psqi");
								
								//reset
								questionScreen.curQuestion = 0;
								questionScreen.psqiAns = "";
	
								String strPSQIScore = Math.ceil((double)questionScreen.psqiScore*(double)100)+"/100.";
								Toast toast = Toast.makeText(context, questionScreen.tThanks+strPSQIScore, Toast.LENGTH_LONG);
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
								toast.show();
								
								pRun.goHome();
								
								return;
								
							}
							
							refreshScreen();
							return;
						}
						

						
						/******* setting btn hit *********/
						else if(displayStatus ==  DisplayStatus.SCREEN_SETTING)
						{
							settingScreen.isSwitchOn = !settingScreen.isSwitchOn;
							configManager.isAgreeConsent = settingScreen.isSwitchOn;
							refreshScreen();
							return;
						}
						
						/******* About screen btn hit *********/
						else if(displayStatus ==  DisplayStatus.SCREEN_ABOUT)
						{
							switch(btn.id)
							{
								case ABOUT_CONTACT:
									if(aboutScreen.isShowEditText)
									{
										//send feed back to server
										String content = editText.getText().toString();
										
										FileUploader fileUploader = new FileUploader();
										File file = fileUploader.createLogFile(content);
										fileUploader.send(file, "feedback");
										
										/*FeedBackSender fbs = new FeedBackSender();
										fbs.setContent(content);
										fbs.send("feedback");*/
										aboutScreen.isShowEditText = false;
									}
									else
										aboutScreen.isShowEditText = true;
									refreshScreen();
									break;
								case ABOUT_INTRO:
									if(!aboutScreen.isShowEditText)
									{
										//send feed back to server
										pRun.displayIntroScreen();
										
									}	
									break;
								case CONSENT:
									if(!aboutScreen.isShowEditText)
									{
				
										//check is display consent form
										if(AppUI.isShowConsentForm)
										{
											
											pRun.displayConsentScreen();
											return;
										}
									}		
									break;
							}						
							return;						
						}	
						
						/******* event result btn hit *********/
						else if(displayStatus ==  DisplayStatus.SCREEN_RESULT_EVENT)
						{
							switch(btn.id)
							{
								case EVENTRESULT_INFO:
									eventScreen.isInfoHit = true;
									break;
								case EVENTRESULT_MOVE:
									eventScreen.ds = DisplayStatus.SCREEN_RESULT_EVENT_MOVE;
									eventScreen.clearBtnSelect();
									btn.isSelected = true;
									break;
								case EVENTRESULT_COUGH:
									eventScreen.ds = DisplayStatus.SCREEN_RESULT_EVENT_COUGH;
									eventScreen.clearBtnSelect();
									btn.isSelected = true;
									break;
								case EVENTRESULT_SNORE:
									eventScreen.ds = DisplayStatus.SCREEN_RESULT_EVENT_SNORE;
									eventScreen.clearBtnSelect();
									btn.isSelected = true;
									break;
							}
							refreshScreen();
							return;						
						}
						
						/******* event result btn hit *********/
						else if(displayStatus ==  DisplayStatus.SCREEN_HISTORY)
						{
							switch(btn.id)
							{
								case HISTORY_INFO:
									historyScreen.isInfoHit = true;
									break;
								case HISTORY_WEEK:
									historyScreen.subScreen = DisplayStatus.UPDATE_HISTORY_WEEK;
									historyScreen.clearTimeBtnSelect();
									historyScreen.clearTouchPoint();
									btn.isSelected = true;
									break;
								case HISTORY_MONTH:
									historyScreen.subScreen = DisplayStatus.UPDATE_HISTORY_MONTH;
									historyScreen.clearTimeBtnSelect();
									historyScreen.clearTouchPoint();
									btn.isSelected = true;
									break;
								case HISTORY_YEAR:
									historyScreen.subScreen = DisplayStatus.UPDATE_HISTORY_YEAR;
									historyScreen.clearTimeBtnSelect();
									historyScreen.clearTouchPoint();
									btn.isSelected = true;
									break;
								case HISTORY_QUALITY:
									historyScreen.curScreen = DisplayStatus.SCREEN_HISTORY_QUALITY;
									historyScreen.clearTypeBtnSelect();
									historyScreen.clearTouchPoint();
									btn.isSelected = true;
									break;
								case HISTORY_EVENT:
									historyScreen.curScreen = DisplayStatus.SCREEN_HISTORY_EVENT;
									historyScreen.clearTypeBtnSelect();
									historyScreen.clearTouchPoint();
									btn.isSelected = true;
									break;
							}
							refreshScreen();
							return;						
						}
						
						/******** handle by process thread *********/
						Log.e("BUTTON HIT", "BTN:"+btnIndex);		
						pRun.btnHit(btn);
					}				
					
				}
														
				break;
			}
			btnIndex++;
		}//EOF while
		
	}//EOF handleTouch
	
	
	/**
	 * used for display animation of Monitoring, Duration, SoundBar and Menu
	 * @author haotianrock
	 *
	 */
	class MonitorTimerTask extends TimerTask
	{

		@Override
		public void run() 
		{
			countTick++;
			
			if(countTick!=ticksPerSecond)
			{
				//update display every 100 ms
				monitorScreen.changeMonitorColor();
				if(pRun!=null)
				{
					monitorScreen.curMax = Math.abs(pRun.getCurFrameMax());
					
					if(AppUI.isShowCoughRealTime)
					{
						int preCnt = monitorScreen.coughFrameCnt; 
						int curCnt = pRun.getRealTimeCough();
						
						Log.e("COUGH DISPLAY", Integer.toString(curCnt));
						
						if(preCnt != curCnt)
						{
							monitorScreen.isDrawCough = true;
							monitorScreen.coughFrameCnt = curCnt;
						}
						else
							monitorScreen.isDrawCough = false;
					}
				}
			}
			else
			{		
				//update display every 1 sec
				monitorScreen.addSecondDuration();
				
				//reset small tick
				countTick=0;
			}
	
			invalidateScreen();
			
		}//EOF run()
		
	}//EOF class MyTimerTask
	

	private void startTimer()
	{
		//set timer
		uiTimer = new Timer();	//create a non-daemon timer
		uiTimerTask = new MonitorTimerTask();
		//timer.schedule(timerTask, 0, 1000);
		uiTimer.scheduleAtFixedRate(uiTimerTask, 0, timerPeriod);
	}
	
	private void endTimer()
	{
		if(uiTimer!=null)
		{
			uiTimer.cancel();
			uiTimer = null;
		}
	}
	
	private void createNotification(boolean isPause) 
	{
		Notification notif = new Notification();

		  Intent contentIntent = new Intent(this, MainActivity.class);
		  contentIntent.setAction(Intent.ACTION_MAIN);
		  contentIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		  String txtPause = "Please use BACK button to exit the app.";
		  String txtExit = "You have exited iSleep.";
		  String str;
		  
		  notif.icon = R.drawable.ic_launcher;
		  
		  if(isPause)
			  str = txtPause;
		  else
			  str = txtExit;
		  
		  notif.setLatestEventInfo(this, "iSleep running background", str, PendingIntent.getActivity(this.getBaseContext(), 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT));      
		
		  notif.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
		  //notif.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_ONLY_ALERT_ONCE;
		  //notif.flags |= Notification.FLAG_AUTO_CANCEL; 
		  notif.number = 0;
		
		  mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		  mNotificationManager.notify(0, notif);
	}
	
	private void refreshScreen()
	{
		uiHandler.post(new UpdateRunnable());
	}
	
	private void enableTextView()
	{
		textView.setEnabled(true);
		textView.setVisibility(View.VISIBLE);
	}
	
	private void disableTextView()
	{
		textView.setText("");
		textView.scrollTo(0, 0);
		//textView.setTextSize(AppUI.SIZE_ORIG_TEXTVIEW);
		textView.setEnabled(false);
		textView.setVisibility(View.INVISIBLE);
	}
	
	private void enableEditText()
	{
		editText.setEnabled(true);
		editText.setVisibility(View.VISIBLE);
	}
	
	private void disableEditText()
	{
		editText.setText("");
		editText.scrollTo(0, 0);
		editText.setEnabled(false);
		editText.setVisibility(View.INVISIBLE);
	}
	
}//EOF MainActivity
