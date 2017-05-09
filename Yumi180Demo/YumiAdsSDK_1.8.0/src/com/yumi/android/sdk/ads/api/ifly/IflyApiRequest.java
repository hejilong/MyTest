package com.yumi.android.sdk.ads.api.ifly;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.yumi.android.sdk.ads.api.ApiRequest;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiGooglePlayServiceCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;
import com.yumi.android.sdk.ads.utils.json.JsonResolveUtils;
import com.yumi.android.sdk.ads.utils.location.LocationHandler;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;
import com.yumi.android.sdk.ads.utils.network.WebParamsMapBuilder;
import com.yumi.android.sdk.ads.utils.network.WebTask;
import com.yumi.android.sdk.ads.utils.network.WebTaskHandler;

import android.content.Context;
import android.location.Location;

/**
 * 160111 update API request params
 *
 */

final class IflyApiRequest extends ApiRequest {

	private static final String TAG = "IflyApiRequest";
	/**http://ws.voiceads.cn/ad/request*/
	private static final String API_URL = "Fg/aekQ24H4wlfyEymnTyrp7ENaAHQ+UO8se2g7DXt4DRzo3AKMD3g=="; 
	private IYumiAPIRequestListener listener;
	private Context context;
	private WebTaskHandler task;
	
	
	IflyApiRequest(Context context, IYumiAPIRequestListener listener){
		this.context = context;
		this.listener = listener;
	}
	
	@SuppressWarnings("unchecked")
	final void requestApi(String appID, String unitID, String ip, int wdip, int hdip){
		JSONObject req = buildJsonRequest(appID, unitID, ip, wdip, hdip);
		Map<String, Object> params = WebParamsMapBuilder.buildParams(YumiDes3Util.decodeDes3(API_URL), req.toString());
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
		task.setHeaders(getHeader());
		task.executeOnPool(params);
	}
	
	private void dealResponse(String data) {
		try {
			JSONObject response = new JSONObject(data);
			int rc = JsonResolveUtils.getIntFromJson(response, "rc", -1);
			if (rc == 70204) {
				callbackApiResult(null, LayerErrorCode.ERROR_NO_FILL);
				return;
			}
			if (rc == 70200) {
				if (response.has("html")) {
					String html = JsonResolveUtils.getStringFromJson(response, "html", "");
					callbackApiResult(html, LayerErrorCode.CODE_SUCCESS);
					return;
				}else {
					ZplayDebug.w(TAG, "iFly don't support original material ", onoff);
				}
			}
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		callbackApiResult(null, LayerErrorCode.ERROR_INTERNAL); 
	}

	private void callbackApiResult(String data, LayerErrorCode error){
		if (listener != null) {
			listener.onAPIRequestDone(data, error);
		}
	}
	
	private Map<String, String> getHeader(){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-protocol-ver", "2.0");
		return headers;
	}
	
	private JSONObject buildJsonRequest(String appID, String unitID, String ip, int wdip, int hdip){
		JSONObject request = new JSONObject();
		try {
			request.put("adunitid", unitID);
			request.put("tramaterialtype ", "html");//1.2.9
			request.put("is_support_dee plink ", "0");//1.2.9
			request.put("devicetype", "0");
			request.put("os", "Android");
			request.put("osv", PhoneInfoGetter.getSysVersion());
			request.put("adid", PhoneInfoGetter.getAndroidID(context));
			request.put("imei", PhoneInfoGetter.getIMEI(context));
			request.put("mac", PhoneInfoGetter.getMAC(context));
			request.put("aaid", YumiGooglePlayServiceCheckUtils.getGooglePlayID(context));
			request.put("density", PhoneInfoGetter.getDisplayDensity(context) + "");
			request.put("operator", getOperator());
			request.put("net", getConnectType());
			request.put("ip", ip);
			request.put("ua", PhoneInfoGetter.getUserAgent(context));
			request.put("ts", System.currentTimeMillis()+"");
			request.put("adw", wdip+"");
			request.put("adh", hdip+"");
			int[] displayMetrics = PhoneInfoGetter.getDisplayMetrics(context);
			request.put("dvw", displayMetrics[0]+"");
			request.put("dvh", displayMetrics[1]+"");
			request.put("orientation", WindowSizeUtils.isPortrait(context) ? "0" : "1" );
			request.put("vendor", PhoneInfoGetter.getManufacture());
			request.put("model", PhoneInfoGetter.getModel());
			request.put("lan", PhoneInfoGetter.getLanguage());
			Location lastKnownLocation = LocationHandler.getLocHandler().getLastKnownLocation(context);
			String geo = "";
			if (lastKnownLocation != null) {
				geo = lastKnownLocation.getLongitude() + "," + lastKnownLocation.getLatitude();
			}
			request.put("ssid", NetworkStatusHandler.getConnectWifiSsid(context));
			request.put("geo", geo);
			request.put("isboot", "0");//1.2.9
			request.put("batch_cnt", "0");
			request.put("appid", appID);
			request.put("appname", PackageInfoGetter.getAppName(context.getPackageManager(), context.getPackageName()));
			request.put("pkgname", context.getPackageName());
			ZplayDebug.v(TAG, "ifly api request json is " + request.toString(), onoff);
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return request;
	}


	private String getConnectType() {
		String name = NetworkStatusHandler.getConnectedNetName(context);
		if (name.equals("wifi")) {
			return "2";
		}
		if (name.equals("2g")) {
			return "4";
		}
		if (name.equals("3g")) {
			return "5";
		}
		if (name.equals("4g")) {
			return "6";
		}
		return "0";
	}


	private String getOperator() {		
		int operator = PhoneInfoGetter.getOperator(context);
		if (operator == 1) {
			return "Mobile";
		}
		if (operator == 2) {
			return "Unicom";
		}
		if (operator == 3) {
			return "Telecom";
		}
		if (operator == 4) {
			return "Railcom";
		}
		return "";
	}
	
	final void onDestroy(){
		if (task != null) {
			task.cancel(true);
		}
	}
	
}
