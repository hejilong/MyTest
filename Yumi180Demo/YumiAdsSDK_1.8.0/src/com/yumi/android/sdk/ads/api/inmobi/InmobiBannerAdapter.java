package com.yumi.android.sdk.ads.api.inmobi;

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
import com.yumi.android.sdk.ads.utils.YumiGooglePlayServiceCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

public final class InmobiBannerAdapter extends YumiWebBannerLayer{


	private static final String TAG = "InmobiApiBannerLayer";
	private InmobiApiReqeust req;
	private int reqAdSizeInt;
	private int reqOrientation;
	private String gpID;
	private YumiProviderBean mProvider;
	private Activity mContext;
	
	public InmobiBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
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
		if (req != null) {
			req.onDestroy();
		}
	}


	@Override
	protected final void onPrepareBannerLayer() {
		ZplayDebug.d(TAG, "inmobi api request new banner", onoff);
		calculateRequestSize();
		if (!NullCheckUtils.isNotNull(gpID) && YumiGooglePlayServiceCheckUtils.isGooglePlayIsAvailable(getContext())) {
			gpID = YumiGooglePlayServiceCheckUtils.getGooglePlayID(getContext());
		}
		if (req != null) {
			req.requestApi(reqAdSizeInt, getProvider().getKey1(), getProvider().getGlobal().getReqIP(), gpID, reqOrientation);
		}
	}



	@Override
	public
	final void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		if (req == null) {
			req = new InmobiApiReqeust(getContext(), new IYumiAPIRequestListener() {
				
				@Override
				public void onAPIRequestDone(String data, LayerErrorCode error) {
					if (data != null) {
						calculateWebSize();
						createWebview(null);
						loadData(data);
					}
					else if (error != null) {
						ZplayDebug.d(TAG, "inmobi api banner failed " + error, onoff);
						layerPreparedFailed(error);
					}
					
				}
			}, LayerType.TYPE_BANNER);
		}
	}


	@Override
	protected final void calculateRequestSize() {
		if (reqAdSizeInt == 0) {
			if (bannerSize == AdSize.BANNER_SIZE_728X90) {
				reqAdSizeInt = 11;
			}else {
				reqAdSizeInt = 15;
			}
		}
		if (WindowSizeUtils.isPortrait(getContext())) {
			reqOrientation = 1;
		}else {
			reqOrientation = 3;
		}
	}

	@Override
	protected final void webLayerClickedAndRequestBrowser(String url) {
		ZplayDebug.d(TAG, "inmobi api banner clicked", onoff);
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
		ZplayDebug.d(TAG, "inmobi api banner prepared", onoff);
		layerPrepared(view, false);
		ZplayDebug.d(TAG, "inmobi api banner shown", onoff);
		layerExposure();
	}

	
}
