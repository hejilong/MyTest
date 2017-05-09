package com.yumi.android.sdk.ads.api.chancead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import com.yumi.android.sdk.ads.api.ApiRequest;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiGooglePlayServiceCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.utils.encrypt.Encrypter;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;
import com.yumi.android.sdk.ads.utils.json.JsonResolveUtils;
import com.yumi.android.sdk.ads.utils.location.LocationHandler;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;
import com.yumi.android.sdk.ads.utils.network.WebParamsMapBuilder;
import com.yumi.android.sdk.ads.utils.network.WebTask;
import com.yumi.android.sdk.ads.utils.network.WebTaskHandler;

/**
 * 160111 update API request params
 *
 */

final class ChanceadApiRequest extends ApiRequest {

	private final String TAG = "ChanceApiRequest";
	/**http://service.cocounion.com/core/ssp/bid/chance*/
	private final String API_URL = "pLGq9bCpKiMzh75FbbemBOGm2rzXnkoj5bOWlsnK8AD4P/+stxcHBvLV3BJD 2G8vDoc0Aeg3oes=";
	private IYumiAPIRequestListener listener;
	private WebTaskHandler task;
	private Context context;
	private LayerType layerType;
	
	ChanceadApiRequest(Context context, IYumiAPIRequestListener listener, LayerType layerType){
		this.context = context;
		this.listener = listener;
		this.layerType = layerType;
	}
	
	@SuppressWarnings("unchecked")
	final void requestApi(String placementID, String publisherID, String secret, int wpix, int hpix, String ip){
		long timestamp = System.currentTimeMillis();
		JSONObject body = buildRequestJson(placementID, publisherID, timestamp, wpix, hpix, ip);
		Map<String, Object> params = WebParamsMapBuilder.buildParams(YumiDes3Util.decodeDes3(API_URL), body.toString());
		String sign = getChanceSign(publisherID, timestamp+"", secret);
		if (task != null) {
			task.cancel(true);
		}
		task = new WebTaskHandler(context, new WebTask() {
			
			@Override
			public void doTask(String data, String msg) {
				if (NullCheckUtils.isNotNull(data)) {
					dealResponse(data);
				}else {
					callbackApiResult(null, LayerErrorCode.ERROR_INTERNAL);
				}
			}
		}, true, false);
		task.setHeaders(getHeaders(sign));
		task.executeOnPool(params);
	}
	
