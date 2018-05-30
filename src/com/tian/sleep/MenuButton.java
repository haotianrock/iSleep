package com.tian.sleep;

import java.util.Iterator;
import java.util.LinkedList;

import com.tian.sleep.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class MenuButton
{
	LinkedList<MyButton> btnList;
	
	ButtonID bid = ButtonID.MENU;
	
	Context context;
	
	
	/****** layout information *******/
	MenuLayout mLayout;
	Rect area;
	int hBtn, wBtn, sizeMenuBtn;
	int vMargin;	//vertical margins btw btns
	
	
	/********* frames of menu btn ***********/
	BitmapDrawable[] bdMenu = null;
	Rect menuBtnSrc;
	Rect menuBtnLoc;
	final static int[] menuBtnId = {R.drawable.menu_0,
							    	R.drawable.menu_1,
							    	R.drawable.menu_2,
							    	R.drawable.menu_3,
							    	R.drawable.menu_4,
							    	R.drawable.menu_5,
							    	R.drawable.menu_6,
							    	R.drawable.menu_7,
							    	R.drawable.menu_8,
							    	R.drawable.menu_9,
							    	R.drawable.menu_10,
							    	R.drawable.menu_11,
							    	R.drawable.menu_12,
							    	R.drawable.menu_13};
	
	
	/********** frame counter *********/
	int curFrame = 0;
	int numFrames = menuBtnId.length;
	
	
	
	public MenuButton (Context c, MenuLayout ml)
	{
		context = c;
		
		mLayout = ml;
		area = ml.area;		
		hBtn = ml.hBtn;
		wBtn = ml.wBtn;
		vMargin = ml.vSpcBtn;
		sizeMenuBtn = ml.sizeMenuBtn;

		
		btnList = new LinkedList<MyButton>();
		
    	bdMenu = new BitmapDrawable[numFrames];
    	
    	
    	//load menu btn imgs
    	for(int i=0; i<numFrames; i++)
    	{
    		bdMenu[i] = (BitmapDrawable) context.getResources().getDrawable(menuBtnId[i]);
    	}
    	
		menuBtnSrc = new Rect(0, 0, bdMenu[0].getIntrinsicWidth(), bdMenu[0].getIntrinsicHeight());
		menuBtnLoc = new Rect(area.right-sizeMenuBtn, area.top, area.right, area.top+sizeMenuBtn);
    	
	}
	

	
	/**
	 * add a button under this menu, the location info will be initialized in initializeAnimation()
	 * @param bid
	 * @param bdBtn
	 * @param bdBtnHit
	 */
	public void addButton (ButtonID bid, BitmapDrawable bdBtn, BitmapDrawable bdBtnHit)
	{
		//the location will be initialize in initializeAnimation()
		MyButton btn = new MyButton(bid, new Rect());
		btn.setImage(bdBtn, bdBtnHit);
		
		btn.rLocations = new Rect[numFrames];
		btn.rSrc = new Rect[numFrames];
		
		for(int i=0; i<numFrames; i++)
		{
			btn.rLocations[i] = new Rect();
			btn.rSrc[i] = new Rect();
		}
		
		btnList.add(btn);
	}
	
	
	/**
	 * initialize the trajectory and location of the expanding and retrieving animation
	 * called after finishing adding buttons.
	 */
	public void initializeAnimation ()
	{
		/*************** get the number of visible btns ****************/
		int numBtns = 0;
		MyButton btn;
		Iterator<MyButton> iterator = btnList.iterator();
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			if(btn.isVisible == true)
			{
				btn.menuIndex = numBtns;
				numBtns++;
			}
		}
		
		
		/************** Calculate the final location of each btn **************/	
		iterator = btnList.iterator();
		int btnInd = -1;
		float ySpeed, xSpeed;
		Rect loc = new Rect();
		Rect preLoc = new Rect();
				
		//the area where the buttons stop when the animation is over
		int spc = mLayout.vSpcMB; //the vertical space between menu btn and the first submenu btn
		Rect rFinalBtnArea =  new Rect(area.left, menuBtnLoc.bottom+spc, 
										area.right, area.bottom);
		
		//calculate x-axis speed
		xSpeed = (numBtns+1)*(wBtn/2)/(numFrames-1);
		
	
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			
			//skip invisible btns
			if(!btn.isVisible)
				continue;
			
			btnInd = btn.menuIndex;	//the index of the current button, indicating its order in menu list
			
			//final location
			btn.area.left = rFinalBtnArea.left;
			btn.area.right = rFinalBtnArea.right;
			btn.area.top = btnInd*hBtn + vMargin*btnInd + rFinalBtnArea.top;
			btn.area.bottom = btn.area.top + hBtn;
			
			//start location
			btn.rLocations[0].left = rFinalBtnArea.right + (wBtn/2)*btnInd;
			btn.rLocations[0].right = btn.rLocations[0].left + wBtn;
			btn.rLocations[0].top = btn.area.top;
			btn.rLocations[0].bottom = btn.area.bottom;
			
			preLoc.set(btn.rLocations[0]);
			
			//not shown in the first frame
			btn.rSrc[0].set(0,0,0,0);
			
			//fill in locations of this button in between
			for(int i=1; i<numFrames; i++)
			{
				loc.set(preLoc);
				
				//move until reach final location
				if(loc.left >= rFinalBtnArea.left+xSpeed)
				{
					loc.left -= xSpeed;
					loc.right -= xSpeed;
					
					if(i==numFrames-1)
						loc.set(btn.area);	//reached final location
				}
				else
					loc.set(btn.area);	//reached final location

				
				preLoc.set(loc);	//save location of last frame
				
				//check if it is currently visible in menu area
				if(loc.intersect(area))
				{
					btn.rLocations[i].set(loc);
					
					//only display a portion
					btn.rSrc[i].set(btn.src);
					btn.rSrc[i].right = (int)(((double)loc.width()/(double)wBtn)*(double)btn.src.width());
				}
				else
					btn.rSrc[i].set(0,0,0,0);
				
				//
				//Log.e("index: "+i, btn.rSrc[i].toString());
				//
			}
		}//EOF while()
		
	}
	
	
	/**
	 * paint the current frame of menu animation, according to the curFrame;
	 * @param cvs
	 */
	public void paintMenu(Canvas cvs, int frameIndex)
	{
		//draw menu btn
		cvs.drawBitmap(bdMenu[frameIndex].getBitmap(), menuBtnSrc, menuBtnLoc, null);
		
		//draw sub-btns
		MyButton btn;
		Iterator<MyButton> iterator = btnList.iterator();
		while (iterator.hasNext()) 
		{	
			iterator.next().paintButton(cvs, frameIndex);			
		}
	}
	
	
	/**
	 * get the number of buttons that is currently visible
	 * used for allocating layout
	 * @return
	 */
	public int getVisibleBtnsNum()
	{
		/*************** get the number of visible btns ****************/
		int numBtns = 0;
		MyButton btn;
		Iterator<MyButton> iterator = btnList.iterator();
		while (iterator.hasNext()) 
		{	
			btn = iterator.next();
			if(btn.isVisible == true)			
				numBtns++;			
		}
		
		return numBtns;
	}
}
