package com.edl.findmyphone.action;

import android.content.Context;

import com.edl.findmyphone.MainActivity;

import java.util.Map;

public class LockScreenAction extends Action {

	@Override
	public String getAction() {
		return "lockScreen";
	}

	@Override
	public boolean doAction(Context context, Map<String, Object> data) {
		if (data == null) {
			return false;
		}

		String receiver = data.get("receiver").toString();
		String sender = data.get("sender").toString();

		String password = data.get("content").toString();//锁屏密码

		MainActivity.mainActivity.actionLockScreen(password);

       return true;

	}

}
