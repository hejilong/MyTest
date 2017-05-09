package com.yumi.android.anaylsis;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yumi.android.sdk.ads.utils.ZplayDebug;


public final class AnaylsisDBHelper extends SQLiteOpenHelper{

	private static final String TAG = "AnaylsisDBHelper";
	
	private static final String C_ID = "_id";
	private static final String C_CID = "cornid";
	private static final String C_ADTYPE = "adtype";
	private static final String C_PROVIDER = "provider";
	private static final String C_ACTION = "action";
	private static final String C_RESULT = "result";
	
	private static final String DB_FILE_NAME = "YumiAnaylsis.db";
	private static final String TABLE_NAME = "anaylsis";
	
	private static AnaylsisDBHelper helper = null;
	private SQLiteDatabase db = null;
	
	
	public static AnaylsisDBHelper getHelper(Context context){
		if (helper == null) {
			helper = new AnaylsisDBHelper(context);
		}
		return helper;
	}
	
	private AnaylsisDBHelper(Context context) {
		super(context, DB_FILE_NAME, null, 1);
		db = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ZplayDebug.d(TAG, "create data base",true);		
		String sql = new StringBuilder().append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append(" (").append(C_ID)
				.append(" integer primary key autoincrement,").append(C_CID).append(" varchar,").append(C_ADTYPE).append(" varchar,")
				.append(C_PROVIDER).append(" varchar,").append(C_ACTION).append(" varchar,").append(C_RESULT).append(" varchar )")
				.toString();
		if (db != null) {
			Log.e("mikoto", "create " + TABLE_NAME);
			db.execSQL(sql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	public synchronized int getCountByAction(String provider, String adFormat, String action){
		StringBuilder builder = new StringBuilder().append("select count(*) from ").append( TABLE_NAME).append(" where ");
		if (provider != null) {
			builder.append(C_PROVIDER).append("='").append(provider).append("'").append(" and ");
		}
		builder.append(C_ADTYPE).append("='").append(adFormat).append("'").append(" and ").append(C_ACTION).append("='").append(action).append("'");
		String sql = builder.toString();
		ZplayDebug.v(TAG, "sql : "+ sql,true);
		Cursor c = null;
		try {
			if (db != null) {
				c = db.rawQuery(sql, null);
				if (c.moveToFirst()) {
					return  c.getInt(0);
				}
			}
		} catch (Exception e) {
			ZplayDebug.e(TAG, "", e,true);
		}finally{
			if (c != null) {
				c.close();
			}
		}
		return -1;
	}
	
	public synchronized int getCountByResponse(String provider, String adFormat, boolean result){
		String sql = new StringBuilder().append("select count(*) from ").append(TABLE_NAME).append(" where ").append(C_PROVIDER).append("='").append(provider).append("'")
				.append(" and ").append(C_ADTYPE).append("='").append(adFormat).append("'").append(" and ").append(C_ACTION).append("='").append("response").append("'").append(" and ")
				.append(C_RESULT).append(result ? "='1'" : "!='1'").toString();
		ZplayDebug.v(TAG, "sql : " + sql,true);
		Cursor c = null;
		try {
			if (db != null) {
				c = db.rawQuery(sql, null);
				if (c.moveToFirst()) {
					return c.getInt(0);
				}
			}
		} catch (Exception e) {
			ZplayDebug.e(TAG, "", e,true);
		}finally{
			if (c != null) {
				c.close();
			}
		}
		return -1;
	}
	
	
	public synchronized ArrayList<String> getProviders(String format){
		ArrayList<String> providers = new ArrayList<String>(); 		
		String sql = new StringBuilder().append("select ").append(C_PROVIDER ).append(" from ")
				.append(TABLE_NAME).append(" where ").append(C_ADTYPE).append("='").append(format).append("'").append(" group by ").append(C_PROVIDER).toString();
		ZplayDebug.v(TAG, "sql : " + sql,true);
		Cursor c = null;
		try {
			if (db != null) {
				c = db.rawQuery(sql, null);
			}
			if (c != null ) {
				while (c.moveToNext()) {
					providers.add(c.getString(0));
				}
			}
		} catch (Exception e) {
			ZplayDebug.e(TAG, "", e,true);
		}finally {
			if (c != null) {
				c.close();
			}
		}
		return providers;
	}
	
	
	
	
	public synchronized void closeDB(){
		ZplayDebug.d(TAG, "database closed",true);
		if (db != null) {
			db.close();
		}
		close();
		helper = null;
	}

	public synchronized void clearDB() {
		ZplayDebug.d(TAG, "clear db",true);
		if (db != null) {
			db.execSQL("delete from " + TABLE_NAME);
		}
	}
	
	
	
	
	
	
	
	
	
}
