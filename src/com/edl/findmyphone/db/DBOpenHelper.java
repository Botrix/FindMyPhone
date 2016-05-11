package com.edl.findmyphone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	private DBOpenHelper(Context context) {
		super(context, DB.NAME, null, DB.VERSION);
	}

	private static DBOpenHelper instance;

	public static DBOpenHelper getInstance(Context context) {
		if (instance == null) {
			synchronized (DBOpenHelper.class) {
				if (instance == null) {
					instance = new DBOpenHelper(context);
				}
			}
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB.Account.SQL_CREATE_TABLE);
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
