package com.yumi.android.sdk.ads.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class SharedpreferenceUtils {
	
	public static void saveBoolean(Context context, String fileName, String key ,  boolean value){
//		ZplayDebug.I(TAG, "save the " + key + " value is " + value );
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}
	
	public static void saveFloat(Context context, String fileName, String key ,  float value){
//		ZplayDebug.I(TAG, "save the " + key + " value is " + value );
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putFloat(key, value);
		edit.commit();
	}
	
	public static void saveInt(Context context, String fileName, String key ,  int value){
//		ZplayDebug.I(TAG, "save the " + key + " value is " + value );
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putInt(key, value);
		edit.commit();
	}
	
	public static void saveString(Context context,String fileName, String key ,  String value){
//		ZplayDebug.I(TAG, "save the " + key + " value is " + value );
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
	public static void saveLong(Context context,String fileName,  String key ,  long value){
//		ZplayDebug.I(TAG, "save the " + key + " value is " + value );
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putLong(key, value);
		edit.commit();
	}
	
	public static boolean getBoolean(Context context, String fileName,  String key, boolean defValue){
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}
	
	public static long getLong(Context context, String fileName,  String key, long defValue){
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getLong(key, defValue);
	}
	
	public static int getInt(Context context, String fileName,  String key, int defValue){
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getInt(key, defValue);
	}
	
	public static float getFloat(Context context, String fileName,  String key, float defValue){
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getFloat(key, defValue);
	}
	
	public static String getString(Context context, String fileName, String key, String defValue){
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getString(key, defValue);
	}
	
	public static boolean hasStringValue(Context context,String fileName,  String key){
		return NullCheckUtils.isNotNull(getString(context, fileName,  key, null));
	}
	
	
}
