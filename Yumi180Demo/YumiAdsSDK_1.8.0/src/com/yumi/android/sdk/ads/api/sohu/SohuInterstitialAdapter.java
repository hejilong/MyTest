package com.yumi.android.sdk.ads.api.sohu;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.yumi.android.sdk.ads.api.sohu.SohuApiRequest.SohuClickListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

public class SohuInterstitialAdapter extends YumiWebInterstitialLayer {

	private static final String TAG = "SohuApiInterstitialAdatper";
	private SohuApiRequest req;
	private int reqWidth = 600;
	private int reqHeight = 500;
	private SohuClickListener cl;
	private Activity activity;
	private YumiProviderBean mProvider;
	private Activity mContext;

	public SohuInterstitialAdapter(Activity activity,
			YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
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
		closeOnResume();
	}

	@Override
	public boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected void webLayerClickedAndRequestBrowser(String url) {
		// ZplayDebug.D(TAG, "Sohu api interstitial clicked", onoff);
		// requestSystemBrowser(url);
		// layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected void webLayerPrepared(WebView view) {
		ZplayDebug.d(TAG, "Sohu api interstitial prepared", onoff);
		layerPrepared();
		if (req != null) {
			req.reoportSohuPrepared();
		}
	}

	@Override
	protected void webLayerOnShow() {
		ZplayDebug.d(TAG, "Sohu api interstitial shown", onoff);
		layerExposure();
		if (req != null) {
			req.reoportSohuExposure();
		}
	}

	@Override
	protected void calculateRequestSize() {
		if (reqWidth == 0 || reqHeight == 0) {
			int dpi = PhoneInfoGetter.getDisplayDensityDpi(getContext());
			if (dpi < 160) {
				reqWidth = 120;
				reqHeight = 78;
			}
			if (dpi >= 160 && dpi < 240) {
				reqWidth = 360;
				reqHeight = 234;
			}
			if (dpi >= 240 && dpi < 320) {
				reqWidth = 480;
				reqHeight = 240;
			}
			if (dpi >= 320) {
				reqWidth = 640;
				reqHeight = 320;
			}
		}
	}

	@Override
	protected void webLayerDismiss() {
		layerClosed();
	}

	@Override
	protected void onPreparedWebInterstitial() {
		ZplayDebug.d(TAG, "Sohu api request new interstitial", onoff);
		ZplayDebug.d(TAG,
				"reqWidth = " + reqWidth + "reqHeight = " + reqHeight, onoff);
		if (req != null) {
			req.requestApi(getProvider().getKey1(), getProvider().getKey3(),
					getProvider().getKey2(), (reqWidth * 10000 + reqHeight) + "");
		}
	}

	@Override
	public void init() {
		ZplayDebug.d(TAG, "developerid " + getProvider().getKey1(), onoff);
		ZplayDebug.d(TAG, "appid " + getProvider().getKey2(), onoff);
		ZplayDebug.d(TAG, "itemspaceid " + getProvider().getKey3(), onoff);
		calculateRequestSize();
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

	private void buildRequest() {
		if (req == null) {
			req = new SohuApiRequest(activity, getContext(),
					new IYumiAPIRequestListener() {

						@Override
						public void onAPIRequestDone(String data,
								LayerErrorCode error) {
							if (NullCheckUtils.isNotNull(data)) {
								calculateWebSize(reqWidth, reqHeight);
								createWebview(new OnClickListener() {

									@Override
									public void onClick(View v) {
										SohuLayerClick();
										layerClicked(upPoint[0], upPoint[1]);
									}
								});
								loadData(data);
							} else {
								ZplayDebug
										.d(TAG, "Sohu api interstitial failed "
												+ error, onoff);
								layerPreparedFailed(error);
							}
						}
					});
		}
	}

	private void SohuLayerClick() {
		ZplayDebug.d(TAG, "Sohu api interstitial clicked", onoff);
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
