package com.edl.findmyphone.action;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

import com.edl.findmyphone.receiver.MyAdmin;

import java.util.Map;

public class ClearDataAction extends Action {

	@Override
	public String getAction() {
		return "clearData";
	}

	@Override
	public boolean doAction(Context context, Map<String, Object> data) {
		if (data == null) {
			return false;
		}

		String receiver = data.get("receiver").toString();
		String sender = data.get("sender").toString();


		//MainActivity.mainActivity.actionClearData();
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName adminComponent = new ComponentName(context, MyAdmin.class);
		boolean isAdminActive = devicePolicyManager.isAdminActive(adminComponent);

		if (isAdminActive) {
			devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
			devicePolicyManager.wipeData(0);  //恢复出厂设置

		}

       return true;

	}

}
