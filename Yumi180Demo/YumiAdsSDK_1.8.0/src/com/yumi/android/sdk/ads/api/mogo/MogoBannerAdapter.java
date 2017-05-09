package com.yumi.android.sdk.ads.api.mogo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.view.View;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebBannerLayer;
import com.yumi.android.sdk.ads.listener.IYumiAPIRequestListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.location.LocationHandler;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;


public final class MogoBannerAdapter extends YumiWebBannerLayer{
	

	private static final String TAG = "MogoApiBannerLayer";

	private int reqWidth = 0;
	private int reqHeight = 0;
	private MogoApiRequest req;
	private YumiProviderBean mProvider;
	private Activity mContext;
	
	public MogoBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider, innerListener);
		mProvider = provider;
		mContext = activity;
	}

	private String[] getParamsValues() {
		List<String> list = new ArrayList<String>();
		list.add("101");
		list.add(getProvider().getKey1());
		list.add("1");
		list.add(reqWidth+"");
		list.add(reqHeight+"");
		int[] displayMetrics = PhoneInfoGetter.getDisplayMetrics(getContext());
		list.add(displayMetrics[0]+"");
		list.add(displayMetrics[1]+"");
		list.add("2");
		list.add(PhoneInfoGetter.getIMEI(getContext()));
		list.add(PhoneInfoGetter.getAndroidID(getContext()));
		list.add(PhoneInfoGetter.getMAC(getContext()));
		list.add(PhoneInfoGetter.getIMSI(getContext()));
		list.add(NetworkStatusHandler.isWIFIConnected(getContext()) ? "1" : "2");
		list.add(PhoneInfoGetter.getUserAgent(getContext()));
		list.add(PhoneInfoGetter.getManufacture());
		list.add(PhoneInfoGetter.getModel());
		list.add("android");
		list.add(PhoneInfoGetter.getAndroidSDK()+"");
		list.add(PhoneInfoGetter.getLanguage());
		list.add(PhoneInfoGetter.getDisplayDensity(getContext())+"");
		list.add("H");
		list.add(PhoneInfoGetter.getMNC(getContext()));
		list.add(NetworkStatusHandler.isWIFIConnected(getContext()) ? "1" : "2");
		double lat = 0d;
		double lon = 0d;
		Location lastKnownLocation = LocationHandler.getLocHandler().getLastKnownLocation(getContext());
		if (lastKnownLocation != null) {
			lat = lastKnownLocation.getLatitude();
			lon = lastKnownLocation.getLongitude();
		}
		list.add(lat+"");
		list.add(lon+"");
		list.add(PhoneInfoGetter.getISOCountryCode(getContext()));
		list.add(NetworkStatusHandler.getGsmLac(getContext())+"");
		list.add(NetworkStatusHandler.getGsmCid(getContext())+"");
		list.add(getContext().getPackageName());
		String[] values = list.toArray(new String[]{});
		return values;
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
	protected final void webLayerClickedAndRequestBrowser(String url) {
		ZplayDebug.d(TAG, "mogo api banner clicked", onoff);
		if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
			YumiInlayBrowserUtils.openBrowser(mContext, url, null);
		} else {
			requestSystemBrowser(url);
		}
		layerClicked(upPoint[0], upPoint[1]);
	}


	@Override
	protected final void webLayerPrepared(View view) {
		ZplayDebug.d(TAG, "mogo api banner prepared", onoff);
		layerPrepared(view, false);
		ZplayDebug.d(TAG, "mogo api banner shown", onoff);
		layerExposure();
		
	}


	@Override
	protected final void calculateRequestSize() {
		if (reqWidth == 0 || reqHeight == 0) {
			if (bannerSize == AdSize.BANNER_SIZE_728X90) {
				reqWidth = 728;
				reqHeight = 90;
			}else {
				reqWidth = 320;
				reqHeight = 50;
			}		
		}
	}

	@Override
	protected final void onPrepareBannerLayer() {
		ZplayDebug.d(TAG, "mogo api request new banner ", onoff);
		req.requestApi();
	}


	@Override
	public
	final void init() {
		ZplayDebug.i(TAG, "mogoId : " + getProvider().getKey1(), onoff);
		if (req == null) {
			calculateRequestSize();
			req = new MogoApiRequest(getContext(), new IYumiAPIRequestListener() {

				@Override
				public void onAPIRequestDone(String data, LayerErrorCode error) {
					if (NullCheckUtils.isNotNull(data)) {
						calculateWebSize();
						createWebview(null);
						sendChangeViewBeforePrepared(web);
						loadData(data);
				}
				else if (error != null) {
					ZplayDebug.d(TAG, "mogo api banner failed " + error, onoff);	
					layerPreparedFailed(error);
				}
					
				}
			}, getParamsValues(), false);
		}
		
	}

}
