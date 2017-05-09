package com.yumi.android.sdk.ads.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;

public final class YumiManifestReaderUtils {
	
	private static final String TAG = "YumiManifestReaderUtils";
	private static final boolean onoff = true;
	private static Map<String, String> baseSupportProviderSet = new HashMap<String, String>();
	static
	{
		baseSupportProviderSet.put("com.google.android.gms.ads.AdActivity" , "admob");
		baseSupportProviderSet.put("com.inmobi.androidsdk.IMBrowserActivity" , "inmobi");
		baseSupportProviderSet.put("com.chartboost.sdk.CBImpressionActivity" , "chartboost");
		baseSupportProviderSet.put("com.qq.e.ads.ADActivity" , "gdtmob");
//		baseSupportProviderSet.put("com.baidu.mobads.AppActivity" , "baidu");
//		baseSupportProviderSet.put("cn.domob.android.ads.DmActivity" , "domob");
		baseSupportProviderSet.put("com.flurry.android.FlurryFullscreenTakeoverActivity" , "flurry");
		baseSupportProviderSet.put("com.apptracker.android.module.AppModuleActivity" , "leadbolt");
		baseSupportProviderSet.put("com.vpadn.widget.VpadnActivity" , "vpadn");
		baseSupportProviderSet.put("com.supersonicads.sdk.controller.ControllerActivity" , "supersonic");
		baseSupportProviderSet.put("com.supersonicads.sdk.controller.InterstitialActivity" , "supersonic");
		baseSupportProviderSet.put("com.supersonicads.sdk.controller.OpenUrlActivity" , "supersonic");
		baseSupportProviderSet.put("com.smaato.soma.interstitial.InterstitialActivity" , "smaato");
		baseSupportProviderSet.put("com.smaato.soma.ExpandedBannerActivity" , "smaato");
//		baseSupportProviderSet.put("com.mobisage.android.MobiSageActivity", "mobisage");
		baseSupportProviderSet.put("com.loopme.AdActivity", "loopme");
		baseSupportProviderSet.put("ActivityWhereBannerLocated", "loopme");
		baseSupportProviderSet.put("com.tapjoy.TJAdUnitActivity", "tapjoy");
		baseSupportProviderSet.put("com.tapjoy.mraid.view.ActionHandler", "tapjoy");
		baseSupportProviderSet.put("com.tapjoy.mraid.view.Browser", "tapjoy");
		baseSupportProviderSet.put("com.unity3d.ads.android.view.UnityAdsFullscreenActivity", "unity");
		baseSupportProviderSet.put("com.applovin.adview.AppLovinInterstitialActivity", "applovin");
		baseSupportProviderSet.put("com.applovin.adview.AppLovinConfirmationActivity", "applovin");
		baseSupportProviderSet.put("com.mopub.mobileads.MoPubActivity", "mopub");
		baseSupportProviderSet.put("com.mopub.mobileads.MraidActivity", "mopub");
		baseSupportProviderSet.put("com.mopub.common.MoPubBrowser", "mopub");
		baseSupportProviderSet.put("com.mopub.mobileads.MraidVideoPlayerActivity", "mopub");
		baseSupportProviderSet.put("com.millennialmedia.internal.MMActivity", "millennial");
		baseSupportProviderSet.put("com.millennialmedia.internal.MMIntentWrapperActivity", "millennial");
		//171修改
        baseSupportProviderSet.put("com.xiaomi.ad.AdActivity", "xiaomi");
        baseSupportProviderSet.put("com.vungle.publisher.VideoFullScreenAdActivity" , "vungle");
        baseSupportProviderSet.put("com.vungle.publisher.MraidFullScreenAdActivity" , "vungle");
        baseSupportProviderSet.put("com.adcolony.sdk.AdColonyInterstitialActivity" , "adcolony");
        baseSupportProviderSet.put("com.adcolony.sdk.AdColonyAdViewActivity" , "adcolony");
        baseSupportProviderSet.put("com.facebook.ads.AudienceNetworkActivity" , "facebook");
        baseSupportProviderSet.put("com.startapp.android.publish.ads.list3d.List3DActivity", "startapp");
        baseSupportProviderSet.put("com.startapp.android.publish.adsCommon.activities.OverlayActivity", "startapp");
        baseSupportProviderSet.put("com.startapp.android.publish.adsCommon.activities.FullScreenActivity", "startapp");
        baseSupportProviderSet.put("com.unity3d.ads.adunit.AdUnitActivity", "unity");
        baseSupportProviderSet.put("com.unity3d.ads.adunit.AdUnitSoftwareActivity", "unity");
	}
	
