package com.yumi.android.sdk.ads.receiver;


import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public final class NetworkReceiver extends BroadcastReceiver{

	private static final boolean onoff = true;
	private static final String TAG = "NetworkReceiver";
	private Handler handler;
	private String lastConnType  = "";
	public static final int HANDLER_NETWORK_CHANGE = 0x323;
	
	public NetworkReceiver(Handler handler, Context context){
		this.handler = handler;
		this.lastConnType = NetworkStatusHandler.getConnectedNetName(context);
	}	
	
	@Override
	public final void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
				String current = NetworkStatusHandler.getConnectedNetName(context);
				if (lastConnType.equalsIgnoreCase("unknown") && !current.equalsIgnoreCase("unknown")) {
					if (!handler.hasMessages(HANDLER_NETWORK_CHANGE)) {
						ZplayDebug.i(TAG, "on network  from unconn to conn", onoff);
						handler.sendEmptyMessageDelayed(HANDLER_NETWORK_CHANGE, 500);
					}
				}
				lastConnType = current;
			}
	}
	
}
