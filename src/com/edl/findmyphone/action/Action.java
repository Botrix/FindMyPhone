package com.edl.findmyphone.action;

import android.content.Context;

import java.util.Map;

public abstract class Action {

	public abstract String getAction();

	public abstract boolean doAction(Context context, Map<String, Object> data);
}