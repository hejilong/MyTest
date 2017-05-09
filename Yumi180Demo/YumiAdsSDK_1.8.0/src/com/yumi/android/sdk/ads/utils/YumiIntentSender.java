package com.yumi.android.sdk.ads.utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yumi.android.sdk.ads.activity.YumiBrowserActivity;
import com.yumi.android.sdk.ads.beans.AdListBean;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.service.YumiAdsEventService;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.location.LocationHandler;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

public final class YumiIntentSender {

	private static final boolean onoff = true;
	private static final String TAG = "YumiIntentSender";
	
	public static final void requestWebActivity(Context context, String url, boolean jump){
		ZplayDebug.i(TAG, "request web activity " + url  + " is jump " + jump, onoff);
		Intent intent = new Intent(context, YumiBrowserActivity.class);
		intent.setAction("com.zplay");
		intent.putExtra("url", url);
		intent.putExtra("302", jump);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public static final void requestSystemBrowser(Context context, String url){
		ZplayDebug.i(TAG, "request system browser " + url, onoff);
		if (NullCheckUtils.isNotNull(url)) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri content_url = Uri.parse(url);
			intent.setData(content_url);
			PackageManager pm = context.getPackageManager();
			if(pm.queryIntentActivities(intent, 0).size() > 0){
				context.startActivity(intent);
			}
		}
	}
	
