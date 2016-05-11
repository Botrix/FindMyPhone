package com.edl.lib.findmyphone.core;

import java.util.HashMap;
import java.util.Map;



import android.util.Log;

import com.edl.lib.findmyphone.callback.HMChatCallBack;
import com.edl.lib.findmyphone.msg.ChatMessage;
import com.google.gson.Gson;

public class ChatRequest {
	private HMChatCallBack callBack;
	private ChatMessage message;

	private String sequence;
	private Map<String, Object> map;

	public ChatRequest(HMChatCallBack callBack, ChatMessage message) {
		super();
		this.callBack = callBack;
		this.message = message;

		map = new HashMap<String, Object>();
		if (message != null) {
			map.putAll(this.message.getMap());
			sequence = (String) map.get("sequence");
		}
	}

	public String getSequence() {
		return sequence;
	}

	public String getTransport() {
		Log.d("", "" + map.toString());
		
		return new Gson().toJson(map);
	}

	public HMChatCallBack getCallBack() {
		return callBack;
	}

	public void setAccount(String account) {
		if (map != null) {
			map.put("account", account);
		}
	}

	public void setToken(String token) {
		if (map != null) {
			map.put("token", token);
		}
	}
}
