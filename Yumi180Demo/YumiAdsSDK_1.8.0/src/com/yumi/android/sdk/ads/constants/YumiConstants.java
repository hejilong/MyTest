package com.yumi.android.sdk.ads.constants;


public final class YumiConstants {

	private static final int SDK_VERSION_INT = 180;
	public static int getSdkVersionInt()
	{
		return SDK_VERSION_INT;
	}
	public static final String SDK_NAME = "YumiMobi";
	public static final String PARTNER_ID = "10000";
	public static final String AD_PLATFORM_ID = "miad2016yumi";
	
	public static final Boolean IS_GOOGLEPLAY_VERSION = false; //google play 去权限开关
	
	public final static String ACTION_DOWNLOAD_BEGIN = "com.android.sdk.action.download.begin";
	
	public static final String ACTION_REPORT = "mutisdk_action_report";
	public static final String ACTION_REPORT_CLICK= "click";
	public static final String ACTION_REPORT_REQUEST = "request";
	public static final String ACTION_REPORT_RESPONSE = "response";
	public static final String ACTION_REPORT_OPPORT = "opport";
	public static final String ACTION_REPORT_EXPOSURE = "exposure";
	public static final String ACTION_REPORT_REWARD = "reward";
	public static final String ACTION_REPORT_ROUND = "round";
	public static final String ACTION_REPORT_START = "start";
	public static final String ACTION_REPORT_END = "end";
	
	public static final String BUNDLE_KEY = "extraBundle";
	public static final String BUNDLE_KEY_YUMI_UUID = "uuid";
	public static final String BUNDLE_KEY_YUMI_ID = "cornID";
	public static final String BUNDLE_KEY_CHANNEL = "channelID";
	public static final String BUNDLE_KEY_VERSION = "versionName";
	public static final String BUNDLE_KEY_PROVIDER = "provider";
	public static final String BUNDLE_KEY_REQTYPE = "reqType";
	public static final String BUNDLE_KEY_ERROR_CODE = "error";
	public static final String BUNDLE_KEY_OPTIMIZATION = "optimization";
	public static final String BUNDLE_KEY_LAYERTYPE = "layerType";
	public static final String BUNDLE_KEY_DEVICETYPE = "devicetype";
	public static final String BUNDLE_KEY_MAC = "mac";
	public static final String BUNDLE_KEY_TIME = "time";
	public static final String BUNDLE_KEY_IMEI = "imei";
	public static final String BUNDLE_KEY_NET_TYPE = "nettype";
	public static final String BUNDLE_KEY_MODEL = "model";
	public static final String BUNDLE_KEY_LANGUAGE = "language";
	public static final String BUNDLE_KEY_LONGITUDE = "longitude";
	public static final String BUNDLE_KEY_LATITUDE = "latitude";
	public static final String BUNDLE_KEY_ADTYPE = "adtype";
	public static final String BUNDLE_KEY_PLANTIME = "plantime";
	public static final String BUNDLE_KEY_ACTION = "aciton";
	public static final String BUNDLE_KEY_RID = "rid";
	public static final String BUNDLE_KEY_PID = "pid";
	public static final String BUNDLE_KEY_ANDROIDID = "androidID";
	public static final String BUNDLE_KEY_PLMN = "plmn";
	public static final String BUNDLE_KEY_TRANS = "trans";
	public static final String BUNDLE_KEY_ADLIST = "ad_list";
	public static final String BUNDLE_KEY_PACKAGENAME = "packageName";
	
	public static final String BUNDLE_KEY_UNBIND = "unbind";
	
	public static final String ASSETS_BANNER_OFFLINE_CONFIG = "banner_offline_config";
	public static final String ASSETS_INTERSTITIAL_OFFLINE_CONFIG ="interstitial_offline_config";
	public static final String ASSETS_MEDIA_OFFLINE_CONFIG = "media_offline_config";
	
	public static final String SP_FILENAME = "29b2e3aa7596f75d0fda1f1f56183907";
	public static final String SP_KEY_LAST_BANNER_CONFIG = "sp_last_banner_config";
	public static final String SP_KEY_LAST_INTERSTITIAL_CONFIG = "sp_last_interstitial_config";
	public static final String SP_KEY_LAST_MEDIA_CONFIG = "sp_last_media_config";
	public static final String SP_KEY_INCENTIVED_REMAIN_TIMES = "sp_incentived_times";
	public static final String SP_KEY_INCENTIVED_LAST_TIMEMILLIS = "sp_key_incentived_last_timemillis";
	
}
