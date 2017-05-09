package com.yumi.android.sdk.ads.api.baidu;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.webkit.WebView;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.utils.json.JsonResolveUtils;

public final class BaiduInterstitialAdapter extends YumiWebInterstitialLayer {

	private static final String TAG = "BaiduApiInterstitialAdapter";
	private BaiduApiRequest request ;
	private IYumiAPIRequestListener resultListener;
	protected int reqWidthDip;
	protected int reqHeightDip;
	private ArrayList<String> impTracker = new ArrayList<String>();
	private YumiProviderBean mProvider;
	private Activity mContext;
	
	public BaiduInterstitialAdapter(Activity activity,
			YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		mProvider = provider;
		mContext = activity;
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
		if (request != null) {
			request.onDestroy();
		}
		impTracker.clear();
	}

	@Override
	public final boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected final void webLayerClickedAndRequestBrowser(String url) {
		ZplayDebug.d(TAG, "baidu api interstitial clicked", onoff);
		 //新增点击判断是否跳转到内置浏览器
		if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
			YumiInlayBrowserUtils.openBrowser(mContext, url, null);
		} else {
			requestSystemBrowser(url);
		}
		layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected final void webLayerPrepared(WebView view) {
		ZplayDebug.d(TAG, "baidu api interstitial prapared", onoff);
		layerPrepared();
	}

	@Override
	protected final void webLayerOnShow() {
		ZplayDebug.d(TAG, "baidu api interstitial shown", onoff);
		layerExposure();
		if (request != null && NullCheckUtils.isNotEmptyCollection(impTracker)) {
			for (String  tracker : impTracker) {
				request.reportUrl(tracker);
			}
		}
	}

	@Override
	protected final void calculateRequestSize() {
		reqWidthDip = 600;
		reqHeightDip = 500;
	}

	@Override
	protected final void onPreparedWebInterstitial() {
		ZplayDebug.d(TAG, "baidu api request new interstitial", onoff);
		if (request != null) {
			request.buildRequestParams(getProvider().getKey1(), getProvider().getKey2(), getProvider().getGlobal().getReqIP(),
					     WindowSizeUtils.dip2px(getContext(), reqWidthDip), WindowSizeUtils.dip2px(getContext(), reqHeightDip));
			request.requestServer();
		}
	}

	@Override
	public
	final void init() {
		ZplayDebug.i(TAG, "appid : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "positionID : " + getProvider().getKey2(), onoff);
		calculateRequestSize();
		buildRequest();
	}

	private void buildRequest() {
		resultListener = new IYumiAPIRequestListener() {
			@Override
			public void onAPIRequestDone(String data, LayerErrorCode error) {
				if (NullCheckUtils.isNotNull(data)) {
					try {
						JSONObject json = new JSONObject(data);
						String html = JsonResolveUtils.getStringFromJson(json, "html", "");
						JSONArray imps = JsonResolveUtils.getJsonArrayFromJson(json, "impTracker");
						if (imps != null && imps.length() > 0) {
							for (int j = 0; j < imps.length(); j++) {
								impTracker.add(imps.getString(j));
							}
						}
						calculateWebSize(reqWidthDip, reqHeightDip);
						createWebview(null);
						loadData(html);
					} catch (JSONException e) {
						ZplayDebug.e(TAG, "", e, onoff);
						layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
					}
				} else {
					layerPreparedFailed(error);
				}
			}
		};
		request = new BaiduApiRequest(getActivity(), resultListener, LayerType.TYPE_INTERSTITIAL);
	}

	@Override
	protected void webLayerDismiss() {
		layerClosed();
	}
}
