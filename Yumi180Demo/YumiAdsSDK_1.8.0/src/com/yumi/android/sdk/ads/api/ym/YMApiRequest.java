package com.yumi.android.sdk.ads.api.ym;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.yumi.android.sdk.ads.api.ApiRequest;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.utils.json.JsonResolveUtils;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;
import com.yumi.android.sdk.ads.utils.network.WebParamsMapBuilder;
import com.yumi.android.sdk.ads.utils.network.WebTask;
import com.yumi.android.sdk.ads.utils.network.WebTaskHandler;

public class YMApiRequest extends ApiRequest {
	private static final String TAG = "YoumiApiRequest";
	/**http://r.api.youmi.net/v1/req*/
	private static final String API_URL = YumiDes3Util.decodeDes3("AiFr8PRqW5pJuV+ODc78KE2t4lHGXEVmDEdpOU0eqmI=");
	/**http://r.api.youmi.net/v1/dwn*/
	private static final String REPORT_URL_TRANSFORM = YumiDes3Util.decodeDes3("AiFr8PRqW5pJuV+ODc78KE2t4lHGXEVm7AqbuyjLDTg=");
	public static final int TRANSFORM_TYPE_DOWNLOAD = 1;
	public static final int TRANSFORM_TYPE_DOWNLOAD_COMPLETE = 2;
	public static final int TRANSFORM_TYPE_INSTALL = 3;
	public static final int TRANSFORM_TYPE_INSTALL_COMPLETE = 4;
	private YoumiClickListener cl;
	private static final String[] REQ_KEY = new String[] { "device", // 设备品牌和型号
			"sw",// 屏幕宽
			"sh",// 屏幕高
			"pk", // app 包名
			"dvidtype", // 1 imei
			"dvid", // Android 平台请输入 imei
			"nettype", // 联网方式
			"carrier", // 运营商
			"os", // android
			"av", // api 版本号。当前 API 版本固定传值 1.1
			"adslot", // 广告位 id 选填
			"postype", // 广告位类型 1 banner 2: 插屏 3: 信息流
			"mtrtype", // 可接受的素材类型，需要接受多种类型数据时，使用逗号分开。
			"pw", // 广告素材宽度
			"ph", // 广告素材高度
			"appid", // 在有米官网获取。
			"ip", // 终端用户 IP 地址
			"si", // 当前用户设备 imsi 值
	};
	private Context context;
	private IYumiAPIRequestListener listener;
	private WebTaskHandler task;
	private String img;
	private String click;
	private List<String> imp;
	private String trackid;
	private int adtype;
	public static String pk;
	private Activity activity;
    public static Map<String ,String> adMap = new HashMap<String ,String>();
	public YMApiRequest(Activity activity, Context context,
			IYumiAPIRequestListener listener) {
		this.context = context;
		this.listener = listener;
		this.activity = activity;
	}

	@SuppressWarnings("unchecked")
	public void requestApi(String appid, String adslot, int pw, int ph,
			int postype, String ip) {
		if (task != null) {
			task.cancel(true);
		}
		Map<String, Object> params = WebParamsMapBuilder.buildParams(API_URL,
				REQ_KEY, buildReqValues(appid, adslot, pw, ph, postype, ip));
		task = new WebTaskHandler(context, new WebTask() {
			@Override
			public void doTask(String data, String msg) {
				if (NullCheckUtils.isNotNull(data)) {
					dealWithData(data);
				} else {
					listener.onAPIRequestDone(null,
							LayerErrorCode.ERROR_INVALID_NETWORK);
				}

			}
		}, false, false);
		task.execute(params);
	}

