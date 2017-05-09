package com.yumi.android.sdk.ads.api.smaato;

import android.app.Activity;
import android.webkit.WebView;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

public class SmaatoInterstitialAdapter extends YumiWebInterstitialLayer {

	private static final String TAG = "SmaatoApiInterstitialAdatper";
	private SmaatoApiRequest req;
	private static final String DIMENSION_POR = "full_320x480";
	private static final String DIMENSION_LAND =  "full_480x320";
	private int wDip = 0;
	private int hDip = 0;
	private YumiProviderBean mProvider;
	private Activity mContext;
	
	
	public SmaatoInterstitialAdapter(Activity activity,
			YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		mProvider = provider;
		mContext = activity;
	}

	@Override
	public void onActivityPause() {

	}

	@Override
	public void onActivityResume() {
		closeOnResume();
	}

	@Override
	public boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected void webLayerClickedAndRequestBrowser(String url) {
		ZplayDebug.d(TAG, "smaato api interstitial clicked", onoff);
		if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
			YumiInlayBrowserUtils.openBrowser(mContext, url, null);
		} else {
			requestSystemBrowser(url);
		}
		layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected void webLayerPrepared(WebView view) {
		ZplayDebug.d(TAG, "smaato api interstitial prepared", onoff);
		layerPrepared();
	}

	@Override
	protected void webLayerOnShow() {
		ZplayDebug.d(TAG, "smaato api interstitial shown", onoff);
		layerExposure();
	}

	@Override
	protected void calculateRequestSize() {
		if (WindowSizeUtils.isPortrait(getContext())) {
			wDip = 320;
			hDip = 480;
		}else {
			wDip = 480;
			hDip = 320;
		}
	}

	@Override
	protected void webLayerDismiss() {
		layerClosed();
	}

	@Override
	protected void onPreparedWebInterstitial() {
		calculateRequestSize();
		ZplayDebug.d(TAG, "smaato api request new interstitial", onoff);
		if (req != null) {
			req.requestApi(getProvider().getKey1(), getProvider().getKey2(), getProvider().getGlobal().getReqIP(), WindowSizeUtils.isPortrait(getContext()) ? DIMENSION_POR : DIMENSION_LAND,wDip,hDip);
		}
	}

	@Override
	public void init() {
		ZplayDebug.i(TAG, "pubID " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "adSpaceID " + getProvider().getKey2(), onoff);
		if (req == null) {
			req = new SmaatoApiRequest(getContext(), new IYumiAPIRequestListener() {
				
				@Override
				public void onAPIRequestDone(String data, LayerErrorCode error) {
					if (NullCheckUtils.isNotNull(data)) {
						calculateWebSize(wDip, hDip);
						createWebview(null);
						loadData(data);
					}else {
						ZplayDebug.d(TAG, "smaato api interstitial failed " + error, onoff);
						layerPreparedFailed(error);
					}
				}
			});
		}
	}

	@Override
	protected void callOnActivityDestroy() {
		if (req != null) {
			req.onDestroy();
		}
	}

}
