package com.yumi.android.sdk.ads.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class DeepLinkUtils {
	public static boolean isDeepLink(final String url) {
		return !isHttpUrl(url);
	}

	public static boolean deviceCanHandleIntent(final Context context,
			final Intent intent) {
		try {
			final PackageManager packageManager = context.getPackageManager();
			final List<ResolveInfo> activities = packageManager
					.queryIntentActivities(intent, 0);
			return !activities.isEmpty();
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static boolean isHttpUrl(final String url) {
		if (url == null) {
			return false;
		}
		if (url.startsWith("http:") || url.startsWith("https:")) {
			return true;
		}
		return false;
	}

}
