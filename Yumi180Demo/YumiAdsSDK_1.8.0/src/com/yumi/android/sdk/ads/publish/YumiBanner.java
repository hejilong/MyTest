package com.yumi.android.sdk.ads.publish;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.control.YumiBannerControl;
import com.yumi.android.sdk.ads.factory.YumiInstanceFactory;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.listener.IYumiBannerListener;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.XiaoMiSDKUtil;
import com.yumi.android.sdk.ads.utils.assets.ConfigValueHandler;
import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;
/**
 * <p> Create a  new {@link YumiBannerControl} with YumiID. You can integrate banner ad into you project. 
 * <p> You can invoke {@link #setVersionName(String)} or invoke {@link #setChannelID(String)}} to distinguish different application version , channel or both of them. 
 * <p> You need set a {@link ViewGroup} as banner container,  and you need to set the container size with {@link #setBannerContainer(ViewGroup, AdSize)}. 
 * <p> If you require the status of the banner request and result.  You need set the {@link IYumiBannerListener} by invoke {@link #setBannerEventListener(IYumiBannerListener)}.
 * 
 * @author Mikoto
 *
 */
public class YumiBanner{

	private boolean hasRequest = false;
	private YumiBannerControl control;
//	private Activity activity;
	/**
	 *  <p>Create the {@link YumiBannerControl} instance with activity and YumiId  to integrate banner ad on your application.
	 *      
	 * @param activity  The activity you want to exposure the banner
	 * @param yumiID  The prime Id to use Yumi SDK.  you can get the ID from Yumi platform. The yumiID can not be null or empty string; 
	 * 
	 */
	public YumiBanner(Activity activity, String yumiID, boolean auto){
		control = YumiInstanceFactory.getBannerControlInstance(activity, yumiID, auto);
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
	 * <p> This method will invoke {@link YumiBanner#setChannelID(String)} and {@link YumiBanner#setVersionName(String)}. 
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
	 *  <p> SDK need set a {@link ViewGroup} as banner container,  and you need assign {@link AdSize} and position to this container .
	 *  Banner view will fill into the container and exposure .  
	 *  <p> You must invoke this method after your activity setContentView().
	 * @param container The banner container
	 * @param bannerSize The banner size you can choose from {@link AdSize}. 
	 * 
	 * @see AdSize#BANNER_SIZE_320X50
	 * @see AdSize#BANNER_SIZE_728X90
	 * @see AdSize#BANNER_SIZE_AUTO
	 */
	public final void setBannerContainer(FrameLayout container, AdSize bannerSize){
		control.setBannerContainer(container, bannerSize, false);
	}
	
	
	/**
     *  <p> SDK need set a {@link ViewGroup} as banner container,  and you need assign {@link AdSize} and position to this container .
     *  Banner view will fill into the container and exposure .  
     *  <p> You must invoke this method after your activity setContentView().
     * @param container The banner container
     * @param bannerSize The banner size you can choose from {@link AdSize}. 
     * 
     * @see AdSize#BANNER_SIZE_320X50
     * @see AdSize#BANNER_SIZE_728X90
     * @see AdSize#BANNER_SIZE_AUTO
     * @param bannerSize The banner size you can choose from {@link AdSize}. 
     */
    public final void setBannerContainer(FrameLayout container, AdSize bannerSize, boolean isMatchWindowWidth){
        control.setBannerContainer(container, bannerSize, isMatchWindowWidth);
    }

	
	/**
	 *  <p> If you require the status of the banner request and result. , you need invoke this method and set {@link IYumiBannerListener}.   
	 *   You can get the banner request success or failed, the banner shown or dismiss on screen, the banner ad click.
	 * @param bannerListener The listener instance.  If you don't require callback , can be null
	 */
	public final void setBannerEventListener(IYumiBannerListener bannerListener){
		control.setBannerEventListener(bannerListener);
	}
	
	
	/**
	 * <p>Cooperate the activity lifecycle , invoke when activity on destroy. 
	 * @see Activity#onDestroy()
	 */
	public final void onDestroy(){
		control.onDestroy();
	}
	
	/**
	 * <p> Hide banner  and pause the banner request circulation at the same time.
	 * 
	 */
	public final void dismissBanner(){
		control.dismissBanner();
	}
	
	/**
	 * <p> Resume banner to visiable, and resume banner request circulation at the same time.
	 */
	public final void resumeBanner(){
		control.resumeBanner();
	}
	
	/**
	 *  <p>Invoke this method , the Yumi SDK will request banner auto, and show banner if there has ad to show. 
	 *  You can get the request status by {@link IYumiBannerListener}
	 *  
	 *  @see #setBannerEventListener(IYumiBannerListener)
	 */
	public final void requestYumiBanner(){
		if (!hasRequest) {
			control.requestYumiBanner();
			hasRequest = true;
		}else if (!control.isAuto())
		{
			control.requestYumiBanner();
		}
	}
}

