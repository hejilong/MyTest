package com.yumi.android.sdk.ads.publish.listener;

import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 *  <p> Incentive media request status callback
 * @author Mikoto
 *
 */
public interface IYumiMediaListener
{

	/**
	 *  <p> Invoke when the interstitial prepared. 
	 */
//	public void onMediaPrepared();
	
	/**
	 *  <p>Invoke when the interstitial prepared failed. 
	 * @param errorCode ErroCode 
	 * 
	 * @see LayerErrorCode#ERROR_INTERNAL
	 * @see LayerErrorCode#ERROR_INVALID
	 * @see LayerErrorCode#ERROR_NETWORK_ERROR
	 * @see LayerErrorCode#ERROR_NO_FILL
	 */
//	public void onMediaPreparedFailed(LayerErrorCode errorCode);
	
	/**
	 *  <p> Invoke when the interstitial exposure on screen.
	 */
	public void onMediaExposure();
	
	/**
	 *   <p> Invoke when the interstitial ad has been clicked.
	 */
	public void onMediaClicked();
	
	/**
	 *   <p> Invoke when the interstitial ad closed.
	 * 
	 */
	public void onMediaClosed();
	
	/**
	 *  <p> Invoke when media play finished and close the media . This callback aways invoke after {@link #onMediaClosed()}
	 */
	public void onMediaIncentived();
	
	/**
	 * <p>Invoke when request media and notify you number of remaining rewards
	 * @param remain The number of remaining rewards
	 */
//	public void onMediaRemainRewards(int remain);
	
//	public void onMediaDownload();
	
}
