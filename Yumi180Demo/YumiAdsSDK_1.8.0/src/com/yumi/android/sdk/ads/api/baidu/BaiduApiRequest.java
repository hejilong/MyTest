package com.yumi.android.sdk.ads.api.baidu;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Build;

import com.google.protobuf.ByteString;
import com.yumi.android.sdk.ads.api.ApiRequest;
import com.yumi.android.sdk.ads.api.baidu.BaiduApiProtoLite54.Ad;
import com.yumi.android.sdk.ads.api.baidu.BaiduApiProtoLite54.Device.DeviceType;
import com.yumi.android.sdk.ads.api.baidu.BaiduApiProtoLite54.MaterialMeta;
import com.yumi.android.sdk.ads.api.baidu.BaiduApiProtoLite54.MaterialMeta.CreativeType;
import com.yumi.android.sdk.ads.api.baidu.BaiduApiProtoLite54.MaterialMeta.InteractionType;
import com.yumi.android.sdk.ads.api.baidu.BaiduApiProtoLite54.MobadsRequest;
import com.yumi.android.sdk.ads.api.baidu.BaiduApiProtoLite54.MobadsRequest.Builder;
import com.yumi.android.sdk.ads.api.baidu.BaiduApiProtoLite54.MobadsResponse;
import com.yumi.android.sdk.ads.api.baidu.BaiduApiProtoLite54.Network.ConnectionType;
import com.yumi.android.sdk.ads.api.baidu.BaiduApiProtoLite54.Network.OperatorType;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
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


final class BaiduApiRequest extends ApiRequest{

	private static final String TAG = "BaiduApiRequest";
//	private static final String API_URL = "http://debug.mobads.baidu.com/api_5"; 
	/**http://mobads.baidu.com/api_5*/
	private static final String API_URL = "B17e86zmKq1lQKvLrUJ/sKwLJiShtPO2+bZiasoiKzk=";
	private Context context;
	private Activity activity;
	private IYumiAPIRequestListener resultListener;
	private AsyncTask<MobadsRequest, Object, String> requestTask;
	private MobadsRequest request;
	private final ExecutorService POOL =  Executors.newFixedThreadPool(20);
	private LayerType layerType;
	
	
	BaiduApiRequest(Activity activity, IYumiAPIRequestListener resultListener, LayerType adFromat) {
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.resultListener = resultListener;
		this.layerType = adFromat;
	}

	public final void buildRequestParams(String appID, String positionID, String ipv4, int widthPix, int heightPix) {
		BaiduApiProtoLite54.MobadsRequest.Builder builder = BaiduApiProtoLite54.MobadsRequest
				.newBuilder();
		buildVersion(builder);
		buildApp(builder, appID);
		buildDevice(builder);
		buildNetwork(builder, ipv4);
		buildGPS(builder);
		buildAdslots(builder, positionID, widthPix, heightPix);
		request = builder.build();
	}
	
	private void buildGPS(Builder builder) {
		Location lastKnownLocation = LocationHandler.getLocHandler().getLastKnownLocation(context);
		if (lastKnownLocation != null) {
			BaiduApiProtoLite54.Gps.Builder gps = BaiduApiProtoLite54.Gps.newBuilder();
			gps.setCoordinateType(BaiduApiProtoLite54.Gps.CoordinateType.WGS84);
			gps.setLongitude(lastKnownLocation.getLongitude());
			gps.setLatitude(lastKnownLocation.getLatitude());
			gps.setTimestamp((int)(lastKnownLocation.getTime() / 1000));
			builder.setGps(gps);
		}
	}

