package com.yumi.android.sdk.ads.api.ifly;

import android.app.Activity;
import android.view.View;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebBannerLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public final class IflyBannerAdapter extends YumiWebBannerLayer {

	private static final String TAG = "IflyApiBannerAdapter";
	private IflyApiRequest req;
	private int reqWdip = 0;
	private int reqHdip = 0;
	private YumiProviderBean mProvider;
	private Activity mContext;

	public IflyBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		mProvider = provider;
		mContext = activity;
	}

	@Override
	public void onActivityPause() {
	}

	@Override
	public void onActivityResume() {

	}

	@Override
	protected void webLayerClickedAndRequestBrowser(String url) {
		ZplayDebug.d(TAG, "ifly api banner clicked", onoff);
        //新增点击判断是否跳转到内置浏览器
		if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
			YumiInlayBrowserUtils.openBrowser(mContext, url, null);
		} else {
			requestSystemBrowser(url);
		}
		layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected void webLayerPrepared(View view) {
		ZplayDebug.d(TAG, "ifly api banner prepared", onoff);
		layerPrepared(view, false);
		ZplayDebug.d(TAG, "ifly api banner shown", onoff);
		layerExposure();
	}

	@Override
	protected void calculateRequestSize() {
		if (bannerSize == AdSize.BANNER_SIZE_320X50) {
			reqWdip = 320;
			reqHdip = 50;
		}
		if (bannerSize == AdSize.BANNER_SIZE_728X90) {
			reqWdip = 728;
			reqHdip = 90;
		}
	}

	@Override
	protected void onPrepareBannerLayer() {
		ZplayDebug.d(TAG, "ifly api  request new banner", onoff);
		calculateRequestSize();
		if (req != null) {
			req.requestApi(getProvider().getKey1(), getProvider().getKey2(),
					getProvider().getGlobal().getReqIP(), reqWdip, reqHdip);
		}
		// sendChangeViewBeforePrepared(web);
	}

	@Override
	public void init() {
		ZplayDebug.i(TAG, "ifly appID " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "ifly unitID " + getProvider().getKey2(), onoff);
		if (req == null) {
			req = new IflyApiRequest(getContext(),
					new IYumiAPIRequestListener() {

						@Override
						public void onAPIRequestDone(String data,
								LayerErrorCode error) {
							if (data != null) {
								calculateWebSize();
								createWebview(null);
								loadData(data);
							} else {
								ZplayDebug.d(TAG, "ifly api banner failed "
										+ error, onoff);
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
