package com.yumi.android.sdk.ads.api.mogo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.webkit.WebView;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.utils.location.LocationHandler;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;


public final class MogoInterstitialAdapter extends YumiWebInterstitialLayer{


	private static final String TAG = "MogoApiInstertitialLayer";
	
	private int reqWidth;
	private int reqHeight;
	private MogoApiRequest request;
	private int so = 0;
	private YumiProviderBean mProvider;
	private Activity mContext;

	
	public MogoInterstitialAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		mProvider = provider;
		mContext = activity;
	}
	
	private String[] getParamsValues() {
		List<String> list = new ArrayList<String>();
		list.add("101");
		list.add(getProvider().getKey1());
		list.add("2");
		int[] displayMetrics = PhoneInfoGetter.getDisplayMetrics(getContext());
		list.add(displayMetrics[0]+"");
		list.add(displayMetrics[1]+"");
		list.add("2");
		list.add(PhoneInfoGetter.getIMEI(getContext()));
		list.add(PhoneInfoGetter.getAndroidID(getContext()));
		list.add(PhoneInfoGetter.getMAC(getContext()));
		list.add(PhoneInfoGetter.getIMSI(getContext()));
		list.add(NetworkStatusHandler.isWIFIConnected(getContext()) ? "1" : "2");
		list.add(PhoneInfoGetter.getUserAgent(getContext()));
		list.add(PhoneInfoGetter.getManufacture());
		list.add(PhoneInfoGetter.getModel());
		list.add("android");
		list.add(PhoneInfoGetter.getAndroidSDK()+"");
		list.add(PhoneInfoGetter.getLanguage());
		list.add(PhoneInfoGetter.getDisplayDensity(getContext())+"");
		list.add(so+"");
		list.add("H");
		list.add(PhoneInfoGetter.getMNC(getContext()));
		list.add(NetworkStatusHandler.isWIFIConnected(getContext()) ? "1" : "2");
		double lat = 0d;
		double lon = 0d;
		Location lastKnownLocation = LocationHandler.getLocHandler().getLastKnownLocation(getContext());
		if (lastKnownLocation != null) {
			lat = lastKnownLocation.getLatitude();
			lon = lastKnownLocation.getLongitude();
		}
		list.add(lat+"");
		list.add(lon+"");
		list.add(PhoneInfoGetter.getISOCountryCode(getContext()));
		list.add(NetworkStatusHandler.getGsmLac(getContext())+"");
		list.add(NetworkStatusHandler.getGsmCid(getContext())+"");
		list.add(getContext().getPackageName());
		String[] values = list.toArray(new String[]{});
		return values;
	}


	@Override
	public final void onActivityPause() {
	}


	@Override
	public final void onActivityResume() {
		closeOnResume();
	}


	@Override
	protected final void callOnActivityDestroy() {
		if (request != null) {
			request.onDestroy();
		}		
	}


	@Override
	public final boolean onActivityBackPressed() {
		return false;
	}


	@Override
	protected final void onPreparedWebInterstitial() {
		ZplayDebug.d(TAG, "mogo api request new interstitial", onoff);
		request.requestApi();
	}
	
	@Override
	public final void init() {
		ZplayDebug.i(TAG, "mogoId : " + getProvider().getKey1(), onoff);
		calculateRequestSize();
		if (request == null) {
			request =  new MogoApiRequest(getContext(), new IYumiAPIRequestListener() {
				
				@Override
				public void onAPIRequestDone(String data, LayerErrorCode error) {
					if (NullCheckUtils.isNotNull(data)) {
							//成功 有广告返回 
							calculateWebSize(reqWidth, reqHeight);
							createWebview(null);
							loadData(data);
					}
					else if (error != null) {
						ZplayDebug.d(TAG, "mogo api interstitial failed " + error, onoff);
						layerPreparedFailed(error);
					}
				}

			}, getParamsValues(), true);
		}		
	}

	@Override
	protected final void calculateRequestSize() {
		if (WindowSizeUtils.isPortrait(getContext())) {
			so = 1;
			reqWidth = 500;
			reqHeight = 600;
		}else {
			so = 2;
			reqWidth = 600;
			reqHeight = 500;
		}
	}


	@Override
	protected final void webLayerClickedAndRequestBrowser(String url) {
		ZplayDebug.d(TAG, "mogo api interstitial clicked", onoff);
		if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
			YumiInlayBrowserUtils.openBrowser(mContext, url, null);
		} else {
			requestSystemBrowser(url);
		}
		layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected final void webLayerPrepared(WebView view) {
		ZplayDebug.d(TAG, "mogo api interstitial prepared", onoff);
		layerPrepared();
	}

	@Override
	protected final void webLayerOnShow() {
		ZplayDebug.d(TAG, "mogo api interstitial shown", onoff);
		layerExposure();
		if (webview != null && isInterstitialLayerReady()) {
			ZplayDebug.d(TAG, "mogo instertitial call js method", onoff);
			webview.loadUrl("javascript:show()");
		}
	}

	@Override
	protected void webLayerDismiss() {
		layerClosed();
	}
}
