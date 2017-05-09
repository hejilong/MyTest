package com.yumi.android.sdk.ads.utils.device;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

public final class WindowSizeUtils {
	
	public static final boolean isPortrait(Context context){
		int[] displayMetrics = PhoneInfoGetter.getDisplayMetrics(context);
		if (displayMetrics[0] <= displayMetrics[1]) {
			return true;
		}
		return false;
	}
	
	public static final int px2dip(Context context, int px){
		float scale = context.getResources().getDisplayMetrics().density;
		return ((int)(px / scale + 0.5f));
	}

	public static final int dip2px(Context context, int dp){
		float scale = context.getResources().getDisplayMetrics().density;
		return ((int)(dp * scale +0.5f));
	}
	
	@SuppressWarnings("deprecation")
	public static final int[] getRealSize(Activity activity){
		//TODO 增加系统版本判断
		if (PhoneInfoGetter.getAndroidSDK() >= 17) {
			Point point = new Point();
			activity.getWindowManager().getDefaultDisplay().getRealSize(point);
			int[] realSize = new int[]{point.x, point.y};
			return realSize;
		}else{
			Display display = activity.getWindowManager().getDefaultDisplay();
			int[] realSize = new int[]{display.getWidth(), display.getHeight()};
			return realSize;
		}
		
	} 
	
	public static final boolean isTablet(Activity activity){
		if (PhoneInfoGetter.getAndroidSDK() >= 17) {
			Point point = new Point();
			activity.getWindowManager().getDefaultDisplay().getRealSize(point);
			float density = activity.getResources().getDisplayMetrics().density;
			double inch = Math.sqrt(Math.pow(point.x, 2)+Math.pow(point.y, 2))/(160*density);
			if (inch >= 8.0d) {
				return true;
			}
			return false;
		}
		return isApproximateTablet(activity.getApplicationContext());
	}
	
	public static final boolean isApproximateTablet(Context context){
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		float density = displayMetrics.density;
		double inch = Math.sqrt(Math.pow(width, 2)+Math.pow(height, 2))/(160*density);
		if (inch >= 8.0d) {
			return true;
		}
		return false;
	}
	
	
}
