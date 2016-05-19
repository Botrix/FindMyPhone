package com.edl.findmyphone.action;

import android.content.Context;

import com.edl.findmyphone.MainActivity;

import java.util.Map;

public class ClearDataAction extends Action {

	@Override
	public String getAction() {
		return "clearData";
	}

	@Override
	public boolean doAction(Context context, Map<String, Object> data) {
		if (data == null) {
			return false;
		}

		String receiver = data.get("receiver").toString();
		String sender = data.get("sender").toString();

		MainActivity.mainActivity.actionClearData();

       return true;

	}

}
