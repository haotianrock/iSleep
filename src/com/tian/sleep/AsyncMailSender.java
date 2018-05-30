package com.tian.sleep;

import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

public class AsyncMailSender extends AsyncTask<Mail, Integer, Long>{

	@Override
	protected Long doInBackground(Mail... m) {
		
		boolean isSent = false;
		
		if(m.length>0)
		{
			try {
				isSent = m[0].send();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		return null;
	}
	

}
