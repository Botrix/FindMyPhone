package com.edl.findmyphone.action;

import android.content.Context;

import java.util.Map;

public class ShowMessageAction extends Action {

	@Override
	public String getAction() {
		return "showMessage";
	}

	@Override
	public boolean doAction(Context context, Map<String, Object> data) {
		if (data == null) {
			return false;
		}

		String receiver = data.get("receiver").toString();
		String sender = data.get("sender").toString();
		String content = data.get("content").toString();
		boolean ring = (boolean) data.get("ring");

		return true;

	}

}
