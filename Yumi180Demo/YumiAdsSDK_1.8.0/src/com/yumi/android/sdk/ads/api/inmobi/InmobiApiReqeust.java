package com.yumi.android.sdk.ads.api.inmobi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.yumi.android.sdk.ads.api.ApiRequest;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiGooglePlayServiceCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.encrypt.Encrypter;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;
import com.yumi.android.sdk.ads.utils.location.LocationHandler;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;
import com.yumi.android.sdk.ads.utils.network.WebParamsMapBuilder;
import com.yumi.android.sdk.ads.utils.network.WebTask;
import com.yumi.android.sdk.ads.utils.network.WebTaskHandler;

/**
 * 160111 update API request params
 *
 */

final class InmobiApiReqeust extends ApiRequest {

	private static final String TAG = "InmobiApiReqeust";

	/**http://api.w.inmobi.com/showad/v2.1*/
	private static final String REQ_URL = "nAGqWZw6f03ZghcewkO/nXUYSHqxgjUW/vH6wvzm1WFdanUEWM+jZA==";
	private Context context;
	private IYumiAPIRequestListener listener;
	private WebTaskHandler task;
	private LayerType type;

	InmobiApiReqeust(Context context, IYumiAPIRequestListener listener,
			LayerType type) {
		this.context = context;
		this.listener = listener;
		this.type = type;
	}

	private JSONArray buildImpParams(int adSize) {
		JSONArray imp = new JSONArray();
		try {
			JSONObject ad = new JSONObject();
			ad.put("ads", 1);
			if (type == LayerType.TYPE_INTERSTITIAL) {
				ad.put("adtype", "int");
			}
			JSONObject banner = new JSONObject();
			banner.put("adsize", adSize);
			ad.put("banner", banner);
			imp.put(ad);
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return imp;

	}

	private JSONObject buildSiteParams(String appID) {
		JSONObject site = new JSONObject();
		try {
			site.put("id", appID);
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return site;
	}

	private JSONObject buildDeviceParams(String ip, String gpid, int orientation) {
		JSONObject device = new JSONObject();
		try {
			device.put("ip", ip);
			device.put("ua", PhoneInfoGetter.getUserAgent(context));
			device.put("locale", PhoneInfoGetter.getLanguage());
			device.put("connectiontype",
					NetworkStatusHandler.getConnectedNetName(context));
			device.put("orientation", orientation);
			device.put("gpid", gpid);
			device.put("o1", Encrypter.doSHA1Encode(PhoneInfoGetter
					.getAndroidID(context)));
			device.put("um5", Encrypter.doMD5Encode(PhoneInfoGetter
					.getAndroidID(context)));
			device.put("iem", PhoneInfoGetter.getIMEI(context));
			Location lastKnownLocation = LocationHandler.getLocHandler().getLastKnownLocation(context);
			if (lastKnownLocation != null) {
				JSONObject geo = new JSONObject();
				geo.put("lat", lastKnownLocation.getLatitude());
				geo.put("lon", lastKnownLocation.getLongitude());
				geo.put("accu", lastKnownLocation.getAccuracy());
				device.put("geo", geo);
			}
			device.put("adt", YumiGooglePlayServiceCheckUtils.getGooglePlayServiceADT() ? 1 : 0);
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return device;
	}

	@SuppressWarnings("unchecked")
	final void requestApi(int adSize, String appID, String ip, String gpid,
			int orientation) {
		JSONObject request = new JSONObject();
		try {
			request.put("responseformat", "html");
			request.put("imp", buildImpParams(adSize));
			request.put("site", buildSiteParams(appID));
			request.put("device", buildDeviceParams(ip, gpid, orientation));
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		Map<String, Object> params = WebParamsMapBuilder.buildParams(YumiDes3Util.decodeDes3(REQ_URL),
				request.toString());
		if (task != null) {
			task.cancelTask();
		}
		task = new WebTaskHandler(context, new WebTask() {

			@Override
			public void doTask(String data, String msg) {
				if (NullCheckUtils.isNotNull(msg)) {
					Integer code = Integer.valueOf(msg);
					ZplayDebug.i(TAG, "inmobi api response code " + code, onoff);
					if (code != 200) {
						if (code == -1) {// 没网
							listener.onAPIRequestDone(null,
									LayerErrorCode.ERROR_INVALID_NETWORK);
						}
						if (code >= 400 && code < 500) { // bad request
							listener.onAPIRequestDone(null,
									LayerErrorCode.ERROR_INVALID);
						}
						if (code >= 500) {
							listener.onAPIRequestDone(null,
									LayerErrorCode.ERROR_INTERNAL);
						}
					} else {
						if (NullCheckUtils.isNotNull(data)) {
							if (data.startsWith("<!--") && data.endsWith("-->")) {
								listener.onAPIRequestDone(null,
										LayerErrorCode.ERROR_NO_FILL);
							} else {
								listener.onAPIRequestDone(data, null);
							}
						}
					}
				}
			}
		}, true, false);
		task.setHeaders(getHeaders());
		task.executeOnPool(params);
	}

	private Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		return headers;
	}

	final void onDestroy() {
		if (task != null) {
			task.cancel(true);
		}
	}

}
