package com.yumi.android.sdk.ads.api.ym;

import com.yumi.android.sdk.ads.observer.DownloadWatched;
import com.yumi.android.sdk.ads.receiver.DownloadReceiver;
import com.yumi.android.sdk.ads.utils.YumiReceiverUtils;

import android.content.Context;

public final class YMRegister {

	private boolean hasRegister = false;
	private DownloadReceiver receiver;

	public final void registerDownloadReceiver(Context context,
			final DownloadWatched watched) {
		if (!hasRegister) {
			receiver = new DownloadReceiver(watched);
			YumiReceiverUtils.registerDownloadReceiver(
					context.getApplicationContext(), receiver);
			hasRegister = true;
		}
	}

	public final void unregisterReceiver(Context context) {
		if (hasRegister && receiver != null) {
			YumiReceiverUtils.unregisterReceiver(context, receiver);
			hasRegister = false;
		}
	}

}
