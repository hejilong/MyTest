    package com.yumi.android.sdk.ads.layer;

import java.util.TimeZone;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.listener.IYumiMediaInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.publish.enumbean.MediaStatus;
import com.yumi.android.sdk.ads.utils.SharedpreferenceUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;

import android.R.bool;
import android.app.Activity;

public abstract class YumiBaseMediaLayer extends YumiBaseLayer {

	private static final boolean onoff = true;
	private static final String TAG = "YumiBaseMediaLayer";
	private boolean callbackOnIncentived = false;
	private boolean callbackOnCloseMedia = false;

    private String providerId;
	
	protected YumiBaseMediaLayer(Activity activity, YumiProviderBean provider) {
		super(activity, provider);
		providerId=provider.getProviderID();
	}
	
	public String getProviderId() {
        return providerId;
    }

    public final void prepareMedia(String rid) {
		setRID(rid);
		setYumiInnerListener();
		callbackOnIncentived = false;
		callbackOnCloseMedia = false;
		int retryLimit = invariantProvider.getGlobal().getRetryLimit();
//		if (retryLimit == 0 || failedTimes < retryLimit) {
			if (!isReachMaxReward()) {
				if (NetworkStatusHandler.isNetWorkAvaliable(mContext)) {
					onPrepareMedia();
				}else {
					layerPreparedFailed(LayerErrorCode.ERROR_INVALID_NETWORK);
				}
			}else {
				layerPreparedFailed(LayerErrorCode.ERROR_OVER_INCENTIVED_LIMIT);
			}
//		} else {
//			layerPreparedFailed(LayerErrorCode.ERROR_OVER_RETRY_LIMIT);
//		}
	}
	
	public final void downloadMedia(){
		synchronized (mInnerListener) {
			if (!isOutTime)
			{
				if (needCallbackInnerListener) {
					((IYumiMediaInnerLayerStatusListener)mInnerListener).onLayoutDownload();
				}
			}
		}
	}
	
	/**
	 * <p> Override this method to prepared next incentive media ad.
	 *	<p>This method will invoke when your close the current, or all the incentive media prepared failed. 
	 */
	protected abstract void onPrepareMedia();

	public final MediaStatus showMedia() {
		if (isReachMaxReward()) {
			return MediaStatus.REACH_MAX_REWARD;
		}
		if (isMediaReady()) {
	        callbackOnIncentived = false;
	        callbackOnCloseMedia = false;
			onShowMedia();
			return MediaStatus.ON_SHOW;
		}
		return MediaStatus.NOT_PREPARED;
	}
	
	//TODO
	private boolean isReachMaxReward() {
		return getRemainIncentiveTimes() <= 0;
	}

	private int getRemainIncentiveTimes(){
		int incentived = invariantProvider.getGlobal().getIncentived();
		int times = SharedpreferenceUtils.getInt(mContext,
				YumiConstants.SP_FILENAME,
				YumiConstants.SP_KEY_INCENTIVED_REMAIN_TIMES, incentived);
		ZplayDebug.v(TAG, "media remain rewards " + times, onoff);
		return times;
	}
	
	/**
	 * <p> When the {@link #isMediaReady()} return true, this method will invoke.
	 * 
	 * @see #isMediaReady()
	 */
	protected abstract void onShowMedia();

	/**
	 * <p>You must Override this method to tell YumiSDK if the incentive media is ready to play. 
	 * @return If incentive media ready to play return true else return false
	 */
	protected abstract boolean isMediaReady();

	/**
	 * <p> Notify YumiSDK the incentive media incentived
	 */
	protected final void layerIncentived() {
        ZplayDebug.v(TAG, "media YumiBaseMediaLayer layerIncentived ", onoff);
		callbackOnIncentived = true;
		delayCallbackIncentivedOnCloseMedia();
	}

	
	/**
	 * <p> Notify YumiSDK the ad clicked.
	 */
	protected final void layerClicked() {
        ZplayDebug.v(TAG, "media YumiBaseMediaLayer layerClicked ", onoff);
		super.layerClicked(LayerType.TYPE_MEDIA, -99f, 99f);
	}

	/**
	 * <p> Notify YumiSDK the ad exposure.
	 */
	protected final void layerExposure() {
        ZplayDebug.v(TAG, "media YumiBaseMediaLayer layerExposure ", onoff);
		super.layerExposure(LayerType.TYPE_MEDIA);
	}

	/**
	 * <p> Notify YumiSDK the ad closed.
	 */
	protected final void layerClosed() {
		super.layerClosed(LayerType.TYPE_MEDIA);
        ZplayDebug.v(TAG, "media YumiBaseMediaLayer layerClosed ", onoff);
		callbackOnCloseMedia = true;
		delayCallbackIncentivedOnCloseMedia();
	}

	/**
	 * <p> Notify YumiSDk the ad prepared
	 */
	protected final void layerPrepared() {
		super.layerPrepared(LayerType.TYPE_MEDIA, true);
        ZplayDebug.v(TAG, "layerPrepared isPrepareMedia true", onoff);
	}

