package com.edl.findmyphone.fragment;


import com.edl.findmyphone.R;
import com.edl.findmyphone.base.BaseFragment;
import com.edl.findmyphone.db.AccountDao;
import com.edl.findmyphone.domain.Account;
import com.edl.findmyphone.lib.HMChatManager;
import com.edl.findmyphone.lib.HMError;
import com.edl.findmyphone.service.CoreService;
import com.edl.findmyphone.uitls.CommonUtil;
import com.edl.findmyphone.uitls.ToastUtil;
import com.edl.lib.findmyphone.callback.HMObjectCallBack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SignUpFra extends BaseFragment implements OnClickListener {
	private String TAG = "SignUpFra";

	private EditText etAccount;
	private EditText etPwd;
	private Button btnSignUp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_sign_up, container, false);

		initView(view);
		initEvent();
		return view;
	}

	private void initEvent() {
		btnSignUp.setOnClickListener(this);
	}

	private void initView(View view) {
		etAccount = (EditText) view.findViewById(R.id.et_sign_up_account);
		etPwd = (EditText) view.findViewById(R.id.et_sign_up_pwd);
		btnSignUp = (Button) view.findViewById(R.id.btn_sign_up);

	}

	@Override
	public void onClick(View v) {
		if (v == btnSignUp) {
			doSignUp();
		}
	}

	private void doSignUp() {
		Context context = getActivity();
		if (context == null) {
			return;
		}

		String account = etAccount.getText().toString().trim();
		if (TextUtils.isEmpty(account)) {
			ToastUtil.show(context, "用户名为空");
			return;
		}
		String password = etPwd.getText().toString().trim();
		if (TextUtils.isEmpty(password)) {
			ToastUtil.show(context, "密码为空");
			return;
		}

        //注册
		HMChatManager.getInstance().register(account, password,
				new HMObjectCallBack<Account>() {

					@Override
					public void onSuccess(Account account) {
						Log.d(TAG, "注册成功!!!");
						

						// 初始化用户连接安全信息
						HMChatManager.getInstance().initAccount(
								account.getAccount(), account.getToken());

						// 存储用户
						AccountDao dao = new AccountDao(getActivity());
						account.setCurrent(true);

						Account localAccount = dao.getByAccount(account
								.getAccount());
						if (localAccount != null) {
							dao.updateAccount(account);
						} else {
							dao.addAccount(account);
						}

						// 开启服务
						if (!CommonUtil.isServiceRunning(getActivity(),
								CoreService.class)) {
							getActivity().startService(
									new Intent(getActivity(),
											CoreService.class));
						}

						
				
					}

					@Override
					public void onError(int error, String msg) {
					

						switch (error) {
						case HMError.ERROR_CLIENT_NET:
							Log.d(TAG, "客户端网络异常");
							ToastUtil.show(getActivity(), "客户端网络异常");
							break;
						case HMError.ERROR_SERVER:
							Log.d(TAG, "服务器异常");
							ToastUtil.show(getActivity(), "服务器异常");
							break;
						case HMError.Register.ACCOUNT_EXIST:
							Log.d(TAG, "用户已经存在");
							ToastUtil.show(getActivity(), "用户已经存在");
							break;
						default:
							break;
						}
					}
				});
	}
}