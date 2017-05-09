package com.yumi.android.sdk.ads.test;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public final class AnaylsisDBHelper extends SQLiteOpenHelper{

	private static final boolean onoff = true;
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
	private Context context;
	
	
	public synchronized static AnaylsisDBHelper getHelper(Context context){
		if (helper == null) {
			helper = new AnaylsisDBHelper(context);
		}
		return helper;
	}
	
	private AnaylsisDBHelper(Context context) {
		super(context, DB_FILE_NAME, null, 1);
		this.context = context;
		db = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ZplayDebug.d(TAG, "create data base", onoff);		
		String sql = new StringBuilder().append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append(" (").append(C_ID)
				.append(" integer primary key autoincrement,").append(C_CID).append(" varchar,").append(C_ADTYPE).append(" varchar,")
				.append(C_PROVIDER).append(" varchar,").append(C_ACTION).append(" varchar,").append(C_RESULT).append(" varchar )").toString(); 
		if (db != null) {
			db.execSQL(sql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	@SuppressLint("NewApi")
	public synchronized void addReportData(Bundle bundle){
		ZplayDebug.d(TAG, "database add bundle info ", onoff);
		if (bundle != null) {
			String coreId = bundle.getString(YumiConstants.BUNDLE_KEY_YUMI_ID, "");
			String adList = bundle.getString(YumiConstants.BUNDLE_KEY_ADLIST);
//			String provider = bundle.getString(YumiConstants.BUNDLE_KEY_PROVIDER, "");
//			String action = bundle.getString(YumiConstants.BUNDLE_KEY_ACTION, "");
//			String result = bundle.getString(YumiConstants.BUNDLE_KEY_ERROR_CODE, "");
			JSONArray array;
			try
			{
				array = new JSONArray(adList);
				int inc = array.length();
				if (inc>0)
				{
					for (int i = 0; i < inc; i++)
					{
						JSONObject obj_adlist = array.getJSONObject(i);
						String adType = obj_adlist.getString("adType");
						String providerID = obj_adlist.getString("providerID");
						String action = obj_adlist.getString("action");
						String result = obj_adlist.getString("result");
						
						ZplayDebug.v(TAG, "保存记录:"+providerID+" action:"+action+" result:"+result, false);
						
						ContentValues cv = new ContentValues();
						cv.put(C_CID, coreId);
						cv.put(C_ADTYPE, adType);
						cv.put(C_PROVIDER, providerID);
						cv.put(C_ACTION, action);
						cv.put(C_RESULT, result);
						if (db != null) {
							db.insert(TABLE_NAME, null, cv);
						}
					}
				}else
				{
					String adTye = bundle.getString(YumiConstants.BUNDLE_KEY_ADTYPE, "");
					String provider = bundle.getString(YumiConstants.BUNDLE_KEY_PROVIDER, "");
					String action = bundle.getString(YumiConstants.BUNDLE_KEY_ACTION, "");
					String result = bundle.getString(YumiConstants.BUNDLE_KEY_ERROR_CODE, "");
					for (int i = 0; i < inc; i++)
					{
						ContentValues cv = new ContentValues();
						cv.put(C_CID, coreId);
						cv.put(C_ADTYPE, adTye);
						cv.put(C_PROVIDER, provider);
						cv.put(C_ACTION, action);
						cv.put(C_RESULT, result);
						if (db != null) {
								db.insert(TABLE_NAME, null, cv);
						}
					}
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			
			
		}
	}
	
	public synchronized int getCountByAction(String provider, String adFormat, String action){
		StringBuilder builder = new StringBuilder().append("select count(*) from ").append(TABLE_NAME).append(" where ");
		if (provider != null) {
			builder.append(C_PROVIDER).append("='").append(provider).append("'").append(" and ");
		}
		builder.append(C_ADTYPE).append("='").append(adFormat).append("'").append(" and ").append(C_ACTION).append("='").append(action).append("'");
		String sql = builder.toString();
		ZplayDebug.v(TAG, "sql : "+ sql, onoff);
		Cursor c = null;
		try {
			if (db != null) {
				c = db.rawQuery(sql, null);
				if (c.moveToFirst()) {
					return  c.getInt(0);
				}
			}
		} catch (Exception e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}finally{
			if (c != null) {
				c.close();
			}
		}
		return -1;
	}
	
	public synchronized int getCountByResponse(String provider, String adFormat, boolean result){
		String sql = new StringBuilder().append("select count(*) from ").append(TABLE_NAME).append(" where ").append(C_PROVIDER).append("='").append(provider).append("'")
				.append(" and ").append(C_ADTYPE).append("='").append(adFormat).append("'").append(" and ").append(C_ACTION).append("='").append(YumiConstants.ACTION_REPORT_RESPONSE).append("'").append(" and ")
				.append(C_RESULT).append(result ? "='1'" : "!='1'").toString();
		ZplayDebug.v(TAG, "sql : " + sql, onoff);
		Cursor c = null;
		try {
			if (db != null) {
				c = db.rawQuery(sql, null);
				if (c.moveToFirst()) {
					return c.getInt(0);
				}
			}
		} catch (Exception e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}finally{
			if (c != null) {
				c.close();
			}
		}
		return -1;
	}
	
	
	
	
	public synchronized ArrayList<String> getProviders(){
		ArrayList<String> providers = new ArrayList<String>(); 		
		String sql = new StringBuilder().append("select ").append(C_PROVIDER ).append(" from ")
				.append(TABLE_NAME).append(" group by ").append(C_PROVIDER).toString();
		ZplayDebug.v(TAG, "sql : " + sql, onoff);
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
			ZplayDebug.e(TAG, "", e, onoff);
		}finally {
			if (c != null) {
				c.close();
			}
		}
		return providers;
	}
	
	
	
	
	public synchronized void closeDB(){
		ZplayDebug.d(TAG, "database closed", onoff);
		if (db != null) {
			db.close();
		}
		close();
		helper = null;
	}

	public synchronized void clearDB() {
		if (db != null) {
			db.execSQL("delete from " + TABLE_NAME);
		}
	}

	
	public void addToSPFile(int inc){
		SharedPreferences sp = context.getSharedPreferences("successCount", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		int success = sp.getInt("success", 0);
		int put = success + 1;
		editor.putInt("success", put);
		editor.commit();
	}
	
	
	
	
	
	
}
