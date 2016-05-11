package com.edl.findmyphone.action;

import java.util.Map;

import android.content.Context;

public class LockScreenAction extends Action {

	@Override
	public String getAction() {
		return "lockScreen";
	}

	@Override
	public void doAction(Context context, Map<String, Object> data) {
		if (data == null) {
			return;
		}

		String receiver = data.get("receiver").toString();
		String sender = data.get("sender").toString();

		String content = data.get("content").toString();


	}

}