	private void buildNetwork(Builder builder, String ipv4) {
		BaiduApiProtoLite54.Network.Builder network = BaiduApiProtoLite54.Network
				.newBuilder();
		network.setIpv4(ipv4);
		network.setConnectionType(getConnType());
		network.setOperatorType(getOperator());
		List<ScanResult> connectWifiInfoList = NetworkStatusHandler.getConnectWifiInfoList(context);
		String connectWifiSsid = NetworkStatusHandler.getConnectWifiSsid(context);
		if (NullCheckUtils.isNotEmptyCollection(connectWifiInfoList)) {
			for (ScanResult result : connectWifiInfoList) {
				BaiduApiProtoLite54.WiFiAp.Builder wifi = BaiduApiProtoLite54.WiFiAp.newBuilder();
				wifi.setApMac(ByteString.copyFromUtf8(result.BSSID).toString());
				wifi.setRssi(result.level);
				wifi.setApName(ByteString.copyFromUtf8(result.SSID));
				wifi.setIsConnected(result.SSID.equals(connectWifiSsid));
				network.addWifiAps(wifi);
			}
		}
		builder.setNetwork(network);		
	}

	private void buildAdslots(Builder builder, String positionID, int widthPix,
			int heightPix) {
		BaiduApiProtoLite54.AdSlot.Builder adslots = BaiduApiProtoLite54.AdSlot
				.newBuilder();
		adslots.setAdslotId(positionID);
		BaiduApiProtoLite54.Size.Builder adSize = BaiduApiProtoLite54.Size
				.newBuilder();
		adSize.setWidth(widthPix);
		adSize.setHeight(heightPix);
		adslots.setAdslotSize(adSize);
		builder.setAdslot(adslots);		
	}

	private void buildDevice(Builder builder) {
		BaiduApiProtoLite54.Device.Builder device = BaiduApiProtoLite54.Device
				.newBuilder();
		device.setDeviceType(getDeviceType());
		device.setOsType(BaiduApiProtoLite54.Device.OsType.ANDROID);
		//os version
		BaiduApiProtoLite54.Version.Builder osVersion = buildOsVersion();
		device.setOsVersion(osVersion);
		//vendor
		device.setVendor(ByteString.copyFromUtf8(PhoneInfoGetter
				.getManufacture()));
		device.setModel(ByteString.copyFromUtf8(PhoneInfoGetter.getModel()));
		//udid
		BaiduApiProtoLite54.UdId.Builder udid = BaiduApiProtoLite54.UdId
				.newBuilder();
		udid.setImei(PhoneInfoGetter.getIMEI(context));
		udid.setMac(PhoneInfoGetter.getMAC(context));
		String androidID = PhoneInfoGetter.getAndroidID(context);
		udid.setAndroidId(androidID.equals("0000000000000000")?"0123456789abcdef":androidID);
		device.setUdid(udid);
		//screen size
		BaiduApiProtoLite54.Size.Builder screenSize = BaiduApiProtoLite54.Size
				.newBuilder();
		int[] displayMetrics = PhoneInfoGetter.getDisplayMetrics(context);
		screenSize.setWidth(displayMetrics[0]);
		screenSize.setHeight(displayMetrics[1]);
		device.setScreenSize(screenSize);
		builder.setDevice(device);		
	}