	/**
	 * <p> Notify YumiSDK the ad prepared failed, you only need to consider these error. 
	 * <li> {@link LayerErrorCode#ERROR_INTERNAL}
	 * <li> {@link LayerErrorCode#ERROR_INVALID}
	 * <li> {@link LayerErrorCode#ERROR_NETWORK_ERROR}
	 * <li> {@link LayerErrorCode#ERROR_NO_FILL}
	 * <p>Accuratly use error type is helpful to statistic analysis.
	 * 
	 * @param error Ad request failed reason 
	 * 
	 * @see LayerErrorCode#ERROR_INTERNAL
	 * @see LayerErrorCode#ERROR_INVALID
	 * @see LayerErrorCode#ERROR_NETWORK_ERROR
	 * @see LayerErrorCode#ERROR_NO_FILL
	 */
	protected final void layerPreparedFailed(LayerErrorCode error) {
		super.layerPreparedFailed(LayerType.TYPE_MEDIA, error, true);
	}
	
	
    protected final void layerMediaStart() {
        layerStartReport(LayerType.TYPE_MEDIA);
    }

    protected final void layerMediaEnd() {
        layerEndReport(LayerType.TYPE_MEDIA);
    }

	private void delayCallbackIncentivedOnCloseMedia() {
		synchronized (mInnerListener) {
	        ZplayDebug.v(TAG, "media YumiBaseMediaLayer delayCallbackIncentivedOnCloseMedia  callbackOnCloseMedia="+callbackOnCloseMedia +"  || callbackOnIncentived="+callbackOnIncentived, onoff);
			if (callbackOnCloseMedia && callbackOnIncentived) {
				if (needCallbackInnerListener) {
					((IYumiMediaInnerLayerStatusListener)mInnerListener).onLayerIncentived(mProvider,LayerType.TYPE_MEDIA);
					addTimes();
					layerIncentivedReport();
                    callbackOnCloseMedia=false;
                    callbackOnIncentived=false;
				}
			}
		}
	}

	/**
	 * 奖励次数累加
	 */
	private final void addTimes() {
		long last_incentived_time = SharedpreferenceUtils.getLong(
				mContext, // 获取上次奖励时间
				YumiConstants.SP_FILENAME,
				YumiConstants.SP_KEY_INCENTIVED_LAST_TIMEMILLIS, 0);
		long currentTimeMillis = System.currentTimeMillis();
		boolean longTimeAgo = isLongTimeAgo(last_incentived_time,
				currentTimeMillis); // 判断距上次奖励是否过了1天或以上
		if (longTimeAgo) // 如果距上次奖励已经过了一天以上，则奖励回归到最大次数
		{
			SharedpreferenceUtils.saveInt(mContext,
					YumiConstants.SP_FILENAME,
					YumiConstants.SP_KEY_INCENTIVED_REMAIN_TIMES, mProvider.getGlobal().getIncentived() - 1);
		} else { // 如果距上次奖励不足一天，则在当前计次基础上-1
			int incentived_times = SharedpreferenceUtils.getInt(mContext,
					YumiConstants.SP_FILENAME,
					YumiConstants.SP_KEY_INCENTIVED_REMAIN_TIMES, mProvider.getGlobal().getIncentived());
			if (incentived_times > 0) {
				incentived_times --;
			}
			SharedpreferenceUtils.saveInt(mContext,
					YumiConstants.SP_FILENAME,
					YumiConstants.SP_KEY_INCENTIVED_REMAIN_TIMES, incentived_times);
		}
		SharedpreferenceUtils.saveLong(
				mContext, // 记录本次奖励时间
				YumiConstants.SP_FILENAME,
				YumiConstants.SP_KEY_INCENTIVED_LAST_TIMEMILLIS,
				currentTimeMillis);
	}

	@Override
	public final boolean onActivityBackPressed() {
		return false;
	}

	/**
	 * <p>If SDK cannot get any customer SDK callback , will invoke this method. 
	 * <p>The superclass has implement this method, and if non response , will tread prepared failed and callback {@link #layerPreparedFailed(LayerErrorCode)}
	 * <p>You can Override this method if your customer SDK has special logic. 
	 * If you Override this method ,make sure the {@link #layerPreparedFailed(LayerErrorCode)} will not be repeated invoke. 
	 */
	@Override
	protected void onRequestNonResponse() {
		layerPreparedFailed(LayerErrorCode.ERROR_NON_RESPONSE);
	}
	
	/**
	 * 对比两次时间是否过了一天
	 * @param last 上次记录时间
	 * @param current 当前时间
	 * @return 如过不是同一天，返回true
	 */
	private boolean isLongTimeAgo(long last, long current){
	    int raw = TimeZone.getDefault().getRawOffset();
	    long day_l = (last+raw)/86400000;
	    long day_c = (current+raw)/86400000;
	    return day_c>day_l;

	}

	/**
	 * <p>Invoke when this provider round finished.
	 */
	public final void onRoundFinished(){
		notInActivityRound();
	}

    public boolean isMediaPrepared() {
        layerRequestReport(LayerType.TYPE_MEDIA);
        if(isMediaReady())
        {
            layerResponseReport(LayerType.TYPE_MEDIA,LayerErrorCode.CODE_SUCCESS);
            return true;
        }else{
            layerResponseReport(LayerType.TYPE_MEDIA,LayerErrorCode.CODE_FAILED);
            return false;
        }
    }

    public boolean isMediaPreparedNoReport() {
        return isMediaReady();
    }
	
}
