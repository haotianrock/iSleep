package com.tian.sleep;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;
import java.util.LinkedList;

import com.tian.sleep.MainActivity.UIHandler;


import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

public class ProcessRunnable implements Runnable
{
	
	private AudioRecord recorder = null;
	
	boolean isShowLog = false;
	
	//thread running switch
	boolean _run = false;
	boolean firstTimeRun = true;
	
	//the handler of parent activity, used for communication
	UIHandler hParentUIHandler = null;
	
	ConfigurationManager configManager;
	
	//the status of current processing
	ProcessStatus processStatus = ProcessStatus.IDLE;
	
	File logFile = null;
	FileWriter mFWriter = null;
	FileOutputStream fos = null;
	String logFileName = "iSleepLog";
	
	public double curFrameMax = -1;
	
	
	//time keeping
	int frameCount = 0;
	
	
	//sampling and framing
	final static int SAMPLE_RATE = 16000;	//16kHz
	final static int READ_BUFFER_DURATION = 4; 	//retrieve data from audio buffer every 4 seconds
	final static int BITS_PER_SAMPLE = 16;
	final static int BYTES_PER_SECOND = SAMPLE_RATE*BITS_PER_SAMPLE/8;
	final static int SAMPLES_PER_FRAME = 1600;
	final static int FRAMES_PER_SECOND = SAMPLE_RATE/SAMPLES_PER_FRAME;
	final static int FRAMES_PER_READ = READ_BUFFER_DURATION*FRAMES_PER_SECOND;
	
	
	//audoi buffer
	int audioBufSize;
	final static int NUM_AUDIO_BUF = 3;
	short[][] audioBuffer = null;
	boolean[][] flagBuffer = null;	//used to indicate env noise frame and calculate event area
	boolean[] globalFlagBuf = null;	//used in event area detection after admission control
	int curBufferInd = 0;	//the index of the available audio buffer
	short[] curAudioBuffer = null;
	short[] availableAudioBuffer = null;
	boolean[] curFlagBuffer = null;
	boolean[] availableFlagBuffer = null;
	
	
	//detection buffer
	double[] varList = new double[FRAMES_PER_READ];		
	double[] engList = new double[FRAMES_PER_READ];		
	double[] rlhList = new double[FRAMES_PER_READ];	
	
	boolean[] moveFlag = new boolean[FRAMES_PER_READ];
	boolean[] snoreFlag = new boolean[FRAMES_PER_READ];
	boolean[] coughFlag = new boolean[FRAMES_PER_READ];
	
	
	//environmental noise
	EnvNoise envNoise = null;
	double TIMES_ENV_DETECTION = 4;

	
	//low pass filter
	double lowPassAlpha = 0.5;
	
	//Cough Detection
	double TIMES_COUGH_DETECTION = 40;
	int MAX_COUGH_DURATION = 5; //the maximum duration of a cough is 1 second in default
	
	//Snore Detection
	double SNORE_ZC_THRESH = -2;
	int MIN_SNORE_GAP = 5;
	
	int LEN_OPEN_SNORE_DETECTION = 2;	//remove frames with less than 2-frame duration
	int LEN_CLOSE_SNORE_DETECTION = 3;
	int LEN_DILATE_SNORE_DETECTION = 3;
	
	//Breath Detection
	double BREATH_LMMR_THRESH = -2;
	int MIN_BREATH_GAP = 5;
	
	//Move Detection
	double TIMES_MOVE_DETECTION = 8;
	int LEN_OPEN_MOVE_DETECTION = 3;	//remove frames with less than 3-frame duration
	int LEN_CLOSE_MOVE_DETECTION = 5;
	int LEN_DILATE_MOVE_DETECTION = 3;
	
	//threshold
	final static double MAX_VAR_ENV_ESTIMATE = 0.5;
	
	//processing switches
	boolean estimatingEnvNoise = true;
	boolean detectEnvNoise = false;
	
	//List that contains all detected sleep events
	LinkedList<SleepEvent> eventList = null;
	LinkedList<SleepEvent> moveList = null;
	LinkedList<SleepEvent> coughList = null;
	LinkedList<SleepEvent> snoreList = null;
	
	boolean displayOnce = false;
	
	
	int coughFrameCnt = 0;
	
	
	void setParentUIHandler(UIHandler ph)
	{
		hParentUIHandler = ph;
	}
	
	void setConfigManager(ConfigurationManager cm)
	{
		configManager = cm;
	}
	
