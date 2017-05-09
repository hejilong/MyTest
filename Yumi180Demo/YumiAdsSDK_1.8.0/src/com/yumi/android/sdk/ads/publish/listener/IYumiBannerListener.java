package com.yumi.android.sdk.ads.publish.listener;
import com.yumi.android.sdk.ads.control.YumiBannerControl;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 *  <p> Banner request status callback
 * @author Mikoto
 *
 */
public interface IYumiBannerListener {

	/**
	 *  <p> Invoke when the banner prepared. 
	 */
	public void onBannerPrepared();
	
	/**
	 *  <p>Invoke when the banner prepared failed. 
	 * @param errorCode ErroCode 
	 * 
	 * @see LayerErrorCode#ERROR_INTERNAL
	 * @see LayerErrorCode#ERROR_INVALID
	 * @see LayerErrorCode#ERROR_NETWORK_ERROR
	 * @see LayerErrorCode#ERROR_NO_FILL
	 */
	public void onBannerPreparedFailed(LayerErrorCode errorCode);
	
	/**
	 *  <p> Invoke when the banner exposure on screen.
	 */
	public void onBannerExposure();
	
	/**
	 *   <p> Invoke when the banner ad has been clicked.
	 */
	public void onBannerClicked();
	
	/**
	 *   <p> Invoke when the banner ad closed.  The method {@link YumiBannerControl#dismissBanner()} will NOT invoke this.
	 * 
	 * @see YumiBannerControl#dismissBanner()
	 */
	public void onBannerClosed();
}
