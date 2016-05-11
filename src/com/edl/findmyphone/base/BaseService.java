package com.edl.findmyphone.base;



import android.app.Service;

import com.edl.findmyphone.ChatApplication;

public abstract class BaseService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		
		((ChatApplication)getApplication()).addService(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		((ChatApplication)getApplication()).removeService(this);
	}
}
