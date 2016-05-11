package com.edl.findmyphone.db;

import java.util.ArrayList;
import java.util.List;

import com.edl.findmyphone.domain.Account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccountDao {
	private DBOpenHelper helper;

	public AccountDao(Context context) {
		helper = DBOpenHelper.getInstance(context);
	}

	public List<Account> getAllAccount() {
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select * from " + DB.Account.TABLE_NAME;
		Cursor cursor = db.rawQuery(sql, null);

		List<Account> list = null;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (list == null) {
					list = new ArrayList<Account>();
				}
				Account account = new Account();

				account.setAccount(cursor.getString(cursor
						.getColumnIndex(DB.Account.COLUMN_ACCOUNT)));

				account.setCurrent(cursor.getInt(cursor
						.getColumnIndex(DB.Account.COLUMN_CURRENT)) == 1);

				account.setName(cursor.getString(cursor
						.getColumnIndex(DB.Account.COLUMN_NAME)));

				account.setToken(cursor.getString(cursor
						.getColumnIndex(DB.Account.COLUMN_TOKEN)));
				list.add(account);
			}
		}
		return list;
	}

	public Account getCurrentAccount() {
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select * from " + DB.Account.TABLE_NAME + " where "
				+ DB.Account.COLUMN_CURRENT + "=1";
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				Account account = new Account();

				account.setAccount(cursor.getString(cursor
						.getColumnIndex(DB.Account.COLUMN_ACCOUNT)));

				account.setCurrent(cursor.getInt(cursor
						.getColumnIndex(DB.Account.COLUMN_CURRENT)) == 1);
			
				account.setName(cursor.getString(cursor
						.getColumnIndex(DB.Account.COLUMN_NAME)));
			
				account.setToken(cursor.getString(cursor
						.getColumnIndex(DB.Account.COLUMN_TOKEN)));
				return account;
			}
		}
		return null;
	}

	public Account getByAccount(String account) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select * from " + DB.Account.TABLE_NAME + " where "
				+ DB.Account.COLUMN_ACCOUNT + "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { account });

		if (cursor != null) {
			while (cursor.moveToNext()) {
				Account a = new Account();

				a.setAccount(cursor.getString(cursor
						.getColumnIndex(DB.Account.COLUMN_ACCOUNT)));
				
				a.setCurrent(cursor.getInt(cursor
						.getColumnIndex(DB.Account.COLUMN_CURRENT)) == 1);
			
				a.setName(cursor.getString(cursor
						.getColumnIndex(DB.Account.COLUMN_NAME)));

				a.setToken(cursor.getString(cursor
						.getColumnIndex(DB.Account.COLUMN_TOKEN)));
				return a;
			}
		}
		return null;
	}

	public void addAccount(Account account) {
		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DB.Account.COLUMN_ACCOUNT, account.getAccount());

		values.put(DB.Account.COLUMN_NAME, account.getName());

		values.put(DB.Account.COLUMN_TOKEN, account.getToken());
		values.put(DB.Account.COLUMN_CURRENT, account.isCurrent() ? 1 : 0);

		db.insert(DB.Account.TABLE_NAME, null, values);
	}

	public void updateAccount(Account account) {
		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(DB.Account.COLUMN_NAME, account.getName());

		values.put(DB.Account.COLUMN_TOKEN, account.getToken());
		values.put(DB.Account.COLUMN_CURRENT, account.isCurrent() ? 1 : 0);

		String whereClause = DB.Account.COLUMN_ACCOUNT + "=?";
		String[] whereArgs = new String[] { account.getAccount() };
		db.update(DB.Account.TABLE_NAME, values, whereClause, whereArgs);
	}

	public void deleteAccount(Account account) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String whereClause = DB.Account.COLUMN_ACCOUNT + "=?";
		String[] whereArgs = new String[] { account.getAccount() };
		db.delete(DB.Account.TABLE_NAME, whereClause, whereArgs);
	}
}