	private static ArrayList<String> activityList = null;
	
	public static final synchronized ArrayList<String> getTheProviderRegistedInManifest(Context context){
		if (activityList != null) {
			return activityList;
		}else {
			Set<String> registerActivity = PackageInfoGetter.getRegisterActivity(context);
			registerActivity.retainAll(baseSupportProviderSet.keySet());
			activityList = new ArrayList<String>();
			for (String key : registerActivity) {
				String provider = baseSupportProviderSet.get(key);
				if (!activityList.contains(provider)) {
					activityList.add(provider);
				}
			}
			return activityList;
		}
	}
	
	public static final synchronized void release(){
		activityList = null;
	}
	
	public final static boolean hasRegisterNecessary(Context context){
		final String webActivity = "com.yumi.android.sdk.ads.activity.YumiBrowserActivity";
		final String eventService = "com.yumi.android.sdk.ads.service.YumiAdsEventService";
		final String openService = "com.yumi.android.sdk.ads.self.module.service.OpenPkgService";
		final String reportService = "com.yumi.android.sdk.ads.self.module.service.ADEventReport";
		boolean hasActivity = false;
		boolean hasService = false;
		try {
			Set<String> registerActivity = PackageInfoGetter.getRegisterActivity(context);
			if (NullCheckUtils.isNotEmptyCollection(registerActivity)) {
				hasActivity = registerActivity.contains(webActivity);
			}
			Set<String> registerService = PackageInfoGetter.getRegisterService(context);
			if (NullCheckUtils.isNotEmptyCollection(registerService)) {
				hasService = registerService.contains(eventService) == true ? (registerService.contains(openService) == true ? registerService.contains(reportService) : false) : false;
			}
			return hasActivity && hasService;
		} catch (Exception e) {
			ZplayDebug.e(TAG, "", e, onoff);
			return false;
		}
	}
	
	public final static boolean hasMediaRegisterNecessary(Context context){
        final String webActivity = "com.yumi.android.sdk.ads.activity.YumiBrowserActivity";
        final String selfmediaActivity = "com.yumi.android.sdk.ads.selfmedia.activity.YumiFullScreenActivity";
        final String eventService = "com.yumi.android.sdk.ads.service.YumiAdsEventService";
        final String openService = "com.yumi.android.sdk.ads.selfmedia.module.service.OpenPkgService";
        final String reportService = "com.yumi.android.sdk.ads.selfmedia.module.service.ADEventReport";
        boolean hasActivity = false;
        boolean hasselfmediaActivity = false;
        boolean hasService = false;
        try {
            Set<String> registerActivity = PackageInfoGetter.getRegisterActivity(context);
            if (NullCheckUtils.isNotEmptyCollection(registerActivity)) {
                hasActivity = registerActivity.contains(webActivity);
                hasselfmediaActivity = registerActivity.contains(selfmediaActivity);
            }
            Set<String> registerService = PackageInfoGetter.getRegisterService(context);
            if (NullCheckUtils.isNotEmptyCollection(registerService)) {
                hasService = registerService.contains(eventService) == true ? (registerService.contains(openService) == true ? registerService.contains(reportService) : false) : false;
            }
            return hasActivity && hasService && hasselfmediaActivity;
        } catch (Exception e) {
            ZplayDebug.e(TAG, "", e, onoff);
            return false;
        }
    }
	
	
}
