package com.yumi.android.sdk.ads.utils.device;

import java.util.HashSet;
import java.util.Set;

import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;

/**
 * 获取已经安装的程序的信息【图标、版本、名称】
 * 
 * @author glzlaohuai
 * @date 2013-4-8
 */
public final class PackageInfoGetter {

	private static final String TAG = "PackageInfoGetter";
	private static final boolean onoff = true;
	
	/**
	 * 获取app的图标
	 * 
	 * @param packname
	 * @return
	 */
//	public static Drawable getAppIcon(PackageManager pm, String pkg) {
//		try {
//			ApplicationInfo info = pm.getApplicationInfo(pkg, 0);
//			return info.loadIcon(pm);
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

//	public static int getAppIconID(Context context) {
//		PackageManager pm = context.getPackageManager();
//		String packageName = context.getPackageName();
//
//		try {
//			ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
//			return info.icon;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//			return 0;
//		}
//
//	}


	/**
	 * 获取程序的版本verName
	 * 
	 * @param pm
	 * @param pkg
	 * @return
	 */
	public static String getAppVersionName(PackageManager pm, String pkg) {
		try {
			PackageInfo packinfo = pm.getPackageInfo(pkg, 0);
			return packinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取程序的版本verName
	 * 
	 * @param pm
	 * @param pkg
	 * @return
	 */
	public static int getAppVersionCode(PackageManager pm, String pkg) {
		try {
			PackageInfo packinfo = pm.getPackageInfo(pkg, 0);
			return packinfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取程序名称
	 * 
	 * @param pkg
	 * @return
	 */
	public static String getAppName(PackageManager pm, String pkg) {
		try {
			ApplicationInfo info = pm.getApplicationInfo(pkg, 0);
			return info.loadLabel(pm).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取启动配置为launcher的intent
	 * 
	 * @param context
	 * @param pkg
	 * @return 如果该app没有配置，那么返回null
	 */
//	public static Intent getLaunchIntent(Context context, String pkg) {
//		PackageManager pm = context.getPackageManager();
//		Intent launchIntent = null;
//		try {
//			launchIntent = pm.getLaunchIntentForPackage(pkg);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return launchIntent;
//	}

	/**
	 * 获取apk包注册的Activity
	 * @param context
	 * @return
	 */
	public static Set<String> getRegisterActivity(Context context){
		Set<String> activitys = new HashSet<String>();
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			for (ActivityInfo info : packageInfo.activities) {
				activitys.add(info.name);
			}
		} catch (Exception e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return activitys;
	}

	public static Set<String> getRegisterService(Context context){
		Set<String> services = new HashSet<String>();
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SERVICES);
			for (ServiceInfo info : packageInfo.services) {
				services.add(info.name);
			}
		} catch (Exception e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return services;
	}
	
	
	/**
	 * 判断是否在manifest里面注册了某种权限. 传入
	 * @param context
	 * @param permission  : android.Manifest.permission 下的常量
 	 * @return
	 */
	public static boolean hasReqeuestPermission(Context context, String permission){
		PackageManager pm = context.getPackageManager();
		int checkPermission = pm.checkPermission(permission, context.getPackageName());
		return checkPermission == PackageManager.PERMISSION_GRANTED;
	}
	
}
