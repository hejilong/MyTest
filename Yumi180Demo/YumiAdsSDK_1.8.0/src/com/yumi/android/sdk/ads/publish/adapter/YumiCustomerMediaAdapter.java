package com.yumi.android.sdk.ads.publish.adapter;

import android.app.Activity;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.YumiBaseMediaLayer;
import com.yumi.android.sdk.ads.listener.IYumiActivityLifecycleListener;

/**
 * <p> If you need integrate incentive media ad with customer SDK which YumiSDK don't supply, you need extends {@link YumiCustomerMediaAdapter} .
 * This abstract class only for integrate incentive media ad. Your customer SDK must has the reward function. If it's normal media ad. You can attempt integrate by {@link YumiCustomerInterstitialAdapter}
 * 
 * <p> You must define an explicit constructor , and invoke super constructor. 
 *  This constructor make sure the YumiSDK can reflect the instance of your customer adapter and throught this to call customer SDK method.
 *  You need override the unimplement method as follow:
 *  <ul>
 *  <li>{@link #init()} Create field which customer SDK required  like some event listener or customer sdk instance and initialize customer SDK.
 *  <li>{@link #onPrepareMedia()} When the SDK initialized. override this method to prepared customer incentive media. 
 *  <li>{@link #isMediaReady()} Override this to notify YumiSDK if the customer media is ready to play.
 *  <li>{@link #showMedia()} Invoke to play media when the {@link #isMediaReady()} return true, and not over limit of get reward times per day.
 *  </ul>
 *  <p> About the activity lifecycle. This class implement the {@link IYumiActivityLifecycleListener}. If customer SDK need handle 
 *  activity lifecycle . You can invoke in these method. 
 *  
 *  <p> You need set customer event listener to get the banner request status as follow at least.  YumiSDK will handle these callback to get next ad provider.
 *  <ul>
 *  <li> incentive media prepared failed
 *  <li> incentive media prepared success
 *  <li> incentive media closed 
 *  <li> incentive media get reward 
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
 *  <li>{@link #layerClicked()}
 *  <li>{@link #layerIncentived()}
 *  </ul>
 * 
 * @author Mikoto
 *
 */
public abstract class YumiCustomerMediaAdapter extends YumiBaseMediaLayer {

	public static final boolean onoff = true;
	
	protected YumiCustomerMediaAdapter(Activity activity,
			YumiProviderBean provider) {
		super(activity, provider);
	}


}
