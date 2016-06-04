package com.edl.findmyphone.action;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.edl.findmyphone.db.AccountDao;
import com.edl.findmyphone.domain.Account;
import com.edl.findmyphone.lib.HMChatManager;
import com.edl.findmyphone.lib.HMURL;
import com.edl.findmyphone.service.CoreService;
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


		CoreService.locationService.registerListener(mListener);
		CoreService.locationService.start();

		String receiver = data.get("receiver").toString();
		String sender = data.get("sender").toString();
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

	/*****
	 *定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 */
	private BDLocationListener mListener = new BDLocationListener() {
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
	};

}