	@Override
	public void run()
	{
		//set priority of this thread
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		eventList = new LinkedList<SleepEvent>();//NOT IN USE
		
		moveList = new LinkedList<SleepEvent>();
		snoreList = new LinkedList<SleepEvent>();
		coughList = new LinkedList<SleepEvent>();
		
		initializeRecorder();		
		initializeBuffer();
		
		while(_run)
		{
		
			// do nothing but display the HOME SCREEN when the app is just started
			if(processStatus==ProcessStatus.IDLE)
			{		 
				if(!displayOnce)
            	{	
					//if(configManager.isFirstTimeUse)
						//displayConsentScreen();
					if(configManager.isShowIntroScreen)
						displayIntroScreen();
					else
						displayHomeScreen();
					
	                displayOnce = true;
            	}			
				continue;
			}
			
			if(processStatus==ProcessStatus.STOP)
			{
				if(recorder.getState() == AudioRecord.RECORDSTATE_RECORDING)
					recorder.stop();
				continue;
			}
			

			
			// !!! FOR TEST ONLY
			//meanEnvNoise = 1.57E-4;
			//stdEnvNoise = 7E-4;
			//acThreshold = meanEnvNoise + timesEnvNoiseStd*stdEnvNoise;
			//acThreshold = 0.01;
			
			//prepare buffer to retrieve audio data
			if(firstTimeRun)
				firstRunPreload();

			//get the available buffer that is ready for another buffer-read
			availableAudioBuffer = getPreviousAudioBuf(curBufferInd);
			availableFlagBuffer = getPreviousFlagBuf(curBufferInd);
			
			//move the index of the current (curBufferInd) focused buffer-read
			moveCurBufferIndex();
			
			//read the audio buffer of the recorder
			//increase the framecount inside
			//recorder.read(availableAudioBuffer, 0, audioBufSize);
			readAudioBuffer(availableAudioBuffer, audioBufSize);
			//Log.e("FRAMES READ", Integer.toString(frameCount));
			
		
			boolean isEnvNoise = false;
			if(estimatingEnvNoise)
			{
				isEnvNoise = estimateEnvNoise(availableAudioBuffer);
			}
					
			//detectEnvNoise  = true;
			if(!isEnvNoise)
			{
				//conducted on the buffer-read next to curBufferInd
				//so that the it is able to conduct event area detection on curAudiobuffer
				//AdmissionControl(availableAudioBuffer, availableFlagBuffer);
				
				//LinkedList<EventArea> areaList = detectEventArea();
				
				//EventDetection(areaList, eventList);
				
				detectSleepEvents(availableAudioBuffer, availableFlagBuffer);		
			}
						
		}//EOF while(_run)
		
		//releaseResources();
		
	}//end of run()
	
	
	private void detectSleepEvents(short[] availableAudioBuffer, boolean[] availableFlagBuffer)
	{
		
		short[] buf = availableAudioBuffer;
		boolean[] flags = availableFlagBuffer;
		
		
		/************** extract features ***************/
		int num_samples = buf.length;
		int samplesPerFrame = SAMPLES_PER_FRAME;		
		int num_frames = num_samples/samplesPerFrame;
		
		
		int s,e;				
		
		//calculate env features for each env noise frame
		for(int i=0; i<num_frames; i++)
		{
			s = i*samplesPerFrame;
			e = s+samplesPerFrame-1;
			
			engList[i] = MathOperation.getEnergy(buf, s, e);
			rlhList[i] = MathOperation.getRLH(buf, s, e);
			varList[i] = MathOperation.getVar(buf, s, e);
		}
		
		
		/************ detect Events *************/
		double eng, var, rlh;

		
		for(int i=0; i<num_frames; i++)
		{
			eng = engList[i];
			var = varList[i];
			rlh = rlhList[i];
			
			//--------move----------
			if(eng>envNoise.engMoveMin && eng<envNoise.engMoveMax
					&& var>envNoise.varMoveMin
					&& rlh<envNoise.rlhMoveMax)
				moveFlag[i] = true;
			else
				moveFlag[i] = false;
			
			
			//--------snore----------
			if(eng>envNoise.engSnoreMin
					&& var>envNoise.varSnoreMin
					&& rlh>envNoise.rlhSnoreMin)
				snoreFlag[i] = true;
			else if(eng>envNoise.engSnoreMin1
					&& var>envNoise.varSnoreMin1
					&& rlh>envNoise.rlhSnoreMin1)
				snoreFlag[i] = true;
			else
				snoreFlag[i] = false;
			
			//--------cough----------
			if(eng>envNoise.engCougnMin
					&& rlh<envNoise.rlhCoughMax
					&& var>envNoise.varCougnMin)
			{
				coughFlag[i] = true;
				
				if(AppUI.isShowCoughRealTime)
				{
					coughFrameCnt = frameCount;
				}
			}
			else
				coughFlag[i] = false;	
		}
		
		
		/*********** filter event result *************/
		int sf = frameCount-FRAMES_PER_READ*2;
		
		//move
		MathOperation.openningOperation(moveFlag, LEN_OPEN_MOVE_DETECTION);
		MathOperation.closingOperation(moveFlag, LEN_CLOSE_MOVE_DETECTION);
		MathOperation.dilationOperation(moveFlag, LEN_DILATE_MOVE_DETECTION);
		
		//snore
		//MathOperation.openningOperation(snoreFlag, LEN_OPEN_SNORE_DETECTION);
		MathOperation.closingOperation(snoreFlag, LEN_CLOSE_SNORE_DETECTION);
		MathOperation.dilationOperation(snoreFlag, LEN_DILATE_SNORE_DETECTION);
		
		//cough
		MathOperation.limitDuration(coughFlag, MAX_COUGH_DURATION);	
		
		if(isShowLog)
		{
			Logger.logBooleanArr(moveFlag, 0, num_frames-1, 40, "MOVE#"+sf);
			Logger.logBooleanArr(snoreFlag, 0, num_frames-1, 40, "SNORE#"+sf);
			Logger.logBooleanArr(coughFlag, 0, num_frames-1, 40, "COUGH#"+sf);
		}
		
		
		/************ save detected events *************/		
		MathOperation.saveSleepEvent(moveList, moveFlag, sf);
		MathOperation.saveSleepEvent(snoreList, snoreFlag, sf);
		MathOperation.saveSleepEvent(coughList, coughFlag, sf);
		
	}
	
