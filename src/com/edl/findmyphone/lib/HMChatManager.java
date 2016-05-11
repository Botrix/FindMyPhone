package com.edl.findmyphone.lib;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.http.Header;
import org.apache.mina.core.session.IoSession;

import android.content.Context;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.edl.lib.findmyphone.callback.HMChatCallBack;
import com.edl.lib.findmyphone.callback.HMObjectCallBack;
import com.edl.lib.findmyphone.core.AuthRequest;
import com.edl.lib.findmyphone.core.ChatRequest;
import com.edl.lib.findmyphone.core.PacketConnector;
import com.edl.lib.findmyphone.core.PacketConnector.ConnectListener;
import com.edl.lib.findmyphone.core.PacketConnector.IOListener;
import com.edl.lib.findmyphone.future.HttpFuture;
import com.edl.lib.findmyphone.msg.ChatMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;


public class HMChatManager {
	private static HMChatManager instance;
	private Context context;
	private Map<String, String> headers = new HashMap<String, String>();

	private String authSequence;
	private PacketConnector connector;

	private List<HMConnectListener> connectListeners = new LinkedList<HMConnectListener>();

	private OnPushListener pushListener;

	private Map<String, ChatRequest> requests = new LinkedHashMap<String, ChatRequest>();

	private Thread mainThread;
	private Handler handler = new Handler();
	
	//手机imei唯一标识
	String imei;
	//手机型号
    String devicename;
	public static HMChatManager getInstance() {
		if (instance == null) {
			synchronized (HMChatManager.class) {
				if (instance == null) {
					instance = new HMChatManager();
				}
			}
		}
		return instance;
	}

	private HMChatManager() {
		context = HMChat.getContext();
		mainThread = Thread.currentThread();
		getDeviceInfo();
	}

	/**
	 * 初始化连接用户的安全信息
	 * 
	 * @param account
	 * @param token
	 */
	public void initAccount(String account, String token) {
		headers.put("account", account);
		headers.put("token", token);
		headers.put("imei", imei);
	}
	
	/**
	 * 获取设备imei和设备名称
	 * 
	 */
	public void getDeviceInfo(){		
		TelephonyManager  mTelephonyMgr  = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		imei = mTelephonyMgr.getDeviceId();
		devicename=android.os.Build.MODEL;
	}
	
	//----------------------------http请求部分------------------------------

	/**
	 * 登录
	 * 
	 * @param account
	 * @param password
	 * @param callBack
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public HMFuture login(String account, String password,
			final HMObjectCallBack callBack) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(30 * 1000);
		client.setMaxRetriesAndTimeout(5, 30 * 1000);
		client.setResponseTimeout(30 * 1000);
		String url = HMURL.URL_HTTP_LOGIN;
		RequestParams params = new RequestParams();
		params.put("account", account);
		params.put("password", password);
		params.put("imei", imei);
		params.put("devicename", devicename);

		return new HttpFuture(client.post(context, url, params,
				newObjectResponseHandler(callBack)));
	}

	/**
	 * 注册
	 * 
	 * @param account
	 * @param password
	 * @param callBack
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public HttpFuture register(String account, String password,
			final HMObjectCallBack callBack) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(30 * 1000);
		client.setMaxRetriesAndTimeout(5, 30 * 1000);
		client.setResponseTimeout(30 * 1000);
		String url = HMURL.URL_HTTP_REGISTER;
		RequestParams params = new RequestParams();
		params.put("account", account);
		params.put("password", password);

		return new HttpFuture(client.post(context, url, params,
				newObjectResponseHandler(callBack)));
	}

	




	@SuppressWarnings("rawtypes")
	private TextHttpResponseHandler newObjectResponseHandler(
			final HMObjectCallBack callBack) {
		return new TextHttpResponseHandler("utf-8") {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String responseString) {
				Log.d("###", "" + responseString);

				if (statusCode == 200) {
					JsonParser parser = new JsonParser();
					JsonObject root = parser.parse(responseString)
							.getAsJsonObject();
					if (root == null) {
						if (callBack != null) {
							callBack.onError(HMError.ERROR_SERVER, "服务器异常");
						}
					} else {
						if (callBack != null) {
							JsonPrimitive flagObj = root
									.getAsJsonPrimitive("flag");
							boolean flag = flagObj.getAsBoolean();
							if (flag) {
								JsonObject dataObj = root
										.getAsJsonObject("data");

								if (dataObj == null) {
									callBack.onSuccess(null);
								} else {
									Object data = new Gson().fromJson(dataObj,
											callBack.getClazz());
									callBack.onSuccess(data);
								}

							} else {
								// 如果返回错误
								// 获得错误code
								JsonPrimitive errorCodeObj = root
										.getAsJsonPrimitive("errorCode");
								// 获得错误string
								JsonPrimitive errorStringObj = root
										.getAsJsonPrimitive("errorString");

								int errorCode = errorCodeObj.getAsInt();
								String errorString = errorStringObj
										.getAsString();

								callBack.onError(errorCode, errorString);
							}
						}
					}
				} else {
					if (callBack != null) {
						callBack.onError(HMError.ERROR_SERVER, "网络异常");
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				if (callBack != null) {
					callBack.onError(HMError.ERROR_SERVER, "网络异常 : "
							+ throwable.getMessage());
				}
			}
		};
	}
	
	
	
	//----------------------------socket请求部分----------------------------

	/**
	 * socket 连接认证
	 * 
	 * @param account
	 * @param token
	 */
	public void auth(final String account, final String token) {
		headers.put("account", account);
		headers.put("token", token);
		headers.put("imei", imei);
		new Thread() {
			public void run() {
				AuthRequest request = new AuthRequest(account,token,imei);

				if (connector == null) {
					connector = new PacketConnector(HMURL.BASE_HM_HOST,
							HMURL.BASE_HM_PORT, 3);
				}

				conncectChatServer();//连接服务器

				authSequence = request.getSequence();//获取连接序列号

				connector.addRequest(request);//往requestQueue队列添加请求

			};
		}.start();
	}

