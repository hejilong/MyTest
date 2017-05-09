package com.yumi.android.sdk.ads.request;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;

import com.yumi.android.sdk.ads.beans.YumiResultBean;
import com.yumi.android.sdk.ads.constants.YumiAPIList;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.publish.YumiDebug;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.SharedpreferenceUtils;
import com.yumi.android.sdk.ads.utils.YumiGooglePlayServiceCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiManifestReaderUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;
import com.yumi.android.sdk.ads.utils.encrypt.YumiSignUtils;
import com.yumi.android.sdk.ads.utils.json.JsonResolveUtils;
import com.yumi.android.sdk.ads.utils.location.LocationHandler;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;
import com.yumi.android.sdk.ads.utils.network.WebParamsMapBuilder;
import com.yumi.android.sdk.ads.utils.network.WebTask;
import com.yumi.android.sdk.ads.utils.network.WebTaskHandler;

public final class ConfigInfoRequest {

	private static final boolean onoff = true;
	private static final String TAG = "ConfigInfoRequest";

	private static final String[] CONFIG_KEYS = new String[] { 
			"cornID",
			"channelID", 
			"versionID", 
			"longitude", 
			"latitude",
			"adType",
			"deviceType", 
			"mac", 
			"plmn", 
			"deviceKey",
			"planTime",
			"deviceNo",
			"netType", 
			"language", 
			"time", 
			"os",
			"osVersion",
			"androidID",
			"imsi",
			"screenWidth",
			"screenHeight",
			"screenPix",
			"screenDirection",
			"accuracy",
			"wifiList",
			"bsInfo",
			"adt",
			"gpID",
			"appVersion",
			"packageName",
			"sdkVersion",
			"supportSDK", 
			"manufacturer",
			"partnerID",
			"sign" };

	private static final long DELAY_RETRY_TIME = 30 * 1000;
	private static final int DELAY_RETRY_TIMES = 3;

	private static final int HANDLER_RETRY = 0x001;

	private static final int HANDLER_REFLASH_CONFIG = 0;

	private Context context;
	private ConfigRequestCallback callback;
	private String yumiID;
	private String channelID;
	private String versionName;
	private Handler mHandler;
	private LayerType type;
	private ArrayList<String> theProviderRegistedInManifest;
	private int retryTimes;
	private long planTime;
	private boolean inReflash;
	private String spkey;
	private WebTaskHandler requestTask = null;

