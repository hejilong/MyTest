package com.yumi.android.sdk.ads.api.gdtmob;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.yumi.android.sdk.ads.api.gdtmob.GdtApiRequest.GdtClickListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.observer.DownloadObserver;
import com.yumi.android.sdk.ads.observer.DownloadWatched;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;

public final class GdtmobInterstitialAdapter extends YumiWebInterstitialLayer {

	public GdtmobInterstitialAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		mProvider = provider;
		mContext = activity;
	}

	private static final String TAG = "GdtApiInstertitialLayer";

	private int reqWidth;
	private int reqHeight;
	private GdtApiRequest req;
	private GdtClickListener cl;
	private DownloadObserver observer;
	private final DownloadWatched watched = new DownloadWatched();
	private final GdtRegister register = new GdtRegister();
	private YumiProviderBean mProvider;
	private Activity mContext;

	private void buildRequest() {
		if (req == null) {
			req = new GdtApiRequest(getContext(), new IYumiAPIRequestListener() {
				
				@Override
				public void onAPIRequestDone(String data, LayerErrorCode error) {
					if (NullCheckUtils.isNotNull(data)) {
						calculateWebSize(reqWidth, reqHeight);
						createWebview(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								gdtLayerClick();
							}
						});
						loadData(data);
					}
					else if (error != null) {
						ZplayDebug.d(TAG, "gdt api interstitial failed " + error, onoff);
						layerPreparedFailed(error);
					}
					
				}
			},GdtApiRequest.REQ_TYPE_INSTERTITIAL);
		}
	}

	private void gdtLayerClick(){
		ZplayDebug.d(TAG, "gdt api interstitial clicked", onoff);
		layerClicked(upPoint[0], upPoint[1]);
		if (req != null) {
			req.reportGdtClick(cl, downPoint[0], downPoint[1], upPoint[0], upPoint[1]);
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
		if (register != null) {
			register.unregisterReceiver(getContext());
		}
		if (req != null) {
			req.onDestroy();
		}
		if (watched != null) {
			watched.unregisterObserver(observer);
		}
	}

	@Override
	public final boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected final void webLayerClickedAndRequestBrowser(String url) {
		gdtLayerClick();
	}

	@Override
	protected final void webLayerPrepared(WebView view) {
		ZplayDebug.d(TAG, "gdt api interstitial prepared", onoff);
		layerPrepared();
	}

	@Override
	protected final void webLayerOnShow() {
		ZplayDebug.d(TAG, "gdt api interstitial shown", onoff);
		layerExposure();
		if (req != null) {
			req.reoporGdtExposure();
		}

	}

	@Override
	protected final void calculateRequestSize() {
		int dpi = PhoneInfoGetter.getDisplayDensityDpi(getContext());
		if (dpi < 320) {
			reqWidth = 300;
			reqHeight = 250;
		}
		if (dpi >= 320) {
			reqWidth = 600;
			reqHeight = 500;
		}

	}

	private void registerObserver() {
		if (observer == null) {
			observer = new DownloadObserver() {

				@Override
				public void onDownloadComplete(String path) {
					ZplayDebug.e(TAG, "gdt watcher ondownload done", onoff);
					if (req != null) {
						req.reportGdtTransform(GdtApiRequest.TRANSFORM_TYPE_DOWNLOAD_COMPLETE);
					}
				}

				@Override
				public void onDownload() {
					ZplayDebug.e(TAG, "gdt watcher ondownload", onoff);
					req.reportGdtTransform(GdtApiRequest.TRANSFORM_TYPE_DOWNLOAD);
				}
			};
		}
		ZplayDebug.i(TAG, "build new observer and register to watched ", onoff);
		watched.registerObserver(observer);
		ZplayDebug.i(TAG, "register download receiver", onoff);
		register.registerDownloadReceiver(getContext(), watched);
	}

	@Override
	protected final void onPreparedWebInterstitial() {
		ZplayDebug.d(TAG, "gdt api request new interstitial", onoff);
		calculateRequestSize();
		req.requestApi(getProvider().getKey1(), getProvider().getKey2(), reqWidth+"", reqHeight+"", 2, getProvider().getGlobal().getReqIP());
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

	@Override
	protected void webLayerDismiss() {
		layerClosed();
	}
}
