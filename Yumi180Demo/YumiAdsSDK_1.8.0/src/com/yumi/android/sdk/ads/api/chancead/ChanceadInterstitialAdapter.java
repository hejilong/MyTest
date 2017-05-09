package com.yumi.android.sdk.ads.api.chancead;

import java.util.ArrayList;
import java.util.List;

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

public final class ChanceadInterstitialAdapter extends YumiWebInterstitialLayer {

	private static final String TAG = "ChanceApiInterstitialAdapter";
	private ChanceadApiRequest req;
	private int reqWdip;
	private int reqHdip;
	private List<String> clkList = new ArrayList<String>();
	private YumiProviderBean mProvider;
	private Activity mContext;
	
	public ChanceadInterstitialAdapter(Activity activity,
			YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
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
		ZplayDebug.d(TAG, "chance api interstitial clicked", onoff);
		 //新增点击判断是否跳转到内置浏览器
		if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
			YumiInlayBrowserUtils.openBrowser(mContext, url, null);
		} else {
			requestSystemBrowser(url);
		}
		if (NullCheckUtils.isNotEmptyCollection(clkList) && req != null) {
			for (String tracker : clkList) {
				req.reportTracker(tracker);
			}
		}
		layerClicked(upPoint[0], upPoint[1]);
	}

	@Override
	protected void webLayerPrepared(WebView view) {
		ZplayDebug.d(TAG, "chance api interstitial prepared", onoff);
		layerPrepared();
	}

	@Override
	protected void webLayerOnShow() {
		ZplayDebug.d(TAG, "chance api interstitial shown", onoff);
		layerExposure();
		if (webview != null && isInterstitialLayerReady()) {
		    webview.loadUrl("javascript:show()");
		}
	}

	@Override
	protected void calculateRequestSize() {
		if (WindowSizeUtils.isPortrait(getContext())) {
			reqWdip = 500;
			reqHdip = 600;
		}else {
			reqWdip = 600;
			reqHdip = 500;
		}
	}

	@Override
	protected void webLayerDismiss() {
		layerClosed();
	}

	@Override
	protected void onPreparedWebInterstitial() {
		ZplayDebug.d(TAG, "chance api request new interstitial", onoff);
		calculateRequestSize();
		if (req != null) {
			req.requestApi(getProvider().getKey2(), getProvider().getKey1(), getProvider().getKey3(),
					WindowSizeUtils.dip2px(getContext(), reqWdip), WindowSizeUtils.dip2px(getContext(), reqHdip), 
					getProvider().getGlobal().getReqIP());
		}
	}

	@Override
	public void init() {
		ZplayDebug.i(TAG, "chance publisherID is " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "chance placementID is " + getProvider().getKey2(), onoff);
		ZplayDebug.i(TAG, "chance secret is " + getProvider().getKey3(), onoff);
		if (req == null) {
			req = new ChanceadApiRequest(getContext(), new IYumiAPIRequestListener() {
				
				@Override
				public void onAPIRequestDone(String data, LayerErrorCode error) {
					if (data != null) {
						
						try {
							JSONObject result = new JSONObject(data);
							int w = JsonResolveUtils.getIntFromJson(result, "w", 0);
							int h = JsonResolveUtils.getIntFromJson(result, "h", 0);
							String html = JsonResolveUtils.getStringFromJson(result, "html", "");
							if (NullCheckUtils.isNotNull(html)) {
								saveClickTracker(JsonResolveUtils.getJsonArrayFromJson(result, "clkTracker"));
								if (w != 0 && h != 0) {
									calculateWebSize(w, h);
								}else {
									calculateWebSize(reqWdip, reqHdip);
								}
								createWebview(null);
								loadData(html);
							}else {
								layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
						}
					}else {
						ZplayDebug.d(TAG, "chance api interstitial failed " + error, onoff);
						layerPreparedFailed(error);
					}
				}
			}, LayerType.TYPE_INTERSTITIAL);
		}
	}

	private void saveClickTracker(JSONArray trackers) {
		if (NullCheckUtils.isNotEmptyCollection(clkList)) {
			clkList.clear();
		}
		if (trackers != null && trackers.length() > 0) {
			for (int i = 0; i < trackers.length(); i++) {
				try {
					clkList.add(trackers.getString(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void callOnActivityDestroy() {
		if (req != null) {
			req.onDestroy();
		}
	}

}
