package com.yumi.android.sdk.ads.ensure;

import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public final class ZplayAdExtra {

	private static final boolean onoff = true;
	
	private static final String TAG = "ZplaySelfExtra";
	private boolean hasInit = false;
	private boolean hasMediaInit = false;

	private ZplayAdExtra(){
		
	}
	
	private static class ZplaySelfExtraHolder{
		private static final ZplayAdExtra instance = new ZplayAdExtra();
	}
	
	public static final ZplayAdExtra getHolder(){
		return ZplaySelfExtraHolder.instance;
	}
	
	public final synchronized void initSelfSDK(Activity activity, String appKey, String appChannel)
	{
		if (!hasInit)
		{
			ZplayDebug.d(TAG, "zplayad init sdk", onoff);
			 com.yumi.android.sdk.ads.self.constants.Constants.setIsGooglePlayVersion(YumiConstants.IS_GOOGLEPLAY_VERSION);
//			ZplayAD.initAD(activity, appKey, new InitCallBack()
//			{
//
//				@Override
//				public void onCallBack(boolean arg0)
//				{
//					ZplayDebug.d(TAG, "zplay ad init " + arg0, onoff);
//				}
//			});
			hasInit = true;
		}
	}

	public final synchronized void initSelfMediaSDK(Activity activity, String appKey, String appChannel)
	{
		if (!hasMediaInit)
		{
			ZplayDebug.d(TAG, "zplayad init media sdk", onoff);
			com.yumi.android.sdk.ads.selfmedia.constants.Constants.setIsGooglePlayVersion(YumiConstants.IS_GOOGLEPLAY_VERSION);
//			com.zplay.android.sdk.zplayad.media.ZplayAD.initMedia(activity, appKey, 
//					new com.zplay.android.sdk.zplayad.media.interf.InitCallBack()
//			{
//
//				@Override
//				public void onCallBack(boolean arg0)
//				{
//					ZplayDebug.d(TAG, "zplay media ad init " + arg0, onoff);
//				}
//			});
			hasMediaInit = true;
		}
	}
	
}
