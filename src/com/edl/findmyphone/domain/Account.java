package com.edl.findmyphone.domain;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {

	private String account;// 账号
	private String name;// 用户名
	private String token;// 用户与服务器交互的唯一标
	private boolean current;// 是否是当前用户

	public Account() {

	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}



	private Account(Parcel parcel) {
		Bundle val = parcel.readBundle();

		account = val.getString("account");
		name = val.getString("name");
		token = val.getString("token");
		current = val.getBoolean("current");
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle val = new Bundle();
		val.putString("account", account);
		val.putString("name", name);

		val.putString("token", token);
		val.putBoolean("current", current);
		dest.writeBundle(val);
	}

	public static final Parcelable.Creator<Account> CREATOR = new Creator<Account>() {

		@Override
		public Account[] newArray(int size) {
			return new Account[size];
		}

		@Override
		public Account createFromParcel(Parcel source) {
			return new Account(source);
		}
	};

}