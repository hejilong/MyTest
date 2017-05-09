package com.yumi.android.sdk.ads.api.smaato;

import android.app.Activity;
import android.view.View;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebBannerLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class SmaatoBannerAdapter extends YumiWebBannerLayer {

	private static final String TAG = "SmaatoApiBannerAdapter";
	private SmaatoApiRequest req;
	private String reqDimension = "xxlarge";
	private YumiProviderBean mProvider;
	private Activity mContext;
	private int width;
	private int height;
	
	
	public SmaatoBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
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
		ZplayDebug.d(TAG, "smaato api banner clicked", onoff);
		if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
			YumiInlayBrowserUtils.openBrowser(mContext, url, null);
		} else {
			requestSystemBrowser(url);
		}
		layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected void webLayerPrepared(View view) {
		ZplayDebug.d(TAG, "smaato api banner prepared", onoff);
		layerPrepared(view, false);
		ZplayDebug.d(TAG, "smaato api banner shown", onoff);
		layerExposure();
	}

	@Override
	protected void calculateRequestSize() {
		if (bannerSize == AdSize.BANNER_SIZE_320X50) {
			reqDimension = "xxlarge";
			width = 320;
			height = 50;
		}
		if (bannerSize == AdSize.BANNER_SIZE_728X90) {
			reqDimension = "leader";
			width = 728;
			height = 90;
		}
	}

	@Override
	protected void onPrepareBannerLayer() {
		ZplayDebug.d(TAG, "smaato api request new banner", onoff);
		calculateRequestSize();
		if (req != null) {
			req.requestApi(getProvider().getKey1(), getProvider().getKey2(), getProvider().getGlobal().getReqIP(), reqDimension,width,height);
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
						calculateWebSize();
						createWebview(null);
						loadData(data);
					}else {
						ZplayDebug.d(TAG, "smaato api banner prepared failed " + error, onoff);
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
