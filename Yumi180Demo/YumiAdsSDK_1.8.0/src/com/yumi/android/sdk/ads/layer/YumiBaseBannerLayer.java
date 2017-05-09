package com.yumi.android.sdk.ads.layer;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.control.YumiBannerControl;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;

public abstract class YumiBaseBannerLayer extends YumiBaseLayer {

	private static final boolean onoff = true;
	private static final String TAG = "YumiBaseBannerLayer";
	protected AdSize bannerSize = AdSize.BANNER_SIZE_AUTO;
	private Handler viewHandler;
	private boolean callbackInPrepared = false;
	private boolean sendChangeViewBeforePreapred = false;
	private boolean isNewRound = false;
	protected boolean isMatchWindowWidth=false;
	protected int[] calculateLayerSize;
	
	protected YumiBaseBannerLayer(Activity activity, YumiProviderBean provider) {
		super(activity, provider);
	}

	private final void setBannerSize(AdSize bannerSize){
		this.bannerSize = bannerSize;
	}
	
	private final void setBannerViewHandler(Handler viewHandler){
		this.viewHandler = viewHandler;
	}
	
	public Handler getHandler()
	{
		return viewHandler;
	}

	/**
	 *  <p> Notify YumiSDK the ad clicked . Don't need to bother to get this point if you use customer SDK . you can pass the -99f , means
	 *  you can't get the point.
	 * @param x  The press point x coordinates relative to ad view top left corner
	 * @param y  The press point y coordinates relative to ad view top left corner
	 */
	protected void layerClicked(float x, float y) {
		super.layerClicked(LayerType.TYPE_BANNER, x, y);
	}
	
	/**
	 * <p> Notify YumiSDK the ad exposure.
	 */
	protected final void layerExposure() {
		if (callbackInPrepared) {
			return ;
		}else {
			super.layerExposure(LayerType.TYPE_BANNER);
		}
	}
	
	/**
	 * <p> Notify YumiSDK the ad closed.
	 */
	protected final void layerClosed() {
		super.layerClosed(LayerType.TYPE_BANNER);
	}
	
	/**
	 *  <p> Notify YumiSDK the ad prepared . 
	 * @param banner The banner view your create include the prepared ad.
	 * @param nonExposureCallback Some customer don't callback the banner exposure on screen, you need pass true. Pass false otherwise. 
	 * <li> If true , YumiSDK think when the banner parepared , it will exposure immediately.  
	 * <li> If true , the method {@link #layerExposure()} has effect only in this method . Direct invoke will ineffect.
	 * <li> Usually the parameter pass false
	 */
	protected final void layerPrepared(View banner, boolean nonExposureCallback) {
		super.layerPrepared(LayerType.TYPE_BANNER, isNewRound);
		if (needCallbackInnerListener && isNewRound) {
			if (!sendChangeViewBeforePreapred) {
				sendChangeView(banner);
			}
			if (nonExposureCallback) {
				layerExposure();
				callbackInPrepared  = true;
			}
		}
		isNewRound = false;
	}
	
	public final void sendChangeViewBeforePrepared(View banner){
		sendChangeViewBeforePreapred  = true;
		sendChangeView(banner);
	}

	private void sendChangeView(View banner) {
		if (viewHandler != null && banner != null) {
			Message msg = Message.obtain();
			msg.what = YumiBannerControl.CHANGE_BANNER_VIEW;
			msg.obj = banner;
			viewHandler.sendMessage(msg);
		}else {
			ZplayDebug.e(TAG, banner + " banner  " + viewHandler + " viewhandler ", onoff);
		}
	}
	
	/**
	 * <p> Notify YumiSDK the ad prepared failed, you only need to consider these error. 
	 * <li> {@link LayerErrorCode#ERROR_INTERNAL}
	 * <li> {@link LayerErrorCode#ERROR_INVALID}
	 * <li> {@link LayerErrorCode#ERROR_NETWORK_ERROR}
	 * <li> {@link LayerErrorCode#ERROR_NO_FILL}
	 * <p>Accuratly use error type is helpful to statistic analysis.
	 * 
	 * @param error Ad request failed reason 
	 * 
	 * @see LayerErrorCode#ERROR_INTERNAL
	 * @see LayerErrorCode#ERROR_INVALID
	 * @see LayerErrorCode#ERROR_NETWORK_ERROR
	 * @see LayerErrorCode#ERROR_NO_FILL
	 */
	protected final void layerPreparedFailed(LayerErrorCode error) {
		super.layerPreparedFailed(LayerType.TYPE_BANNER, error, isNewRound);
		isNewRound = false;
	}
	
	public final void prepareBannerLayer(AdSize adSize, Handler handler, String rid, boolean isMatchWindowWidth,int[] calculateLayerSize){
	    this.isMatchWindowWidth=isMatchWindowWidth;
	    this.calculateLayerSize=calculateLayerSize;
		setBannerSize(adSize);
		setBannerViewHandler(handler);
		setRID(rid);
		isNewRound = true;
		setYumiInnerListener();
		sendNonResoponseHandler();
		int retryLimit = invariantProvider.getGlobal().getRetryLimit();
		callbackInPrepared = false;
		sendChangeViewBeforePreapred= false;
		if (retryLimit == 0 || failedTimes < retryLimit) {
			if (NetworkStatusHandler.isNetWorkAvaliable(mContext)) {
				layerRequestReport(LayerType.TYPE_BANNER);
				onPrepareBannerLayer();
			}else {
				layerPreparedFailed(LayerErrorCode.ERROR_INVALID_NETWORK);
			}
		}else {
			layerPreparedFailed(LayerErrorCode.ERROR_OVER_RETRY_LIMIT);
		}
	};
	
	@Override
	protected final void onRequestNonResponse() {
		layerPreparedFailed(LayerErrorCode.ERROR_NON_RESPONSE);
		onActivityDestroy();
	}
	
	/**
	 * <p>Invoke when this provider round finished.
	 */
	public final void onRoundFinished(){
		notInActivityRound();
	}
	
	/**
	 * <p> Override this method, prepared banner ad. 
	 * <p> This method will invoke by interval configuration. 
	 */
	protected abstract void onPrepareBannerLayer();
	
	@Override
	public final boolean onActivityBackPressed() {
		return false;
	}
	
}
