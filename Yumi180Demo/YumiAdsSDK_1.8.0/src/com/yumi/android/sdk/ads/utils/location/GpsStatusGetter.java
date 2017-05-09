package com.yumi.android.sdk.ads.utils.location;

import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;

import android.Manifest.permission;
import android.content.Context;
import android.location.LocationManager;

public final class GpsStatusGetter {

	public static final boolean isGpsEnable(Context context){
		return isGpsEnableByProvider(context, LocationManager.GPS_PROVIDER) || isGpsEnableByProvider(context, LocationManager.NETWORK_PROVIDER);
	}
	
	public static final boolean isGpsEnableByProvider(Context context, String provider){
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean isEnable = false;
		if (manager != null) {
			try {
				isEnable = manager.isProviderEnabled(provider);
			} catch (SecurityException e) {
				isEnable = false;
			}
		}
		return isEnable;
	}
	
	public static GpsPermissionLevel getManifestGpsPermissionLevel(Context context){
		if (PackageInfoGetter.hasReqeuestPermission(context, permission.ACCESS_FINE_LOCATION)) {
			return GpsPermissionLevel.LEVEL_FINE;
		}
		if (PackageInfoGetter.hasReqeuestPermission(context, permission.ACCESS_COARSE_LOCATION)) {
			return GpsPermissionLevel.LEVEL_COARSE;
		}
		return GpsPermissionLevel.LEVEL_OFF;
	}
}
