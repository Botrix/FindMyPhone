package com.edl.findmyphone.action;

import android.content.Context;
import android.util.Log;

import com.edl.findmyphone.db.AccountDao;
import com.edl.findmyphone.domain.Account;
import com.edl.findmyphone.lib.HMChatManager;
import com.edl.findmyphone.lib.HMURL;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.Map;

public class LocationAction extends Action {
	public static String lat;
	public static String lng;


	@Override
	public String getAction() {
		return "location";
	}

	@Override
	public boolean doAction(final Context context, Map<String, Object> data) {
		if (data == null) {
			return false;
		}




		/*String receiver = data.get("receiver").toString();
		String sender = data.get("sender").toString(); */
		AccountDao dao = new AccountDao(context);
		final Account account = dao.getCurrentAccount();

		HMChatManager chatManager = HMChatManager.getInstance();


		RequestParams params = new RequestParams();
		params.addBodyParameter("lat", lat);
		params.addBodyParameter("lng", lng);
		params.addBodyParameter("account", account.getAccount());
		params.addBodyParameter("deviceImei", chatManager.imei);

		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpRequest.HttpMethod.POST,
				HMURL.URL_HTTP_LOCUPLOAD, params, new RequestCallBack<String>() {


					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						Log.i("LocationAction", "上传位置信息成功>>>>");

					}

					@Override
					public void onFailure(com.lidroid.xutils.exception.HttpException e, String s) {

					}
				});

       return true;

	}

}
