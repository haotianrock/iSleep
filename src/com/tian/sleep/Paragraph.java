package com.tian.sleep;

import java.util.LinkedList;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class Paragraph 
{
	LinkedList<String> strList;
	int numLines = 0;
	
	public Paragraph (String line, Paint paint, int width)
	{
		strList = new LinkedList<String>();
		
		int numChar = 0;
		char lastChar, c;
		String subStr;
		
		while(true)
		{
			numChar = paint.breakText(line, true, width, null);
			
			if(numChar<=0)
				break;
			
			/********* do not break word **********/
			lastChar = line.charAt(numChar-1);		
			if(lastChar!=' ' && numChar != line.length())
			{
				if(line.charAt(numChar)!=' ')
				{
					//move the word to the next line
					int i = numChar-1;
					while(true)
					{
						c = line.charAt(i);
						if(c!=' ')
							i--;
						else
							break;
					}
					numChar = c+1;
				}
			}
			
			
			subStr = line.substring(0,numChar-1);
			
			//
			//Log.e("STR"+numChar, subStr);
			//
			
			strList.add(subStr); 
			
			line = line.substring(numChar, line.length());			
		}
		
		numLines = strList.size();
	}
}
