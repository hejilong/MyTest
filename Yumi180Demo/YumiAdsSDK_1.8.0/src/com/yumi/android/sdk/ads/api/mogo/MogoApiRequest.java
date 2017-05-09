package com.yumi.android.sdk.ads.api.mogo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.yumi.android.sdk.ads.api.ApiRequest;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.encrypt.Encrypter;
import com.yumi.android.sdk.ads.utils.encrypt.YumiSignUtils;
import com.yumi.android.sdk.ads.utils.network.WebParamsMapBuilder;
import com.yumi.android.sdk.ads.utils.network.WebTask;
import com.yumi.android.sdk.ads.utils.network.WebTaskHandler;


/**
 * 160111 update API requet params
 *
 */

final class MogoApiRequest extends ApiRequest {
	private static final String TAG = "MogoApiRequest";
	private static final String[] BANNER_KEYS = new String[] { "ver", "aid", "ast",
			"w", "h", "sw", "sh", "p", "imei", "anid", "mac", "imsi","net",  "ua",
			"bn", "mn", "os", "ov", "la", "den", "mk" ,"mnc", "net" , "lat", "lon", "co", "lac", "cell" , "pkg"};
	private static final String[] INSTERTITIAL_KEYS = new String[] { "ver", "aid", "ast",
		"sw", "sh", "p", "imei", "anid", "mac", "imsi","net", "ua",
		"bn", "mn", "os", "ov", "la", "den","so", "mk", "mnc", "net" , "lat", "lon", "co", "lac", "cell" , "pkg" };
	private static final String URL = "qDdt5Ble1ZbyAogDGXn0tw5ywnwCMcWsS5azYhlvoQ4=";
//	private static final String URL_NEW = "http://zhangyou.adsmogo.net/ad/";
	private static final String MD5 = Encrypter.doMD5Encode16("zplay");
	
	private IYumiAPIRequestListener listener;
	private Context context;
	private WebTaskHandler task;
	private String[] values;
	private boolean isInstertitial;

	MogoApiRequest(Context context, IYumiAPIRequestListener listener,
			String[] parValues, boolean isInstertitial) {
		this.context = context;
		this.listener = listener;
		this.values = parValues;
		this.isInstertitial = isInstertitial;
	}

	@SuppressWarnings("unchecked")
	final void requestApi() {
			Map<String, String> headers = createHeader();
			Map<String, Object> params = WebParamsMapBuilder.buildParams(Encrypter.doDESDecode(URL, MD5), isInstertitial ? INSTERTITIAL_KEYS : BANNER_KEYS ,
					values);
			task = new WebTaskHandler(context, new WebTask() {
				@Override
				public void doTask(String data, String msg) {
					Integer code = Integer.valueOf(msg);
					ZplayDebug.i(TAG, "mogo api response code " + code, onoff);
					if (code != 200) {
						if (code == -1) {// 没网
							listener.onAPIRequestDone(null, LayerErrorCode.ERROR_INVALID_NETWORK);
						}
						if (code >= 500 && code <= 601) { // bad request
							listener.onAPIRequestDone(null, LayerErrorCode.ERROR_INVALID);
						}
						if (code >= 602) {
							listener.onAPIRequestDone(null, LayerErrorCode.ERROR_INTERNAL);
						}
					}else {
						if (NullCheckUtils.isNotNull(data)) {
							listener.onAPIRequestDone(data, LayerErrorCode.CODE_SUCCESS);
						}else {
							listener.onAPIRequestDone(null, LayerErrorCode.ERROR_NO_FILL);
						}
					}
				}
			}, true, false);
			task.setHeaders(headers);
			task.executeOnPool(params);
	}
	
	
	private Map<String, String> createHeader() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Encrypter.doDESDecode("cvmmvladPctneKJq1N2+AflJzXz9FcPS", MD5), getAuthSecret());
		headers.put(Encrypter.doDESDecode("cvmmvladPcvfElD1SRJiq9JSznsxY4rI", MD5), "zhangyou_ssp");
		return headers;
	}

	private final String getAuthSecret() {
		if (values != null) {
			return YumiSignUtils.getMogoSign(isInstertitial ? INSTERTITIAL_KEYS : BANNER_KEYS, values);
		}
		return "";
	}
	
	final void onDestroy(){
		if (task != null) {
			task.cancel(true);
		}
	}
}
