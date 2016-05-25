package com.edl.findmyphone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectedReceiver extends BroadcastReceiver {
	public static String Action = "android.net.conn.CONNECTIVITY_CHANGE";

	@Override
	public void onReceive(Context context, Intent intent) {

		/*if (intent.getAction().equals(Action) && CommonUtil.isNetConnected(context)) {
			Intent i = new Intent(context, CoreService.class);
			context.startService(i);
		}
		Log.i("ConnectedReceive", "start CoreService");*/
	}

}
