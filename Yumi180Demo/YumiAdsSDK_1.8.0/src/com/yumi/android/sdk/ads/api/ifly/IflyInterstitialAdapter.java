package com.yumi.android.sdk.ads.api.ifly;

import android.app.Activity;
import android.webkit.WebView;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public final class IflyInterstitialAdapter extends YumiWebInterstitialLayer {

	private static final String TAG = "IflyApiInterstitialAddatper";
	private IflyApiRequest req;
	private int reqWdip;
	private int reqHdip; 
	private YumiProviderBean mProvider;
	private Activity mContext;
	
	public IflyInterstitialAdapter(Activity activity,
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
		ZplayDebug.d(TAG, "ifly api interstitial clicked", onoff);
        //新增点击判断是否跳转到内置浏览器
		if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
			YumiInlayBrowserUtils.openBrowser(mContext, url, null);
		} else {
			requestSystemBrowser(url);
		}
		layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected void webLayerPrepared(WebView view) {
		ZplayDebug.d(TAG, "ifly api interstitial prepared", onoff);
		layerPrepared();
	}

	@Override
	protected void webLayerOnShow() {
		ZplayDebug.d(TAG, "ifly api interstitial shown", onoff);
		layerExposure();
	}

	@Override
	protected void calculateRequestSize() {
			reqWdip = 600;
			reqHdip = 500;
	}

	@Override
	protected void webLayerDismiss() {
		layerClosed();
	}

	@Override
	protected void onPreparedWebInterstitial() {
		ZplayDebug.d(TAG, "ifly api request new interstitial", onoff);
		calculateRequestSize();
		if (req != null) {
			req.requestApi(getProvider().getKey1(), getProvider().getKey2(), getProvider().getGlobal().getReqIP(), reqWdip, reqHdip);
		}
	}

	@Override
	public void init() {
		ZplayDebug.i(TAG, "ifly appID " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "ifly unitID " + getProvider().getKey2(), onoff);
		if (req == null) {
			req = new IflyApiRequest(getContext(), new IYumiAPIRequestListener() {
				
				@Override
				public void onAPIRequestDone(String data, LayerErrorCode error) {
					if (data != null) {
						calculateWebSize(reqWdip, reqHdip);
						createWebview(null);
						loadData(data);
					}else {
						ZplayDebug.d(TAG, "ifly api interstitial failed " + error, onoff);
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
		YumiInlayBrowserUtils.callOnActivityDestroy();
	}

}
