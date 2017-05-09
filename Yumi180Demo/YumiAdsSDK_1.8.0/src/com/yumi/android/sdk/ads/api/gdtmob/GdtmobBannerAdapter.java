package com.yumi.android.sdk.ads.api.gdtmob;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.yumi.android.sdk.ads.api.gdtmob.GdtApiRequest.GdtClickListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebBannerLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.observer.DownloadObserver;
import com.yumi.android.sdk.ads.observer.DownloadWatched;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;


public final class GdtmobBannerAdapter extends YumiWebBannerLayer {


	private static final String TAG = "GdtApiBannerLayer";
	private GdtApiRequest req;
	private GdtClickListener cl;
	private int reqWidth = 0;
	private int reqHeight = 0;
	private DownloadObserver observer;
	private final DownloadWatched watched = new DownloadWatched();
	private final GdtRegister register = new GdtRegister();
	private YumiProviderBean mProvider;
	private Activity mContext;
	
	public GdtmobBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		mProvider = provider;
		mContext = activity;
	}
	
	private void buildRequest() {
		if (req == null) {
			
			req = new GdtApiRequest(getContext(), new IYumiAPIRequestListener() {
				
				@Override
				public void onAPIRequestDone(String data, LayerErrorCode error) {
					if (NullCheckUtils.isNotNull(data)) {
						calculateWebSize();
						createWebview(
								new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								gdtLayerClick();
							}
						}
								);
						loadData(data);
					}
					else if (error != null) {
						ZplayDebug.d(TAG, "gdt api banner failed " + error, onoff);
						layerPreparedFailed(error);
					}
				}
			},GdtApiRequest.REQ_TYPE_BANNER);
		}
	}

	
	@Override
	public void onActivityPause() {
		
	}

	@Override
	public void onActivityResume() {
		
	}

	@Override
	protected final void callOnActivityDestroy() {
		if (register != null) {
			register.unregisterReceiver(getContext());
		}
		if (req != null) {
			req.onDestroy();
		}
	}

	@Override
	protected void webLayerClickedAndRequestBrowser(String url) {
	}

	private void gdtLayerClick(){
		ZplayDebug.d(TAG, "gdt api banner clicked", onoff);
		layerClicked(upPoint[0], upPoint[1]);
		if (req != null) {
			req.reportGdtClick(cl, downPoint[0], downPoint[1], upPoint[0], upPoint[1]);
		}
	}
	
	@Override
	protected final void webLayerPrepared(View view) {
		ZplayDebug.d(TAG, "gdt api banner prepared", onoff);
		layerPrepared(view, false);
		ZplayDebug.d(TAG, "gdt api banner shown", onoff);
		layerExposure();
		if (req != null) {
			req.reoporGdtExposure();
		}
		
	}

	@Override
	protected final void calculateRequestSize() {
		if (reqWidth == 0 || reqHeight == 0) {
			int dpi = PhoneInfoGetter.getDisplayDensityDpi(getContext());
			if (dpi < 160) {
				reqWidth = 240;
				reqHeight = 38;
			}
			if (dpi >= 160 && dpi < 240) {
				reqWidth = 320;
				reqHeight = 50;
			}
			if (dpi >= 240 && dpi < 320) {
				reqWidth = 480;
				reqHeight = 75;
			}
			if (dpi >= 320) {
				reqWidth = 640;
				reqHeight = 100;
			}
		}
	}

	private void registerObserver() {
		if (observer == null) {
			observer = new DownloadObserver() {
				
				@Override
				public void onDownloadComplete(String path) {
					if (req != null) {
						req.reportGdtTransform(GdtApiRequest.TRANSFORM_TYPE_DOWNLOAD_COMPLETE);
					}
				}
				
				@Override
				public void onDownload() {
					if (req != null) {
						req.reportGdtTransform(GdtApiRequest.TRANSFORM_TYPE_DOWNLOAD);
					}
				}
			};
			ZplayDebug.i(TAG, "build new observer and register to watched ", onoff);
			watched.registerObserver(observer);
			ZplayDebug.i(TAG, "register download receiver", onoff);
			register.registerDownloadReceiver(getContext(), watched);
		}
	}

	@Override
	protected final void onPrepareBannerLayer() {
		ZplayDebug.d(TAG, "gdt api request new banner ", onoff);
		calculateRequestSize();
		ZplayDebug.d(TAG, "reqWidth = "+reqWidth+"  "+reqHeight, onoff);
		req.requestApi(getProvider().getKey1(), getProvider().getKey2(), reqWidth+"", reqHeight+"", 1, getProvider().getGlobal().getReqIP());
	}

	@Override
	public
	final void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "positionId : " + getProvider().getKey2(), onoff);
		registerObserver();
		cl = new GdtClickListener() {
			@Override
			public void onClick(boolean jumpWeb, String downloadUrl) {
				 //新增点击判断是否跳转到内置浏览器
				if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
					YumiInlayBrowserUtils.openBrowser(mContext, downloadUrl, null);
				} else {
					requestSystemBrowser(downloadUrl);
				}
//				if (jumpWeb) {
//					if (NullCheckUtils.isNotNull(downloadUrl)) {
//						requestWebActivity(downloadUrl, true);
//					}
//				}else {
//					if (NullCheckUtils.isNotNull(downloadUrl)) {
//						requestWebActivity(downloadUrl, false);
//					}
//				}
			}
		};		
		buildRequest();
	}
	
}
