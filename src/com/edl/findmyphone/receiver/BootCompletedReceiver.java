package com.edl.findmyphone.receiver;


import com.edl.findmyphone.service.CoreService;
import com.edl.findmyphone.uitls.CommonUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// 开机启动服务
		if (!CommonUtil.isServiceRunning(context, CoreService.class)) {
			 context.startService(new Intent(context, CoreService.class));
		}
	}
}