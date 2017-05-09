package com.yumi.android.sdk.ads.api.inmobi;

import android.app.Activity;
import android.webkit.WebView;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiGooglePlayServiceCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

public final class InmobiInterstitialAdapter extends YumiWebInterstitialLayer {

	private static final String TAG = "InmobiApiInsteritialLayer";

	private int reqWidth;
	private int reqHeight;
	private int reqAdSizeInt;
	private int reqOrientation;
	private InmobiApiReqeust req;
	private String gpID = "";
	private YumiProviderBean mProvider;
	private Activity mContext;

	protected boolean instertitialPageError;

	protected boolean interstitialReady;

	public InmobiInterstitialAdapter(Activity activity,
			YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		mProvider = provider;
		mContext = activity;
	}

	private String renderData(String data) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append("<head><title></title><meta name=\"viewport\" content=\"user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"/><style type=\"text/css\">body {margin: 0; overflow: hidden;}</style></head>");
		buffer.append(data);
		return buffer.toString();
	}

	@Override
	protected final void calculateRequestSize() {
		if (WindowSizeUtils.isPortrait(getContext())) { // 横屏
			reqOrientation = 1;
			reqAdSizeInt = 14;
			reqWidth = 320;
			reqHeight = 480;
		} else {
			reqOrientation = 3;
			reqAdSizeInt = 32;
			reqWidth = 480;
			reqHeight = 320;
		}

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
		if (req != null) {
			req.onDestroy();
		}
	}

	@Override
	public final boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected final void onPreparedWebInterstitial() {
		ZplayDebug.d(TAG, "inmobi api request new interstitial", onoff);
		if (!NullCheckUtils.isNotNull(gpID)
				&& YumiGooglePlayServiceCheckUtils
						.isGooglePlayIsAvailable(getContext())) {
			gpID = YumiGooglePlayServiceCheckUtils.getGooglePlayID(getContext());
		}
		if (req != null) {
			req.requestApi(reqAdSizeInt, getProvider().getKey1(), getProvider().getGlobal()
					.getReqIP(), gpID, reqOrientation);
		}
	}

	@Override
	public
	final void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		calculateRequestSize();
		if (req == null) {
			req = new InmobiApiReqeust(getContext(), new IYumiAPIRequestListener() {

				@Override
				public void onAPIRequestDone(String data, LayerErrorCode error) {
					if (data != null) {
						// listener.onLayerPrepared(ZplayLayerListener.LAYER_TYPE_INSTERTITIAL,
						// provider.getProviderName(), TYPE_API);
						calculateWebSize(reqWidth, reqHeight);
						createWebview(null);
						loadData(renderData(data));
					}
					else if (error != null) {
						ZplayDebug.d(TAG, "inmobi api interstitial failed " + error, onoff);
						layerPreparedFailed(error);
					}
				}
			}, LayerType.TYPE_INTERSTITIAL);
		}
	}

	@Override
	protected final void webLayerClickedAndRequestBrowser(String url) {
		ZplayDebug.d(TAG, "inmobi api interstitial clicked", onoff);
		 //新增点击判断是否跳转到内置浏览器
		if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
			YumiInlayBrowserUtils.openBrowser(mContext, url, null);
		} else {
			requestSystemBrowser(url);
		}
		layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected final void webLayerOnShow() {
		ZplayDebug.d(TAG, "inmobi api interstitial shown", onoff);
		layerExposure();
	}

	@Override
	protected final void webLayerPrepared(WebView view) {
		ZplayDebug.d(TAG, "inmobi api interstitial prapared", onoff);
		layerPrepared();
	}

	@Override
	protected void webLayerDismiss() {
		layerClosed();
	}
}
