package com.yumi.android.sdk.ads.ensure;

import android.app.Activity;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.self.ads.i.IntersititialAD;
import com.yumi.android.sdk.ads.self.ads.i.IntersititialADListener;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public final class YumiInterstitialAdapter extends YumiCustomerInterstitialAdapter {

	private static final String TAG = "YumiInterstitialAdapter";
	private ZplayAdExtra holder;
	private IntersititialADListener interstitialListener; 
	private IntersititialAD interstitial;
	private boolean isInterstitialReady = false;
	private int is_tail;
	
	public YumiInterstitialAdapter(Activity activity,
			YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider);
		String providerName = getProvider().getProviderName();
		mInnerListener = innerListener;
		if ("yumimobi".equals(providerName.toLowerCase()))
		{
			this.is_tail = 1;
		}
	}

	@Override
	public final void onActivityPause() {
	}

	@Override
	public final void onActivityResume() {
	}

	@Override
	protected final void callOnActivityDestroy() {
		if (interstitial != null) {
			interstitial.destory();
		}
	}

	@Override
	public final boolean onActivityBackPressed() {
		return false;
	}

	@Override
	protected final void onPrepareInterstitial() {
		ZplayDebug.d(TAG, "Yumi interstitial prepared new ", onoff);
		isInterstitialReady = false;
		if (interstitial != null) {
			interstitial.prepareIntersititial();
		}
	}

	@Override
	protected final void onShowInterstitialLayer(Activity activity) {
		if (interstitial != null) {
			interstitial.showIntersititial();
		}
	}

	@Override
	protected final boolean isInterstitialLayerReady() {
		if (isInterstitialReady) {
			return true;
		}
		return false;
	}

	@Override
	public final void init() {
		createZplayAdListener();
		holder = ZplayAdExtra.getHolder();
		holder.initSelfSDK(getActivity(), getProvider().getKey1(), getProvider().getKey2());
//		if (interstitial != null)
//		{
//			interstitial.destory();
//			interstitial = null;
//		}
		interstitial = new IntersititialAD(getActivity(), is_tail,getProvider().getKey1(), interstitialListener);
	}

	private void createZplayAdListener() {
		interstitialListener = new IntersititialADListener() {
			
			@Override
			public void onIntersititialClick() {
				ZplayDebug.d(TAG, "zplay interstitial clicked", onoff);
				layerClicked(-99f, -99f);
			}

			@Override
			public void onIntersititialDismiss() {
				layerClosed();
				ZplayDebug.d(TAG, "zplay interstitial dismiss", onoff);
//				if (interstitial != null) {
//					interstitial.destory();
//					interstitial = null;
//				}
			}

			@Override
			public void onIntersititialPrepare(String arg0) {
				ZplayDebug.d(TAG, "zplay interstitial prepared", onoff);
				isInterstitialReady = true;
				layerPrepared();
			}

			@Override
			public void onIntersititialRequest(String arg0) {
				ZplayDebug.d(TAG, "zplay interstitial request success", onoff);
			}

			@Override
			public void onIntersititialRequestFailed(String arg0) {
				ZplayDebug.d(TAG, "zplay interstitial request failed " + arg0, onoff);
				layerPreparedFailed(getErrorCode(arg0));
			}

			@Override
			public void onIntersititialShow(String arg0) {
				ZplayDebug.d(TAG, "zplay interstitial prepared and shown", onoff);
				layerExposure();
			}

			@Override
			public void onIntersititialShowFailed(String arg0) {
				ZplayDebug.d(TAG, "zplay interstitial html load failed", onoff);
				layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
			}
		};
	}
	
	   private LayerErrorCode getErrorCode(String errorMsg)
	    {
	        if ( com.yumi.android.sdk.ads.self.entity.Failed.FAILED_REQUEST_NO_FILL.getMsg().equals(errorMsg)) {
	            return LayerErrorCode.ERROR_NO_FILL;
	        }else{
	            return LayerErrorCode.ERROR_INTERNAL;
	        }
	    }

}
