package com.yumi.android.sdk.ads.api.ym;

import com.yumi.android.sdk.ads.api.ym.YMApiRequest.YoumiClickListener;
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
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

public final class YMInterstitialAdapter extends YumiWebInterstitialLayer {

	private static final String TAG = "YoumiApiInsteritialLayer";

	private YMApiRequest req;
	private YoumiClickListener cl;
	private DownloadObserver observer;
	private final DownloadWatched watched = new DownloadWatched();
	private final YMRegister register = new YMRegister();
	private int reqWidth = 0;
	private int reqHeight = 0;
	protected boolean instertitialPageError;
	private boolean hasregister = false;
	protected boolean interstitialReady;
	private Activity activity;
	private YumiProviderBean mProvider;
	private Activity mContext;

	public YMInterstitialAdapter(Activity activity,
			YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		this.activity = activity;
		mProvider = provider;
		mContext = activity;
	}

	@Override
	protected final void calculateRequestSize() {
		if (reqWidth == 0 || reqHeight == 0) {
			if (WindowSizeUtils.isTablet(activity)) {
				reqWidth = 900;
				reqHeight = 750;
			} else {
				reqWidth = 600;
				reqHeight = 500;
			}
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
		Log.d(TAG, "销毁");
		if (req != null) {
			req.onDestroy();
		}
		if (register != null) {
			register.unregisterReceiver(getContext());
		}
		if (watched != null) {
			watched.unregisterObserver(observer);
		}
		if (myReceiver != null) {
			try {
				Log.d(TAG, "注销广播接收器");
				activity.unregisterReceiver(myReceiver);
				hasregister = false;
			} catch (Exception e) {
				ZplayDebug.e(TAG, e.getMessage(), e, onoff);
			}
		}
	}

	@Override
	public final boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected final void onPreparedWebInterstitial() {
		ZplayDebug.d(TAG, "youmi api request new interstitial", onoff);
		Log.d(TAG, "注册广播接收器");
		if (req != null) {
			req.requestApi(getProvider().getKey1(), getProvider().getKey2(),
					reqWidth, reqHeight, 2, getProvider().getGlobal()
							.getReqIP());
		}
	}

	@Override
	public final void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		registerObserver();
		calculateRequestSize();
		if (!hasregister) {
			hasregister = true;
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.intent.action.PACKAGE_ADDED");
			filter.addDataScheme("package");
			((Context) activity).registerReceiver(myReceiver, filter);
		}
		cl = new YoumiClickListener() {
			@Override
			public void onClick(boolean jumpWeb, String downloadUrl) {
				ZplayDebug.d(TAG, "下载11", onoff);
				if (NullCheckUtils.isNotNull(downloadUrl)) {
					if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
						YumiInlayBrowserUtils.openBrowser(mContext,	downloadUrl, null);
					} else {
						requestSystemBrowser(downloadUrl);
					}
				} else {
					closeOnResume();
				}

//				if (jumpWeb) {
//					if (NullCheckUtils.isNotNull(downloadUrl)) {
//						requestWebActivity(downloadUrl, true);
//					} else {
//						closeOnResume();
//					}
//				} else {
//					if (NullCheckUtils.isNotNull(downloadUrl)) {
//						ZplayDebug.d(TAG, "下载", onoff);
//						requestWebActivity(downloadUrl, false);
//					} else {
//						closeOnResume();
//					}
//				}
			}
		};
		buildRequest();

	}

	private void buildRequest() {
		if (req == null) {
			req = new YMApiRequest(activity, getContext(),
					new IYumiAPIRequestListener() {
						@Override
						public void onAPIRequestDone(String data,
								LayerErrorCode error) {
							if (data != null) {
								calculateWebSize(reqWidth, reqHeight);
								createWebview(new OnClickListener() {

									@Override
									public void onClick(View v) {
										YoumiLayerClick();
									}
								});
								Log.d(TAG, "加载  data = " + data);
								loadData(data);
							} else if (error != null) {
								ZplayDebug.d(TAG,
										"Youmi api interstitial failed "
												+ error, onoff);
								layerPreparedFailed(error);
							}
						}
					});
		}
	}

	private void YoumiLayerClick() {
		ZplayDebug.d(TAG, "Youmi api banner clicked", onoff);
		layerClicked(upPoint[0], upPoint[1]);
		if (req != null) {
			req.reportYoumiClick(cl);
		}
	}

	private void registerObserver() {
		if (observer == null) {
			observer = new DownloadObserver() {

				private String packageName;

				@Override
				public void onDownloadComplete(String path) {
					Log.d(TAG, "path = "+path);
					if(NullCheckUtils.isNotNull(path)){
					PackageInfo info = activity.getPackageManager().getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES);
                      packageName = info.packageName; 
                      Log.d(TAG, "packageName = "+packageName);
					}
					if(YMApiRequest.adMap.containsKey(packageName)){
						String track = YMApiRequest.adMap.get(packageName);
						if (req != null && NullCheckUtils.isNotNull(track)) {
							req.reportTransform(YMApiRequest.TRANSFORM_TYPE_DOWNLOAD_COMPLETE,track);
							Log.d(TAG, "下载完成上报");
						}
					
					if (req != null && NullCheckUtils.isNotNull(track)) {
						req.reportTransform(YMApiRequest.TRANSFORM_TYPE_INSTALL,track);
						Log.d(TAG, "下载完成上报");
					}
					}
				}

				@Override
				public void onDownload() {
					if (req != null) {
						req.reportYoumiTransform(YMApiRequest.TRANSFORM_TYPE_DOWNLOAD);
						Log.d(TAG, "下载上报");
					}
				}
			};
			ZplayDebug.i(TAG, "build new observer and register to watched ",
					onoff);
			watched.registerObserver(observer);
			ZplayDebug.i(TAG, "register download receiver", onoff);
			register.registerDownloadReceiver(getContext(), watched);
		}
	}

	@Override
	protected final void webLayerClickedAndRequestBrowser(String url) {
		ZplayDebug.d(TAG, "Youmi api interstitial clicked", onoff);
		YoumiLayerClick();
	}

	@Override
	protected final void webLayerOnShow() {
		Log.d(TAG, "Youmi api interstitial shown");
		layerExposure();
		if (req != null) {
			req.reoporYoumiExposure();
		}
	}

	@Override
	protected final void webLayerPrepared(WebView view) {
		ZplayDebug.d(TAG, "youmi api interstitial prapared", onoff);
		layerPrepared();
	}

	@Override
	protected void webLayerDismiss() {
		layerClosed();
	}

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String packageName = intent.getData().getSchemeSpecificPart();
			String trackid = YMApiRequest.adMap.get(packageName);
			Log.d(TAG, "action = " + action + "packageName = " + packageName);
			Log.d(TAG, "YoumiApiRequest.adMap = " +YMApiRequest.adMap.toString() );
			Log.d(TAG, "trackid = " +trackid );
			if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
				if(YMApiRequest.adMap.containsKey(packageName)){
					if (req != null) {
						req.reportTransform(YMApiRequest.TRANSFORM_TYPE_INSTALL_COMPLETE,trackid);
						Log.d(TAG, "安装上报");
					}
				}
				

			}
		}

	};
}
