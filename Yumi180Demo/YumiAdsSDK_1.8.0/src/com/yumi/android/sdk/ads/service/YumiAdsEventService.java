package com.yumi.android.sdk.ads.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.yumi.android.sdk.ads.beans.YumiResultBean;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.request.ConfigInfoRequest;
import com.yumi.android.sdk.ads.request.ConfigInfoRequest.ConfigRequestCallback;
import com.yumi.android.sdk.ads.request.EventRequest;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.SharedpreferenceUtils;
import com.yumi.android.sdk.ads.utils.YumiGooglePlayServiceCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiManifestReaderUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.assets.YumiAssetsReader;
import com.yumi.android.sdk.ads.utils.json.JsonResolveUtils;
import com.yumi.android.sdk.ads.utils.location.LocationHandler;

public final class YumiAdsEventService extends Service{

	private static final boolean onoff = true;
	private static final String TAG = "AdsEventReportService";
	private ServiceBinder mBinder = new ServiceBinder();
	private Map<String, ConfigInfoRequest> requests = null;
	private ArrayList<String> regProviders = null;
	private LocationHandler loc;
	private EventRequest reporter;
	
	@Override
	public final void onCreate() {
		ZplayDebug.v(TAG, "event service created", onoff);
		loc = LocationHandler.getLocHandler();
//		loc.updateGpsLocation(getApplicationContext());
		reporter = new EventRequest(getApplicationContext());
		YumiGooglePlayServiceCheckUtils.getGooglePlayID(getApplicationContext());
		YumiGooglePlayServiceCheckUtils.updateGooglePlayServiceADT(getApplicationContext());
		if (regProviders == null) {
			regProviders = YumiManifestReaderUtils.getTheProviderRegistedInManifest(getApplicationContext());
		}
		super.onCreate();
	}
	
	
	@Override
	public final IBinder onBind(Intent intent) {
		ZplayDebug.d(TAG, "event service onbind", onoff);
		return mBinder;
	}


	@Override
	public final int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			reportEvent(intent);
		}
		return START_NOT_STICKY;
	}


	private void reportEvent(Intent intent) {
		String action = intent.getAction();
		if (action.equals(YumiConstants.ACTION_REPORT)) {
			Bundle extras = intent.getBundleExtra(YumiConstants.BUNDLE_KEY);
			if (extras != null && reporter != null) {
				reporter.reportEvent(extras);
			}
		}
	}

	
	
	@Override
	public final boolean onUnbind(Intent intent) {
		if (intent != null) {
			String unbind = intent.getStringExtra(YumiConstants.BUNDLE_KEY_UNBIND);
			ZplayDebug.w(TAG, "event service unbind " + intent.toString() + " extra is " + unbind, onoff);
			if (NullCheckUtils.isNotNull(unbind)) {
				if (NullCheckUtils.isNotEmptyMap(requests) && requests.containsKey(unbind)) {
					ConfigInfoRequest configInfoRequest = requests.get(unbind);
					if (configInfoRequest != null) {
						configInfoRequest.release();
					}
				}
			}
		}
		return super.onUnbind(intent);
	}
	
	@Override
	public final void onRebind(Intent intent) {
		ZplayDebug.w(TAG, "event service rebind " + intent.toString(), onoff);
		super.onRebind(intent);
	}
	
	@Override
	public final void onDestroy() {
		if (loc != null) {
			loc.releaseHandler();
		}
		YumiManifestReaderUtils.release();
		super.onDestroy();
	}
	
	public final void requestConfig(String yumiID, String channelID, String versionName, LayerType type, String spkey, ConfigRequestCallback callback){
		ConfigInfoRequest configInfoRequest = new ConfigInfoRequest(getApplicationContext(), yumiID, channelID, versionName, type, spkey, callback);
		putConfigRequester(type, configInfoRequest);
		configInfoRequest.requestConfig();
	}
	
	public final YumiResultBean getResultBeanByLocalConfig(String spKey, String assetsFile) {
			String localConfig = SharedpreferenceUtils.getString(getApplicationContext(), YumiConstants.SP_FILENAME, spKey, null);
			if (!NullCheckUtils.isNotNull(localConfig)) {
				localConfig = YumiAssetsReader.getFromAssets(getApplicationContext(), assetsFile);
				if (NullCheckUtils.isNotNull(localConfig)) {
					try {
						YumiResultBean localResult = JsonResolveUtils.resolveJson2JaveBean(new JSONObject(localConfig), YumiResultBean.class);
						if (localResult != null) {
							return localResult;
						}
					} catch (Exception e) {
						ZplayDebug.e(TAG, "", e, onoff);
					}
				}
			}
			return null;
	}
	
	
	private void putConfigRequester(LayerType type, ConfigInfoRequest configInfoRequest) {
		if (requests == null) {
			requests = new HashMap<String, ConfigInfoRequest>();
		}
		requests.put(type.getType(), configInfoRequest);
	}

	public final class ServiceBinder extends Binder {
        public YumiAdsEventService getService() {
            return YumiAdsEventService.this;
        }
    }
}