	private BaiduApiProtoLite54.Version.Builder buildOsVersion() {
		int androidSDK = PhoneInfoGetter.getAndroidSDK();
		BaiduApiProtoLite54.Version.Builder osVersion = BaiduApiProtoLite54.Version.newBuilder();
		if (androidSDK < 8 ) {
			osVersion.setMajor(2);
			osVersion.setMinor(0);
		}else {
			switch (androidSDK) {
			case Build.VERSION_CODES.FROYO:
				osVersion.setMajor(2);
				osVersion.setMinor(2);
				break;
			case Build.VERSION_CODES.GINGERBREAD:
				osVersion.setMajor(2);
				osVersion.setMinor(3);
				break;
			case Build.VERSION_CODES.GINGERBREAD_MR1:
				osVersion.setMajor(2);
				osVersion.setMinor(3);
				osVersion.setMicro(3);
				break;
			case Build.VERSION_CODES.HONEYCOMB:
				osVersion.setMajor(3);
				osVersion.setMinor(0);
				break;
			case Build.VERSION_CODES.HONEYCOMB_MR1:
				osVersion.setMajor(3);
				osVersion.setMinor(1);
				break;
			case Build.VERSION_CODES.HONEYCOMB_MR2:
				osVersion.setMajor(3);
				osVersion.setMinor(2);
				break;
			case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
				osVersion.setMajor(4);
				osVersion.setMinor(0);
				break;
			case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
				osVersion.setMajor(4);
				osVersion.setMinor(0);
				osVersion.setMicro(3);
				break;
			case Build.VERSION_CODES.JELLY_BEAN:
				osVersion.setMajor(4);
				osVersion.setMinor(1);
				break;
			case Build.VERSION_CODES.JELLY_BEAN_MR1:
				osVersion.setMajor(4);
				osVersion.setMinor(2);
				break;
			case Build.VERSION_CODES.JELLY_BEAN_MR2:
				osVersion.setMajor(4);
				osVersion.setMinor(3);
				break;
			case Build.VERSION_CODES.KITKAT:
			case Build.VERSION_CODES.KITKAT_WATCH:
				osVersion.setMajor(4);
				osVersion.setMinor(4);
				osVersion.setMicro(3);
				break;
			case Build.VERSION_CODES.LOLLIPOP:
				osVersion.setMajor(5);
				osVersion.setMinor(0);
				break;
			default:
				osVersion.setMajor(6);
				osVersion.setMinor(0);
				break;
			}
		}
		return osVersion;
	}

	private void buildApp(Builder builder, String appID) {
		BaiduApiProtoLite54.App.Builder app = BaiduApiProtoLite54.App
				.newBuilder();
		app.setAppId(appID);
        BaiduApiProtoLite54.Version.Builder appVersion = BaiduApiProtoLite54.Version.newBuilder();
        appVersion.setMajor(PackageInfoGetter.getAppVersionCode(context.getPackageManager(), context.getPackageName()));
        appVersion.setMinor(0);
        app.setAppVersion(appVersion);
		app.setAppPackage(context.getPackageName());
		builder.setApp(app);		
	}

	private void buildVersion(Builder builder) {
		BaiduApiProtoLite54.Version.Builder version = BaiduApiProtoLite54.Version
				.newBuilder();
		version.setMajor(5);
		version.setMinor(4);
		version.setMicro(0);
		builder.setApiVersion(version);
	}

	private BaiduApiProtoLite54.Network.ConnectionType getConnType() {
		String type = NetworkStatusHandler.getConnectedNetName(context);
		if (type.equals("wifi")) {
			return ConnectionType.WIFI;
		}
		if (type.equals("2g")) {
			return ConnectionType.CELL_2G;
		}
		if (type.equals("3g")) {
			return ConnectionType.CELL_3G;
		}
		if (type.equals("4g")) {
			return ConnectionType.CELL_4G;
		}
		return ConnectionType.CONNECTION_UNKNOWN;
	}

	private BaiduApiProtoLite54.Network.OperatorType getOperator() {
		int operator = PhoneInfoGetter.getOperator(context);
		switch (operator) {
		case 1:
			return OperatorType.CHINA_MOBILE;
		case 2:
			return OperatorType.CHINA_UNICOM;
		case 3:
			return OperatorType.CHINA_TELECOM;
		default:
			return OperatorType.UNKNOWN_OPERATOR;
		}
	}

