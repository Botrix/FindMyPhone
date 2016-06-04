package com.edl.findmyphone.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import com.edl.findmyphone.ChatApplication;
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

public class CoreService extends BaseService implements HMConnectListener, OnPushListener {

	public static LocationService locationService;
	public static String lat;
	public static String lng;

	private static HMChatManager chatManager;
	private static AccountDao accountDao;

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
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Make this service run in the foreground
		/*Notification notification = new Notification();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;

		startForeground(1, notification);*/

		//startForeground(-1213, new Notification());



		return START_STICKY;
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


		locationService = ((ChatApplication) getApplication()).locationService;
		//locationService.registerListener(mListener);

		int pid = Process.myPid();
		Log.i("Process-pid", String.valueOf(pid));

		scanClass();

/*

		//获取AlarmManager系统服务
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		//包装需要执行Service的Intent
		Intent i = new Intent(getApplicationContext(), CoreService.class);
		//intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0,
				i, PendingIntent.FLAG_UPDATE_CURRENT);

		//触发服务的起始时间
		long triggerAtTime = SystemClock.elapsedRealtime();

		//使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,
				60 * 1000, pendingIntent);
				*/
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

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
		restartServiceIntent.setPackage(getPackageName());

		PendingIntent restartServicePendingIntent = PendingIntent
				.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager) getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);
		alarmService.set(
				AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + 2000,
				restartServicePendingIntent);

		super.onTaskRemoved(rootIntent);
	}

	public static void connectServer() {
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
			flag = actioner.doAction(this, data);
		}

		return flag;
	}




	/*****
	 *定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 */
/*	private BDLocationListener mListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				String locationInfo = "手机定位："+location.getLatitude() + "---" + location.getLongitude();
				System.out.println(locationInfo);

				lat = String.valueOf(location.getLatitude());
				lng = String.valueOf(location.getLongitude());

			}
		}
	};*/
}
