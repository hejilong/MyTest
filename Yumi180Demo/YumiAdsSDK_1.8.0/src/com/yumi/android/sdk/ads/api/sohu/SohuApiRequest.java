package com.yumi.android.sdk.ads.api.sohu;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import com.yumi.android.sdk.ads.api.ApiRequest;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.utils.encrypt.Encrypter;
import com.yumi.android.sdk.ads.utils.json.JsonResolveUtils;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;
import com.yumi.android.sdk.ads.utils.network.ParamsBuilder;
import com.yumi.android.sdk.ads.utils.network.WebParamsMapBuilder;
import com.yumi.android.sdk.ads.utils.network.WebTask;
import com.yumi.android.sdk.ads.utils.network.WebTaskHandler;

public class SohuApiRequest extends ApiRequest {
	private static final String TAG = "SohuApiRequest";
	/**http://s.go.sohu.com/adgtrout/*/
	private static final String API_URL = "pLGq9bCpKiNcvuFTvjerMs7XvbCGJEzC2vM8gM4OTD4=";
	/**http://i.go.sohu.com/count/v*/
	private static final String LOAD_URL = "FRd0iVo9DbqqNpOoGKwToSJIzsIx/jzsvTm8wSpCTAY=";
	/**http://i.go.sohu.com/count/av*/
	private static final String EXPOSURE_URL = "FRd0iVo9DbqqNpOoGKwToSJIzsIx/jzsoDreAVLmWeQ=";
	/**http://i.go.sohu.com/count/c*/
	private static final String CLICK_URL = "FRd0iVo9DbqqNpOoGKwToSJIzsIx/jzsft8KuBoYsEA=";
	/**http://i.go.sohu.com/count/v*/
	private static final String FAILURE_URL = "FRd0iVo9DbqqNpOoGKwToSJIzsIx/jzsvTm8wSpCTAY=";
	
    //测试环境
//    private static final String API_URL = "http://s.go.sohu.com/adgtrout_test";
//    private static final String LOAD_URL = "http://i.go.sohu.com/countout_test/v";
//    private static final String EXPOSURE_URL = "http://i.go.sohu.com/countout_test/av";
//    private static final String CLICK_URL = "http://i.go.sohu.com/countout_test/c";
//    private static final String FAILURE_URL = " http://i.go.sohu.com/countout_test/v";
	
	
	private static final String[] REQ_KEY = new String[] { 
//		    "bucketid",	//TODO 测试环境需要
		    "supplyid", // 平台标识ID
		    "itemspaceid",// 广告位ID
			"developerid",// 开发者ID
			"appid", // 第三方Appid
			"adps", // 广告位接受的图片大小
			"apt", // 广告位所属站点
			"sv", // 操作系统＋App版本号
			"nets", // 用户网络环境
			"appv", // App版本号
			"cid", // 移动端用户标识idfa或者imei的md5值
			"adsid", // 移动端用户标识idfa或者imei
			"imsi", 
			"imei", 
			"AndroidID" };
	private static final String[] REP_KEY = new String[] {
			"cid", // 移动端用户标识idfa或者imei的md5值
			"impid",// impressionId
			"appv",// App版本号
			"apid", // itemspaceid
			"mkey", "viewmonitor", "clickmonitor",
			"sys", // 操作系统版本
			"pn", // 手机型号
			"nets",// 网络环境
			"scs", // 手机屏幕的宽高
			"supplyid",// 平台标识id
			"appid", 
			"developerid",
			"timetag", 
			"mac", 
			"imei", 
			"imsi",
			"AndroidID" };
	private static final String[] FAIL_KEY = new String[] {
			"cid", // 移动端用户标识idfa或者imei的md5值
			"impid",// impressionId
			"appv",// App版本号
			"apid", // itemspaceid
			"mkey", 
			"viewmonitor",
			"clickmonitor",
			"sys", // 操作系统版本
			"pn", // 手机型号
			"nets",// 网络环境
			"scs", // 手机屏幕的宽高
			"supplyid",// 平台标识id
			"appid", "developerid", "timetag", "mac", "imei", "imsi",
			"AndroidID", 
			 "status"};
	private Context context;
	private IYumiAPIRequestListener listener;
	private WebTaskHandler task;
	private String clickmonitor;
	private String impid;
	private String apid;
	private String mkey;
	private String viewmonitor;
	private SohuClickListener cl;
	private String appid;
	private String developerid;
	private Activity activity;
	private String click;

	public SohuApiRequest(Activity activity, Context context,
			IYumiAPIRequestListener listener) {
		this.activity = activity;
		this.context = context;
		this.listener = listener;
	}

