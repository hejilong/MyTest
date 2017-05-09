package com.yumi.android.sdk.ads.api.sohu;

import com.yumi.android.sdk.ads.api.sohu.SohuApiRequest.SohuClickListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebBannerLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class SohuBannerAdapter extends YumiWebBannerLayer {
	private static final String TAG = "SohuBannerAdapter";
	private SohuApiRequest req;
	private SohuClickListener cl;
	private Activity activity;
	private int reqWidth = 640;
	private int reqHeight = 100;
	private YumiProviderBean mProvider;
	private Activity mContext;

	public SohuBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		this.activity = activity;
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
		// ZplayDebug.D(TAG, "chance api banner click", onoff);
		// requestSystemBrowser(url);
		// if (req!= null) {
		// req.reportSohuClick() ;
		// }
		// layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected void webLayerPrepared(View view) {
		ZplayDebug.d(TAG, "sohu api banner prepared", onoff);
		layerPrepared(view, false);
		if (req != null) {
			req.reoportSohuPrepared();
		}
		ZplayDebug.d(TAG, "sohu api banner shown", onoff);
		layerExposure();
		if (req != null) {
			req.reoportSohuExposure();
		}
	}

	@Override
	protected void calculateRequestSize() {

	}

	@Override
	protected void onPrepareBannerLayer() {
		ZplayDebug.d(TAG, "sohu api request new banner", onoff);
		calculateRequestSize();
		if (req != null) {
			req.requestApi(getProvider().getKey1(), getProvider().getKey3(),
					getProvider().getKey2(), (reqWidth * 10000 + reqHeight) + "");
		}
	}

	@Override
	public void init() {
		ZplayDebug.i(TAG, "developerid " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "appid  " + getProvider().getKey2(), onoff);
		ZplayDebug.i(TAG, "itemspaceid " + getProvider().getKey3(), onoff);
		// registerObserver();
		cl = new SohuClickListener() {
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

	// private void registerObserver() {
	// if (observer == null) {
	// observer = new DownloadObserver() {
	//
	// @Override
	// public void onDownloadComplete() {
	// if (req != null) {
	// req.reportGdtTransform();
	// }
	// }
	//
	// @Override
	// public void onDownload() {
	// if (req != null) {
	// req.reportGdtTransform();
	// }
	// }
	// };
	// ZplayDebug.I(TAG, "build new observer and register to watched ", onoff);
	// watched.registerObserver(observer);
	// ZplayDebug.I(TAG, "register download receiver", onoff);
	// register.registerDownloadReceiver(getContext(), watched);
	// }
	// }
	private void buildRequest() {
		if (req == null) {

			req = new SohuApiRequest(activity, getContext(),
					new IYumiAPIRequestListener() {

						@Override
						public void onAPIRequestDone(String data,
								LayerErrorCode error) {
							if (NullCheckUtils.isNotNull(data)) {
								ZplayDebug.d(TAG, "data = " + data.toString(),
										onoff);
								calculateWebSize();
								createWebview(new OnClickListener() {
									@Override
									public void onClick(View v) {
										SohuLayerClick();
									}
								});
								loadData(data);
							} else {
								ZplayDebug.d(TAG,
										"sohu api banner prepared failed "
												+ error, onoff);
								layerPreparedFailed(error);

							}
						}
					});
		}
	}

	private void SohuLayerClick() {
		ZplayDebug.d(TAG, "Sohu api banner clicked", onoff);
		layerClicked(upPoint[0], upPoint[1]);
		if (req != null) {
			req.reportSohuClick(cl);
		}
	}

	@Override
	protected void callOnActivityDestroy() {
		if (req != null) {
			req.onDestroy();
		}
	}

}
