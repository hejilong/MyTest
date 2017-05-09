package com.yumi.android.sdk.ads.publish;

import android.app.Activity;
import android.content.Context;

import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.control.YumiInterstitialControl;
import com.yumi.android.sdk.ads.factory.YumiInstanceFactory;
import com.yumi.android.sdk.ads.publish.listener.IYumiInterstititalListener;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.XiaoMiSDKUtil;
import com.yumi.android.sdk.ads.utils.assets.ConfigValueHandler;
import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;

/**
 * 
 * <p> Create a new {@link YumiInterstitial} with YumiID. You can integrate interstitial ad into you project. 
 * <p> You can invoke {@link #setVersionName(String)} or invoke {@link #setChannelID(String)}} to distinguish different application version , channel or both of them. 
 * <p> If you require the status of the interstitial request and result.  You need set the {@link IYumiInterstititalListener} by invoke {@link #setInterstitialEventListener(IYumiInterstititalListener)}.
 * 
 * @author Mikoto
 *
 */
public final class YumiInterstitial {

	private boolean hasRequest  = false;
	private final YumiInterstitialControl control;
	/**
	 *  <p>Create the {@link YumiInterstitial} instance with activity and YumiId  to integrate interstitial ad on your application.
	 *      
	 * @param activity  The activity you want to exposure the interstitial
	 * @param yumiID  The prime Id to use Yumi SDK.  you can get the ID from Yumi platform. The yumiID can not be null or empty string; 
	 * 
	 */
	public YumiInterstitial(Activity activity, String yumiID, boolean auto){
		control = YumiInstanceFactory.getInterstitialControlInstance(activity, yumiID, auto);
		XiaoMiSDKUtil.init(activity);
	}

	/**
	 *  <p>If your platform set different channel in one application , you can set this Id to distinguish.
	 * @param channelID The Id on platform.  If you need distinguish different channel ,  configure it first. The channel Id is composed of number or English character. 
	 */
	public final void setChannelID(String channelID){
        if ("10000".equals(YumiConstants.PARTNER_ID)) {
            control.setChannelID(channelID);
        }
	}

	/**
	 *  <p>Set the different version of your application,  you can distinguish by version if you set up .  
	 *  This version can use any String which is composed of number and English character. Not limit the package version name ,
	 *   if and only if this String equals your platform configure is accepted.
	 * @param versionName :  the version name on platform if you need distinguish different version . You can configure this id on platform.  
	 */
	public final void setVersionName(String versionName){
        if ("10000".equals(YumiConstants.PARTNER_ID)) {
            control.setVersionName(versionName);
        }
	}
	
	/**
	 * <p>Set the default version and channelID ;
	 * The version will set the  package  version name;
	 * The channel will read the assets ZplayConfig.xml and get the channel.
	 * <p> This method will invoke {@link YumiInterstitial#setChannelID(String)} and {@link YumiInterstitial#setVersionName(String)}. 
	 * If you have the config file, you can invoke this method instead
	 * @param applicationContext applicationContext
	 */
	public final void setDefaultChannelAndVersion(Context applicationContext){
        if ("10000".equals(YumiConstants.PARTNER_ID)) {
            control.setVersionName(PackageInfoGetter.getAppVersionName(applicationContext.getPackageManager(), applicationContext.getPackageName()));
            String channel = ConfigValueHandler.getChannel(applicationContext);
            if (NullCheckUtils.isNotNull(channel)) {
                control.setChannelID(channel);
            } else {
                control.setChannelID("");
            }
        }
	}
	
	/**
	 *  <p> If you require the status of the interstitial request and result. , you need invoke this method and set {@link IYumiInterstititalListener}.   
	 *   You can get the interstitial request success or failed, the interstitial show or close , the interstitial ad click.
	 * @param interstitialListener The listener instance.  If you don't require callback , can be null
	 */
	public final  void setInterstitialEventListener(IYumiInterstititalListener interstitialListener){
		control.setInterstitialEventListener(interstitialListener);
	}
	
	/**
	 *  <p>Invoke this method , the Yumi SDK will request interstitial auto. When the interstitial prepared , you can invoke {{@link #showInterstitial(boolean)} to show interstitial.
	 *  You can get the request status by {@link IYumiInterstititalListener}
	 *  
	 *  @see #setInterstitialEventListener(IYumiInterstititalListener)
	 */
	public final void requestYumiInterstitial(){
		if (!hasRequest) {
			control.requestYumiInterstitial();
			hasRequest = true;
		}else if (!control.isAuto())
		{
			control.requestYumiInterstitial();
		}
	}
	
	/**
	 *  <p> Show interstitial when the interstitial prepared .
	 * @param delayToShowEnable True means there is no prepared interstitial when you invoke this method, allow to wait interstitial to load, and when the interstitial prepared will show auto. 
	 * And if you cannot to wait the interstitial shown , you need return to your application and don't hope the interstitial shown to disturb your own function. You must invoke {@link #cancelInterstitialDelayShown()}. 
	 *          <p> False means if there is interstitial prepared interstitial will be show immediately,  else doesn't show anything when you invoke 
	 * 
	 *  @see #cancelInterstitialDelayShown()
	 */
	public final void showInterstitial(boolean delayToShowEnable){
		control.showInterstitial(delayToShowEnable);
	}
	
	/**
	 * <p> When you invoke {@link #showInterstitial(boolean)} and passing true. You can invoke this method to cancel waiting. Even if the interstitial prepared soon , the interstitial doesn't shown.
	 * 
	 * @see #showInterstitial(boolean)
	 */
	public final void cancelInterstitialDelayShown(){
		control.cancelInterstitialDelayShown();
	}
	
	
	/**
	 * <p>Cooperate the activity lifecycle , invoke when activity on destroy . 
	 * @see Activity#onDestroy()
	 */
	public final void onDestory(){
		control.onDestory();
	}
	
	/**
	 * <p>Cooperate the activity lifecycle , invoke when press back . 
	 * 
	 * @return  If true, you need direct return else invoke super.onBackPressed()
	 *     
	 *   <p> Sample
	 * <pre class="prettyprint">
	 *   public void onBackPressed() {
	 *	   	if (interstitial.onBackPressed()) {
	 *	 		return ;
	 *	 	}
	 *		super.onBackPressed();
	 *	}
	 * </pre>              
	 * 
	 * @see Activity#onBackPressed()
	 */
	public final boolean onBackPressed(){
		return control.onBackPressed();
	}
}
