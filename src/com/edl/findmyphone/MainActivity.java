package com.edl.findmyphone;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.edl.findmyphone.action.LocationAction;
import com.edl.findmyphone.base.BaseActivity;
import com.edl.findmyphone.fragment.FindFra;
import com.edl.findmyphone.fragment.MainFra;
import com.edl.findmyphone.fragment.SettingsFra;
import com.edl.findmyphone.receiver.MyAdmin;
import com.edl.findmyphone.service.LocationService;
import com.edl.findmyphone.widget.NormalTopBar;

public class MainActivity extends BaseActivity {
	public static MainActivity mainActivity;
	public LocationService locationService;

	private String locationInfo;
	NormalTopBar mTopBar;

	FragmentManager fm;
	FindFra findFra;
	SettingsFra settingsFra;


	DevicePolicyManager devicePolicyManager;
	ComponentName adminComponent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fra_container);

		mainActivity = this;

		/*mTopBar = (NormalTopBar) findViewById(R.id.topbar);
		mTopBar.setVisibility(View.VISIBLE);
		mTopBar.setTitle("手机找回");
		mTopBar.setBackVisibility(false);*/

		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminComponent = new ComponentName(this, MyAdmin.class);

		/*//----------仅用于测试----------------
		updateUI();*/


		MainFra mainFra = new MainFra();

		fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.container_fra, mainFra);
		transaction.commit();
	}

/*

	private void updateUI() {
		final TextView locaView = (TextView) findViewById(R.id.locInfoView);
		final Handler handler= new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (locationInfo != null) {
					locaView.setText(locationInfo);
				}

				//String distanceMeterStr = String.format("%1$,.2f meters", distance);

				handler.postDelayed(this, 1000);
			}
		});
	}
*/


	/***
	 * Stop location service
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		locationService.unregisterListener(mListener); //注销掉监听
		locationService.stop(); //停止定位服务
		super.onStop();
	}
	@Override
	protected void onStart() {
		super.onStart();
		// -----------location config ------------
		locationService = ((ChatApplication) getApplication()).locationService;
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		locationService.registerListener(mListener);
		locationService.start();
	}
	/*****
	 *定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 */
	private BDLocationListener mListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				locationInfo = "手机定位："+location.getLatitude() + "---" + location.getLongitude();
				System.out.println(locationInfo);

				LocationAction.lat = String.valueOf(location.getLatitude());
				LocationAction.lng = String.valueOf(location.getLongitude());

			}
		}
	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void onStartSettingsFra() {
		if (settingsFra == null) {
			settingsFra = new SettingsFra();
		}
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.container_fra, settingsFra);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void onStartFindFra() {
		if (findFra == null) {
			findFra = new FindFra();
		}
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.container_fra, findFra);
		transaction.addToBackStack(null);
		transaction.commit();
	}

/*****************************************************************

 * 手机找回
 * 锁屏和清除数据

/////////////////////////////////////////////////////////////////*/


	public void actionClearData() {
		/*// 设备安全管理服务
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		// 申请权限
		ComponentName componentName = new ComponentName(this, MyAdmin.class);*/
		// 判断该组件是否有系统管理员的权限
		boolean isAdminActive = devicePolicyManager.isAdminActive(adminComponent);
		if (isAdminActive) {
			devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
			devicePolicyManager.wipeData(0);  //恢复出厂设置

		} else {
			activeDeviceManager();
		}
	}

	public void actionLockScreen(String password) {
		/*// 设备安全管理服务
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		// 申请权限
		ComponentName componentName = new ComponentName(this, MyAdmin.class);
		// 判断该组件是否有系统管理员的权限*/
		boolean isAdminActive = devicePolicyManager.isAdminActive(adminComponent);
		if (isAdminActive) {
			devicePolicyManager.lockNow(); //锁屏
			devicePolicyManager.resetPassword(password, 0); //设置锁屏密码
		} else {
			activeDeviceManager();
			devicePolicyManager.lockNow();
			devicePolicyManager.resetPassword(password, 0);
		}
	}

	private void activeDeviceManager() {
		//使用隐式意图调用系统方法来激活指定的设备管理器
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		// 指定动作名称
		//intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 指定给哪个组件授权
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
		//intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后可锁屏");
		startActivity(intent);

	}
}