	@SuppressWarnings("unchecked")
	public void requestApi(String developerid, String itemspaceid,
			String appid, String adps) {
		this.appid = appid;
		this.developerid = developerid;
		if (task != null) {
			task.cancel(true);
		}
		Map<String, Object> params = WebParamsMapBuilder.buildParams(YumiDes3Util.decodeDes3(API_URL),REQ_KEY, buildReqValues(developerid, itemspaceid, appid, adps));
//		Map<String, Object> params = WebParamsMapBuilder.buildParams(API_URL,REQ_KEY, buildReqValues(developerid, itemspaceid, appid, adps));  //测试环境
		task = new WebTaskHandler(context, new WebTask() {

			@Override
			public void doTask(String data, String msg) {
				if (Integer.valueOf(msg) == 200) {
					if (NullCheckUtils.isNotNull(data)) {
						dealWithData(data);
					}
				} else {
                    if (msg != null) {
                        if (Integer.valueOf(msg) != 200) {
                            if (NullCheckUtils.isNotNull(data)) {
                                try {
                                    JSONObject date = new JSONObject(data);
                                    saveExposureReportClickmonitor(JsonResolveUtils.getStringFromJson(date, "clickmonitor", ""));
                                    saveExposureReportViewmonitor(JsonResolveUtils.getStringFromJson(date, "viewmonitor", ""));
                                    saveExposureReportImpid(JsonResolveUtils.getStringFromJson(date, "impressionid", ""));
                                    saveExposureReportApid(JsonResolveUtils.getStringFromJson(date, "itemspaceid", ""));
                                   Map<String, Object> params = WebParamsMapBuilder.buildParams(YumiDes3Util.decodeDes3(FAILURE_URL), FAIL_KEY,buildFailValues(clickmonitor, impid, apid, mkey, viewmonitor));
                                   reportEvent(YumiDes3Util.decodeDes3(FAILURE_URL), FAIL_KEY,buildFailValues(clickmonitor, impid, apid, mkey, viewmonitor));
                                    
//                                    Map<String, Object> params = WebParamsMapBuilder.buildParams(FAILURE_URL, FAIL_KEY,buildFailValues(clickmonitor, impid, apid, mkey, viewmonitor)); //测试环境
//                                    reportEvent(FAILURE_URL, FAIL_KEY,buildFailValues(clickmonitor, impid, apid, mkey, viewmonitor)); //测试环境
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            listener.onAPIRequestDone(null, LayerErrorCode.ERROR_NON_RESPONSE);

                        }
					}
				}
			}

		}, false, false);
		task.executeOnPool(params);
	}

	protected String[] buildFailValues(String clickmonitor, String impid,
			String apid, String mkey, String viewmonitor) {
		int[] screen = WindowSizeUtils.getRealSize(activity);
		if (mkey == null) {
			mkey = "";
		}
		String[] values = new String[] {
				Encrypter.doMD5Encode(PhoneInfoGetter.getIMEI(context)), // 移动端用户标识idfa或者imei的md5值
				impid,// impressionId
				PhoneInfoGetter.getAndroidSDK() + "",// App版本号
				apid, // itemspaceid
				mkey,
				viewmonitor,
				clickmonitor,
				"Android" + PhoneInfoGetter.getSysVersion(), // 操作系统版本
				PhoneInfoGetter.getModel(), // 手机型号
				getConn(),// 网络环境
				screen[0] * 100000 + screen[1] + "", // 手机屏幕的宽高
				"6",// 平台标识id
				appid,
				developerid,
				System.currentTimeMillis()+"", 
				PhoneInfoGetter.getMAC(context),
				PhoneInfoGetter.getIMEI(context),
				PhoneInfoGetter.getIMSI(context),
				PhoneInfoGetter.getAndroidID(context)
				,""+0};
		return values;
	}

	private String[] buildReqValues(String developerid, String itemspaceid,
			String appid, String adps) {
		String[] values = new String[] {
//				"2", //TODO 测试环境需要    
				"6", // 平台标识ID
				itemspaceid,// 广告位ID
				developerid,// 开发者ID
				appid, // 第三方Appid
				adps, // 广告位接受的图片大小
				"1", // 广告位所属站点
				"Android" + PhoneInfoGetter.getSysVersion(), // 操作系统＋App版本号
				NetworkStatusHandler.getConnectedNetName(context), // 用户网络环境
				PhoneInfoGetter.getAndroidSDK() + "", // App版本号
				Encrypter.doMD5Encode(PhoneInfoGetter.getIMEI(context)),// 移动端用户标识idfa或者imei的md5值
				PhoneInfoGetter.getIMEI(context), // 移动端用户标识idfa或者imei
				PhoneInfoGetter.getIMSI(context),
				PhoneInfoGetter.getIMSI(context),
				PhoneInfoGetter.getAndroidID(context) };
		return values;
	}

	protected void dealWithData(String data) {
		try {
			JSONArray list = new JSONArray(data);
			if (list.length() > 0) {
				JSONObject json = list.getJSONObject(0);
				ZplayDebug.d(TAG, json.toString(), onoff);
				saveExposureReportClickmonitor(JsonResolveUtils
						.getStringFromJson(json, "clickmonitor", ""));
				saveExposureReportImpid(JsonResolveUtils.getStringFromJson(
						json, "impressionid", ""));
				saveExposureReportApid(JsonResolveUtils.getStringFromJson(json,
						"itemspaceid", ""));
				JSONObject resource = JsonResolveUtils.getJsonObjectFromJson(
						json, "resource");

				if (resource != null) {

					String adcode = JsonResolveUtils.getStringFromJson(
							resource, "file", "");
					String imgxml = XMLRendering.renderingImgXML(adcode);
					if (NullCheckUtils.isNotNull(imgxml)) {
						listener.onAPIRequestDone(imgxml, null);
					} else {
						listener.onAPIRequestDone(null,
								LayerErrorCode.ERROR_NO_FILL);
					}
					saveExposureReportMkey(JsonResolveUtils.getStringFromJson(
							resource, "md5", ""));
					saveExposureReportViewmonitor(JsonResolveUtils
							.getStringFromJson(json, "viewmonitor", ""));
					saveExposureReportClick(JsonResolveUtils.getStringFromJson(
							resource, "click", null));
				} else {
					listener.onAPIRequestDone(null,
							LayerErrorCode.ERROR_NO_FILL);
				}
			} else {
				listener.onAPIRequestDone(null,
						LayerErrorCode.ERROR_INVALID_NETWORK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final void reportSohuClick(SohuClickListener cl) {
		this.cl = cl;
		reportSohuClick();
	}

	public void reportSohuClick() {
		ZplayDebug.v(TAG, "sohu report click ", onoff);
		reportEvent(YumiDes3Util.decodeDes3(CLICK_URL));
//		reportEvent(CLICK_URL); //测试环境

	}

	private void saveExposureReportClickmonitor(String stringFromJson) {
		this.clickmonitor = stringFromJson;
	}

	private void saveExposureReportImpid(String stringFromJson) {
		this.impid = stringFromJson;
	}

	private void saveExposureReportApid(String stringFromJson) {
		this.apid = stringFromJson;
	}

	private void saveExposureReportMkey(String stringFromJson) {
		this.mkey = stringFromJson;
	}

	private void saveExposureReportClick(String stringFromJson) {
		this.click = stringFromJson;
	}

	private void saveExposureReportViewmonitor(String stringFromJson) {
		this.viewmonitor = stringFromJson;
	}

	public interface SohuClickListener {
		public void onClick(boolean jumpWeb, String downloadUrl);
	}

	/**
	 * 展示上报
	 */
	public void reoportSohuExposure() {
		ZplayDebug.d(TAG, "Sohu report exposure ", onoff);
		reportEvent(YumiDes3Util.decodeDes3(EXPOSURE_URL));
//		reportEvent(EXPOSURE_URL); //测试环境
	}

	@SuppressWarnings("unchecked")
	private void reportEvent(final String url) {
        ZplayDebug.d(TAG, "clickmonitor = " + clickmonitor + "impid = " + impid + "apid = " + apid + "mkey = " + mkey + "viewmonitor = " + viewmonitor, onoff);
        if (NullCheckUtils.isNotNull(clickmonitor) && NullCheckUtils.isNotNull(impid) && NullCheckUtils.isNotNull(apid) && NullCheckUtils.isNotNull(mkey) && NullCheckUtils.isNotNull(viewmonitor)) {
//            Map<String, Object> params = WebParamsMapBuilder.buildParams(url, REP_KEY, buildRepValues(clickmonitor, impid, apid, mkey, viewmonitor));
//            new WebTaskHandler(context, new WebTask() {
//                @Override
//                public void doTask(String data, String msg) {
//                    ZplayDebug.d(TAG, "data = " + data, onoff);
//                    ZplayDebug.d(TAG, "msg = " + msg, onoff);
//                    if (url.equals(YumiDes3Util.decodeDes3(EXPOSURE_URL))) {
//                        ZplayDebug.d(TAG, "Sohu exposure report result is " + data, onoff);
//                    } else if (url.equals(YumiDes3Util.decodeDes3(LOAD_URL))) {
//                        ZplayDebug.d(TAG, "Sohu parper report result is " + data, onoff);
//                    } else if (YumiDes3Util.decodeDes3(CLICK_URL).equals(url)) {
//                        ZplayDebug.d(TAG, "Sohu click report result is " + data, onoff);
//                        ZplayDebug.d(TAG, "click = " + click, onoff);
//                        if (NullCheckUtils.isNotNull(click)) {
//                            if (cl != null) {
//                                cl.onClick(true, click);
//                            }
//                        }
//                    }
//                }
//
//			}, false, false).executeOnPool(params);
            
            String[] values = buildRepValues(clickmonitor, impid, apid, mkey, viewmonitor);
            String tempUrl = url;
            tempUrl += ParamsBuilder.buildGetParamsNotEncode(REP_KEY, values);
            requestReport(tempUrl);
		} else {
            ZplayDebug.i(TAG, "exposure id is null , the Sohu resoponse maybe error", onoff);
		}
	}
	
	
	private void reportEvent(String url, String[] keys,String[] values)
	{
        String tempUrl = url;
        tempUrl += ParamsBuilder.buildGetParamsNotEncode(FAIL_KEY, values);
        requestReport(tempUrl);
	}
	
	/**
     * 上报第三方
     * 
     * @param reporturl
     * @param entity
     */
    private void requestReport(final String reporturl) {
        ZplayDebug.i(TAG, "sohu API 上报地址：" + reporturl, onoff);
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    ZplayDebug.i(TAG, "sohu API 开始上报", onoff);
                    URL url = new URL(reporturl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(true);
                    conn.setConnectTimeout(2000);
                    conn.setDoInput(true);
                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                    int len = 0;
                    char[] buf = new char[1024];
                    StringBuffer sb = new StringBuffer();
                    while ((len = isr.read(buf)) != -1) {
                        sb.append(buf, 0, len);
                    }
                    isr.close();
                    String result = sb.toString();
                    ZplayDebug.d(TAG, "sohu API 上报返回：" + result, onoff);

                    if (url.equals(YumiDes3Util.decodeDes3(EXPOSURE_URL))) {
//                        if (url.equals(EXPOSURE_URL)) { //测试环境
                        ZplayDebug.d(TAG, "Sohu exposure report result is " + result, onoff);
                    } else if (url.equals(YumiDes3Util.decodeDes3(LOAD_URL))) {
//                    } else if (url.equals(LOAD_URL)) { //测试环境
                        ZplayDebug.d(TAG, "Sohu parper report result is " + result, onoff);
                    } else if (YumiDes3Util.decodeDes3(CLICK_URL).equals(url)) {
//                    } else if (CLICK_URL.equals(url)) { //测试环境
                        ZplayDebug.d(TAG, "Sohu click report result is " + result, onoff);
                        ZplayDebug.d(TAG, "click = " + click, onoff);
                        if (NullCheckUtils.isNotNull(click)) {
                            if (cl != null) {
                                cl.onClick(true, click);
                            }
                        }
                    }

                } catch (Exception e) {
                    ZplayDebug.e(TAG, "sohu API 上报异常: [url:] "+reporturl+" [error]: ", e, onoff);
                }
            }
        };
        new Thread(run).start();
    }

	private String getConn() {
		String connectedNetName = NetworkStatusHandler
				.getConnectedNetName(context);
		String name = "";
		if (NullCheckUtils.isNotNull(connectedNetName)) {
			if (connectedNetName.equalsIgnoreCase("wifi")) {
				name = "wifi";
			}
			if (connectedNetName.equalsIgnoreCase("2g")) {
				name = "2g";
			}
			if (connectedNetName.equalsIgnoreCase("3g")) {
				name = "3g";
			}
			if (connectedNetName.equalsIgnoreCase("4g")) {
				name = "4g";
			}
		}
		return name;
	}

	private String[] buildRepValues(String clickmonitor, String impid,
			String apid, String mkey, String viewmonitor) {
		int[] screen = WindowSizeUtils.getRealSize(activity);
		String[] values = new String[] {
				Encrypter.doMD5Encode(PhoneInfoGetter.getIMEI(context)), // 移动端用户标识idfa或者imei的md5值
				impid,// impressionId
				PhoneInfoGetter.getAndroidSDK() + "",// App版本号
				apid, // itemspaceid
				mkey,
				viewmonitor,
				clickmonitor,
				"Android" + PhoneInfoGetter.getSysVersion(), // 操作系统版本
				PhoneInfoGetter.getModel(), // 手机型号
				getConn(),// 网络环境
				screen[0] * 100000 + screen[1] + "", // 手机屏幕的宽高
				"6",// 平台标识id
				appid, 
				developerid,
				System.currentTimeMillis()+"",
				PhoneInfoGetter.getMAC(context),
				PhoneInfoGetter.getIMEI(context),
				PhoneInfoGetter.getIMSI(context),
				PhoneInfoGetter.getAndroidID(context) };
		return values;
	}

	/**
	 * 加载上报
	 */
	public void reoportSohuPrepared() {
		reportEvent(YumiDes3Util.decodeDes3(LOAD_URL));
//		reportEvent(LOAD_URL); //测试环境
	}

	void onDestroy() {
		if (task != null) {
			task.cancel(true);
		}
	}

}