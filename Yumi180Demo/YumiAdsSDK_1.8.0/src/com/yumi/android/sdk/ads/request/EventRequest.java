package com.yumi.android.sdk.ads.request;

import java.util.Arrays;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;

import com.yumi.android.sdk.ads.constants.YumiAPIList;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;
import com.yumi.android.sdk.ads.utils.network.WebParamsMapBuilder;
import com.yumi.android.sdk.ads.utils.network.WebTask;
import com.yumi.android.sdk.ads.utils.network.WebTaskHandler;

public final class EventRequest {

	private static final boolean onoff = true;
	private static final String[] REPORT_KEY_SET = new String[]
		{
				"partnerID",
		        "uuid",				//110新增
		        "cornID",
		        "versionID",
		        "channelID",
		        "deviceType",
		        "mac",
		        "deviceKey",
		        "time",				//110新增
		        "os",
		        "netType",
		        "deviceNo",
		        "language",
		        "longitude",
		        "latitude",
		        "planTime",
		        "optimization",
		        "PLMN",
		        "sdkver",
		        "rid",
		        "androidID",
		        "trans",
		        "ad_list",			//110改变
		        "packageName" //180新增
		};
	
	private static final String TAG = "EventRequest";
	private Context context;
//	private AnaylsisDBHelper helper = null; //TODO 本地统计代码   发包时要注释掉

	public EventRequest(Context context) {
		this.context = context;
		//TODO 本地统计代码   发包时要注释掉  start
//		if (YumiDebug.isDebugMode()) {
//			this.helper = AnaylsisDBHelper.getHelper(context);
//		}
		//TODO 本地统计代码   发包时要注释掉  end
	}

	public final void release() {

	}

	@SuppressWarnings("unchecked")
	public final void reportEvent(final Bundle bundle) {
		if (NetworkStatusHandler.isNetWorkAvaliable(context)) {
			String url = YumiAPIList.EVENT_REPORT_TEMP_URL();
			ZplayDebug.v(TAG, "[report_url]="+url, onoff);
			String ad_list = bundle.getString("ad_list");
			ZplayDebug.v(TAG, "[report_adlist]="+ad_list, onoff);
			Map<String, Object> params = WebParamsMapBuilder.buildParams(url, REPORT_KEY_SET,
					buildBundleValues(bundle));
			int inc = 1;
			try
			{
				JSONArray array = new JSONArray(ad_list);
				inc = array.length();
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			if (inc<1)
			{
				inc = 1;
			}
			final int inc_final = inc;
			
	        //TODO 本地统计代码   发包时要注释掉  start
//			if (helper != null) {
//				helper.addReportData(bundle);
//			}
			//TODO 本地统计代码   发包时要注释掉  end
			
			new WebTaskHandler(context, new WebTask() {
				@Override
				public void doTask(String data, String msg) {
					ZplayDebug.v(TAG, "[report_back]:"+data, onoff);
					//TODO 本地统计代码   发包时要注释掉  start
//					if (helper != null && data != null) {
//						helper.addToSPFile(inc_final);
//					}
					//TODO 本地统计代码   发包时要注释掉  end
				}
			}, true, false).executeOnPool(params);
//			ZplayDebug.D(TAG, "[report action]:" + getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_ACTION) + " error is " + getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_ERROR_CODE));
		} else {
			// add the message to db ??
			// add your sister!!
		}
	}

	
	private String[] buildBundleValues(Bundle bundle) {
		String[] values = new String[]{
				//"partnerID",
				YumiConstants.PARTNER_ID,
				//"uuid",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_YUMI_UUID),
				//"cornID",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_YUMI_ID),
				//"versionID",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_VERSION),
				//"channelID",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_CHANNEL),
				//"deviceType", 	//getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_DEVICETYPE),
				"3",
				//"mac",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_MAC),
				//"deviceKey",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_IMEI),
				//"time", 	//getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_TIME),
				System.currentTimeMillis()+"",
				//"os",
				"android",
				//"netType",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_NET_TYPE),
				//"deviceNo",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_MODEL),
				//"language",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_LANGUAGE),
				//"longitude",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_LONGITUDE),
				//"latitude",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_LATITUDE),
				//"planTime",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_PLANTIME),
				//"optimization",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_OPTIMIZATION),
				//"PLMN",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_PLMN),
				//"sdkver",
				YumiConstants.getSdkVersionInt()+"",
				//"rid",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_RID),
				//androidId
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_ANDROIDID),
				//trans
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_TRANS),
				//"ad_list"
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_ADLIST),
				//packageName
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_PACKAGENAME),
				
		};
		ZplayDebug.v(TAG , "get bundle and post bundle message " +  Arrays.toString(values), false);
		
		/*StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int i = 0; i < REPORT_KEY_SET.length; i++)
		{
			String k = REPORT_KEY_SET[i];
			String v = values[i];
			sb.append(k);
			sb.append(":");
			sb.append(v);
			sb.append("\n");
		}
		ZplayDebug.d(TAG, sb.toString(), onoff);*/
		return values;
	}
	
	
	
	
/*		String[] values = new String[]{
//			"uuid"
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_YUMI_UUID),
//		    "cornID",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_YUMI_ID),
//			"versionID",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_VERSION),
//			"channelID", 
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_CHANNEL),
//			"deviceType",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_DEVICETYPE),
//         "os"
			   "android",	
//			"MAC",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_MAC),
//			"deviceKey",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_IMEI),
//			"androidID"
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_ANDROIDID),
//			"netType", 
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_NET_TYPE),
//			"deviceNo", 
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_MODEL),
//			"language", 
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_LANGUAGE),
//			"longitude",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_LONGITUDE),
//			"latitude",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_LATITUDE),
//			"adType", 
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_ADTYPE),
//			"supportSDK",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_PROVIDER),
//			"plantime",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_PLANTIME),
//			"optimization",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_OPTIMIZATION),
//			"action",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_ACTION),
//			"result",
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_ERROR_CODE),
//			"interfaceType" 
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_REQTYPE),
//			"PLMN"
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_PLMN),
//			"sdkVer"
				YumiConstants.getSdkVersionInt() + "",
//			"rid"
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_RID),
//			"pid"
				getBundleInfo(bundle, YumiConstants.BUNDLE_KEY_PID)
		};*/
	
	private String getBundleInfo(Bundle bundle, String key ){
		if (bundle != null) {
			String string = bundle.getString(key);
			if (NullCheckUtils.isNotNull(string)) {
				return string;
			}
		}
		return "-";
	}

}
