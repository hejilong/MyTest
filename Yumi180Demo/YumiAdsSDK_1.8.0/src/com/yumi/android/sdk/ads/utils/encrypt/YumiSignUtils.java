package com.yumi.android.sdk.ads.utils.encrypt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;

public final class YumiSignUtils {

	private static final boolean onoff = true;
	private static final String TAG = "ZplaySignUtils";

	public final static String getAdViewSign(String appid, String sn, String os, String nop, String pack, String time, String secretKey){
		StringBuilder builder = new StringBuilder("");
		builder.append(appid);
		builder.append(sn);
		builder.append(os);
		builder.append(nop);
		builder.append(pack);
		builder.append(time);
		builder.append(secretKey);
		String doMD5Encode = Encrypter.doMD5Encode(builder.toString());
		ZplayDebug.v(TAG, "adview sign original " + builder.toString(), onoff);
		ZplayDebug.v(TAG, "adview sign " + doMD5Encode, onoff);
		return doMD5Encode;
	}
	
	public final static String getTrackerID(Context context, String rORp){
		StringBuilder builder = new StringBuilder();
		builder.append(rORp);
		builder.append(PhoneInfoGetter.getDeviceID(context));
		builder.append(PhoneInfoGetter.getMAC(context));
		builder.append(System.currentTimeMillis());
		return Encrypter.doMD5Encode(builder.toString());
	};
	
	public final static String getMogoSign(String[] keys, String[] values){
		List<String> list = new ArrayList<String>();
 		if (keys.length != values.length) {
			throw new IllegalArgumentException();
		}else {
			for (int i = 0; i < keys.length; i++) {
				StringBuffer buffer = new StringBuffer("");
				buffer.append(keys[i]);
				buffer.append("=");
				buffer.append(values[i]);
				list.add(buffer.toString());
			}
		}
 		String[] arr = list.toArray(new String[]{});
 		StringBuffer sb = new StringBuffer("");
 		for (int i = 0; i < arr.length; i++) {
			if (i != arr.length-1) {
				sb.append(arr[i]);
				sb.append("&");
			}else {
				sb.append(arr[i]);
			}
		}
 		sb.append("9a70A90F3FDk4182901DF4C14A536728");
 		ZplayDebug.v(TAG, "mogo sign " + sb.toString(), onoff);
 		ZplayDebug.v(TAG, Encrypter.doMD5Encode(sb.toString()), onoff);
 		
 		return Encrypter.doMD5Encode(sb.toString());
	}
	
	public final static String getConfigRequestSign(String[] keys, String[] values){
		List<String> list = new ArrayList<String>();
 		if (keys.length != values.length) {
			throw new IllegalArgumentException();
		}else {
			for (int i = 0; i < keys.length-1; i++) {
				StringBuffer buffer = new StringBuffer("");
				buffer.append(keys[i]);
				buffer.append("=");
				buffer.append(values[i]);
				list.add(buffer.toString());
			}
		}
 		String[] arr = list.toArray(new String[]{});
 		Arrays.sort(arr);
 		StringBuffer sb = new StringBuffer("");
 		sb.append("jDVbiQGUGr5tShbL22");
 		for (String string : arr) {
 			sb.append(string);
 			sb.append("&");
		}
 		String str = sb.toString();
 		String md5 = str.substring(0, str.length()-1);
 		return Encrypter.doMD5Encode(md5);
	}
}