	protected void dealWithData(String data) {
		try {
			JSONObject json = new JSONObject(data);
			int c = JsonResolveUtils.getIntFromJson(json, "c", -1);
			String msg = JsonResolveUtils.getStringFromJson(json, "msg", "");
			if (c != 0) {
				if (c == -2007 && "No Ava Ad".equals(msg)) {
					requestThirdApiFailed(LayerErrorCode.CODE_FAILED);
				} else if (c == -2007 && "No Ad Fit Material".equals(msg)) {
					requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
				} else if (c == -3003 && "".equals(msg)) {
					requestThirdApiFailed(LayerErrorCode.ERROR_INVALID);
				} else if (c == -2222 && "Time is Not Effective".equals(msg))
					requestThirdApiFailed(LayerErrorCode.ERROR_NON_RESPONSE);
			} else {
				JSONArray array = JsonResolveUtils.getJsonArrayFromJson(json,
						"data");
				if (array == null) {
					requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
				} else {
					for (int i = 0; i < array.length(); i++) {
						JSONObject date = array.getJSONObject(i);
						Log.e(TAG, "-------array=" + i);
						saveReportAdtype(JsonResolveUtils.getIntFromJson(date,
								"adtype", 0));
						JSONArray list = JsonResolveUtils.getJsonArrayFromJson(
								date, "imp");
						for (int j = 0; j < list.length(); j++) {
							imp = new ArrayList<>(list.length());
							imp.add(list.getString(j));
						}
						saveReportImg(JsonResolveUtils.getStringFromJson(date,
								"img", ""));
						String imgxml = XMLRendering.renderingImgXML(img);
						// String imgxml =NativeAdsBuild.getImageAdHtml(img,
						// click);
						if (!("".equals(imgxml))) {
							listener.onAPIRequestDone(imgxml, null);
						} else {
							listener.onAPIRequestDone(null,
									LayerErrorCode.ERROR_NO_FILL);
//							String title = JsonResolveUtils.getStringFromJson(
//									date, "title", "");
//							String icon = JsonResolveUtils.getStringFromJson(
//									date, "icon", "");
						}
						saveReportClick(JsonResolveUtils.getStringFromJson(
								date, "click", ""));
						saveReportTrackid(JsonResolveUtils.getStringFromJson(
								date, "trackid", ""));
						saveReportPackage(JsonResolveUtils.getStringFromJson(
								date, "pk", ""));
					}
				}

			}

		} catch (Exception e) {
			ZplayDebug.e(TAG, e.getMessage(), e, onoff);
		}

	}

	@SuppressWarnings("unchecked")
	void reoporYoumiExposure() {
		ZplayDebug.i(TAG, "Youmi report exposure ", onoff);
		for (int i = 0; i < imp.size(); i++) {
			if (NullCheckUtils.isNotNull(imp.get(i))) {
				new WebTaskHandler(context, new WebTask() {

					@Override
					public void doTask(String data, String msg) {
						ZplayDebug.i(TAG, "Youmi exposure report result is "
								+ data, onoff);
						if (NullCheckUtils.isNotNull(data)) {
							try {
								JSONObject date = new JSONObject(data);
								int c = JsonResolveUtils.getIntFromJson(date,
										"c", -1);
								if (c == 0) {
									ZplayDebug.i(TAG,
											"Youmi exposure report result is Success"
													+ data, onoff);
								} else {
									ZplayDebug.i(TAG,
											"Youmi exposure report result is Fail"
													+ data, onoff);

								}
							} catch (JSONException e) {
								ZplayDebug.e(TAG, e.getMessage(), e, onoff);
							}
						}
					}
				}, false, false).executeOnPool(WebParamsMapBuilder.buildParams(
						imp.get(i), null, null));
			} else {
				ZplayDebug
						.i(TAG,
								"exposure id is null , the Youmi resoponse maybe error",
								onoff);
			}
		}
	}

	private void saveReportImg(String stringFromJson) {
		this.img = stringFromJson;
	}

	private void saveReportClick(String stringFromJson) {
		this.click = stringFromJson;
	}

	private void saveReportAdtype(int intFromJson) {
		this.adtype = intFromJson;
	}

	private void saveReportTrackid(String stringFromJson) {
		this.trackid = stringFromJson;
	}

	@SuppressWarnings("static-access")
	private void saveReportPackage(String stringFromJson) {
		this.pk = stringFromJson;
	}

	private void requestThirdApiFailed(LayerErrorCode errorInternal) {
		listener.onAPIRequestDone(null, errorInternal);
	}

