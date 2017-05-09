package com.yumi.android.sdk.ads.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.receiver.DownloadReceiver;
import com.yumi.android.sdk.ads.receiver.NetworkReceiver;

public final class YumiReceiverUtils {

	public static final void registerDownloadReceiver(Context context, DownloadReceiver receiver) {
		IntentFilter filter = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		filter.addAction(YumiConstants.ACTION_DOWNLOAD_BEGIN);
		context.getApplicationContext().registerReceiver(receiver, filter);
	}

	public final static void registerNetworkReceiver(Context context, NetworkReceiver receiver) {
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		filter.setPriority(1000);
		context.getApplicationContext().getApplicationContext().registerReceiver(receiver, filter);
	}
	
	public final static void unregisterReceiver(Context context , BroadcastReceiver receiver){
		if (receiver != null && context!=null) {
			try
			{
				context.getApplicationContext().unregisterReceiver(receiver);
			} catch (Exception e)
			{
			}
		}
	}
}
