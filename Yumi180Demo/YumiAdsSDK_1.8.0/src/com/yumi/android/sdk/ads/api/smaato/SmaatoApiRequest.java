package com.yumi.android.sdk.ads.api.smaato;

import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.location.Location;

import com.yumi.android.sdk.ads.api.ApiRequest;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiGooglePlayServiceCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.encrypt.Encrypter;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;
import com.yumi.android.sdk.ads.utils.location.LocationHandler;
import com.yumi.android.sdk.ads.utils.network.WebParamsMapBuilder;
import com.yumi.android.sdk.ads.utils.network.WebTask;
import com.yumi.android.sdk.ads.utils.network.WebTaskHandler;

/**
 * 160111 update API request params
 *
 */

final class SmaatoApiRequest extends ApiRequest {

	private static final String TAG = "SmaatoApiRequest";
	/**http://soma.smaato.net/oapi/reqAd.jsp*/
	private static final String API_URL = "pLGq9bCpKiMXAALM06xRp33bmdiLp/l3PWSnzJHSGgR78a7eUAch7A==";
	private static final String[] REQ_KEY = new String[]{
		"apiver",
		"adspace",
		"pub",
		"devip",
		"device",//
		"divid",//
//		"ref",//app应用不需要传此字段
		"nsupport",//
//		"vastver", //如果不是请求视频的话不填
//		"nver", //如果是原生不需要传
//		"videotype",//如果不是请求视频的话不填
		"formatstrict",//
		"bbid",//
		"carrier",//
		"width",//
		"height",//
		"session",//
		"format",
		"dimension",
		"dimensionstrict",
		"googleadid",
		"googlednt",
		"androidid",
		"response",
		"coppa",
		"carriercode",
        "mraidver",
		"bundle",
		"gps",
		"devicemodel",
		"devicemake"
		};
			
	
	private Context context;
	private IYumiAPIRequestListener listener;
	private WebTaskHandler task;
	
	
	
	public SmaatoApiRequest(Context context, IYumiAPIRequestListener listener){
		this.context = context;
		this.listener = listener;
	}
	
	@SuppressWarnings("unchecked")
	public void requestApi(String pub, String adspace, String ip, String dimension,int width,int height){
		if (task != null) {
			task.cancel(true);
		}
		Map<String, Object> params = WebParamsMapBuilder.buildParams(YumiDes3Util.decodeDes3(API_URL), REQ_KEY, buildReqValues(adspace, pub, ip, dimension,width,height));
		task = new WebTaskHandler(context, new WebTask() {
			
			@Override
			public void doTask(String data, String msg) {
				if (NullCheckUtils.isNotNull(data)) {
					if (data.equals("<p>&nbsp;</p>")) {
						ZplayDebug.d(TAG, "smaato api request no fill", onoff);
						callbackApiResult(null, LayerErrorCode.ERROR_NO_FILL);
						return;
					}else {
						StringBuilder builder = new StringBuilder();
						builder.append("<head><title></title><meta name=\"viewport\" content=\"user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"/><style type=\"text/css\">body {margin: 0; overflow: hidden;}</style></head>");
						builder.append(data);
						String html = builder.toString();
//						if (html.contains("width=\"320\"")) {
//							html = html.replace("width=\"320\"", "width=\"100%\"").replace("height=\"480\"", "height=\"100%\"");
//						}
						callbackApiResult(html, LayerErrorCode.CODE_SUCCESS);
					}
				}else {
					ZplayDebug.d(TAG, "smaato api request failed", onoff);
					callbackApiResult(null, LayerErrorCode.ERROR_INTERNAL);
				}
			}
		}, false, false);
		task.executeOnPool(params);
	}
	
	private void callbackApiResult(String data, LayerErrorCode error){
		if (listener != null ) {
			listener.onAPIRequestDone(data, error);
		}
	}
	
	
	private String[] buildReqValues(String adspace, String pub, String ip, String dimension,int width,int height){
		String gps = "";
		Location lastKnownLocation = LocationHandler.getLocHandler().getLastKnownLocation(context);
		if (lastKnownLocation != null) {
			gps = lastKnownLocation.getLatitude() + ","+ lastKnownLocation.getLongitude();
		}
		String[] values = new String[]{
//				"apiver",
				"502",
//				"adspace",
				adspace,
//				"pub",
				pub,
//				"devip",
				ip,
//				"device",//
				PhoneInfoGetter.getUserAgent(context),
//				"divid",//
				"smt-"+adspace,
//				"nsupport",title, txt, icon, image, ctatext是否支持原生的这些元素
				"image",
//				"formatstrict",//
				"false",
//				"bbid",//imei
				PhoneInfoGetter.getIMEI(context),
//				"carrier",//
				getCurrentCarrier(),//
//				"width",//
				width+"",
//				"height",//
				height+"",
//				"session",//
 				Encrypter.doMD5Encode(PhoneInfoGetter.getDeviceID(context)+new Date()),
//				"format",
 				"all",
//				"dimension",
 				dimension,
//				"dimensionstrict",
 				"true",
//				"googleadid",
 				YumiGooglePlayServiceCheckUtils.getGooglePlayID(context),
//				"googlednt",
 				YumiGooglePlayServiceCheckUtils.getGooglePlayServiceADT() ? "true" : "false",
//				"androidid",
 				PhoneInfoGetter.getAndroidID(context),
//				"response",
 				"html",
//				"coppa",
 				"0",
//				"carriercode",
 				PhoneInfoGetter.getPLMN(context),
//		        "mraidver" 这个是指的支持的哪个版本的mraid ,如果不支持就填mraidver=0
 				"0",
//				"bundle",
				context.getPackageName(),
//				"gps",
				gps,
//				"devicemodel",
				PhoneInfoGetter.getModel(),
//				"devicemake"
				PhoneInfoGetter.getManufacture()

		};
		return values;
	}
	/***
	 *  获取运营商 1代表中国移动2代表中国联通 3代表中国电信 4 代表中国铁通
	 * @return
	 */
private String getCurrentCarrier(){
	String carrier = "";
	int code = PhoneInfoGetter.getOperator(context);
	switch (code) {
	case 0:
		carrier = "unknown";
		break;
	case 1:
		carrier = "China Mobile";
		break;
	case 2:
		carrier = "China Unicom";
		break;
	case 3:
		carrier = "China Telecom";
		break;
	case 4:
		carrier = "China Railcom";
		break;
	default:
		break;
	}
	
	return carrier;
}
	void onDestroy() {
		if (task != null) {
			task.cancel(true);
		}
	}
	
}
