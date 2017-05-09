package com.yumi.android.sdk.ads.publish;

import android.app.Activity;
import android.content.Context;

import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.control.YumiMediaControl;
import com.yumi.android.sdk.ads.factory.YumiInstanceFactory;
import com.yumi.android.sdk.ads.factory.YumiMediaAdapterFactory;
import com.yumi.android.sdk.ads.publish.enumbean.MediaStatus;
import com.yumi.android.sdk.ads.publish.listener.IYumiMediaListener;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.assets.ConfigValueHandler;
import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;

/**
 * <p> Create a new {@link YumiMedia} with YumiID. You can integrate incentive media ad into you project. 
 * <p> You can invoke {@link #setVersionName(String)} or invoke {@link #setChannelID(String)}} to distinguish different application version , channel or both of them. 
 * <p>  If you require the status of the media request and result.  You need set the {@link IYumiMediaListener} by invoke {@link #setMediaEventListner(IYumiMediaListener)}.
 * 
 * @author Mikoto
 *
 */
public final class YumiMedia {

	private boolean hasRequest = false;
	private final YumiMediaControl control;
	/**
	 *  <p>Create the {@link YumiMedia} instance with activity and YumiId  to integrate incentive media ad on your application.
	 *      
	 * @param activity  The activity you want to exposure the banner
	 * @param yumiID  The prime Id to use Yumi SDK.  you can get the ID from Yumi platform. The yumiID can not be null or empty string; 
	 * 
	 */
	public YumiMedia(Activity activity, String yumiID) {
		control = YumiInstanceFactory.getMediaControlInstance(activity, yumiID);		
	}

	/**
	 *  <p>If your platform set different channel in one application , you can set this Id to distinguish.
	 * @param channelID The Id on platform.  If you need distinguish different channel ,  configure it first. The channel Id is composed of number or English character. 
	 */
	public final void setChannelID(String channelID) {
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
	public final void setVersionName(String versionName) {
        if ("10000".equals(YumiConstants.PARTNER_ID)) {
            control.setVersionName(versionName);
        }
	}

	/**
	 * <p>Set the default version and channelID ;
	 * The version will set the  package  version name;
	 * The channel will read the assets ZplayConfig.xml and get the channel.
	 * <p> This method will invoke {@link YumiMedia#setChannelID(String)} and {@link YumiMedia#setVersionName(String)}. 
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
	 *  <p> If you require the status of the incentive media request and result. , you need invoke this method and set {@link IYumiMediaListener}.   
	 *   You can get the incentive media request success or failed, the incentive media show or close and get incentive.
	 * @param mediaListener The listener instance.  If you don't require callback , can be null, but you can't get the incentive. Highly recommend to set this listener.
	 */
	public final void setMediaEventListner(IYumiMediaListener mediaListener) {
		control.setMediaEventListner(mediaListener);
	}

	/**
	 *  <p>Invoke this method , the Yumi SDK will request incentive media auto. When the incentive media prepared , you can invoke {{@link #showMedia()} to play media.
	 *  You can get the request status by {@link IYumiMediaListener}
	 *  
	 *  @see #setMediaEventListner(IYumiMediaListener)
	 */
	public final void requestYumiMedia() {
		if (!hasRequest) {
			control.requestYumiMedia();
			hasRequest = true;
		}else if (!control.isAuto())
		{
			control.requestYumiMedia();
		}
	}

	/**
	 *  <p> Play incentive media immediately when incentive media prepared. 
	 *  
	 * @return {@link MediaStatus} of current incentive media .
	 * 
	 * @see MediaStatus#NOT_PREPARED
	 * @see MediaStatus#ON_SHOW
	 * @see MediaStatus#REACH_MAX_REWARD 
	 */
	public final MediaStatus showMedia() {
		return control.showMedia();
	}

	/**
     *  <p> examine incentive media is prepared. 
     *  
     * @return {@link boolean} .
     * 
     */
    public boolean isMediaPrepared()
    {
        return control.isMediaPrepared();
    }
    
    /**
     *  <p> return Media Remain Rewards Times. 
     *  
     * @return {@link int} .
     * 
     */
    public int getMediaRemainRewards()
    {
        return control.getMediaRemainRewards();
    }
	

	/**
	 * <p>Cooperate the activity lifecycle , invoke when activity on destroy . 
	 * @see Activity#onDestroy()
	 */
	public final void onDestory() {
		control.onDestory();
	}

}