	public void closeSocket() {
		if (connector != null && connector.isConnected()) {
			connector.disconnect();
			connector = null;
		}
	}

	private void conncectChatServer() {
		if (connector != null) {
			connector.connect();//连接服务器
			// 设置输入输出监听
			connector.setIOListener(ioListener);
			// 设置Mina连接监听
			connector.setConnectListener(connectListener);
		}
	}
	
	
	
	
	

	/**
	 * 添加连接服务器监听
	 * 
	 * @param listener
	 */
	public void addConnectionListener(HMConnectListener listener) {
		if (!connectListeners.contains(listener)) {
			connectListeners.add(listener);
		}
	}

	/**
	 * 移除连接服务器监听
	 * 
	 * @param listener
	 */
	public void removeConnectionListener(HMConnectListener listener) {
		if (connectListeners.contains(listener)) {
			connectListeners.remove(listener);
		}
	}

	/**
	 * 添加服务器消息推送监听
	 * 
	 * @param listener
	 */
	public void setPushListener(OnPushListener listener) {
		this.pushListener = listener;
	}
	

	/**
	 * 发送消息
	 * 
	 * @param message
	 * @param callBack
	 */
	public void sendMessage(final ChatMessage message,
			final HMChatCallBack callBack) {
		new Thread() {
			public void run() {
				if (connector == null) {
					connector = new PacketConnector(HMURL.BASE_HM_HOST,
							HMURL.BASE_HM_PORT, 3);
				}

				conncectChatServer();

				addRequest(message, callBack);
			}
		}.start();
	}
	
	
   
