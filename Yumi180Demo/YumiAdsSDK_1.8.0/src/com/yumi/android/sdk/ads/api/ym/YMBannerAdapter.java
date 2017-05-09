package com.yumi.android.sdk.ads.api.ym;

import com.yumi.android.sdk.ads.api.ym.YMApiRequest.YoumiClickListener;
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

public final class YMBannerAdapter extends YumiWebBannerLayer {

	private static final String TAG = "YoumiApiBannerLayer";
	private int reqWidth = 0;
	private int reqHeight = 0;
	private YMApiRequest req;
	private YoumiClickListener cl;
	private DownloadObserver observer;
	private final DownloadWatched watched = new DownloadWatched();
	private final YMRegister register = new YMRegister();
	private boolean hasregister = false;
	private Activity activity;
	private YumiProviderBean mProvider;
	private Activity mContext;

	public YMBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		this.activity = activity;
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
		if (register != null) {
			register.unregisterReceiver(getContext());
		}
		if (watched != null) {
			watched.unregisterObserver(observer);
		}
		if (myReceiver != null) {
			try {
				activity.unregisterReceiver(myReceiver);
				hasregister = false;
			} catch (Exception e) {
				ZplayDebug.e(TAG, e.getMessage(), e, onoff);
			}
		}
	}

	@Override
	protected final void onPrepareBannerLayer() {
		ZplayDebug.d(TAG, "youmi api request new banner", onoff);
		calculateRequestSize();
		if (req != null) {
			req.requestApi(getProvider().getKey1(), getProvider().getKey2(),
					640, 100, 1, getProvider().getGlobal().getReqIP());
		}
	}

	@Override
	public
	final void init() {
		ZplayDebug.i(TAG, "appId : " + getProvider().getKey1(), onoff);
		registerObserver();
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

	private void buildRequest() {
		if (req == null) {
			req = new YMApiRequest(activity, getContext(),
					new IYumiAPIRequestListener() {

						@Override
						public void onAPIRequestDone(String data,
								LayerErrorCode error) {
							if (data != null) {
								calculateWebSize();
								createWebview(new OnClickListener() {

									@Override
									public void onClick(View v) {
										YoumiLayerClick();
									}
								});
								loadData(data);
							} else if (error != null) {
								ZplayDebug.d(TAG, "youmi api banner failed "
										+ error, onoff);
								layerPreparedFailed(error);
							}

						}
					});
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

	private void YoumiLayerClick() {
		ZplayDebug.d(TAG, "Youmi api banner clicked", onoff);
		layerClicked(upPoint[0], upPoint[1]);
		if (req != null) {
			req.reportYoumiClick(cl);
		}
	}

	@Override
	protected final void calculateRequestSize() {

	}

	@Override
	protected final void webLayerClickedAndRequestBrowser(String url) {
	}

	@Override
	protected final void webLayerPrepared(View view) {
		ZplayDebug.d(TAG, "Youmi api banner prepared", onoff);
		layerPrepared(view, false);
		ZplayDebug.d(TAG, "Youmi api banner shown", onoff);
		layerExposure();
		if (req != null) {
			req.reoporYoumiExposure();
		}
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
