package com.yumi.android.sdk.ads.publish.listener;

import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 *  <p> Interstitial request status callback
 * @author Mikoto
 *
 */
public interface IYumiInterstititalListener {

	/**
	 *  <p> Invoke when the interstitial prepared. 
	 */
	public void onInterstitialPrepared();
	
	/**
	 *  <p>Invoke when the interstitial prepared failed. 
	 * @param errorCode ErroCode 
	 * 
	 * @see LayerErrorCode#ERROR_INTERNAL
	 * @see LayerErrorCode#ERROR_INVALID
	 * @see LayerErrorCode#ERROR_NETWORK_ERROR
	 * @see LayerErrorCode#ERROR_NO_FILL
	 */
	public void onInterstitialPreparedFailed(LayerErrorCode errorCode);
	
	/**
	 *  <p> Invoke when the interstitial exposure on screen.
	 */
	public void onInterstitialExposure();
	
	/**
	 *   <p> Invoke when the interstitial ad has been clicked.
	 */
	public void onInterstitialClicked();
	
	/**
	 *   <p> Invoke when the interstitial ad closed.
	 * 
	 */
	public void onInterstitialClosed();
}