	private void EventDetection(LinkedList<EventArea> areaList, LinkedList<SleepEvent> eventList)
	{
		EventArea ea = areaList.pollFirst();
		
		while(ea!=null)
		{
			FeatureExtraction(ea);
			
			//
			//Logger.logEventFeatures(ea, "FEATURES-"+frameCount);
			//
			
			CoughDetection(ea);
			
			SnoreDetection(ea);
			
			MovementDetection(ea);
			
			//
			int s  = ea.len;
			int[] b = new int[s];
			for (int i = 0; i<s; i++)
			{
				if(ea.typeArr[i]==EventType.COUGH)
					b[i] = 1;
				else if (ea.typeArr[i]==EventType.SNORE)
					b[i] = 2;
				else if (ea.typeArr[i]==EventType.MOVEMENT)
					b[i] = 3;
				else
					b[i] = 0;
			}
			Logger.logIntArr(b,0,s-1,"EVENT Detection-"+frameCount);
			//
			
			//get the next event area
			ea = null;
			ea = areaList.pollFirst();
		}
	}
	
	
	private void MovementDetection (EventArea ea)
	{
		//int movementThreshold = (int) ((meanEnvNoise + TIMES_MOVE_DETECTION*stdEnvNoise) 
			//					* (double)Short.MAX_VALUE);
		
		int movementThreshold = 0;
		 
		int size = ea.len;
	 
		//flags indicating movement frame
		boolean[] flags = new boolean[size];
	 
		//check each frame in of event area
		for(int i=0; i<size; i++)
		{
			if(ea.maxArr[i]>movementThreshold)
				flags[i] = true;
			else
				flags[i] = false;
		}	
		
		MathOperation.openningOperation(flags, LEN_OPEN_MOVE_DETECTION);
		MathOperation.closingOperation(flags, LEN_CLOSE_MOVE_DETECTION);
		MathOperation.dilationOperation(flags, LEN_DILATE_MOVE_DETECTION);
		
		//save the detected events to event area
		for(int i=0; i<size; i++)
		{
			if(flags[i] && ea.typeArr[i]==EventType.NONE)
				ea.typeArr[i] = EventType.MOVEMENT;
		}
		
	}
	

	
	private void CoughDetection(EventArea ea)
	{
		 //int coughThreshold = (int) ((meanEnvNoise + TIMES_COUGH_DETECTION*stdEnvNoise) * (double)Short.MAX_VALUE);
		 int coughThreshold=0;
		
		 int size = ea.len;
		 
		 //flags indicating cough frame
		 boolean[] flags = new boolean[size];
		 
		 //check each frame in of event area
		 for(int i=0; i<size; i++)
		 {
			 if(ea.maxArr[i]>coughThreshold)
				 flags[i] = true;
			 else
				 flags[i] = false;
		 }
		 
		 //filter out continuous cough frames of more than MAX_COUGH_DURATION frames
		 int duration = 0;
		 for(int i=0; i<size; i++)
		 {
			 if(flags[i]==true)
				 duration++;
			 else
			 {
				 if(duration>MAX_COUGH_DURATION)
				 {
					 //remove flags
					 for(int j=1; j<=duration; j++)
					 {
						 flags[i-j] = false;
					 }
				 }
				 duration=0;
			 }
		 }
		 
		 //save the detected events to event area
		 for(int i=0; i<size; i++)
		 {
			 if(flags[i])
				 ea.typeArr[i] = EventType.COUGH;
		 }
		 
	}
	
	
	private void SnoreDetection(EventArea ea)
	{
		int size = ea.len;
		
		double[] r = MathOperation.CalStdInt(ea.zcArr);
		double mean = r[0];
		double std = r[1];
		
		//flags indicating snore frame
		boolean[] flags = new boolean[size];
		
		double nZC;
		
		//check each frame in event area for snore frame
		for(int i=0; i<size; i++)
		{
			nZC = ((double)ea.zcArr[i]-mean)/std;
			
			if(nZC<=SNORE_ZC_THRESH)
				flags[i] = true;
			else
				flags[i] = false;
		}
		
		//connect two sequence of snore frames if their gap is less than MIN_SNORE_GAP;
		 int gap = 0;
		 for(int i=0; i<size; i++)
		 {
			 if(flags[i]==false)
				 gap++;
			 else
			 {
				 if(gap<MIN_SNORE_GAP)
				 {
					 //fill in the gap
					 for(int j=1; j<=gap; j++)
					 {
						 flags[i-j] = true;
					 }
				 }
				 gap = 0;
			 }
		 }
		 
		//save the detected snore events to event area
		 for(int i=0; i<size; i++)
		 {
			 if(flags[i] && ea.typeArr[i]==EventType.NONE)
				 ea.typeArr[i] = EventType.SNORE;
		 }
	}
	
	
	private void BreathDetection(EventArea ea)
	{
		int size = ea.len;
		
		double[] r = MathOperation.CalStdInt(ea.lmmrArr);
		double mean = r[0];
		double std = r[1];
		
		//flags indicating breath frame
		boolean[] flags = new boolean[size];
		
		double nLMMR;
		
		//check each frame in event area for breath frame
		for(int i=0; i<size; i++)
		{
			nLMMR = ((double)ea.lmmrArr[i]-mean)/std;
			
			if(nLMMR<=BREATH_LMMR_THRESH)
				flags[i] = true;
			else
				flags[i] = false;
		}
		
		//connect two sequence of snore frames if their gap is less than MIN_BREATH_GAP;
		 int gap = 0;
		 for(int i=0; i<size; i++)
		 {
			 if(flags[i]==false)
				 gap++;
			 else
			 {
				 if(gap<MIN_BREATH_GAP)
				 {
					 //fill in the gap
					 for(int j=1; j<=gap; j++)
					 {
						 flags[i-j] = true;
					 }
				 }
				 gap = 0;
			 }
		 }
		 
		//save the detected snore events to event area
		 for(int i=0; i<size; i++)
		 {
			 if(flags[i] && ea.typeArr[i]==EventType.NONE)
				 ea.typeArr[i] = EventType.BREATH;
		 }
	}
	
	
	private void FeatureExtraction(EventArea ea)
	{
		//allocate space for storing its features
		ea.allocate();
		
		int sf = ea.locStart;
		int ef = ea.locEnd;
		
		int startPos = 0;
		int fLen = SAMPLES_PER_FRAME;
		
		short[] readBuffer;
		Feature feature = new Feature();
		
		//calculate features for each frame in this event area
		for(int i = sf; i<=ef; i++)
		{		
			readBuffer = getReadBuffer(i);
			startPos = (i%FRAMES_PER_READ)*fLen;
			
			calculateFeatures(feature, readBuffer, startPos, fLen);
			ea.setFeature(feature, i);
		}
	}
	
	
	private void calculateFeatures(Feature fea, short[] d, int pos, int len)
	{
		int end = pos+len;
		
		int max = Short.MIN_VALUE;
		int zcCnt = 0;
		int lmmrCnt = 0;
		
		int pre, cur, next;
		
		//ignore considering the first two element in computing features
		pre = d[pos];
		cur = d[pos+1];		
		
		//travel through audio samples in this frame
		for(int i=pos+2; i<end; i++)
		{
			next = d[i];
			
			//update MAX
			if(next>max)
				max = next;
			
			//low pass filter
			next = (int) (lowPassAlpha*next + (1-lowPassAlpha)*cur);

			//count ZC
			if((cur>=0 && next<=0) || (cur<=0 && next>=0))
				zcCnt++;
		
			//count LMMR
			if( ((cur-pre)<0 && (cur-next)<0) || ((cur-pre)>0 && (cur-next)>0) )
				lmmrCnt++;
			
			pre = cur;
			cur = next;
		}
		
		fea.max = max;
		fea.lmmr = lmmrCnt;
		fea.zc = zcCnt;
	}
	
	private void readAudioBuffer(short[] buf, int size)
	{
		int sizeCnt = 0;
		int sizePerRead = SAMPLES_PER_FRAME;
		short sMax;
		
		while(sizeCnt<size)
		{
			if(recorder==null)
				break;
			
			recorder.read(buf, sizeCnt, sizePerRead);
			
			sMax = MathOperation.CalMax(buf, sizeCnt, sizeCnt+sizePerRead-1);
			curFrameMax = (double)sMax/(double)Short.MAX_VALUE;
			
			sizeCnt+=sizePerRead;
		}
		
		frameCount += FRAMES_PER_READ;
		
		//Log.e("FRAMES READ", Integer.toString(frameCount));
	}
	
	
	public double getCurFrameMax()
	{
		return curFrameMax;
	}
	
	public int getRealTimeCough()
	{
		return coughFrameCnt;
	}
	
