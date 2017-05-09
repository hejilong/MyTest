package com.yumi.android.sdk.ads.observer;

import java.util.ArrayList;

import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public final class DownloadWatched {
	
	private static final boolean onoff = true;

	private static final String TAG = "DownloadWather";
	private final ArrayList<DownloadObserver> observers ;
	
	public DownloadWatched(){
		this.observers = new ArrayList<DownloadObserver>();
	}
	
	public final void registerObserver(final DownloadObserver observer){
		if (!observers.contains(observer)) {
			ZplayDebug.v(TAG, "download watcher added", onoff);
			observers.add(observer);
		}
	}
	
	public final void unregisterObserver(final DownloadObserver observer){
		if (!NullCheckUtils.isNotEmptyCollection(observers)) {
			if (observers.contains(observer)) {
				ZplayDebug.v(TAG, "download watcher remove the observer", onoff);
				observers.remove(observer);
			}
		}
	}
	
	
	public void notifyDownload(){
		if (NullCheckUtils.isNotEmptyCollection(observers)) {
			for (DownloadObserver downloadObserver : observers) {
				downloadObserver.onDownload();
			}
		}
	}
	
	public void notifyDownloadComplete(String path){
		if (NullCheckUtils.isNotEmptyCollection(observers)) {
			for (DownloadObserver downloadObserver : observers) {
				downloadObserver.onDownloadComplete(path);
			}
		}
	}
	
	
	public void onDestroy(){
		if (NullCheckUtils.isNotEmptyCollection(observers)) {
			ZplayDebug.v(TAG, "download watcher clear", onoff);
			observers.clear();
		}
	}
}
