package com.yumi.android.sdk.ads.ensure;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.self.ads.b.BannerAD;
import com.yumi.android.sdk.ads.self.ads.b.BannerADListener;
import com.yumi.android.sdk.ads.self.entity.ADSize;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.widget.FrameLayout;

public final class YumiBannerAdapter extends YumiCustomerBannerAdapter {

	private static final String TAG = "YumiBannerAdapter";
	private FrameLayout bannerContainer;
	private ZplayAdExtra holder;
	private int bannerWidth;
	private int bannerHeight;
	private BannerAD lastBanner;
	private BannerAD banner;
	private BannerADListener bannerAdListener;
	private int is_tail;
	
	public YumiBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider);
		mInnerListener = innerListener;
		String providerName = getProvider().getProviderName();
		if ("yumimobi".equals(providerName.toLowerCase()))
		{
			this.is_tail = 1;
		}
	}

	@Override
	public final void onActivityPause() {
		if (banner != null) {
			banner.pause();
		}
	}

	@Override
	public final void onActivityResume() {
		if (banner != null) {
			banner.resume();
		}
	}

	@Override
	protected final void callOnActivityDestroy() {
		if (banner != null) {
			banner.destory();
		}
	}

	@Override
	protected final void onPrepareBannerLayer() {
		ZplayDebug.d(TAG, "yumi banner prepared new", onoff);
		lastBanner = banner;
//		if (banner != null) {
//			banner.destory();
//			banner = null;
//		}
		createBannerContainer();
		ADSize bannersize = ADSize.BANNER_SIZE_320_50_DIP;
		if (bannerSize == AdSize.BANNER_SIZE_728X90) {
			bannersize = ADSize.BANNER_SIZE_728_90_DIP;
		}
		banner = new BannerAD(getActivity(), bannerContainer, bannersize, is_tail,getProvider().getKey1(), bannerAdListener);	
	}

	@Override
	public
	final void init() {
		holder = ZplayAdExtra.getHolder();
		holder.initSelfSDK(getActivity(), getProvider().getKey1(), getProvider().getKey2());
		createBannerListener();
	}

	private void createBannerListener() {
		bannerAdListener = new BannerADListener() {
			
			@Override
			public void onBannerClick() {
				ZplayDebug.d(TAG, "zplay banner clicked", onoff);
				layerClicked(-99f, -99f);
			}

			@Override
			public void onBannerRequest(String arg0) {
				ZplayDebug.d(TAG, "zplay banner request success and add banner container", onoff);
				sendChangeViewBeforePrepared(bannerContainer);
			}

			@Override
			public void onBannerRequestFailed(String arg0) {
				ZplayDebug.d(TAG, "zplay banner request failed " + arg0, onoff);
				layerPreparedFailed(getErrorCode(arg0));
			}

			@Override
			public void onBannerShow(String arg0) {
				ZplayDebug.d(TAG, "zplay banner prepared and shown", onoff);
				layerPrepared(bannerContainer, false);
				layerExposure();
				
				Handler handler = getHandler();
				if (handler!=null)
				{
					handler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							if (lastBanner!=null)
							{
								try
								{
									lastBanner.destory();
								} catch (Exception e)
								{
								}
							}
						}
					}, 2000);
				}
			}

			@Override
			public void onBannerShowFailed(String arg0) {
				ZplayDebug.d(TAG, "zplay banner html load failed", onoff);
				layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
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

	private void createBannerContainer() {
		calculateBannerSize();
		bannerContainer = new FrameLayout(getContext());
		bannerContainer.setLayoutParams(new FrameLayout.LayoutParams(bannerWidth, bannerHeight, Gravity.CENTER));
	}

	private void calculateBannerSize() {
		if (bannerSize == AdSize.BANNER_SIZE_728X90) {
			bannerWidth = WindowSizeUtils.dip2px(getContext(), 728);
			bannerHeight = WindowSizeUtils.dip2px(getContext(), 90);
		}else {
			bannerWidth = WindowSizeUtils.dip2px(getContext(), 320);
			bannerHeight = WindowSizeUtils.dip2px(getContext(), 50);
		}
	}


}