	private short[] getReadBuffer(int locFrameIndex)
	{
		short[] buf = null;
		
		if(locFrameIndex>=0 && locFrameIndex<FRAMES_PER_READ)
		{
			buf = getPreviousAudioBuf(curBufferInd);
		}
		else if(locFrameIndex>=FRAMES_PER_READ && locFrameIndex<2*FRAMES_PER_READ)
		{
			buf = getCurAudioBuf();
		}
		else if(locFrameIndex>=2*FRAMES_PER_READ && locFrameIndex<3*FRAMES_PER_READ)
		{
			buf = getNextAudioBuf(curBufferInd);
		}
		
		return buf;
	}
	
	public void releaseResources()
	{
		if(recorder!=null)
		{
			recorder.stop();			
			recorder.release();
			recorder = null;
			Log.e("INFO", "recorder released");
		}	
		
		/*if(mFWriter!=null)
		{
			try 
			{
				mFWriter.write("!");	//indicate end file
				mFWriter.flush();
				mFWriter.close();
				mFWriter = null;
				Log.e("INFO", "Logger released");
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
	
	
	private void initializeRecorder()
	{
		int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		
		//Log.e("DEBUG", Integer.toString(minBufSize));
		
		//initialize audio recorder
		recorder = new AudioRecord(AudioSource.CAMCORDER, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, 
				AudioFormat.ENCODING_PCM_16BIT, BYTES_PER_SECOND*READ_BUFFER_DURATION*3);		
		if(recorder!=null)
			Log.e("monitor", "recorder initialized");
		else
			Log.e("monitor", "FAIL to initialize recorder");
	}
	
	private void initializeBuffer()
	{
		//initialize buffer used to retrieve data from audio recorder buffer
		audioBufSize = SAMPLE_RATE*READ_BUFFER_DURATION;
		audioBuffer = new short[NUM_AUDIO_BUF][audioBufSize];
		curBufferInd = 0;
		
		flagBuffer = new boolean[NUM_AUDIO_BUF][FRAMES_PER_READ];
		
		globalFlagBuf = new boolean[NUM_AUDIO_BUF*FRAMES_PER_READ];
		
		Log.e("monitor", "buffer initialized");
	}
	
	private void firstRunPreload()
	{
		envNoise = new EnvNoise();
		
		//pre-load two buffer-read, 
		//since it requires three buffer-reads to conduct event area detection
		//recorder.read(audioBuffer[0], 0, audioBufSize);
		readAudioBuffer(audioBuffer[0], audioBufSize);
		//frameCount += FRAMES_PER_READ;
		AdmissionControl(audioBuffer[0], flagBuffer[0]);				
		//recorder.read(audioBuffer[1], 0, audioBufSize);
		readAudioBuffer(audioBuffer[1], audioBufSize);
		//frameCount += FRAMES_PER_READ;
		AdmissionControl(audioBuffer[1], flagBuffer[1]);
		
		//preset the current buffer index 
		curBufferInd = 0;
		
		//prevent this from running in the future
		firstTimeRun = false;
	}
	
	public void setRunning(boolean run)
	{
		_run = run;
	}
	
	private short[] getPreviousAudioBuf(int curInd)
	{
		//get the previous buffer in audioBuffer[][] containing the last audio samples
		int ind = curInd-1;
		if(ind<0)
			ind = NUM_AUDIO_BUF-1;
		
		return audioBuffer[ind];
	}
	
	private boolean[] getPreviousFlagBuf(int curInd)
	{
		//get the previous buffer in audioBuffer[][] containing the last audio samples
		int ind = curInd-1;
		if(ind<0)
			ind = NUM_AUDIO_BUF-1;
		
		return flagBuffer[ind];
	}
	
	private short[] getNextAudioBuf(int curInd)
	{
		//get the next buffer in audioBuffer[][]
		int ind = curInd+1;
		if(ind>=NUM_AUDIO_BUF)
			ind = 0;
		
		return audioBuffer[ind];
	}
	
	private boolean[] getNextFlagBuf(int curInd)
	{
		//get the next buffer in audioBuffer[][]
		int ind = curInd+1;
		if(ind>=NUM_AUDIO_BUF)
			ind = 0;
		
		return flagBuffer[ind];
	}
	
	private boolean[] getCurFlagBuf()
	{

		return flagBuffer[curBufferInd];
	}
	
	private short[] getCurAudioBuf()
	{

		return audioBuffer[curBufferInd];
	}
	
	private void moveCurBufferIndex()
	{
		//reset the current buffer index
		curBufferInd+=1;

		if(curBufferInd>=NUM_AUDIO_BUF)
			curBufferInd = 0;
	}

	
	public void setProcessStatus(ProcessStatus ps)
	{
		processStatus = ps;
	}
	
	public void btnHit(MyButton btn)
	{
		if(btn.id == ButtonID.HOME_START)
			startMonitoring();
		else if(btn.id == ButtonID.HOME_HISTORY || btn.id == ButtonID.MENU_HISTORY)
			displayHistory();
		else if(btn.id == ButtonID.HOME_SETTING)
			displaySettingScreen();
		else if(btn.id == ButtonID.MENU_HOME)
			goHome();
		
		else if(btn.id == ButtonID.HOME_PSQI)
			displayPSQIScreen();
		else if(btn.id == ButtonID.PSQI_OK)
			displayQuestionScreen();
		else if(btn.id == ButtonID.PSQI_NO)
			goHome();
		else if(btn.id == ButtonID.CONSENT_AGREE || btn.id==ButtonID.CONSENT_NO)
			goHome();
		else if(btn.id == ButtonID.HOME_ABOUT ||btn.id == ButtonID.MENU_ABOUT)
			//sendStatEmail();
			displayAboutScreen();
		else if(btn.id == ButtonID.MONITOR_STOP)
			stopMonitoring();
		else if(btn.id == ButtonID.RESULT_EVENT)
			displayEventResult();
			//displayMoveEventResult();
		else if(btn.id == ButtonID.RESULT_HOME)
			goHome();
		else if(btn.id == ButtonID.EVENTRESULT_BACK)
			displayResult();
		else if(btn.id == ButtonID.EVENTRESULT_MOVE)
			displayMoveEventResult();
		else if(btn.id == ButtonID.EVENTRESULT_COUGH)
			displayCoughEventResult();
		else if(btn.id == ButtonID.EVENTRESULT_GETUP)
			displayGetupEventResult();
		else if(btn.id == ButtonID.EVENTRESULT_SNORE)
			displaySnoreEventResult();
		else if(btn.id == ButtonID.HISTORY_QUALITY
				||btn.id == ButtonID.HISTORY_QUALITYWEEK
				||btn.id == ButtonID.HISTORY_QUALITYMONTH
				||btn.id == ButtonID.HISTORY_QUALITYYEAR)
			displaySleepQualityHistory(btn);
		else if(btn.id == ButtonID.HISTORY_EVENT
				||btn.id == ButtonID.HISTORY_EVENTWEEK
				||btn.id == ButtonID.HISTORY_EVENTMONTH
				||btn.id == ButtonID.HISTORY_EVENTYEAR)
			displaySleepEventHistory(btn);
		/*else if(btn.id == ButtonID.HISTORY_TIME)
			displaySleepTimeHistory();*/
		else if(btn.id == ButtonID.HISTORY_HOME)
			goHome();
		
	}
	
