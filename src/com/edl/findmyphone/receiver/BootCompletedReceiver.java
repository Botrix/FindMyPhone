package com.edl.findmyphone.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.edl.findmyphone.service.CoreService;
import com.edl.findmyphone.uitls.CommonUtil;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// 开机启动服务
		if (!CommonUtil.isServiceRunning(context, CoreService.class)) {
			 context.startService(new Intent(context, CoreService.class));
		}

		/*
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.d("BootCompleted", "检测到开机启动，去启动服务");
			Intent newIntent = new Intent(context, CoreService.class);
			context.startService(newIntent);
		}
*/
	}
}