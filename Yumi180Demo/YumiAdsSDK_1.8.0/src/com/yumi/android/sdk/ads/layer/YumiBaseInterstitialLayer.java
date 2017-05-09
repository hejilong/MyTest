package com.yumi.android.sdk.ads.layer;

import android.app.Activity;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;

public abstract class YumiBaseInterstitialLayer extends YumiBaseLayer {

	protected YumiBaseInterstitialLayer(Activity activity,
			YumiProviderBean provider) {
		super(activity, provider);
	}

	public final void prepareInterstitialLayer(String rid) {
		setRID(rid);
		setYumiInnerListener();
		sendNonResoponseHandler();
		int retryLimit = invariantProvider.getGlobal().getRetryLimit();
		if (retryLimit == 0 || failedTimes < retryLimit) {
			if (NetworkStatusHandler.isNetWorkAvaliable(mContext)) {
				layerRequestReport(LayerType.TYPE_INTERSTITIAL);
				onPrepareInterstitial();
			}else {
				layerPreparedFailed(LayerErrorCode.ERROR_INVALID_NETWORK);
			}
		} else {
			layerPreparedFailed(LayerErrorCode.ERROR_OVER_RETRY_LIMIT);
		}
	};

	/**
	 * <p> Override this method to prepared next interstitial ad.
	 *	<p>This method will invoke when your close the current, or all the interstitial prepared failed. 
	 */
	protected abstract void onPrepareInterstitial();

	public final void showInterstitialLayer(final Activity activity) {
		if (isInterstitialLayerReady()) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					onShowInterstitialLayer(activity);
				}
			});
			layerShowOpportReport(LayerType.TYPE_INTERSTITIAL, LayerErrorCode.CODE_SUCCESS);
		}else {
			layerShowOpportReport(LayerType.TYPE_INTERSTITIAL, LayerErrorCode.CODE_FAILED);
		}
		
	};

	/**
	 * <p> When the {@link #isInterstitialLayerReady()} return true, this method will invoke on UI thread. 
	 * @param activity The activity will show intersitital
	 * @see #isInterstitialLayerReady()
	 */
	protected abstract void onShowInterstitialLayer(Activity activity);

	/**
	 * <p>You must Override this method to tell YumiSDK if the interstitial is ready to show. 
	 * @return If interstitial ready to show return true else return false
	 */
	protected abstract boolean isInterstitialLayerReady();

	/**
	 *  <p> Notify YumiSDK the ad clicked . Don't need to bother to get this point if you use customer SDK . you can pass the -99f , means
	 *  you can't get the point.
	 * @param x  The press point x coordinates relative to ad view top left corner
	 * @param y  The press point y coordinates relative to ad view top left corner
	 */
	protected void layerClicked(float x, float y) {
		super.layerClicked(LayerType.TYPE_INTERSTITIAL, x, y);
	}

	/**
	 * <p> Notify YumiSDK the ad exposure.
	 */
	protected final void layerExposure() {
		super.layerExposure(LayerType.TYPE_INTERSTITIAL);
	}

	/**
	 * <p> Notify YumiSDK the ad closed.
	 */
	protected final void layerClosed() {
		super.layerClosed(LayerType.TYPE_INTERSTITIAL);
	}

	/**
	 * <p> Notify YumiSDk the ad prepared
	 */
	protected final void layerPrepared() {
		super.layerPrepared(LayerType.TYPE_INTERSTITIAL, true);
	}

	/**
	 *  <p> Notify YumiSDk the ad video end
	 */
    protected final void layerMediaEnd() {
        layerEndReport(LayerType.TYPE_INTERSTITIAL);
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
		super.layerPreparedFailed(LayerType.TYPE_INTERSTITIAL, error, true);
	}

	@Override
	protected final void onRequestNonResponse() {
		layerPreparedFailed(LayerErrorCode.ERROR_NON_RESPONSE);
	}
	
	/**
	 * <p>Invoke when this provider round finished.
	 */
	public final void onRoundFinished(){
		notInActivityRound();
	}
	
}
