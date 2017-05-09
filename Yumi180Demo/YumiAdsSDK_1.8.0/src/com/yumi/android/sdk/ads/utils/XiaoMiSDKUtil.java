package com.yumi.android.sdk.ads.utils;

import java.lang.reflect.Field;

import com.mi.adtracker.MiAdTracker;
import com.yumi.android.sdk.ads.constants.YumiConstants;

import android.app.Activity;

public class XiaoMiSDKUtil
{
    private static final boolean onoff = true;
    private static final String TAG = "XiaoMiSDKUtil";

    public static void init(Activity activity)
    {
        if ("10001".equals(YumiConstants.PARTNER_ID))
        {
            try
            {
                Field field_sInitialized = MiAdTracker.class.getDeclaredField("sInitialized");
                field_sInitialized.setAccessible(true);
                boolean sInitialized = field_sInitialized.getBoolean(null);

                Field field_sContext = MiAdTracker.class.getDeclaredField("sContext");
                field_sContext.setAccessible(true);
                Object sContext = field_sContext.get(null);

                if (sInitialized && sContext != null)
                {
                    ZplayDebug.v(TAG, "XiaoMiSDKUtil is already init", onoff);
                    return;
                } else
                {
                    MiAdTracker.trackInit(activity, YumiConstants.AD_PLATFORM_ID);
                    ZplayDebug.v(TAG, "XiaoMiSDKUtil init", onoff);
                }
            } catch (NoSuchFieldException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            } catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void trackRequest()
    {
        if ("10001".equals(YumiConstants.PARTNER_ID))
        {
            try
            {
                MiAdTracker.trackRequestAd();
                ZplayDebug.v(TAG, "XiaoMiSDKUtil trackRequestAd", onoff);
            } catch (Exception e)
            {
            }
        }
    }

    public static void trackFetched()
    {
        if ("10001".equals(YumiConstants.PARTNER_ID))
        {
            try
            {
                MiAdTracker.trackFetchedAd();
                ZplayDebug.v(TAG, "XiaoMiSDKUtil trackFetchedAd", onoff);
            } catch (Exception e)
            {
            }
        }
    }

    public static void trackShow()
    {
        if ("10001".equals(YumiConstants.PARTNER_ID))
        {
            try
            {
                MiAdTracker.trackShowAd();
                ZplayDebug.v(TAG, "XiaoMiSDKUtil trackShowAd", onoff);
            } catch (Exception e)
            {
            }
        }
    }

}