	@SuppressLint("NewApi")
	final void requestServer() {
		if (requestTask != null && requestTask.getStatus() != AsyncTask.Status.FINISHED) {
			requestTask.cancel(true);
		}
		requestTask = new AsyncTask<MobadsRequest, Object, String>() {

			@Override
			protected String doInBackground(MobadsRequest... request) {
				String html = null;
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
					HttpPost post = new HttpPost(YumiDes3Util.decodeDes3(API_URL));
					post.setEntity(new ByteArrayEntity(request[0].toByteArray()));
					HttpResponse httpResponse = httpClient.execute(post);
					int statusCode = httpResponse.getStatusLine()
							.getStatusCode();
					if (statusCode == 200) {
						InputStream content = httpResponse.getEntity()
								.getContent();
						BaiduApiProtoLite54.MobadsResponse parseFrom = BaiduApiProtoLite54.MobadsResponse
								.parseFrom(content);
						if (parseFrom != null && parseFrom.getAdsCount() > 0) {
							html = parseResultAndBuildXml(parseFrom);
						}else {
							if (parseFrom != null) {
								ZplayDebug.e(TAG,  "baidu api response is null or ads is 0 " + parseFrom.getErrorCode(), onoff);
							}
						}
					}else{
                        ZplayDebug.e(TAG,  "baidu api response statusCode is " + statusCode, onoff);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return html;
			}

			@Override
			protected void onPostExecute(String html) {
				if (NullCheckUtils.isNotNull(html)) {
					callback(html, LayerErrorCode.CODE_SUCCESS);
					ZplayDebug.i(TAG, "baidu api html = "+html, onoff);
				} else {
					callback(null, LayerErrorCode.ERROR_NO_FILL);
				}
				super.onPostExecute(html);
			}
		};
		if (PhoneInfoGetter.getAndroidSDK() >= 11) {
			requestTask.executeOnExecutor(POOL, request);
		}else {
			requestTask.execute(request);
		}
	}

	private void callback(String html, LayerErrorCode error) {
		if (resultListener != null) {
			resultListener.onAPIRequestDone(html, error);
		}
	}

	private String parseResultAndBuildXml(MobadsResponse parseFrom) {
		String html = null;
		Ad ads = parseFrom.getAds(0);
		if (ads != null) {
			MaterialMeta materialMeta = ads.getMaterialMeta();
			if (materialMeta != null) {
				InteractionType interactionType = materialMeta.getInteractionType();
				CreativeType creative = materialMeta.getCreativeType();
				if (isAvailableInteractionType(interactionType) && isAvailableCreativeType(creative))
				{
					
				    String clickUrl = materialMeta.getClickUrl();
                    List<String> winNoticeUrlList = materialMeta.getWinNoticeUrlList();
                    
					if(creative.equals(CreativeType.IMAGE) || creative.equals(CreativeType.NO_TYPE)){
	                    String mediaUrl = materialMeta.getImageSrc(0);
						if (materialMeta.getImageSrcCount() > 0) {
							if (notNullString(clickUrl)	&& notNullString(mediaUrl)) {
								if (layerType == LayerType.TYPE_BANNER) {
									html = XMLRendering.getBannerImageHtml(mediaUrl, clickUrl,winNoticeUrlList);
								}
								if (layerType == LayerType.TYPE_INTERSTITIAL) {
									JSONObject json =putHtmlNoticeUrlListToJson(XMLRendering.getCpImageHtml(mediaUrl,clickUrl, null),winNoticeUrlList);
									html = json.toString();
								}
							}
						} else {
							ZplayDebug.e(TAG, "image src count is 0", onoff);
						}
					}
					else if(creative.equals(CreativeType.TEXT))
					{
					    
                        String title=materialMeta.getTitle().toStringUtf8();
                        String description=materialMeta.getDescription(0).toStringUtf8();
						if (title.length() > 0) {
							if (notNullString(clickUrl) && notNullString(title) && notNullString(description)) {
								if (layerType == LayerType.TYPE_BANNER) {
									html = XMLRendering.getBannerTextHtml(title,description, clickUrl,winNoticeUrlList);
								}
								if (layerType == LayerType.TYPE_INTERSTITIAL) {
									JSONObject json =putHtmlNoticeUrlListToJson(XMLRendering.getCpTextHtml(title,description, clickUrl,null),winNoticeUrlList);
									html = json.toString();
								}
							}
						} else {
							ZplayDebug.e(TAG, "text length is 0", onoff);
						}
					}
					else if(creative.equals(CreativeType.TEXT_ICON))
					{
                        String title=materialMeta.getTitle().toStringUtf8();
                        String description=materialMeta.getDescription(0).toStringUtf8();
                        String icon=materialMeta.getIconSrc(0);
						if (title.length() > 0) {
							if (notNullString(clickUrl) && notNullString(icon) && notNullString(title) && notNullString(description)) {
								if (layerType == LayerType.TYPE_BANNER) {
									html = XMLRendering.getBannerIconTextHtml(icon,title,description, clickUrl,winNoticeUrlList);
								}
								if (layerType == LayerType.TYPE_INTERSTITIAL) {
									JSONObject json =putHtmlNoticeUrlListToJson(XMLRendering.getCpIconTextHtml(icon,title,description, clickUrl,null),winNoticeUrlList);
									html = json.toString();
								}
							}
						} else {
							ZplayDebug.e(TAG, "textIcon length is 0", onoff);
						}
					}
					
				} else
				{
					ZplayDebug.e(TAG, "error interaction type, not surfing or download  but " + interactionType
					        + " , or error creative type " + creative, onoff);

				}
			}
		}
		return html;
	}
	
	private JSONObject putHtmlNoticeUrlListToJson(String html,List<String> winNoticeUrlList)
	{
		JSONObject json = new JSONObject();
		try {
			json.put("html", html);
			if (NullCheckUtils.isNotEmptyCollection(winNoticeUrlList)) {
				JSONArray imps = new JSONArray();
				for (String imp : winNoticeUrlList) {
					imps.put(imp);
				}
				json.put("impTracker", imps);
			}
		} catch (JSONException e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return json;
	}

	private boolean isAvailableCreativeType(CreativeType creative) {
		if (creative != null) {
			return creative.equals(CreativeType.IMAGE) || creative.equals(CreativeType.NO_TYPE)|| creative.equals(CreativeType.TEXT)|| creative.equals(CreativeType.TEXT_ICON);
		}
		return false;
	}

	private boolean isAvailableInteractionType(InteractionType interactionType) {
		if (interactionType != null) {
			return interactionType == InteractionType.SURFING
					|| interactionType == InteractionType.DOWNLOAD;
		}
		return false;
	}

	private boolean notNullString(String text) {
		if (text != null && text.length() > 0) {
			return true;
		}
		return false;
	}

	private DeviceType getDeviceType() {
		if (WindowSizeUtils.isTablet(activity)) {
			return DeviceType.TABLET;
		}
		return DeviceType.PHONE;
	}

	private String getRequestID() {
		String timestemp = System.currentTimeMillis() + "";
		StringBuffer sb = new StringBuffer("");
		sb.append("ZAD");
		sb.append(Encrypter.doMD5Encode16("zplay_api" + timestemp));
		sb.append(timestemp);
		String requestID = sb.toString();
		Pattern pattern = Pattern.compile("[A-Z][0-9a-zA-Z]{31}");
		Matcher matcher = pattern.matcher(requestID);
		if (!matcher.matches()) {
			ZplayDebug.e(TAG, "baidu api build request id not matcher standard", onoff);
		}
		ZplayDebug.v(TAG, "baidu request id is " + requestID, onoff);
		return requestID;
	}

	final void onDestroy() {
		resultListener = null;
		if (requestTask != null) {
			requestTask.cancel(true);
		}
	}

	@SuppressWarnings("unchecked")
	final void reportUrl(String url){
		WebTaskHandler reportTask = new WebTaskHandler(context, new WebTask() {
			
			@Override
			public void doTask(String data, String msg) {
				ZplayDebug.v(TAG, "baidu api data tracker " + data, onoff);
				ZplayDebug.v(TAG, "baidu api msg tracker " + msg, onoff);
			}
		}, false, false);
		reportTask.executeOnPool(WebParamsMapBuilder.buildParams(url, null));
	}
}
