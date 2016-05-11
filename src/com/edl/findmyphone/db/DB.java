package com.edl.findmyphone.db;

public interface DB {
	String NAME = "findmyphone.db";
	int VERSION = 1;

	public interface Account {
		String TABLE_NAME = "account";

		String COLUMN_ID = "_id";
		String COLUMN_ACCOUNT = "account";
		String COLUMN_NAME = "name";

		String COLUMN_TOKEN = "token";
		String COLUMN_CURRENT = "current";

		String SQL_CREATE_TABLE = "create table " + TABLE_NAME + " ("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_ACCOUNT + " text," + COLUMN_NAME + " text,"

				+ COLUMN_TOKEN + " text," + COLUMN_CURRENT + " integer" + ")";
	}


}