	public final static String getAdListBeanJson(List<AdListBean> beans)
	{
		if (beans == null || beans.size() == 0)
		{
			return "[]";
		} else
		{
			try
			{
				JSONArray array = new JSONArray();
				for (int i = 0; i < beans.size(); i++)
				{
					AdListBean bean = beans.get(i);
					JSONObject obj = new JSONObject();
					obj.put("adType", bean.getAdType());
					obj.put("action", bean.getAction());
					obj.put("result", bean.getResult());
					obj.put("interfaceType", bean.getInterfaceType());
					obj.put("pid", bean.getPid());
					obj.put("providerID", bean.getProviderID());
					obj.put("eventTime", bean.getEventTime());
					obj.put("keyID", bean.getKeyID());
					JSONObject obj_area = new JSONObject();
					if (bean.getClickArea()!=null)
					{
						obj_area.put("showAreaWidth", bean.getClickArea().getShowAreaWidth());
						obj_area.put("showAreaHeight", bean.getClickArea().getShowAreaHeight());
						obj_area.put("clickX", bean.getClickArea().getClickX());
						obj_area.put("clickY", bean.getClickArea().getClickY());
					}
					obj.put("clickArea", obj_area);
                    //左上角广告位坐标和展示容器宽高
                    JSONObject obj_lpArea = new JSONObject();
                    if (bean.getLPArea() != null) {
                        obj_lpArea.put("width", bean.getLPArea().getwidth());
                        obj_lpArea.put("height", bean.getLPArea().getheight());
                        obj_lpArea.put("showX", bean.getLPArea().getshowX());
                        obj_lpArea.put("showY", bean.getLPArea().getshowY());
                        obj.put("LPArea", obj_lpArea);
                    }
                    int templateID = bean.getTemplateID();
                    if (templateID==0)
					{
                    	templateID = 0;
					}
                    obj.put("templateID", templateID);
					array.put(obj);
				}
				String json = array.toString();
				return json;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return "[]";
		}
	}
	
	public final static void reportEvent(Context context, YumiProviderBean provider, String action, LayerType layer, LayerErrorCode error, String rid, String pid, String trans, List<AdListBean> beans){
		Bundle eventBundle = putBundleInfo(context, provider, action, layer, error, rid, pid, trans, getAdListBeanJson(beans));
		startReportService(context, eventBundle);
	}
	
//	public final static void reportEventMerge(Context context, YumiProviderBean provider, String action, LayerType layer, LayerErrorCode error, String rid, String pid){
//		Bundle eventBundle = putBundleInfo(context, provider, action, layer, error, rid, pid);
//		startReportService(context, eventBundle);
//	}
	
	public final static void reportRoundFailedEvent(Context context, String yumiID, String version, String channel, String planTime, String optimization,  LayerType layer, String rid, String trans, List<AdListBean> beans){
		Bundle eventBundle = putBundleInfo(context, yumiID, version, channel, YumiConstants.SDK_NAME, planTime, optimization, "SDK", YumiConstants.ACTION_REPORT_ROUND, layer, LayerErrorCode.CODE_FAILED, rid, "", trans, getAdListBeanJson(beans));
		startReportService(context, eventBundle);
	}

    public final static void reportRoundSuccessEvent(Context context, String yumiID, String version, String channel, String planTime, String optimization, LayerType layer, String rid, String pid, String trans,
            List<AdListBean> beans) {
        Bundle eventBundle = putBundleInfo(context, yumiID, version, channel, YumiConstants.SDK_NAME, planTime, optimization, "SDK", YumiConstants.ACTION_REPORT_ROUND, layer,
                LayerErrorCode.CODE_FAILED, rid, pid, trans, getAdListBeanJson(beans));
        startReportService(context, eventBundle);
    }
    
	private static void startReportService(Context context, Bundle eventBundle){
		Intent reportIntent = new Intent(context, YumiAdsEventService.class);
		reportIntent.setAction(YumiConstants.ACTION_REPORT);
		reportIntent.putExtra(YumiConstants.BUNDLE_KEY, eventBundle);
		context.startService(reportIntent);
	}
	
	private static Bundle putBundleInfo(Context context, String yumiID, String version, String channel, String providerName, 
			String planTime, String optimization, String reqtype, 
			String action, LayerType layer, LayerErrorCode error, String rid, String pid, String trans, String ad_list){
		//TODO
		String imei = PhoneInfoGetter.getIMEI(context);
		String mac = PhoneInfoGetter.getMAC(context);
		String androidID = PhoneInfoGetter.getAndroidID(context);
		
		Bundle event = new Bundle();
		//uuid
		event.putString(YumiConstants.BUNDLE_KEY_YUMI_UUID, SharedpreferenceUtils.getString(context, YumiConstants.SP_FILENAME, "uuid", ""));
		//cornid
		event.putString(YumiConstants.BUNDLE_KEY_YUMI_ID, yumiID);
		//versionid
		event.putString(YumiConstants.BUNDLE_KEY_VERSION, version);
		//channelid
		event.putString(YumiConstants.BUNDLE_KEY_CHANNEL, channel);
		//deviceType
		event.putString(YumiConstants.BUNDLE_KEY_DEVICETYPE, "android");
		//mac
		event.putString(YumiConstants.BUNDLE_KEY_MAC, NullCheckUtils.isNotNull(mac) ? mac : "00:00:00:00:00:00");
		//devicekey
		event.putString(YumiConstants.BUNDLE_KEY_IMEI, NullCheckUtils.isNotNull(imei) ? imei : "000000000000000");
		//androidID
		event.putString(YumiConstants.BUNDLE_KEY_ANDROIDID, NullCheckUtils.isNotNull(androidID) ? androidID : "0000000000000000");
		//nettype
		event.putString(YumiConstants.BUNDLE_KEY_NET_TYPE, NetworkStatusHandler.getConnectedNetName(context));
		//deviceno
		event.putString(YumiConstants.BUNDLE_KEY_MODEL, PhoneInfoGetter.getModel());
		//language
		event.putString(YumiConstants.BUNDLE_KEY_LANGUAGE, PhoneInfoGetter.getLanguage());
		//longitude
		Location lastKnownLocation = LocationHandler.getLocHandler().getLastKnownLocation(context);
		String longitude = "";
		String latitude = "";
		if (lastKnownLocation != null) {
			longitude = lastKnownLocation.getLongitude()+"";
			latitude = lastKnownLocation.getLatitude()+"";
		}
		event.putString(YumiConstants.BUNDLE_KEY_LONGITUDE, longitude);
		//latititude
		event.putString(YumiConstants.BUNDLE_KEY_LATITUDE, latitude);
		//adtype
		event.putString(YumiConstants.BUNDLE_KEY_ADTYPE, layer.getType());
		//supportsdk
		event.putString(YumiConstants.BUNDLE_KEY_PROVIDER, providerName);
		//plantime
		event.putString(YumiConstants.BUNDLE_KEY_PLANTIME, planTime);
		//getOptimization
		event.putString(YumiConstants.BUNDLE_KEY_OPTIMIZATION, optimization);
		//action
		event.putString(YumiConstants.BUNDLE_KEY_ACTION, action);
		//result
		event.putString(YumiConstants.BUNDLE_KEY_ERROR_CODE, error.getCode());
		//interfacetype
		event.putString(YumiConstants.BUNDLE_KEY_REQTYPE, reqtype);
		//rid
		event.putString(YumiConstants.BUNDLE_KEY_RID, rid);
		//pid
		event.putString(YumiConstants.BUNDLE_KEY_PID, pid);
		//plmn
		event.putString(YumiConstants.BUNDLE_KEY_PLMN, PhoneInfoGetter.getPLMN(context));
		//trans
		event.putString(YumiConstants.BUNDLE_KEY_TRANS, trans);
		//ad_list
		event.putString(YumiConstants.BUNDLE_KEY_ADLIST, ad_list);
        //packageName  1.8.0添加
        event.putString(YumiConstants.BUNDLE_KEY_PACKAGENAME, context.getPackageName());
		return event; 
	}
	
	
	private static Bundle putBundleInfo(Context context, YumiProviderBean provider, String action, LayerType layer,
	    LayerErrorCode error, String rid, String pid, String trans, String ad_list)
	{
		String reqTypeStr = "";
		String yumiID = "";
		String versionName = "";
		String channelID = "";
		String planTime = "";
		String optimization = "";
		String providerName = "";
		if (provider != null)
		{
			int reqType = provider.getReqType();
			if (reqType == 1)
			{
				reqTypeStr = "SDK";
			}
			if (reqType == 2)
			{
				reqTypeStr = "API";
			}
			if (reqType == 3)
			{
				reqTypeStr = "CSR";
			}
			providerName = provider.getProviderName();
			if (provider.getGlobal() != null)
			{
				yumiID = provider.getGlobal().getYumiID();
				versionName = provider.getGlobal().getVersionName();
				channelID = provider.getGlobal().getChannelID();
				planTime = provider.getGlobal().getPlanTime() + "";
				optimization = provider.getGlobal().getOptimization() + "";
			}
		}
		return putBundleInfo(context, yumiID, versionName, channelID, providerName, planTime, optimization, reqTypeStr,
		        action, layer, error, rid, pid, trans, ad_list);
	}

	
	public static final void bindService(Context context, ServiceConnection conn, LayerType layerType){
		Intent service = new Intent(context, YumiAdsEventService.class);
		service.putExtra(YumiConstants.BUNDLE_KEY_UNBIND, layerType.getType());
		context.getApplicationContext().bindService(service, conn, Context.BIND_AUTO_CREATE);
	}

	public static final void unbindService(Context context, ServiceConnection conn) {
        try {
            context.getApplicationContext().unbindService(conn);
        } catch (Exception e) {
        }
	}
	
}