	public ConfigInfoRequest(Context context, String yumiID, String channelID,
			String versionName, LayerType type, String spkey,
			ConfigRequestCallback callback) {
		this.context = context;
		this.yumiID = yumiID;
		this.channelID = channelID;
		this.versionName = versionName;
		this.type = type;
		this.spkey = spkey;
		this.callback = callback;
		this.mHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == HANDLER_RETRY) {
					if (!inReflash) {
						requestConfig();
					}
				}
				if (msg.what == HANDLER_REFLASH_CONFIG) {
					inReflash = true;
					cancelHandlerMsg(HANDLER_RETRY);
					requestConfig();
				}
			};
		};
	}

	public final void requestConfig() {
		updateActivityRegistedInfo();
		Location loc = LocationHandler.getLocHandler().getLastKnownLocation(
				context);
		requestLayerConfigure(loc);
	}

	private void updateActivityRegistedInfo() {
		if (theProviderRegistedInManifest == null) {
			theProviderRegistedInManifest = YumiManifestReaderUtils
					.getTheProviderRegistedInManifest(context);
		}
	}

	@SuppressWarnings("unchecked")
	private void requestLayerConfigure(Location location) {
		if (NetworkStatusHandler.isNetWorkAvaliable(context)) {
			String[] values = buildConfigRequestValues(yumiID, channelID,
					versionName, location, type, theProviderRegistedInManifest);
			String url = YumiAPIList.INIT_CONFIG_URL();
			ZplayDebug.v(TAG, "[request_url]:"+url, onoff);
			Map<String, Object> params = WebParamsMapBuilder.buildParams(
					url, CONFIG_KEYS, values);
			requestTask = new WebTaskHandler(context, new WebTask() {

				@Override
				public void doTask(String data, String msg) {
					if (NullCheckUtils.isNotNull(data)) {
						//ZplayDebug.v(TAG, "[request_undecoded]:"+data, onoff);
						YumiResultBean result = null;
						try {
							JSONObject json = new JSONObject(data);
							int errcode = json.getInt("errcode");
							
							String des3data = json.getString("data");
							String decodeData = YumiDes3Util.decodeDes3_data(des3data);
							ZplayDebug.i(TAG, "[request_data]:"+data, onoff);
							ZplayDebug.i(TAG, "[request_decoded]:"+decodeData, onoff);
							if (errcode==200)
							{
								//TODO 解密
								result = JsonResolveUtils.resolveJson2JaveBean(
										new JSONObject(decodeData), YumiResultBean.class);
								if (result != null) {
									if (result.getResult() == 0) {
										planTime = result.getPlanTime();
										callback.onReqeustDone(result);
										retryTimes = 0;
										ZplayDebug.i(TAG, "config request success", onoff);
									}
									if (result.getSpaceTime() != 0) {
										ZplayDebug.i(TAG, "reflash config in "
												+ result.getSpaceTime()
												+ " seconds", onoff);
										mHandler.sendEmptyMessageDelayed(
												HANDLER_REFLASH_CONFIG,
												result.getSpaceTime() * 1000);
									}
									String uuid = result.getUuid();
									if (uuid!=null && !"".equals(uuid))
									{
										SharedpreferenceUtils.saveString(context,
												YumiConstants.SP_FILENAME, "uuid", result.getUuid());
									}
								} else {
									retryLoadConfig();
								}
							}else
							{
								retryLoadConfig();
							}
							
						} catch (JSONException e) {
							ZplayDebug.e(TAG, "", e, onoff);
							retryLoadConfig();
						} finally {
							if (result != null && result.getResult() == 0) {
								SharedpreferenceUtils.saveString(context,
										YumiConstants.SP_FILENAME, spkey, data);
							}
						}
					} else {
						retryLoadConfig();
					}
				}
			}, true, false);
			requestTask.executeOnPool(params);
		} else {
			ZplayDebug.w(TAG, "Invalid network", onoff);
			mHandler.sendEmptyMessageDelayed(HANDLER_RETRY, DELAY_RETRY_TIME);
		}
	}

	private void retryLoadConfig() {
		callback.onReqeustDone(null);
		if (retryTimes < DELAY_RETRY_TIMES) {
			retryTimes++;
			mHandler.sendEmptyMessageDelayed(HANDLER_RETRY, DELAY_RETRY_TIME);
		}
	}

	private String[] buildConfigRequestValues(String yumiID, String channelID,
			String versionName, Location location, LayerType layerType,
			ArrayList<String> regProviders) {
		int[] displayMetrics = PhoneInfoGetter.getDisplayMetrics(context);
		String imei = PhoneInfoGetter.getIMEI(context);
		String mac = PhoneInfoGetter.getMAC(context);
		String[] values = new String[] {
//				"cornID",
				yumiID,
//				"channelID", 
				channelID,
//				"versionID", 
				versionName,
//				"longitude", 
				location == null ? "" : location.getLongitude() + "",
//				"latitude",
				location == null ? "" : location.getLatitude() + "",		
//				"adType",
				layerType.getType(),
//				"deviceType", 
				"3",
//				"mac", 
				NullCheckUtils.isNotNull(mac) ? mac : "00:00:00:00:00:00",
//				"plmn", 
				PhoneInfoGetter.getPLMN(context),
//				"deviceKey",
				NullCheckUtils.isNotNull(imei) ? imei : "000000000000000",
//				"planTime",
				planTime + "",
//				"deviceNo",
				PhoneInfoGetter.getModel(),
//				"netType", 
				NetworkStatusHandler.getConnectedNetName(context),
//				"language", 
				PhoneInfoGetter.getLanguage(),
//				"time", 
				System.currentTimeMillis() + "",
//				"os",
				"android",
//				"osVersion",
				PhoneInfoGetter.getSysVersion(),
//				"androidID",
				PhoneInfoGetter.getAndroidID(context),
//				"imsi",
				PhoneInfoGetter.getIMSI(context),
//				"screenWidth",
				displayMetrics[0] +"",
//				"screenHeight",
				displayMetrics[1] + "",
//				"screenPix",
				PhoneInfoGetter.getDisplayDensity(context) + "",
//				"screenDirection",
				WindowSizeUtils.isPortrait(context) ? "0" : "1",
//				"accuracy",
				location == null ? "" : location.getAccuracy() + "",	
//				"wifiList",
				NetworkStatusHandler.getConnectWifiInfoList(context, 3),
//				"bsInfo",
				NetworkStatusHandler.getConnectCellInfoList(context, 3),
//				"adt",
				YumiGooglePlayServiceCheckUtils.getGooglePlayServiceADT() ? "1" : "0",
//				"gpID",
				YumiGooglePlayServiceCheckUtils.getGooglePlayID(context),
//				"appVersion",
				PackageInfoGetter.getAppVersionName(context.getPackageManager(), context.getPackageName()),
//				"packageName",
				context.getPackageName(),
//				"sdkVersion",
				YumiConstants.getSdkVersionInt() + "",
//				"supportSDK", 
				getSupportSDKString(regProviders),
//				"manufacturer",
				PhoneInfoGetter.getManufacture(),
//				"partnerID",
				YumiConstants.PARTNER_ID,
//				"sign" 
				""
				 };
		String sign = YumiSignUtils.getConfigRequestSign(CONFIG_KEYS, values);
		ZplayDebug.v(TAG, "sign="+sign, onoff);
		values[values.length - 1] = sign;
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int i = 0; i < CONFIG_KEYS.length; i++)
		{
			String k = CONFIG_KEYS[i];
			String v = values[i];
			sb.append(k);
			sb.append(":");
			sb.append(v);
			sb.append("\n");
		}
		ZplayDebug.d(TAG, sb.toString(), onoff);
		return values;
	}

	private String getSupportSDKString(ArrayList<String> regProviders) {
		if (NullCheckUtils.isNotEmptyCollection(regProviders)) {
			StringBuffer buffer = new StringBuffer("");
			for (String reg : regProviders) {
				buffer.append(reg);
				buffer.append(",");
			}
			return buffer.toString().substring(0,
					buffer.toString().length() - 1);
		}
		return "";
	}

	public interface ConfigRequestCallback {
		public void onReqeustDone(YumiResultBean result);
	}

	public final void release() {
		ZplayDebug.i(TAG, "config info request release", onoff);
		LocationHandler.getLocHandler().releaseHandler();
		if (requestTask != null) {
			requestTask.cancel(true);
		}
		cancelHandlerMsg(HANDLER_RETRY, HANDLER_REFLASH_CONFIG);
	}

	private void cancelHandlerMsg(int... whats) {
		for (int what : whats) {
			if (mHandler != null && mHandler.hasMessages(what)) {
				mHandler.removeMessages(what);
			}
		}
	}

}