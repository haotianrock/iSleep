package com.tian.sleep;

import android.os.AsyncTask;

public class AsyncFileUploader extends AsyncTask<FileUploader, Integer, Long>{

	@Override
	protected Long doInBackground(FileUploader... fu) {
		
		boolean isSent = false;
		
		if(fu.length>0)
		{
			try {
				fu[0].send();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		return null;
	}
	

}