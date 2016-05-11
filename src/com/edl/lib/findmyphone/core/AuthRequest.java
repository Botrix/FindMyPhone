package com.edl.lib.findmyphone.core;

import java.util.HashMap;
import java.util.Map;



import com.edl.lib.findmyphone.msg.SequenceCreater;
import com.google.gson.Gson;

public class AuthRequest extends ChatRequest {
	private String account;
	private String token;
    private String imei;
    
	private String sequence;

	public AuthRequest(String account, String token,String imei) {
		super(null, null);

		this.account = account;
		this.token = token;
        this.imei = imei;
		this.sequence = SequenceCreater.createSequence();
	}

	@Override
	public String getSequence() {
		return sequence;
	}

	@Override
	public String getTransport() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("action", "auth");
		map.put("type", "request");
		map.put("sequence", sequence);
		map.put("sender", account+imei);//发送者
		map.put("account", account);//用户账号
		map.put("token", token);
		return new Gson().toJson(map);
	}

}
