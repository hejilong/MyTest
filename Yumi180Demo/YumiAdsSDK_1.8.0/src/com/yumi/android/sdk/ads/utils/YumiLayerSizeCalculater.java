package com.yumi.android.sdk.ads.utils;

import android.app.Activity;
import android.content.Context;

import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

public final class YumiLayerSizeCalculater {
	
	private static final boolean onoff = false;
	private static final String TAG = "YumiAdsLayerSizeCalculater";
	
    public static final int[] calculateLayerSize(Activity activity, AdSize standardSize,final boolean isMatchWindowWidth) {
        int standardWidthPix = 0;
        int standardHeightPix = 0;
        AdSize mStandardSize=standardSize;
        switch (mStandardSize) {
        case BANNER_SIZE_320X50:
            standardWidthPix = WindowSizeUtils.dip2px(activity, 320);
            standardHeightPix = WindowSizeUtils.dip2px(activity, 50);
            break;
        case BANNER_SIZE_728X90:
            standardWidthPix = WindowSizeUtils.dip2px(activity, 728);
            standardHeightPix = WindowSizeUtils.dip2px(activity, 90);
            break;
        case BANNER_SIZE_AUTO:
            if (WindowSizeUtils.isTablet(activity)) {
                mStandardSize=AdSize.BANNER_SIZE_728X90;
                standardWidthPix = WindowSizeUtils.dip2px(activity, 728);
                standardHeightPix = WindowSizeUtils.dip2px(activity, 90);
            } else {
                mStandardSize=AdSize.BANNER_SIZE_320X50;
                standardWidthPix = WindowSizeUtils.dip2px(activity, 320);
                standardHeightPix = WindowSizeUtils.dip2px(activity, 50);
            }
            break;
        default:
            calculateLayerSize(activity, AdSize.BANNER_SIZE_AUTO,isMatchWindowWidth);
        }
        if (WindowSizeUtils.isPortrait(activity)) {
            if (isMatchWindowWidth) {
                return calculateMatchWindowWidthLayerSize(activity, mStandardSize);
            } else {
                return calculatePortraitSize(activity, standardWidthPix, standardHeightPix);
            }
        } else {
            return calculateLandspaceSize(activity, standardWidthPix, standardHeightPix);
        }
    }
    
    /**
     * 根据比例计算占满全屏宽度banner的尺寸
     * @param context
     * @param standardSize
     * @return
     */
    public static final int[] calculateMatchWindowWidthLayerSize(Context context, AdSize standardSize) {
        int[] displayMetrics = PhoneInfoGetter.getDisplayMetrics(context);
        int displayWidth = displayMetrics[0];
        int displayHeight = displayMetrics[1];
        int actualWidth = 0;
        int actualHeight = 0;
        switch (standardSize) {
        case BANNER_SIZE_728X90:
            actualWidth = displayWidth;
            actualHeight = (int) ((displayWidth * 1.00f) / 8.00f); //根据比例算高度
            break;
        case BANNER_SIZE_320X50:
        default:
            actualWidth = displayWidth;
            actualHeight = (int) ((displayWidth * 1.00f) / 6.40f);//根据比例算高度
        }
//        actualHeight =  WindowSizeUtils.dip2px(context, 50); //固定banenr高度为50dp
        return new int[]{actualWidth, actualHeight};
    }

	public static final int[] calculateLayerSize(Context context, int widthDip,
			int heightDip) {
		if (WindowSizeUtils.isPortrait(context)) {
			return calculatePortraitSize(context,
					WindowSizeUtils.dip2px(context, widthDip),
					WindowSizeUtils.dip2px(context, heightDip));
		} else {
			return calculateLandspaceSize(context,
					WindowSizeUtils.dip2px(context, widthDip),
					WindowSizeUtils.dip2px(context, heightDip));
		}
	}

	private static int[] calculateLandspaceSize(Context context,
			int standardWidthPix, int standardHeightPix) {
		int[] displayMetrics = PhoneInfoGetter.getDisplayMetrics(context);
		int displayWidth = displayMetrics[0];
		int displayHeight = displayMetrics[1];
		int actualWidth = standardWidthPix;
		int actualHeight = standardHeightPix;
		if (standardHeightPix >= displayHeight) {
			float heightScale = (displayHeight * 1.00f)/(standardHeightPix * 1.00f);
			float widthScale = (displayWidth * 1.00f)/(standardWidthPix * 1.00f);
			float scale = heightScale < widthScale ? heightScale : widthScale;  
			actualWidth = (int) (standardWidthPix * scale);
			actualHeight = (int)(standardHeightPix * scale);
		}
		ZplayDebug.v(TAG, "land space standard " + standardWidthPix + " / " + standardHeightPix + "    actual " + actualWidth + " / " +  actualHeight , onoff);
		return new int[]{actualWidth, actualHeight};
	}

	private static int[] calculatePortraitSize(Context context,
			int standardWidthPix, int standardHeightPix) {
		int[] metrics = PhoneInfoGetter.getDisplayMetrics(context);		
		int displayWidth = metrics[0];
		int displayHeight = metrics[1];
		int actualWidth = standardWidthPix;
		int actualHeight = standardHeightPix;
		if (standardWidthPix >= displayWidth) {
			float widthScale = (displayWidth*1.00f)/(standardWidthPix*1.00f);
			float heightScale = (displayHeight * 1.00f)/(standardHeightPix * 1.00f);
			float scale = heightScale < widthScale ? heightScale : widthScale;
			actualWidth = (int) (standardWidthPix * scale);
			actualHeight = (int) (standardHeightPix * scale);
		}
		ZplayDebug.v(TAG, "portrait standard " + standardWidthPix + " / " + standardHeightPix + "    actual " + actualWidth + " / " +  actualHeight , onoff);
		
		
        ZplayDebug.e(TAG, "calculatePortraitSize actualWidth:"+actualWidth +"  actualHeight:"+actualHeight, onoff);
		return new int[]{actualWidth, actualHeight};
	}
}
