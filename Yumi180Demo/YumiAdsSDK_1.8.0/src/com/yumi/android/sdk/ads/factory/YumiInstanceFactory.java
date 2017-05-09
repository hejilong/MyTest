package com.yumi.android.sdk.ads.factory;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.yumi.android.sdk.ads.control.YumiBannerControl;
import com.yumi.android.sdk.ads.control.YumiInterstitialControl;
import com.yumi.android.sdk.ads.control.YumiMediaControl;

/**
 * Control 实例化工厂
 * @author Administrator
 *
 */
public class YumiInstanceFactory {

    private static Map<String, YumiBannerControl> bannerObtain=new HashMap<String, YumiBannerControl>();
    private static Map<String, YumiInterstitialControl> interstitialObtain=new HashMap<String, YumiInterstitialControl>();
    private static Map<String, YumiMediaControl> mediaObtain=new HashMap<String, YumiMediaControl>();
    
    public static YumiBannerControl getBannerControlInstance(Activity activity, String yumiID, boolean auto)
    {
        if (bannerObtain.containsKey(yumiID)) {
            return bannerObtain.get(yumiID);
        } else {
            YumiBannerControl control = new YumiBannerControl(activity, yumiID, auto);
            bannerObtain.put(yumiID, control);
            return control;
        }
    }
    
    public static void releaseBannerControlInstance( String yumiID)
    {
       try{
           if (bannerObtain.containsKey(yumiID)) {
               bannerObtain.remove(yumiID);
           }
       }catch(Exception e){}
    }
    
    public static YumiInterstitialControl getInterstitialControlInstance(Activity activity, String yumiID, boolean auto)
    {
        if (interstitialObtain.containsKey(yumiID)) {
            return interstitialObtain.get(yumiID);
        } else {
            YumiInterstitialControl control = new YumiInterstitialControl(activity, yumiID, auto);
            interstitialObtain.put(yumiID, control);
            return control;
        }
    }
    
    public static void releaseInterstitialControlInstance( String yumiID)
    {
       try{
           if (interstitialObtain.containsKey(yumiID)) {
               interstitialObtain.remove(yumiID);
           }
       }catch(Exception e){}
    }
    
    
    public static YumiMediaControl getMediaControlInstance(Activity activity, String yumiID)
    {
        if (mediaObtain.containsKey(yumiID)) {
            return mediaObtain.get(yumiID);
        } else {
            YumiMediaControl control = new YumiMediaControl(activity, yumiID);
            mediaObtain.put(yumiID, control);
            return control;
        }
    }
    
    public static void releaseMediaControlInstance( String yumiID)
    {
       try{
           if (mediaObtain.containsKey(yumiID)) {
               mediaObtain.remove(yumiID);
           }
       }catch(Exception e){}
    }
    
    
}
