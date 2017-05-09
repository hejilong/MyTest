package com.yumi.android.sdk.ads.publish;

import android.app.Activity;
import android.content.Intent;

import com.yumi.android.sdk.ads.mediation.activity.MediationTestActivity;

public class YumiDebugging {

    /**
     * Open the debug mode Activity
     * @param yumiID  
     * @param activity
     */
    public static void startDebugging(Activity activity,String yumiID)
    {
        Intent intent=new Intent(activity, MediationTestActivity.class);
        intent.putExtra("yumiId", yumiID);
        activity.startActivity(intent);
    }
    
    
    /**
     * Open the debug mode Activity
     * @param yumiID  
     * @param activity
     */
    public static void startDebugging(Activity activity,String yumiID,String channelID,String versionName)
    {
        Intent intent=new Intent(activity, MediationTestActivity.class);
        intent.putExtra("yumiId", yumiID);
        intent.putExtra("channelID", channelID);
        intent.putExtra("versionName", versionName);
        activity.startActivity(intent);
    }
}