	public void fingerMove(DisplayUpdateMsg dsmsg, int x, int y)
	{
		if(dsmsg.ds == DisplayStatus.SCREEN_RESULT)
			dsmsg.update = DisplayStatus.UPDATE_RESULT_MOVE;
		else if(dsmsg.ds == DisplayStatus.SCREEN_RESULT_EVENT||
				dsmsg.ds == DisplayStatus.SCREEN_RESULT_EVENT_MOVE||
				dsmsg.ds == DisplayStatus.SCREEN_RESULT_EVENT_SNORE||
				dsmsg.ds == DisplayStatus.SCREEN_RESULT_EVENT_COUGH||
				dsmsg.ds == DisplayStatus.SCREEN_RESULT_EVENT_GETUP)
			dsmsg.update = DisplayStatus.UPDATE_EVENTRESULT_MOVE;
		else if(dsmsg.ds == DisplayStatus.SCREEN_HISTORY_EVENT)
			dsmsg.update1 = DisplayStatus.UPDATE_HISTORY_EVENT_MOVE;
		else if(dsmsg.ds == DisplayStatus.SCREEN_HISTORY_QUALITY)
			dsmsg.update1 = DisplayStatus.UPDATE_HISTORY_QUALITY_MOVE;
			
		dsmsg.x = x;
		dsmsg.y = y;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dsmsg;
        hParentUIHandler.sendMessage(msg);
	}
	
	
	private void displaySleepQualityHistory(MyButton btn)
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_HISTORY_QUALITY;
		
		if(btn.id == ButtonID.HISTORY_QUALITYWEEK)
			dum.update = DisplayStatus.UPDATE_HISTORY_QUALITY_WEEK;
		else if(btn.id == ButtonID.HISTORY_QUALITYMONTH)
			dum.update = DisplayStatus.UPDATE_HISTORY_QUALITY_MONTH;
		else if(btn.id == ButtonID.HISTORY_QUALITYYEAR)
			dum.update = DisplayStatus.UPDATE_HISTORY_QUALITY_YEAR;
		else
			dum.update = DisplayStatus.UPDATE_HISTORY_QUALITY_WEEK;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	private void displaySleepEventHistory(MyButton btn)
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_HISTORY_EVENT;
		
