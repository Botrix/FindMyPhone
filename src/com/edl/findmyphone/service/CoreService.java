package com.edl.findmyphone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import com.edl.findmyphone.R;
import com.edl.findmyphone.action.Action;
import com.edl.findmyphone.base.BaseService;
import com.edl.findmyphone.db.AccountDao;
import com.edl.findmyphone.domain.Account;
import com.edl.findmyphone.lib.HMChatManager;
import com.edl.findmyphone.lib.HMChatManager.HMConnectListener;
import com.edl.findmyphone.lib.HMChatManager.OnPushListener;
import com.edl.findmyphone.uitls.CommonUtil;

import java.util.HashMap;
import java.util.Map;

public class CoreService extends BaseService implements HMConnectListener,
		OnPushListener {
	
	private HMChatManager chatManager;
	private AccountDao accountDao;

	private int reconnectCount = 0;// 重连次数

	private Map<String, Action> actionMaps = new HashMap<>();

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (CommonUtil.isNetConnected(CoreService.this)) {
					// 网络已经连接
					connectServer();
					Log.i("CoreService", ">>>BroadcastReceiver>>>onReceive()>>>connectServer()");
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("CoreService", "executing onCreate()");
		accountDao = new AccountDao(this);
		chatManager = HMChatManager.getInstance();
		chatManager.addConnectionListener(this);
		chatManager.setPushListener(this);

		// 注册网络监听
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);

		scanClass();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("Core", "onDestroy");

		chatManager.removeConnectionListener(this);
		unregisterReceiver(mReceiver);

		// 断开连接
		chatManager.closeSocket();
	}

	private void connectServer() {
		Account account = accountDao.getCurrentAccount();
		if (account != null) {
			chatManager.auth(account.getAccount(), account.getToken());

			// 后台服务xxx开启
			//startService(new Intent(this, xxxxx.class));
		}
	}

	@Override
	public void onConnecting() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected() {
		// TODO
		reconnectCount = 0;
	}

	@Override
	public void onDisconnected() {
		Log.d("Core", "onDisconnected");

		if (CommonUtil.isNetConnected(CoreService.this)) {
			// 有网络的
			Log.d("Core", "网络断开重连");
			reconnectCount++;

			if (reconnectCount < 10) {
				connectServer();
			}
		}
	}

	@Override
	public void onReconnecting() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuthFailed() {
		// TODO Auto-generated method stub

	}

	private void scanClass() {
	
		String[] array = getResources().getStringArray(R.array.actions);

		if (array == null) {
			return;
		}

		String packageName = getPackageName();
		ClassLoader classLoader = getClassLoader();

		for (int i = 0; i < array.length; i++) {
			try {

				Class<?> clazz = classLoader.loadClass(packageName + "."
						+ array[i]);

				Class<?> superclass = clazz.getSuperclass();

				if (superclass != null
						&& Action.class.getName().equals(superclass.getName())) {

					Action action = (Action) clazz.newInstance();
					actionMaps.put(action.getAction(), action);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onPush(String action, Map<String, Object> data) {
		Log.d("Core", "action : " + action + " data : " + data);

		boolean flag = false;
		Action actioner = actionMaps.get(action);
		if (actioner != null) {
			flag =actioner.doAction(this, data);
		}

		return flag;
	}
}
