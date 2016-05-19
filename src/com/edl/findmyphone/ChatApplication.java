package com.edl.findmyphone;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Process;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.edl.findmyphone.db.AccountDao;
import com.edl.findmyphone.domain.Account;
import com.edl.findmyphone.lib.HMChat;
import com.edl.findmyphone.service.LocationService;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ChatApplication extends Application {
	private List<Activity> activitys = new LinkedList<>();
	private List<Service> services = new LinkedList<>();
	private Account account;
	private int pid;

	public LocationService locationService;
	public Vibrator mVibrator;

	@Override
	public void onCreate() {
		super.onCreate();
		pid = Process.myPid();
		
		Log.d("ChatApplication", "init");
		// 初始化
		HMChat.getInstance().init(this);


		/***
		 * 初始化定位sdk，建议在Application中创建
		 */
		locationService = new LocationService(getApplicationContext());
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
		SDKInitializer.initialize(getApplicationContext());
		Log.i("sdk", "Baidu Location SDK init");
	}

	public void addActivity(Activity activity) {
		activitys.add(activity);
	}

	public void removeActivity(Activity activity) {
		activitys.remove(activity);
	}

	public void addService(Service service) {
		services.add(service);
	}

	public void removeService(Service service) {
		services.remove(service);
	}

	public void closeApplication() {
		closeActivitys();
		closeServices();
//		Process.killProcess(pid);
	}

	private void closeActivitys() {
		ListIterator<Activity> iterator = activitys.listIterator();
		while (iterator.hasNext()) {
			Activity activity = iterator.next();
			if (activity != null) {
				activity.finish();
			}
		}
	}

	private void closeServices() {
		ListIterator<Service> iterator = services.listIterator();
		while (iterator.hasNext()) {
			Service service = iterator.next();
			if (service != null) {
				stopService(new Intent(this, service.getClass()));
			}
		}
	}

	public Account getCurrentAccount() {
		if (account == null) {
			AccountDao dao = new AccountDao(this);
			account = dao.getCurrentAccount();
		}
		return account;
	}

}