	private String[] buildReqValues(String appid, String adslot, int pw,
			int ph, int postype, String ip) {
		int[] screen = WindowSizeUtils.getRealSize(activity);
		String[] values = new String[] {
				PhoneInfoGetter.getBrand() + "+" + PhoneInfoGetter.getModel(),
				screen[0] + "",// 屏幕宽
				screen[1] + "",// 屏幕高
				context.getPackageName(),
				"1", // 1 imei
				PhoneInfoGetter.getIMEI(context), getConn() + "",
				PhoneInfoGetter.getOperator(context) + "", "android", "1.1", // api
				adslot, // 广告位 id 选填
				postype + "", // 广告位类型 1 banner 2: 插屏 3: 信息流
				"2", // 可接受的素材类型，需要接受多种类型数据时，使用逗号分开。
				pw + "", // 广告素材宽度
				ph + "", // 广告素材高度
				appid, // 在有米官网获取。
				ip, // 终端用户 IP 地址
				PhoneInfoGetter.getIMSI(context) // 当前用户设备 imsi 值
		};
		return values;
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

	public interface YoumiClickListener {
		public void onClick(boolean jumpWeb, String downloadUrl);
	}

	void onDestroy() {
		if (task != null) {
			task.cancel(true);
		}
	}

	public void reportYoumiClick(YoumiClickListener cl) {
		this.cl = cl;
		adMap.put(pk, trackid);
		reportYoumiClick();
	}

	@SuppressWarnings("unchecked")
	private void reportYoumiClick() {
		
		if (NullCheckUtils.isNotNull(click)) {
			new WebTaskHandler(context, new WebTask() {

				@Override
				public void doTask(String data, String msg) {
					if (NullCheckUtils.isNotNull(data)) {
						ZplayDebug.i(TAG, "Youmi exposure report result is "
								+ data, onoff);
						try {
							JSONObject date = new JSONObject(data);
							int c = JsonResolveUtils.getIntFromJson(date, "c",
									-1);
							final String downLoadUrl = JsonResolveUtils
									.getStringFromJson(date, "url", "")
									.replace("\\", "").trim();
							int code = JsonResolveUtils.getIntFromJson(date,
									"code", -1);
							ZplayDebug.d(TAG, "downLoadUrl = " + downLoadUrl,
									onoff);
							if (c == 0) {
								if (adtype == 1 || adtype == 2) {
									if (NullCheckUtils.isNotNull(downLoadUrl)) {
										if (cl != null) {
											Log.e(TAG, "-------onclick");
											cl.onClick(false, downLoadUrl);
											Log.e(TAG, "-------onclick1");
											ZplayDebug.i(TAG,
													"Youmi exposure report result is Success"
															+ downLoadUrl,
													onoff);

										}
									}
								} else if (adtype == 3) {
									cl.onClick(true, data);
								}
							} else if (c == -3280) {
								ZplayDebug.i(TAG,
										"Youmi exposure report result is Fail"
												+ data, onoff);
							} else if (code != -1 && code == -3303) {
								if (cl != null) {
									cl.onClick(false, null);
									ZplayDebug.i(TAG,
											"Youmi ad already clicked", onoff);
								}
							}
						} catch (JSONException e) {
							ZplayDebug.e(TAG, e.getMessage(), e, onoff);
						}
					}
				}
			}, false, false).executeOnPool(WebParamsMapBuilder.buildParams(
					click + "&goto=0", null, null));
		} else {
			ZplayDebug.i(TAG,
					"exposure id is null , the Youmi resoponse maybe error",
					onoff);
		}
	}

	@SuppressWarnings("unchecked")
	public void reportYoumiTransform(int reportType) {
		if (NullCheckUtils.isNotNull(trackid)) {
			new WebTaskHandler(context, new WebTask() {

				@Override
				public void doTask(String data, String msg) {

				}
			}, false, false).execute(WebParamsMapBuilder.buildParams(
					REPORT_URL_TRANSFORM,
					new String[] { "actionid", "trackid" }, new String[] {
							reportType + "", trackid }));
		}
	}
	@SuppressWarnings("unchecked")
	public void reportTransform(int reportType,String track) {
		if (NullCheckUtils.isNotNull(trackid)) {
			new WebTaskHandler(context, new WebTask() {

				@Override
				public void doTask(String data, String msg) {

				}
			}, false, false).execute(WebParamsMapBuilder.buildParams(
					REPORT_URL_TRANSFORM,
					new String[] { "actionid", "trackid" }, new String[] {
							reportType + "", track }));
		}
	}
}
