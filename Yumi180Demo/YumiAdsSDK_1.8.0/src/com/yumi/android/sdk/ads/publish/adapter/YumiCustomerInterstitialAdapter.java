package com.yumi.android.sdk.ads.publish.adapter;

import android.app.Activity;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.YumiBaseInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiActivityLifecycleListener;

/**
 * <p> If you need integrate interstitial ad with customer SDK which YumiSDK don't supply, you need extends {@link YumiCustomerInterstitialAdapter} .
 * 
 * <p> You must define an explicit constructor , and invoke super constructor. 
 *  This constructor make sure the YumiSDK can reflect the instance of your customer adapter and throught this to call customer SDK method.
 *   You need override the unimplement method as follow:
 *   <ul>
 *  <li> {@link #init()} Create field which customer SDK required  like some event listener or customer sdk instance and initialize customer SDK. 
 *  <li> {@link #onPrepareInterstitial()} When the SDK initialized. override this method to prepared customer interstiial.
 *  <li> {@link #isInterstitialLayerReady()} Override this method to notify YumiSDK  if the customer interstitial is ready to show.
 *  <li> {@link #showInterstitialLayer(Activity)} If {@link #isInterstitialLayerReady()} return true, invoke to show customer interstitial
 *  </ul>
 *  <p> About the activity lifecycle. This class implement the {@link IYumiActivityLifecycleListener}. If customer SDK need handle 
 *  activity lifecycle . You can invoke in these method. 
 *  
 *  <p> You need set customer event listener to get the banner request status as follow at least.  YumiSDK will handle these callback to get next ad provider.
 *  <ul>
 *  <li> interstitial prepared failed
 *  <li> interstitial prepared success
 *  <li> interstitial closed 
 *  </ul>
 *  <br>
 *   
 *   
 *  <p> You can invoke these method when customer SDK callback to notify YumiSDK to enter the next step.
 *  <ul>
 *  <li>{@link #layerPrepared()} 
 *  <li>{@link #layerPreparedFailed(com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode)}
 *  <li>{@link #layerExposure()}
 *  <li>{@link #layerClosed()}
 *  <li>{@link #layerClicked(float, float)}
 *  </ul>
 * 
 * @author Mikoto
 *
 */
public abstract class YumiCustomerInterstitialAdapter extends YumiBaseInterstitialLayer {

	public static final boolean onoff = true;
	
	protected YumiCustomerInterstitialAdapter(Activity activity,
			YumiProviderBean provider) {
		super(activity, provider);
	}

}
