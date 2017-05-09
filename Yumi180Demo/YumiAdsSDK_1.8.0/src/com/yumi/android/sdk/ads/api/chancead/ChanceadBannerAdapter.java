package com.yumi.android.sdk.ads.api.chancead;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.yumi.android.sdk.ads.utils.json.JsonResolveUtils;

public final class ChanceadBannerAdapter extends YumiWebBannerLayer {
	
	private static final String TAG = "ChanceApiBannerAdapter";
	private ChanceadApiRequest req;
	private int reqWpix = 0;
	private int reqHpix = 0;
	private List<String> clkList = new ArrayList<String>();
	private YumiProviderBean mProvider;
	private Activity mContext;
	
	public ChanceadBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
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
	protected void calculateRequestSize() {
		if (bannerSize == AdSize.BANNER_SIZE_320X50) {
			reqWpix = WindowSizeUtils.dip2px(getContext(), 320);
			reqHpix = WindowSizeUtils.dip2px(getContext(), 50);
		}
		if (bannerSize == AdSize.BANNER_SIZE_728X90) {
			reqWpix = WindowSizeUtils.dip2px(getContext(), 728);
			reqHpix = WindowSizeUtils.dip2px(getContext(), 90);
		}		
	}

	@Override
	protected void onPrepareBannerLayer() {
		ZplayDebug.d(TAG, "chance api request new banner", onoff);
		calculateRequestSize();
		if (req != null) {
			req.requestApi(getProvider().getKey2(), getProvider().getKey1(), getProvider().getKey3(), reqWpix, reqHpix, getProvider().getGlobal().getReqIP());
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
							String html = JsonResolveUtils.getStringFromJson(result, "html", null);
							if (NullCheckUtils.isNotNull(html)) {
								saveClickTracker(JsonResolveUtils.getJsonArrayFromJson(result, "clkTracker"));
								calculateWebSize();
								createWebview(null);
								loadData(html);
							}else {
								layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
							}
						} catch (JSONException e) {
							layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
						}
					}else {
						ZplayDebug.d(TAG, "chance api banner failed " + error, onoff);
						layerPreparedFailed(error);
					}
				}
			}, LayerType.TYPE_BANNER);
		}
	}

	@Override
	protected void callOnActivityDestroy() {
		if (req != null) {
			req.onDestroy();
		}
	}

	@Override
	protected void webLayerClickedAndRequestBrowser(String url) {
		ZplayDebug.d(TAG, "chance api banner click", onoff);
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
	protected void webLayerPrepared(View view) {
		ZplayDebug.d(TAG, "chance api banner prepared", onoff);
		layerPrepared(view, false);
		ZplayDebug.d(TAG, "chance api banner shown", onoff);
		layerExposure();
	}

}
