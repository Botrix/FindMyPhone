package com.edl.findmyphone.action;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

import com.edl.findmyphone.receiver.MyAdmin;

import java.util.Map;

public class LockScreenAction extends Action {

	@Override
	public String getAction() {
		return "lockScreen";
	}

	@Override
	public boolean doAction(Context context, Map<String, Object> data) {
		if (data == null) {
			return false;
		}

		String receiver = data.get("receiver").toString();
		String sender = data.get("sender").toString();

		String password = data.get("content").toString();//锁屏密码

		//MainActivity.mainActivity.actionLockScreen(password);
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName adminComponent = new ComponentName(context, MyAdmin.class);

		boolean isAdminActive = devicePolicyManager.isAdminActive(adminComponent);
		if (isAdminActive) {
			devicePolicyManager.lockNow(); //锁屏
			devicePolicyManager.resetPassword(password, 0); //设置锁屏密码
		}
		/*else {
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "使用手机找回功能，需要激活设备管理器");
			context.startActivity(intent);
			devicePolicyManager.lockNow();
			devicePolicyManager.resetPassword(password, 0);
		}
*/
		return true;


	}

}
