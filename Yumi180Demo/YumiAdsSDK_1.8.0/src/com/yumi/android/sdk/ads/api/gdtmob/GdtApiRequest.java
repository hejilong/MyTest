package com.yumi.android.sdk.ads.api.gdtmob;

import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yumi.android.sdk.ads.api.ApiRequest;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiGooglePlayServiceCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.encrypt.Encrypter;
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


final class GdtApiRequest extends ApiRequest {
	
	public static final int TRANSFORM_TYPE_DOWNLOAD = 5;
	public static final int TRANSFORM_TYPE_DOWNLOAD_COMPLETE = 7;
	public static final int TRANSFORM_TYPE_INSTALL = 6;
	public static final int REQ_TYPE_BANNER = 0x123;
	public static final int REQ_TYPE_INSTERTITIAL =0x213;
	

	private static final String TAG = "GdtApiRequest";
	private static final String[] REQ_KEYS = new String[]
		{
		        "adposcount", // 1
		        "count", // 1
		        "posid",
		        "posw",
		        "posh",
		        "charset", // utf8
		        "datafmt", // html
		        "ext"
		};

	/**http://mi.gdt.qq.com/gdt_mview.fcg*/
	private static final String REQUEST_URL = "B17e86zmKq0vdxC1RVAp1cbL3im01QgnPy7OhBZTMqpAeRK3VRTyvw==";
	/**http://v.gdt.qq.com/gdt_stats.fcg*/
	private static final String REPORT_URL_EXP = "BtSHitvBooDf1J/3/EVZAV9+xn/FmRuWp5PaE9gY/srL9Rh1IqiafQ==";
	/**http://c.gdt.qq.com/gdt_trace_a.fcg*/
	private static final String REPORT_URL_TRANSFORM = "3nuX3i0MR3ONYa9QDsclx7SscbKB8eSvw25KlpIREvwosGwKm3PDGw==";
	
	private IYumiAPIRequestListener listener;
	private Context context;
	private WebTaskHandler task;
	private String posID;
	private String targetId;
	private int acttypeId = -1;
	private String viewID;
	private String reportUrl;
	private GdtClickListener cl;
	private String clickId;
	private int reqType;

	GdtApiRequest(Context context, IYumiAPIRequestListener listener, int reqType) {
		this.context = context;
		this.listener = listener;
		this.reqType = reqType;
	}

	@SuppressWarnings("unchecked")
	final void requestApi(String appid, String positionID, String width, String height, int postype, String ip) {
			Map<String, Object> params = WebParamsMapBuilder.buildParams(YumiDes3Util.decodeDes3(REQUEST_URL), REQ_KEYS, buildReqParams(positionID, width, height, appid, postype, ip));
			if (task != null) {
				task.cancelTask();
			}
			task = new WebTaskHandler(context, new WebTask() {
				
				@Override
				public void doTask(String data, String msg) {
					if (data != null) {
						dealWithData(data);
					}else {
						if (msg != null) {
							if (Integer.valueOf(msg) == -1) {
								listener.onAPIRequestDone(null, LayerErrorCode.ERROR_INVALID_NETWORK);
							}else {
								requestThirdApiFailed(LayerErrorCode.ERROR_INTERNAL);
							}
						}
					}
				}
			}, false, false);
			task.executeOnPool(params);
	}

