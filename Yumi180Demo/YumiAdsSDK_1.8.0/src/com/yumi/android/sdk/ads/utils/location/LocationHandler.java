package com.yumi.android.sdk.ads.utils.location;

import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public final class LocationHandler {

	private static final String TAG = "LocationHandler";
	private static final boolean onoff = true;
	private static final int HANDLER_CANCEL_LOC = 0x1;
	private static final long CANCEL_DELAYED = 10 * 1000;
	private static final long RECENTLY_DEFINE = 60 * 60 * 1000;
	private LocationManager locManager;
	private Location recentlyLoc = null;
	private Criteria criteria = new Criteria();

	private final Handler locHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == HANDLER_CANCEL_LOC) {
				if (locHandler.hasMessages(HANDLER_CANCEL_LOC)) {
					locHandler.removeMessages(HANDLER_CANCEL_LOC);
				}
				removeLocListener();
			}
		};
	};

	private final LocationListener locListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			ZplayDebug.d(TAG, "status change", onoff);
		}

		@Override
		public void onProviderEnabled(String provider) {
			ZplayDebug.d(TAG, "provider enabled", onoff);
		}

		@Override
		public void onProviderDisabled(String provider) {
			ZplayDebug.d(TAG, "provider disabled", onoff);
		}

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				ZplayDebug.d(TAG, "location " + location.getLatitude() + " / "
						+ location.getLongitude(), onoff);
				recentlyLoc = location;
				// 移除 取消事件
				if (locHandler != null
						&& locHandler.hasMessages(HANDLER_CANCEL_LOC)) {
					locHandler.removeMessages(HANDLER_CANCEL_LOC);
				}
				// 移除 locListener 省电
				removeLocListener();
			}
		}
	};

	private LocationHandler() {
	}

	public final void updateGpsLocation(final Context context) {
		// 判断gps权限等级
		if (GpsStatusGetter.isGpsEnable(context)) {
			switch (GpsStatusGetter.getManifestGpsPermissionLevel(context)) {
			case LEVEL_FINE:
				requestGpsLocation(context);
				break;
			case LEVEL_COARSE:
				requestNetworkLocation(context);
				break;
			default:
				break;
			}
		}

	}

	private boolean hasRecentlyGpsLocation() {
		if (recentlyLoc != null ) {
			if (Math.abs(recentlyLoc.getTime() - System.currentTimeMillis()) < RECENTLY_DEFINE) {
				return true;
			}
		}
		return false;
	}

	public final synchronized void releaseHandler() {
		recentlyLoc = null;
	}

	public final synchronized Location getLastKnownLocation(Context context) {
		try {
			if (GpsStatusGetter.isGpsEnable(context)) {
				locManager = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);
				String bestProvider = locManager.getBestProvider(criteria, true);
				if (recentlyLoc == null && NullCheckUtils.isNotNull(bestProvider)) {
					try {
						recentlyLoc = locManager.getLastKnownLocation(bestProvider);
					} catch (SecurityException e) {
						recentlyLoc = null;
					} catch (IllegalArgumentException e) {
						recentlyLoc = null;
					}
				}
				if (!hasRecentlyGpsLocation()) {
					updateGpsLocation(context);
					return null;
				}
				return recentlyLoc;
			}
		} catch (SecurityException e) {
		}
		return null;
	}

	private void requestNetworkLocation(Context context) {
		try
		{
			if (GpsStatusGetter.isGpsEnableByProvider(context, LocationManager.NETWORK_PROVIDER)
					&& locHandler != null && !locHandler.hasMessages(HANDLER_CANCEL_LOC)) {
				ZplayDebug.i(TAG, "request network location", onoff);
				locManager = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						1L, 0.1f, locListener);
				locHandler.sendEmptyMessageDelayed(HANDLER_CANCEL_LOC,
						CANCEL_DELAYED);
			}
		} catch (Exception e)
		{
			ZplayDebug.e(TAG, "requestNetworkLocation error", e, onoff);
		}
	}

	private void requestGpsLocation(Context context) {
		try
		{
			if (GpsStatusGetter.isGpsEnableByProvider(context, LocationManager.GPS_PROVIDER)
					&& locHandler != null && !locHandler.hasMessages(HANDLER_CANCEL_LOC)) {
				ZplayDebug.i(TAG, "request gps location", onoff);
				locManager = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1L,
						0.1f, locListener);
				locHandler.sendEmptyMessageDelayed(HANDLER_CANCEL_LOC,
						CANCEL_DELAYED);
			}
		} catch (Exception e)
		{
			ZplayDebug.e(TAG, "requestGpsLocation error", e, onoff);
		} 
	}

	private void removeLocListener() {
		if (locManager != null) {
			ZplayDebug.i(TAG, "remove loc listener", onoff);
			locManager.removeUpdates(locListener);
		}
	}

	private static class LocationHandlerHolder {
		private static LocationHandler handler = new LocationHandler();
	}

	public static LocationHandler getLocHandler() {
		return LocationHandlerHolder.handler;
	}

}