		if(btn.id == ButtonID.HISTORY_EVENTWEEK)
			dum.update = DisplayStatus.UPDATE_HISTORY_EVENT_WEEK;
		else if(btn.id == ButtonID.HISTORY_EVENTMONTH)
			dum.update = DisplayStatus.UPDATE_HISTORY_EVENT_MONTH;
		else if(btn.id == ButtonID.HISTORY_EVENTYEAR)
			dum.update = DisplayStatus.UPDATE_HISTORY_EVENT_YEAR;
		else
			dum.update = DisplayStatus.UPDATE_HISTORY_EVENT_WEEK;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	/*private void displaySleepTimeHistory()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_HISTORY_TIME;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}*/
	
	
	private File createLogFile(String fileName)
	{
		File ReportFile = null;
	    File directory = new File 
	    (Environment.getExternalStorageDirectory().getPath()+"/iSleepLogFiles");
	    if (!directory.exists()) { 
              	directory.mkdir(); 
	    } 

        try {
            ReportFile = new File(directory.getPath()+"/"+fileName+".txt");
            ReportFile.createNewFile();

            FileOutputStream fOut = new FileOutputStream(ReportFile);                   
            OutputStreamWriter OutWriter = new OutputStreamWriter(fOut);

            OutWriter.write("TEXT"+"\r\n");

            OutWriter.close();
            fOut.close();
            
            return ReportFile;

        } catch (Exception e) {
            e.printStackTrace();
        }//End of try/catch
        
        return null;
	}
	
	
	private void sendStatEmail()
	{
		Mail m = new Mail("sleep.quality.monitor@gmail.com", "haotiansleep"); 
		 
	    m.setBody("Email body."); 
	    
	    String fileName = "Tian";
	   
	    File logFile = createLogFile(fileName);
        
        try {
			m.addAttachment(logFile.getPath(), fileName+".txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    new AsyncMailSender().execute(m);
	}
	
	
	public void displayResult()
	{
		//show main result screen
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_RESULT;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	private void displayHistory()
	{
		//show main result screen
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_HISTORY;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	
	private void displayAboutScreen()
	{
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_ABOUT;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void goHome()
	{
		//reset parameters in HandleTouch()
		displayHomeScreen();
	}
	
	
	private void stopMonitoring()
	{	
		processStatus = ProcessStatus.STOP;
		
		
		int totalSec = frameCount/FRAMES_PER_SECOND;
		int framesPerMin = FRAMES_PER_SECOND*60;
		int totalMin = frameCount/framesPerMin;
		
		//show main result screen
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
				
		
		
		/******** fake UI **********/
		if(AppUI.isFakeUI)
		{
			FAKEpostProcessEvents();
			dum.ds = DisplayStatus.SCREEN_RESULT;
		}
		else
		{
			// do not process too short data
			if(totalMin<120)
			{
				dum.ds = DisplayStatus.SCREEN_HOME;
				dum.toastStr = "At least 2-hour monitoring is needed.";
			}
			else
			{
				dum.ds = DisplayStatus.SCREEN_RESULT;
				postProcessEvents();
			}
		}
		
		
		
		//dum.ds = DisplayStatus.SCREEN_RESULT;
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
              

		//reset parameters
		firstTimeRun = true;
		frameCount = 0;
		eventList.clear();
		//	
	}
	
	private void FAKEpostProcessEvents()
	{
		/*************** FAKE UI ***************/
		int len = 48;
		double[] moveTenMinList = new double[len];
		double[] snoreTenMinList = new double[len];
		double[] coughTenMinList = new double[len];
		double[] stateList = new double[len];
		
		for(int i=0; i<len; i++)
		{
			moveTenMinList[i] = Math.random();
			snoreTenMinList[i] = Math.random();
			coughTenMinList[i] = Math.random();
			stateList[i] = Math.random();
			stateList[i] = (stateList[i]-0.5)*2;
		}
		
		HistoryData.setLastNight(moveTenMinList, snoreTenMinList, coughTenMinList, stateList);
		return;
		//////////////////////////////////////////
	}
	
	private void postProcessEvents()
	{
		boolean isLog = false;
		
		int totalSec = frameCount/FRAMES_PER_SECOND;
		int framesPerMin = FRAMES_PER_SECOND*60;
		int totalMin = frameCount/framesPerMin;
		
		Log.e("POST", "totalMin:"+totalMin);

		
		/************* compute actigraph score ******************/
		int[] moveScoreMin = new int[totalMin];
		for(int i=0; i<totalMin; i++)
			moveScoreMin[i] = 0;
		
		//group move event in min
		int minInd = 1;
		int sF, eF, numf;
		int sMin, eMin;
		int curMinEndFrame;
		SleepEvent se;
		Iterator<SleepEvent> iterator = moveList.iterator();
		while (iterator.hasNext()) 
		{	
			se = iterator.next();
			
			sF = se.startFrame;
			eF = se.endFrame;
			
			sMin = (int) Math.ceil((double)sF/(double)framesPerMin);
			
			if(sMin>=totalMin)
				sMin = totalMin;
			
			if(isLog)
			Log.e("moveScoreMin", sF+" "+framesPerMin+" "+sMin);
			
			moveScoreMin[sMin-1] += se.duration;		
		}
		
		
		/**************** Sleep State ******************/
		boolean[] stateSleepMin = new boolean[totalMin-6];
		double D;
		
		for(int i=4; i<totalMin-2; i++)
		{
			D = 0.15*moveScoreMin[i-4]
				+0.15*moveScoreMin[i-3]
				+0.15*moveScoreMin[i-2]
				+0.08*moveScoreMin[i-1]
				+0.21*moveScoreMin[i]
				+0.12*moveScoreMin[i+1]
				+0.13*moveScoreMin[i+2];
			
			//if(D<=1)
			if(D<=1.25)
				stateSleepMin[i-4] = true;
			else
				stateSleepMin[i-4] = false;
		}
		
		int numTenMin = (int) Math.floor(stateSleepMin.length/10);
		double[] stateList = new double[numTenMin];
		int s,e, sum;
		for(int i=0; i<numTenMin; i++)
		{
			s = i*10;
			e = s+9;
			sum=0;
			
			for(int j = s; j<=e; j++)
			{
				if(stateSleepMin[j]==true)
					sum++;
				else
					sum--;
			}
			
			stateList[i] = (double)sum/(double)10;
		}
		
		
		/**************** MOVE list ******************/
		numTenMin = (int) Math.floor(totalMin/10);
		int sTenMin;
		
		double[] moveTenMinList = new double[numTenMin];
		for(int i=0; i<numTenMin; i++)
			moveTenMinList[i] = 0;
		
		//group move event in min
		iterator = moveList.iterator();
		while (iterator.hasNext()) 
		{	
			se = iterator.next();
			
			sF = se.startFrame;
			eF = se.endFrame;
			
			sTenMin = (int) Math.ceil((double)sF/(double)framesPerMin/10);
			
			if(sTenMin>numTenMin)
				sTenMin = numTenMin;
			
			if(isLog)
			Log.e("moveTenMinList", sF+" "+numTenMin+" "+sTenMin+" "+framesPerMin);
			
			moveTenMinList[sTenMin-1] += se.duration;		
		}
		
		
		/**************** Snore list ******************/
		double[] snoreTenMinList = new double[numTenMin];
		for(int i=0; i<numTenMin; i++)
			snoreTenMinList[i] = 0;
		
		//group move event in min
		iterator = snoreList.iterator();
		while (iterator.hasNext()) 
		{	
			se = iterator.next();
			
			sF = se.startFrame;
			eF = se.endFrame;
			
			sTenMin = (int) Math.ceil((double)sF/(double)framesPerMin/10);
			
			if(sTenMin>numTenMin)
				sTenMin = numTenMin;
			
			if(isLog)
			Log.e("snoreTenMinList", sF+" "+numTenMin+" "+sTenMin+" "+framesPerMin);
			
			snoreTenMinList[sTenMin-1] += 1;		
		}
		
		
		/**************** Cough list ******************/
		double[] coughTenMinList = new double[numTenMin];
		for(int i=0; i<numTenMin; i++)
			coughTenMinList[i] = 0;
		
		//group move event in min
		iterator = coughList.iterator();
		while (iterator.hasNext()) 
		{	
			se = iterator.next();
			
			sF = se.startFrame;
			eF = se.endFrame;
			
			sTenMin = (int) Math.ceil((double)sF/(double)framesPerMin/10);
			
			if(sTenMin>numTenMin)
				sTenMin = numTenMin;
			
			if(isLog)
			Log.e("coughTenMinList", sF+" "+numTenMin+" "+sTenMin+" "+framesPerMin);
			
			coughTenMinList[sTenMin-1] += 1;		
		}
		
		
		//calculate bar value
		for(int i=0; i<numTenMin; i++)
		{
			moveTenMinList[i] = moveTenMinList[i]/100;
			if(moveTenMinList[i]>1)
				moveTenMinList[i] = 1;
			
			
			snoreTenMinList[i] = snoreTenMinList[i]/50;
			if(snoreTenMinList[i]>1)
				snoreTenMinList[i] = 1;
			else if(snoreTenMinList[i]<0.1) 
				snoreTenMinList[i] = 0;
			
			coughTenMinList[i] = coughTenMinList[i]/30;
			if(coughTenMinList[i]>1)
				coughTenMinList[i] = 1;
			else if(coughTenMinList[i]<0.1)
				coughTenMinList[i] = 0;
		}
		
		
		//set history data
		HistoryData.setLastNight(moveTenMinList, snoreTenMinList, coughTenMinList, stateList);
		

		
	}//EOF post process
	
	private void displayHomeScreen()
	{	
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_HOME;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	

	
	public void startMonitoring()
	{	
		//start recording and processing				
		recorder.startRecording();
		setProcessStatus(ProcessStatus.MONITORING);
		//	
		
		//show monitor screen
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_MONITOR;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
              
	}
	
	public void displayMonitorScreen()
	{	
		//show monitor screen
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_MONITOR;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
              
	}
	
	public void displayEventResult()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_RESULT_EVENT;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void displayIntroScreen()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_INTRO;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void displayPSQIScreen()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_PSQI;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void displayMoveEventResult()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_RESULT_EVENT_MOVE;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void displaySettingScreen()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_SETTING;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void displayConsentScreen()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_CONSENT;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void displayQuestionScreen()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_PSQI_QUESTION;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void displaySnoreEventResult()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_RESULT_EVENT_SNORE;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void displayGetupEventResult()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_RESULT_EVENT_GETUP;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void displayCoughEventResult()
	{
		//update UI thread using handler
		DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_RESULT_EVENT_COUGH;
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);
	}
	
	public void updateSoundMeter(int index)
	{
		//update sound meter in monitoring screen
		/*DisplayUpdateMsg dum = new DisplayUpdateMsg();
		dum.ds = DisplayStatus.SCREEN_MONITOR;
		dum.index = index;
		dum.data1 = 
		
		Message msg = Message.obtain(hParentUIHandler);
        msg.obj = dum;
        hParentUIHandler.sendMessage(msg);*/
	}

	private boolean estimateEnvNoise(short[] buf)
	{	
		boolean isLog = false;
		
		int num_samples = buf.length;
		int samplesPerFrame = SAMPLES_PER_FRAME/10;		
		int num_frames = num_samples/samplesPerFrame;
		
		double[] stdArr = new double[num_frames];		
		
		int s,e;	
		double std;
		
		double meanStd=0;
		double minStd = Double.MAX_VALUE;
		
		//calculate std for each frame in buffer
		for(int i=0; i<num_frames; i++)
		{
			s = i*samplesPerFrame;
			e = s+samplesPerFrame-1;
			
			std = MathOperation.CalStd(buf, s, e);
			stdArr[i] = std;
			
			if(std<minStd)
				minStd = std;
			
			meanStd += std;			
						
			//Log.e("STD"+i, Double.toString(stdArr[i]));
		}
		
		meanStd /= num_frames;
		
		//calculate normalized std
		for(int i=0; i<num_frames; i++)
		{
			stdArr[i] = (stdArr[i]-meanStd)/(meanStd-minStd);
		}
		
		double varNorStd = MathOperation.CalVarDouble(stdArr);
		
		
		
		if(varNorStd<=MAX_VAR_ENV_ESTIMATE)
		{
			envNoise.extractEnvFeatures(buf);
			
			if(isShowLog)
			{
				Log.e("ENV", "ENV NOISE #" + (frameCount-2*FRAMES_PER_READ) + "---" + Double.toString(varNorStd));
				envNoise.print();
			}
			
			return true;
		}
		else
		{
			Log.e("ENV", "NON ENV NOISE #" + (frameCount-2*FRAMES_PER_READ) + "---" + Double.toString(varNorStd));
			return false;
		}
			
		
	}
	
	
	
	private void AdmissionControl(short[] audioBuf, boolean[] flagBuf)
	{
		short[] sBuf = audioBuf;
		
		int num_samples = sBuf.length;

		int num_frames = num_samples/SAMPLES_PER_FRAME;
		
		short[] maxArr = new short[num_frames];
		
		int s,e;
		
		double maxN;
		
		int frameStart = frameCount-num_frames;
		
		for(int i=0; i<num_frames; i++)
		{
			s = i*SAMPLES_PER_FRAME;
			e = s+SAMPLES_PER_FRAME-1;
			
			maxArr[i] = MathOperation.CalMax(sBuf, s, e);
			
			maxN = (double)maxArr[i]/(double)Short.MAX_VALUE;
			
			//Log.e("data"+curBufferInd, "maxN:"+maxN+"/th:"+acThreshold);
			
			/*if(maxN<acThreshold)
			{
				//environmental noise detected
				flagBuf[i] = false;
				//Log.e("FRAME:"+(frameStart+i), "NOISE~~~"+"maxN:"+maxN+"/th:"+acThreshold);
			}
			else
			{
				flagBuf[i] = true;
				//Log.e("FRAME:"+(frameStart+i), "non"+"maxN:"+maxN+"/th:"+acThreshold);
			}*/
				
		}
		
	}//EOF AdmissionControl()
	
	private LinkedList<EventArea> detectEventArea()
	{
		LinkedList<EventArea> lList = new LinkedList<EventArea>();
		
		//copy buffer arrays to global buffer
		boolean[] src = getPreviousFlagBuf(curBufferInd);
		int ind = 0;
		System.arraycopy(src, 0, globalFlagBuf, ind, FRAMES_PER_READ);		
		
		src = getCurFlagBuf();
		ind+=FRAMES_PER_READ;
		System.arraycopy(src, 0, globalFlagBuf, ind, FRAMES_PER_READ);	
		
		src = getNextFlagBuf(curBufferInd);
		ind+=FRAMES_PER_READ;
		System.arraycopy(src, 0, globalFlagBuf, ind, FRAMES_PER_READ);
			
		// !!! TEST ONLY
		//logBooleanArr(globalFlagBuf, 0, globalFlagBuf.length-1, "BEFORE->"+frameCount);
		//
		
		//conduct operations to find event area
		MathOperation.openningOperation(globalFlagBuf, 0);
		MathOperation.closingOperation(globalFlagBuf, 0);
		MathOperation.dilationOperation(globalFlagBuf, 0);
		
		//logBooleanArr(globalFlagBuf, 0, globalFlagBuf.length-1, "before->"+frameCount);
		
		//generate event area for the current focused buffer-read
		int curReadStart = FRAMES_PER_READ;
		int curReadEnd = 2*FRAMES_PER_READ-1;
		generateEventArea(lList, globalFlagBuf, curReadStart, curReadEnd);		
		
		//logEventArea(lList, "EVENT AREA");
		
		return lList;
	}//EOF detectEventArea()
	
	private void generateEventArea(LinkedList<EventArea> lList, boolean[] fbuf, int s, int e)
	{
		int areaHead = 0;
		int areaEnd = 0;
		
		int bufLen = fbuf.length;
		
		int i = s;
		
		if(fbuf[s]==true)
		{
			//part of the event area may be in previous buffer-read
			i = s;
			//search back
			while(true)
			{
				if(i-1>=0 && fbuf[i-1]==true)
					i--;
				else
					break;
			}
			areaHead = i;
			
			i = s;
			//search forward
			while(true)
			{
				if(i+1<bufLen && fbuf[i+1]==true)
					i++;
				else
					break;
			}
			areaEnd = i;
			
			//create and save event area
			if(MathOperation.isInCurrentArea(areaHead, areaEnd, s, e))
				addEventArea(lList, areaHead, areaEnd);
		
			i = areaEnd+1;
		}
		
		boolean isStartDetected = false;
		boolean cur, next;
		//search forward
		while(true)
		{
			//check end
			if(i+1>=bufLen)
			{
				if(i==bufLen-1)
				{
					if(fbuf[i]==true && isStartDetected)
					{
						areaEnd = i;
						
						if(MathOperation.isInCurrentArea(areaHead, areaEnd, s, e))
							addEventArea(lList, areaHead, areaEnd);
					}
					break;
				}
				break;
			}
			
			cur = fbuf[i];
			next = fbuf[i+1];
			if(cur==false && next==true)
			{
				isStartDetected = true;
				areaHead = i+1;
			}
			else if(cur==true && next==false && isStartDetected)
			{
				isStartDetected = false;
				areaEnd = i;
				
				if(MathOperation.isInCurrentArea(areaHead, areaEnd, s, e))
					addEventArea(lList, areaHead, areaEnd);
			}
			
			i++;
			
		}//EOF while
		
	}
	
	private void addEventArea(LinkedList<EventArea> lList, int head, int end)
	{
		int sf = frameCount-FRAMES_PER_READ*NUM_AUDIO_BUF;
		EventArea ea = new EventArea(sf+head, sf+end, head, end);
		lList.addLast(ea);
	}

	
	
	public void close()
	{
		_run = false;
	}
	
	
	class EnvNoise
	{
		//environment features
		double meanEng = 95;
		double stdEng = 13;
		double meanVar = 8000;
		double stdVar = 3000;
		double meanRLH = 17;
		double stdRLH = 4;
		
		double updateSpeed = 0.5;
		
		
		//event detection threshold
		
		double engMoveMax;
		double engMoveMin;
		double rlhMoveMax;
		double varMoveMin;
		
		double engSnoreMin;
		double rlhSnoreMin;
		double varSnoreMin;
		
		double engSnoreMin1;
		double rlhSnoreMin1;
		double varSnoreMin1;
		
		double engCougnMin;
		double rlhCoughMax;
		double varCougnMin;
		
		
		public EnvNoise()
		{
			engMoveMax = meanEng + 9*stdEng;
			engMoveMin = meanEng + 3*stdEng;
			rlhMoveMax = meanRLH + 2*stdRLH;
			varMoveMin = meanVar + 1*stdVar;
			
			engSnoreMin = meanEng + 5*stdEng;
			rlhSnoreMin = meanRLH + 1*stdRLH;
			varSnoreMin = meanVar + 2*stdVar;
			
			engSnoreMin1 = meanEng + 3*stdEng;
			rlhSnoreMin1 = meanRLH + 2*stdRLH;
			varSnoreMin1 = meanVar + stdVar;
			
			engCougnMin = meanEng + 200*stdEng;
			varCougnMin = meanVar + 150*stdVar;
			rlhCoughMax = meanRLH + stdRLH;
		}
		
		
		public void extractEnvFeatures(short[] buf)
		{
			int num_samples = buf.length;
			int samplesPerFrame = SAMPLES_PER_FRAME;		
			int num_frames = num_samples/samplesPerFrame;
			
			double[] varList = new double[num_frames];		
			double[] engList = new double[num_frames];		
			double[] rlhList = new double[num_frames];		
			
			double mnVar = 0;
			double mnRLH = 0;
			double mnEng = 0;
			
			double sdVar = 0;
			double sdRLH = 0;
			double sdEng = 0;
			
			int s,e;				
			double eng, rlh, var;
			
			//calculate env features for each env noise frame
			for(int i=0; i<num_frames; i++)
			{
				s = i*samplesPerFrame;
				e = s+samplesPerFrame-1;
				
				eng = MathOperation.getEnergy(buf, s, e);
				rlh = MathOperation.getRLH(buf, s, e);
				var = MathOperation.getVar(buf, s, e);
				
				engList[i] = eng;
				rlhList[i] = rlh;
				varList[i] = var;	
				
				mnEng+=eng;
				mnRLH+=rlh;
				mnVar+=var;
			}
			
			//get means
			mnEng/=num_frames;
			mnRLH/=num_frames;
			mnVar/=num_frames;
			
			//get std
			sdEng = MathOperation.CalStdDouble(engList)[1];
			sdRLH = MathOperation.CalStdDouble(rlhList)[1];
			sdVar = MathOperation.CalStdDouble(varList)[1];
			
			
			/************* update current env features ****************/
			meanEng = meanEng + updateSpeed*(mnEng-meanEng);
			meanVar = meanVar + updateSpeed*(mnVar-meanVar);
			meanRLH = meanRLH + updateSpeed*(mnRLH-meanRLH);
			stdEng = stdEng + updateSpeed*(sdEng-stdEng);
			stdVar = stdVar + updateSpeed*(sdVar-stdVar);
			stdRLH = stdRLH + updateSpeed*(sdRLH-stdRLH);
			
			
			/************* update detection thresholds ****************/
			engMoveMax = meanEng + 9*stdEng;
			engMoveMin = meanEng + 3*stdEng;
			rlhMoveMax = meanRLH + 2*stdRLH;
			varMoveMin = meanVar + 1*stdVar;
			
			engSnoreMin = meanEng + 5*stdEng;
			rlhSnoreMin = meanRLH + 1*stdRLH;
			varSnoreMin = meanVar + 2*stdVar;
			
			engSnoreMin1 = meanEng + 3*stdEng;
			rlhSnoreMin1 = meanRLH + 2*stdRLH;
			varSnoreMin1 = meanVar + stdVar;
			
			engCougnMin = meanEng + 200*stdEng;
			varCougnMin = meanVar + 150*stdVar;
			rlhCoughMax = meanRLH + stdRLH;
		}
		
		public void print()
		{
			System.out.print("ENG---"+"mean:"+meanEng+" std:"+stdEng+"\n");
			System.out.print("VAR---"+"mean:"+meanVar+" std:"+stdVar+"\n");
			System.out.print("RLH---"+"mean:"+meanRLH+" std:"+stdRLH+"\n");
		}
		
	}//EOF class EnvNoise
	
	
	
}
