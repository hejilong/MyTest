package com.yumi.android.sdk.ads.api.baidu;

import android.app.Activity;
import android.view.View;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebBannerLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

public final class BaiduBannerAdapter extends YumiWebBannerLayer {

	private static final String TAG = "BaiduApiBannerAdapter";
	private BaiduApiRequest request;
	private IYumiAPIRequestListener resultListener;
	private int reqWidth = 0;
	private int reqHeight = 0;
	private YumiProviderBean mProvider;
	private Activity mContext;

	public BaiduBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		mProvider = provider;
		mContext = activity;
	}

	@Override
	public final void onActivityPause() {

	}

	@Override
	public final void onActivityResume() {

	}

	@Override
	protected final void callOnActivityDestroy() {
		if (request != null) {
			request.onDestroy();
		}
	}

	@Override
	protected final void webLayerClickedAndRequestBrowser(String url) {
		ZplayDebug.d(TAG, "baidu api banner clicked", onoff);
		 //新增点击判断是否跳转到内置浏览器
		if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
			YumiInlayBrowserUtils.openBrowser(mContext, url, null);
		} else {
			requestSystemBrowser(url);
		}
		layerClicked(upPoint[0], upPoint[1]);

	}

	@Override
	protected final void webLayerPrepared(View view) {
		ZplayDebug.d(TAG, "baidu api banner prepared", onoff);
		layerPrepared(view, false);
		ZplayDebug.d(TAG, "baidu api banner shown", onoff);
		layerExposure();
	}

	@Override
	protected final void calculateRequestSize() {
		if (reqWidth == 0 || reqHeight == 0) {
			if (bannerSize == AdSize.BANNER_SIZE_728X90) {
				reqWidth = WindowSizeUtils.dip2px(getContext(), 728);
				reqHeight = WindowSizeUtils.dip2px(getContext(), 90);
			}else {
				reqWidth = WindowSizeUtils.dip2px(getContext(), 320);
				reqHeight = WindowSizeUtils.dip2px(getContext(), 50);
			}
		}
	}

	@Override
	protected final void onPrepareBannerLayer() {
		ZplayDebug.d(TAG, "baidu api request new banner", onoff);
		calculateRequestSize();
		if (request != null) {
			request.buildRequestParams(getProvider().getKey1(), getProvider().getKey2(), getProvider().getGlobal().getReqIP(), reqWidth, reqHeight);
			request.requestServer();
		}
	}

	@Override
	public
	final void init() {
		ZplayDebug.i(TAG, "baidu api banner appid : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "baidu api banner positionID : " + getProvider().getKey2(), onoff);
		buildRequest();
	}

	private void buildRequest() {
		resultListener = new IYumiAPIRequestListener() {

			
			@Override
			public void onAPIRequestDone(String html, LayerErrorCode error) {
				if (NullCheckUtils.isNotNull(html)) {
					calculateWebSize();
					createWebview(null);
					loadData(html);
				} else {
					layerPreparedFailed(error);
				}
			}
		};
		request = new BaiduApiRequest(getActivity(), resultListener, LayerType.TYPE_BANNER);
	}

}
