package com.yumi.android.sdk.ads.utils;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.content.Context;

public final class YumiGooglePlayServiceCheckUtils {

	private static final boolean onoff = true;
	private static final String TAG = "YumiGooglePlayServiceCheckUtils";
	private static String googleID = "";
	private static boolean adt = false;

	public final static String getGooglePlayID(final Context context) {
		if (!NullCheckUtils.isNotNull(googleID)
				&& isGooglePlayIsAvailable(context)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						googleID = AdvertisingIdClient.getAdvertisingIdInfo(
								context).getId();
					}catch(Exception e)
					{
						ZplayDebug.e(TAG, "", e, onoff);
					    e.printStackTrace();
					}
				}
			}).start();
		}
		return googleID;
	}

	public final static boolean getGooglePlayServiceADT(){
		return adt;
	}
	
	public final static void updateGooglePlayServiceADT(final Context context){
		if (isGooglePlayIsAvailable(context)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						adt = AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled();
					} catch(Exception e)
                    {
						ZplayDebug.e(TAG, "", e, onoff);
                    }
				}
			}).start();
		}
	}
	
	
	public static final boolean isGooglePlayIsAvailable(Context context) {
		try {
			int available = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(context);
			if (available == ConnectionResult.SUCCESS) {
				return true;
			}
		} catch (Exception e) {
			ZplayDebug.e(TAG, "", e, onoff);
		} catch (Error e) {
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return false;
	}
}