	private void dealWithData(String data) {
		try {
			JSONObject json = new JSONObject(data);
			int ret = JsonResolveUtils.getIntFromJson(json, "ret", -1);
			if (ret != 0) {//返回错误
				ZplayDebug.i(TAG, "-------9", onoff);
				requestThirdApiFailed(LayerErrorCode.ERROR_INTERNAL);
			}else {
				int rpt = JsonResolveUtils.getIntFromJson(json, "rpt", 0);
				if (rpt != 0) {//系统异常
					ZplayDebug.i(TAG, "-------8", onoff);
					requestThirdApiFailed(LayerErrorCode.ERROR_INTERNAL);
				}else {//系统正常
					JSONObject objData = JsonResolveUtils.getJsonObjectFromJson(json, "data");
					if (objData != null) {
						if (NullCheckUtils.isNotNull(posID)) {
						JSONObject pos = JsonResolveUtils.getJsonObjectFromJson(objData, posID);
						if (pos != null) {
							if (JsonResolveUtils.getIntFromJson(pos, "ret", 0) != 0) {//广告请求异常
								ZplayDebug.i(TAG, "-------7", onoff);
								requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
							}else {
								JSONArray list = JsonResolveUtils.getJsonArrayFromJson(pos, "list");
								if (list != null && list.length() > 0) {
									JSONObject materiel = list.getJSONObject(0);
									if (materiel != null) {
										saveReportUrl(JsonResolveUtils.getStringFromJson(materiel, "rl", ""));
										saveExposureReportId(JsonResolveUtils.getStringFromJson(materiel, "viewid", ""));
										saveClickReportInfo(JsonResolveUtils.getIntFromJson(materiel, "acttype", -1));
										saveTransformReportInfo(JsonResolveUtils.getStringFromJson(materiel, "targetid", ""));
										if (reqType == REQ_TYPE_BANNER) {
											String htmlOriginal = JsonResolveUtils.getStringFromJson(materiel, "html_snippet", "");
											String html = cutGdtLogo(htmlOriginal);
											if (NullCheckUtils.isNotNull(html)) {
												listener.onAPIRequestDone(html, null);
											}else {
												ZplayDebug.i(TAG, "-------1", onoff);
												requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
											}
										}else {
											dealInstertitialMateriel(materiel);
										}
									}else {
										ZplayDebug.i(TAG, "-------2", onoff);
										requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
									}
								}else {
									ZplayDebug.i(TAG, "-------3", onoff);
									requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
								}
							}
						}else {
							ZplayDebug.i(TAG, "-------4", onoff);
							requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL); //pos为空, 则无广告
						}
					}
					}else {
						ZplayDebug.i(TAG, "-------5", onoff);
						requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
					}
				}
			}

		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
			//json  异常. 回调内部错误
			ZplayDebug.i(TAG, "-------6", onoff);
			requestThirdApiFailed(LayerErrorCode.ERROR_INTERNAL);
		}
	}

	private static final String cut = "<img src=\"http://imgcache.qq.com/gdt/cdn/api/static/image/gdt_logo.png\"";
	private String cutGdtLogo(String htmlOriginal) {
		boolean contains = htmlOriginal.contains(cut);
		if (contains) {
			int from = htmlOriginal.indexOf(cut);
			int to = htmlOriginal.indexOf("/>", from) + 2;
			String substring = htmlOriginal.substring(from, to);
			String replace = htmlOriginal.replace(substring, "");
			return replace;
		}
		return htmlOriginal;
	}
	
	private void dealInstertitialMateriel(JSONObject materiel) {
		ZplayDebug.i(TAG, materiel.toString(), onoff);
		int crt_type = JsonResolveUtils.getIntFromJson(materiel, "crt_type", -10);
		switch (crt_type) {
		case -10:
			requestThirdApiFailed(LayerErrorCode.ERROR_INTERNAL);
			break;
		case 2: // 图片
			String imgUrl = JsonResolveUtils.getStringFromJson(materiel, "img", "");
			String imgxml = XMLRendering.renderingImgXML(imgUrl);
			if (NullCheckUtils.isNotNull(imgxml)) {
				listener.onAPIRequestDone(imgxml, null);
			}else {
				requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
			}
			break;
		case 3: // 图文
		case 7: // 图文
			String icon = JsonResolveUtils.getStringFromJson(materiel, "img", "");
			String title = JsonResolveUtils.getStringFromJson(materiel, "txt", "");
			String desc = JsonResolveUtils.getStringFromJson(materiel, "desc", "");
			String imgtextxml = XMLRendering.renderingImgTextXML(icon, title, desc);
			if (NullCheckUtils.isNotNull(imgtextxml)) {
				listener.onAPIRequestDone(imgtextxml, null);
			}else {
				requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
			}
			break;
		default:
			requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
			break;
		}
	}

	private void requestThirdApiFailed(LayerErrorCode error) {
		listener.onAPIRequestDone(null, error);
	}

	@SuppressWarnings("unchecked")
	final void reoporGdtExposure(){
		ZplayDebug.i(TAG, "gdt report exposure ", onoff);
		if (NullCheckUtils.isNotNull(viewID)) {
			new WebTaskHandler(context, new WebTask() {
				
				@Override
				public void doTask(String data, String msg) {
					if (NullCheckUtils.isNotNull(data)) {
						ZplayDebug.i(TAG, "gdt exposure report result is " +  data, onoff);
					}
				}
			}, false, false).executeOnPool(WebParamsMapBuilder.buildParams(YumiDes3Util.decodeDes3(REPORT_URL_EXP),
					new String[]{"count", "viewid0"}, 
					new String[]{"1", viewID}));
		}else {
			ZplayDebug.i(TAG, "exposure id is null , the gdt resoponse maybe error", onoff);
		}
	}
	
	public final void reportGdtClick(GdtClickListener cl,double downX, double downY, double upX, double upY){
		this.cl = cl;
		reportGdtClick(downX, downY, upX, upY);
	}
	
	
	@SuppressWarnings("unchecked")
	private void reportGdtClick(double downX, double downY, double upX, double upY){
		if (acttypeId != -1) {
			ZplayDebug.v(TAG, "gdt report click ", onoff);
			@SuppressWarnings("deprecation")
			String url = String.format(reportUrl+"&acttype=%s&s=%s",URLEncoder.encode(acttypeId+""), URLEncoder.encode(buildClickStr(downX,downY, upX, upY)));
			if (acttypeId == 0 || acttypeId == 18) {
				cl.onClick(true, url);
			}else {
				new WebTaskHandler(context, new WebTask() {
					
					@Override
					public void doTask(String data, String msg) {
						if (NullCheckUtils.isNotNull(msg)) {
							Integer valueOf = Integer.valueOf(msg);
							ZplayDebug.i(TAG, "gdt api response code " + valueOf, onoff);
							if (valueOf == 200) { // 下载
								if (NullCheckUtils.isNotNull(data)) {
									try {
										JSONObject json = new JSONObject(data);
										if (JsonResolveUtils.getIntFromJson(json, "ret", -1) == 0) {
											JSONObject clickData = JsonResolveUtils.getJsonObjectFromJson(json, "data");
											if (clickData != null) {
												String downloadUrl = JsonResolveUtils.getStringFromJson(clickData, "dstlink", "");
												if (NullCheckUtils.isNotNull(downloadUrl)) {
													if (cl != null) {
														cl.onClick(false, downloadUrl);
													}
												}
												saveClickID(JsonResolveUtils.getStringFromJson(clickData, "clickid", ""));
											}
										}
									} catch (JSONException e) { // 无法转成json 使用跳转
										cl.onClick(true, data);
									}
								}
							}
						}
					}
				},false,false).executeOnPool(WebParamsMapBuilder.buildParams(url, null,null));
			}
		
		}
	}
	

	@SuppressWarnings("unchecked")
	final void reportGdtTransform(int reportType){
		if (NullCheckUtils.isNotNull(targetId) && NullCheckUtils.isNotNull(clickId)) {
			ZplayDebug.i(TAG, "gdt report transform " + reportType, onoff);
			new WebTaskHandler(context, new WebTask() {
				
				@Override
				public void doTask(String data, String msg) {
					
				}
			}, false, false).executeOnPool(WebParamsMapBuilder.buildParams(YumiDes3Util.decodeDes3(REPORT_URL_TRANSFORM), 
					new String[]{"actionid", "targettype", "tagetid", "clickid"}, 
					new String[]{reportType+"", "6", targetId, clickId}));
		}
	}
	
	private void saveClickID(String stringFromJson) {
		this.clickId = stringFromJson;
	}
	
	
	private String buildClickStr(double downX, double downY, double upX,
			double upY) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("down_x", downX+"");
			obj.put("down_y", downY+"");
			obj.put("up_x", upX+"");
			obj.put("up_y", upY+"");
			return obj.toString();
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return "{\"down_x\":\"-999\" , \"down_y\":\"-999\" , \"up_x\":\"-999\", \"up_y\":\"-999\"}";
	}

	private void saveTransformReportInfo(String stringFromJson) {
		this.targetId = stringFromJson;
		
	}

	private void saveClickReportInfo(int intFromJson) {
		this.acttypeId = intFromJson;
		
	}

	private void saveExposureReportId(String stringFromJson) {
		this.viewID = stringFromJson;
		
	}

	private void saveReportUrl(String stringFromJson) {
		this.reportUrl = stringFromJson;
	}

	private String[] buildReqParams(String posID, String width, String height, String appid, int postype, String ip){
		this.posID = posID;
		return new String[]{"1", "1", posID, width, height, "utf8" ,(reqType == REQ_TYPE_BANNER ?  "html" : "json") ,  createExtParams(appid, postype, ip)};
	}
	
	
	

	private String createExtParams(String appid, int postype, String ip) {
		JSONObject req = new JSONObject();
		try {
			req.put("req", buildExtJson(appid, postype, ip));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return req.toString();
	}

	private int getConn() {
		String connectedNetName = NetworkStatusHandler
				.getConnectedNetName(context);
		int conn = 0;
		if (NullCheckUtils.isNotNull(connectedNetName)) {
			if (connectedNetName.equalsIgnoreCase("wifi")) {
				conn = 1;
			}
			if (connectedNetName.equalsIgnoreCase("2g")) {
				conn = 2;
			}
			if (connectedNetName.equalsIgnoreCase("3g")) {
				conn = 3;
			}
			if (connectedNetName.equalsIgnoreCase("4g")) {
				conn = 4;
			}
		}
		return conn;
	}
	
	private JSONObject buildExtJson(String appid, int postype, String ip) {
		int[] displayMetrics = PhoneInfoGetter.getDisplayMetrics(context);
		int width = displayMetrics[0] <= displayMetrics[1] ? displayMetrics[0]
				: displayMetrics[1];
		int height = displayMetrics[0] > displayMetrics[1] ? displayMetrics[0]
				: displayMetrics[1];		
		Location loc = LocationHandler.getLocHandler().getLastKnownLocation(context);
		JSONObject ext = new JSONObject();
		try {
			ext.put("c_device", PhoneInfoGetter.getModel());
			//ext.put("c_ori", ""); TODO
			ext.put("c_w", width);
			ext.put("c_h", height);
			ext.put("c_pkgname", context.getPackageName());
			ext.put("muid", Encrypter.doMD5Encode(PhoneInfoGetter.getDeviceID(context)));
			ext.put("muidtype", 1);
			ext.put("conn", getConn());
			//ext.put("carrier", ""); TODO
			ext.put("c_os", "android");
			ext.put("apiver", "2.1");
			ext.put("postype", postype);
			ext.put("appid", appid);
			ext.put("carrier", PhoneInfoGetter.getOperator(context));
			ext.put("inline_full_screen", false);
			ext.put("c_ori", 0);
			ext.put("remoteip",ip );
			ext.put("c_osver", PhoneInfoGetter.getSysVersion());
			ext.put("screen_density",PhoneInfoGetter.getDisplayDensity(context)+"" );
			ext.put("imei", Encrypter.doMD5Encode(PhoneInfoGetter.getIMEI(context).toLowerCase()));
			ext.put("mac",Encrypter.doMD5Encode(PhoneInfoGetter.getMAC(context).replace(":", "").trim().toUpperCase()));
			
			//影响填充，暂时去掉。麻痹腾讯的臭SB
//			String androidID = PhoneInfoGetter.getAndroidID(context);
//			ZplayDebug.d(TAG, "androidID="+androidID, onoff);
//			String androidID_md5 = Encrypter.doMD5Encode(androidID);
//			ZplayDebug.d(TAG, "androidID_md5="+androidID_md5, onoff);
//			ext.put("aid", androidID_md5);
			
			ext.put("aaid", YumiGooglePlayServiceCheckUtils.getGooglePlayID(context));
			ext.put("useragent", PhoneInfoGetter.getUserAgent(context));
			//ext.put("referer", Request.getHeader("referer"));//如果要获取需要在ZplayUtil中增加一个方法WebMethodHandler.class中???
			if (loc != null) {
				ext.put("lat", loc.getLatitude() * 1000000);
				ext.put("lng", loc.getLongitude() * 1000000);
				ext.put("coordtime", loc.getTime());
			}
		} catch (JSONException e) {
		}
		return ext;
	}

	final void onDestroy() {
		if (task != null) {
			task.cancel(true);
		}
	}

	public interface GdtClickListener{
		public void onClick(boolean jumpWeb, String downloadUrl);
	}
	
}