	private void addRequest(final ChatMessage message,
			final HMChatCallBack callBack) {
		// 加入到请求map中 为以后的response做处理
		ChatRequest request = new ChatRequest(callBack, message);
		requests.put(request.getSequence(), request);

		connector.addRequest(request);
	}

	
	//设置输入输出监听
	private IOListener ioListener = new IOListener() {

		@Override
		public void onOutputFailed(ChatRequest request, Exception e) {
			// 消息发送失败，通知回调，让显示层做处理
			HMChatCallBack callBack = request.getCallBack();
			if (callBack != null) {
				callBack.onError(HMError.ERROR_CLIENT_NET, "客户端网络未连接");
			}
		}

		
		//有消息进来
		@SuppressWarnings("unchecked")
		@Override
		public void onInputComed(IoSession session, Object message) {
			if (message instanceof String) {
				String json = ((String) message).trim();

				JsonParser parser = new JsonParser();
				JsonObject root = parser.parse(json).getAsJsonObject();

				// 获得方向:是请求还是response
				JsonPrimitive typeJson = root.getAsJsonPrimitive("type");
				String type = typeJson.getAsString();

				// 获得序列号
				JsonPrimitive sequenceJson = root
						.getAsJsonPrimitive("sequence");
				String sequence = sequenceJson.getAsString();

				if ("request".equalsIgnoreCase(type)) {// 服务器推送消息
					JsonPrimitive actionJson = root
							.getAsJsonPrimitive("action");
					String action = actionJson.getAsString();
					
					if (pushListener != null) {//处理服务器推送的消息
						
						boolean pushed = pushListener.onPush(action,
								(Map<String, Object>) new Gson().fromJson(root,
										new TypeToken<Map<String, Object>>() {
										}.getType()));
						
						if (pushed) {//处理成功，返回返回服务器
							
							session.write("{type:'response',sequence:'"
									+ sequence + "',flag:" + true + "}");
							
						} else {////处理失败，返回返回服务器
							
							session.write("{type:'response',sequence:'"
									+ sequence + "',flag:" + false
									+ ",errorCode:1,errorString:'客户端未处理成功!'}");
							
						}
					}
				} else if ("response".equalsIgnoreCase(type)) {//服务器回应
					// 请求返回response
					JsonPrimitive flagJson = root.getAsJsonPrimitive("flag");
					boolean flag = flagJson.getAsBoolean();
					// 消息发送结果只有 成功或者 失败,不需要返回对象
					if (flag) {
						if (sequence.equals(authSequence)) {
							Log.d("#####", "认证成功");
							return;
						}

						// 消息成功发送
						ChatRequest request = requests.remove(sequence);
						if (request != null) {
							final HMChatCallBack callBack = request
									.getCallBack();
							if (callBack != null) {
								// 在主线程中调用
								if (Thread.currentThread() != mainThread) {
									handler.post(new Runnable() {
										@Override
										public void run() {
											callBack.onSuccess();
										}
									});
								} else {
									callBack.onSuccess();
								}
							}
						}
					} else {
						if (sequence.equals(authSequence)) {
							Log.d("#####", "认证失败");
							return;
						}

						// 认证失败
						ListIterator<HMConnectListener> iterator = connectListeners
								.listIterator();
						while (iterator.hasNext()) {
							final HMConnectListener listener = iterator.next();
							// 在主线程中调用
							if (Thread.currentThread() != mainThread) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										listener.onAuthFailed();
									}
								});
							} else {
								listener.onAuthFailed();
							}
						}
					}
				}
			}
		}
	};

	private ConnectListener connectListener = new ConnectListener() {

		@Override
		public void onReConnecting() {
			ListIterator<HMConnectListener> iterator = connectListeners
					.listIterator();
			while (iterator.hasNext()) {
				HMConnectListener listener = iterator.next();
				listener.onReconnecting();
			}
		}

		@Override
		public void onDisconnected() {
			Log.d("HM", "onDisconnected");

			ListIterator<HMConnectListener> iterator = connectListeners
					.listIterator();
			while (iterator.hasNext()) {
				HMConnectListener listener = iterator.next();
				listener.onDisconnected();
			}

			authSequence = null;
			connector = null;
		}

		@Override
		public void onConnecting() {
			ListIterator<HMConnectListener> iterator = connectListeners
					.listIterator();
			while (iterator.hasNext()) {
				HMConnectListener listener = iterator.next();
				listener.onReconnecting();
			}
		}

		@Override
		public void onConnected() {
			ListIterator<HMConnectListener> iterator = connectListeners
					.listIterator();
			while (iterator.hasNext()) {
				HMConnectListener listener = iterator.next();
				listener.onConnected();
			}
		}
	};

	public interface HMConnectListener {
		/**
		 * 正在连接
		 */
		void onConnecting();

		/**
		 * 已经连接
		 */
		void onConnected();

		/**
		 * 已经断开连接
		 */
		void onDisconnected();

		/**
		 * 正在重试连接
		 */
		void onReconnecting();

		/**
		 * 用户认证失败
		 */
		void onAuthFailed();
	}

	public interface OnPushListener {
		boolean onPush(String type, Map<String, Object> data);
	}
}
