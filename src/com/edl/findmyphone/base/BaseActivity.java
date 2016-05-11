package com.edl.findmyphone.base;



import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.edl.findmyphone.ChatApplication;

public class BaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		((ChatApplication) getApplication()).addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		((ChatApplication) getApplication()).removeActivity(this);
	}
}