	private void dealResponse(String data) {
		try {
			JSONObject response = new JSONObject(data);
			if (JsonResolveUtils.getBooleanFromJson(response, "result")) {
				JSONArray ads = JsonResolveUtils.getJsonArrayFromJson(response, "ads");
				if (ads != null && ads.length() > 0) {
					JSONObject ad = ads.getJSONObject(0);
					if (ad != null) {
						String ctype = JsonResolveUtils.getStringFromJson(ad, "ctype", "");
						if (ctype.equals("1")) {
							String imgUrl = JsonResolveUtils.getStringFromJson(ad, "stuffurl", "");
							JSONArray imps = JsonResolveUtils.getJsonArrayFromJson(ad, "impmonurl");
							List<String> impTrackers = new ArrayList<String>();
							if (imps != null && imps.length() > 0) {
								for (int i = 0; i < imps.length(); i++) {
									impTrackers.add(imps.getString(i));
								}
							}else {
								JSONArray gims = JsonResolveUtils.getJsonArrayFromJson(response, "gimpmonurl");
								if (gims != null && gims.length() > 0) {
									for (int i = 0; i < gims.length(); i++) {
										impTrackers.add(gims.getString(i));
									}
								}
							}
							String clkUrl = JsonResolveUtils.getStringFromJson(ad, "curl", "");
							JSONArray clks = JsonResolveUtils.getJsonArrayFromJson(ad, "clkmonurl");
							if (NullCheckUtils.isNotNull(imgUrl) && NullCheckUtils.isNotNull(clkUrl)) {
								String renderHtml = renderHtml(layerType, imgUrl, clkUrl, impTrackers);
								if (NullCheckUtils.isNotNull(renderHtml)) {
									if (layerType == LayerType.TYPE_BANNER) {
										JSONObject result = new JSONObject();
										result.put("html", renderHtml);
										result.put("clkTracker", clks);
										callbackApiResult(result.toString(), LayerErrorCode.CODE_SUCCESS);
									}
									if (layerType == LayerType.TYPE_INTERSTITIAL) {
										JSONObject result = new JSONObject();
										result.put("html", renderHtml);
										result.put("w", JsonResolveUtils.getIntFromJson(ad, "w", 0));
										result.put("h", JsonResolveUtils.getIntFromJson(ad, "h", 0));
										result.put("clkTracker", clks);
										callbackApiResult(result.toString(), LayerErrorCode.CODE_SUCCESS);
									}
									return;
								}
							}
						}
					}
				}
				callbackApiResult(null, LayerErrorCode.ERROR_NO_FILL);
				return;
			} else {
				int err = JsonResolveUtils.getIntFromJson(response, "err", -1);
				if (err == 1007) {
					callbackApiResult(null, LayerErrorCode.ERROR_NO_FILL);
					return;
				}
				if (err == 1002 || err == 1001 || err == 1015) {
					callbackApiResult(null, LayerErrorCode.ERROR_INVALID);
					return;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		callbackApiResult(null, LayerErrorCode.ERROR_INTERNAL);
	}

	private String renderHtml(LayerType layerType, String imgUrl, String clickUrl, List<String> notifyUrls) {
		String html = null;
		if (layerType == LayerType.TYPE_BANNER) {
			html = XMLRendering.renderingImgXML(imgUrl, clickUrl, notifyUrls);			
		}
		if (layerType == LayerType.TYPE_INTERSTITIAL) {
			html = XMLRendering.renderingImgInterstitialXML(imgUrl, clickUrl, notifyUrls);
		}
		return html;
	}

	private void callbackApiResult(String html, LayerErrorCode error){
		if (listener != null) {
			listener.onAPIRequestDone(html, error);
		}
	}
	
	private Map<String, String> getHeaders(String sign) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-CAD-S2S-VER", "1.6.1");
		headers.put("X-CAD-S2S-SIGNITURE", sign);
		headers.put("Content-Type", "application/json;chartset=utf-8");
		return headers;
	}

	private String getChanceSign(String pid, String time, String secret) {
		StringBuilder builder = new StringBuilder();
		builder.append("pid=");
		builder.append(pid);
		builder.append("&adtype=");
		builder.append(getAdtype()+"");
		builder.append("&timestamp=");
		builder.append(time);
		builder.append("&os=1");
		builder.append("&secret=");
		builder.append(secret);
		ZplayDebug.v(TAG, "chance original " + builder.toString(), onoff);
		String doMD5Encode = Encrypter.doMD5Encode(builder.toString());
		ZplayDebug.v(TAG, "chance sign " + doMD5Encode, onoff);
		return doMD5Encode;
	}

	private JSONObject buildRequestJson(String placementID, String pid, long time, int wpix, int hpix, String ip){
		JSONObject reqJson = new JSONObject();
		try {
			reqJson.put("id", getRequestID(time + ""));
			reqJson.put("imp", buildImpJsonArray(placementID, wpix, hpix));
			reqJson.put("app", buildAppJson(pid));
			reqJson.put("device", buildDeviceJson(ip));
			reqJson.put("time", time);
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return reqJson;
	}		

	private JSONObject buildDeviceJson(String ip) {
		JSONObject device = new JSONObject();
		try {
			device.put("type", WindowSizeUtils.isApproximateTablet(context) ? "5" : "4");
			device.put("ip", ip);
			device.put("ua", PhoneInfoGetter.getUserAgent(context));
			device.put("geo", buildGeoJson());
			device.put("imei", PhoneInfoGetter.getIMEI(context));
			device.put("imsi", PhoneInfoGetter.getIMSI(context));
			device.put("anid", PhoneInfoGetter.getAndroidID(context));
			device.put("aaid", YumiGooglePlayServiceCheckUtils.getGooglePlayID(context));
			device.put("udid", "");
			device.put("ouid", "");
			device.put("mac", PhoneInfoGetter.getMAC(context));
			device.put("mnc", PhoneInfoGetter.getMNC(context));
			device.put("mcc", PhoneInfoGetter.getMCC(context));
			device.put("lang", PhoneInfoGetter.getLanguage());
			device.put("maker", PhoneInfoGetter.getManufacture());
			device.put("brand", PhoneInfoGetter.getBrand());
			device.put("model", PhoneInfoGetter.getModel());
			device.put("os", "1");
			device.put("osv", PhoneInfoGetter.getSysVersion());
			device.put("conntype", getConnType());
			device.put("ifa", "");
			device.put("oifa", "");
			device.put("idv", "");
			int[] displayMetrics = PhoneInfoGetter.getDisplayMetrics(context);
			device.put("sw", displayMetrics[0]);
			device.put("sh", displayMetrics[1]);
			device.put("den", PhoneInfoGetter.getDisplayDensityDpi(context));
			device.put("ori", WindowSizeUtils.isPortrait(context) ? 1 : 2);
			device.put("jb", 0);
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return device;
	}

	private String getConnType() {
		String conn = NetworkStatusHandler.getConnectedNetName(context);
		String connType = "6";
		if (conn.equals("wifi")) {
			connType = "1";
		}
		if (conn.equals("2g")) {
			connType = "2";
		}
		if (conn.equals("3g")) {
			connType = "3";
		}
		if (conn.equals("4g")) {
			connType = "4";
		}
		return connType;
	}

	private JSONObject buildGeoJson() {
		JSONObject geo = new JSONObject();
		Location lastKnownLocation = LocationHandler.getLocHandler().getLastKnownLocation(context);
		float lat = 0f;
		float lon = 0f;
		if (lastKnownLocation != null ) {
			lat =  (float) lastKnownLocation.getLatitude();
			lon = (float) lastKnownLocation.getLongitude();
		}
		try {
			geo.put("lat", lat);
			geo.put("lon", lon);
			geo.put("cc", "");
			geo.put("city", "");
		} catch (Exception e) {
		}
		return geo;
	}

	private JSONObject buildAppJson(String pid) {
		JSONObject app = new JSONObject();
		PackageManager pm = context.getPackageManager();
		String packageName = context.getPackageName();
		try {
			app.put("pid", pid);
			app.put("id", pid);
			app.put("bundle", packageName);
			app.put("name", PackageInfoGetter.getAppName(pm, packageName));
			app.put("paid", 0);
			app.put("storeurl", "");
			app.put("appv", PackageInfoGetter.getAppVersionName(pm, packageName));
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return app;
	}
	
	private JSONArray buildImpJsonArray(String positionId, int wpix, int hpix) {
		JSONArray imps = new JSONArray();
		JSONObject imp = new JSONObject();
		try {
			imp.put("positionid", positionId);
			imp.put("adtype", getAdtype());
			imp.put("w", wpix);
			imp.put("h", hpix);
			imps.put(imp);
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return imps;
	}

	private int getAdtype() {
		int adtype = 1;
		if (layerType == LayerType.TYPE_BANNER) {
			adtype = 1;
		}
		if (layerType == LayerType.TYPE_INTERSTITIAL) {
			adtype = 2;
		}
		return adtype;
	}
	
	
	private String getRequestID(String timestemp) {
		StringBuffer sb = new StringBuffer("");
		sb.append("ZAD");
		sb.append(Encrypter.doMD5Encode16("zplay_api" + timestemp));
		sb.append(timestemp);
		String requestID = sb.toString();
		ZplayDebug.v(TAG, "chance request id is " + requestID, onoff);
		return requestID;
	}
	
	@SuppressWarnings("unchecked")
	final void reportTracker(String tracker){
		WebTaskHandler reportTask = new WebTaskHandler(context, new WebTask() {
				
				@Override
				public void doTask(String data, String msg) {
					ZplayDebug.v(TAG, "report tracker result " + data + " / " + msg, onoff);
				}
			}, false, false);
		reportTask.executeOnPool(WebParamsMapBuilder.buildParams(tracker, null));
	}
	
	
	final void onDestroy(){
		if (task != null) {
			task.cancel(true);
		}
	}
}
