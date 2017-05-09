package com.yumi.android.sdk.ads.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.yumi.android.sdk.ads.beans.AdListBean;
import com.yumi.android.sdk.ads.beans.YumiGlobalBean;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.beans.YumiResultBean;
import com.yumi.android.sdk.ads.constants.YumiAPIList;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.factory.YumiInstanceFactory;
import com.yumi.android.sdk.ads.factory.YumiMediaAdapterFactory;
import com.yumi.android.sdk.ads.layer.YumiBaseMediaLayer;
import com.yumi.android.sdk.ads.listener.IYumiActivityLifecycleListener;
import com.yumi.android.sdk.ads.listener.IYumiMediaInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.publish.enumbean.MediaStatus;
import com.yumi.android.sdk.ads.publish.listener.IYumiMediaListener;
import com.yumi.android.sdk.ads.receiver.NetworkReceiver;
import com.yumi.android.sdk.ads.request.ConfigInfoRequest.ConfigRequestCallback;
import com.yumi.android.sdk.ads.service.YumiAdsEventService;
import com.yumi.android.sdk.ads.utils.CheckSelfPermissionUtils;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.SharedpreferenceUtils;
import com.yumi.android.sdk.ads.utils.YumiIntentSender;
import com.yumi.android.sdk.ads.utils.YumiManifestReaderUtils;
import com.yumi.android.sdk.ads.utils.YumiReceiverUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.encrypt.YumiSignUtils;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;

/**
 * <p>
 * Create a new {@link YumiMediaControl} with YumiID. You can integrate
 * incentive media ad into you project.
 * <p>
 * You can invoke {@link #setVersionName(String)} or invoke
 * {@link #setChannelID(String)}} to distinguish different application version ,
 * channel or both of them.
 * <p>
 * If you require the status of the media request and result. You need set the
 * {@link IYumiMediaListener} by invoke
 * {@link #setMediaEventListner(IYumiMediaListener)}.
 * 
 * @author Mikoto
 *
 */
public final class YumiMediaControl extends Control {

    private static final String TAG = "YumiMedia";
    private static final int NEXT_ROUND_MEDIA = 0x005;
    private static final int INIT_MEDIA_ADAPTER = 0x007;

    private String versionName = "";
    private String channelID = "";
    private final IYumiMediaInnerLayerStatusListener innerListener;
    private IYumiMediaListener mediaListener;
    private Frequency mediaFrequency;
    private Set<IYumiActivityLifecycleListener> lifecycle;
    private YumiAdsEventService homeService;
    private ServiceConnection mConn;
    private NetworkReceiver nr;
    private String mediaRid = "";
    private List<YumiProviderBean> providers;
    private int incentived = 0;

    private YumiGlobalBean handleGlobal;
    
    //开发者询问后得到的准备好的平台ID
    private String PreparedProviderId;
    
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case NetworkReceiver.HANDLER_NETWORK_CHANGE:
                if (getConfigResult() == null || mediaFrequency == null) {
                    getLocalConfigAndRequestAsynchronous();
                }
                break;
            case NEXT_ROUND_MEDIA:
                mediaFrequency.toNextRound();
                updateMediaRid();
                break;
            case INIT_MEDIA_ADAPTER:
                YumiProviderBean provider=(YumiProviderBean)msg.obj;
                initMediaAdapter(provider);
                break;
            default:
                break;
            }
        };
    };

    /**
     * <p>
     * Create the {@link YumiMediaControl} instance with activity and YumiId to
     * integrate incentive media ad on your application.
     * 
     * @param activity
     *            The activity you want to exposure the banner
     * @param yumiID
     *            The prime Id to use Yumi SDK. you can get the ID from Yumi
     *            platform. The yumiID can not be null or empty string;
     * 
     */
    public YumiMediaControl(Activity activity, String yumiID) {
        super(activity, yumiID, true);
        CheckSelfPermissionUtils.CheckSelfPermissionYumi(activity);
        this.innerListener = new IYumiMediaInnerLayerStatusListener() {

            @Override
            public void onLayerPreparedFailed(YumiProviderBean provider, LayerType type, LayerErrorCode error) {

            }

            @Override
            public void onLayerPrepared(YumiProviderBean provider, LayerType type) {
                if (mediaListener != null) {
                }
            }

            @Override
            public void onLayerIncentived(YumiProviderBean provider, LayerType type) {
                if (mediaListener != null) {
                    mediaListener.onMediaIncentived();
                }

            }

            @Override
            public void onLayerExposure(YumiProviderBean provider, LayerType type) {
                if (mediaListener != null) {
                    mediaListener.onMediaExposure();
                }
            }

            @Override
            public void onLayerClosed(YumiProviderBean provider, LayerType type) {
                if (mediaListener != null) {
                    mediaListener.onMediaClosed();
                }
                mHandler.sendEmptyMessage(NEXT_ROUND_MEDIA);
            }

            @Override
            public void onLayerCLicked(YumiProviderBean provider, LayerType type, float x, float y) {
                if (mediaListener != null) {
                    mediaListener.onMediaClicked();
                }
            }

            @Override
            public void onLayerCanGetReward(YumiProviderBean provider, LayerType type, int remain) {
            }

            @Override
            public void onLayoutDownload() {
            }
        };
        registerNetworkReceiver();
    }

    private void updateMediaRid() {
        mediaRid = YumiSignUtils.getTrackerID(mContext, "r");
        YumiMediaAdapterFactory.getFactory().UpdateRid(mediaRid);
        ZplayDebug.d(TAG, "media update tracker rid " + mediaRid, onoff);
    }

    private void registerNetworkReceiver() {
        nr = new NetworkReceiver(mHandler, mContext.getApplicationContext());
        YumiReceiverUtils.registerNetworkReceiver(mContext, nr);
    }

    private void addLifeycle(YumiBaseMediaLayer cycle) {
        if (lifecycle == null) {
            lifecycle = new HashSet<IYumiActivityLifecycleListener>();
        }
        lifecycle.add(cycle);
    }

    private void requestMediaConfigFromServer() {
        ZplayDebug.i(TAG, "media request service YumiID " + yumiID + " channelID " + channelID + " versionName " + versionName, onoff);
        if (NetworkStatusHandler.isNetWorkAvaliable(mContext)) {
            if(homeService == null)
            {
                return;
            }
            homeService.requestConfig(yumiID, channelID, versionName, LayerType.TYPE_MEDIA, YumiConstants.SP_KEY_LAST_MEDIA_CONFIG, new ConfigRequestCallback() {

                @Override
                public void onReqeustDone(YumiResultBean result) {
                    if (result != null) {
                        if (result.getResult() == 0) {
                        	String logUrl = result.getLogUrl();
                        	if (logUrl!=null && !"".equals(logUrl) && !"null".equals(logUrl)) {
                        		YumiAPIList.setEVENT_REPORT_HOST_RESET(logUrl);
                        	}
                            incentived = result.getIncentived();
                            setConfigResult(result);
                            setInit();
                            updateMediaRid();
                            createMediaFrequency();
                            initAllMediaAdapter();
                        } else {
                            ZplayDebug.d(TAG, "get config failed by " + result.getResult(), onoff);
                        }
                    }
                }
            });
        } else {
            ZplayDebug.w(TAG, "Invalid network", onoff);
        }
    }

    private void createMediaFrequency() {
        if (getConfigResult() != null) {
            synchronized (mActivity) {
                if (providers != null) {
                    providers.clear();
                }
                providers = null;
                providers = getConfigResult().getProviders();
            }
            if (NullCheckUtils.isNotEmptyCollection(providers)) {
                mediaFrequency = new Frequency(getConfigResult().getProviders(), getConfigResult().getOptimization() == 1 ? true : false);
                ZplayDebug.v(TAG, "reflash new config , clear adapter obtain", onoff);
                YumiMediaAdapterFactory.getFactory().clearAdapterObtain();
                ZplayDebug.v(TAG, "reflash new config , cancel  handler ", onoff);
                cancelHandlerMessage(mHandler, NEXT_ROUND_MEDIA);
            }
        }
    }

    private void initAllMediaAdapter() {
        try {
            if (providers != null && providers.size() > 0) {
                handleGlobal = new YumiGlobalBean(getConfigResult(), yumiID, channelID, versionName);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            for (YumiProviderBean provider : providers) {
                                Message mesage = new Message();
                                mesage.what = INIT_MEDIA_ADAPTER;
                                mesage.obj = provider;
                                mHandler.sendMessage(mesage);
                                try {
                                    ZplayDebug.w(TAG, "initAllMediaAdapter media Thread.sleep(15000)", onoff);
                                    Thread.sleep(15000);
                                } catch (InterruptedException e) {
                                    ZplayDebug.e(TAG, "initAllMediaAdapter media Thread.sleep(15000) error", e, onoff);
                                }
                            }
                        } catch (Exception e) {
                            ZplayDebug.e(TAG, e.getMessage(), e, onoff);
                        }
                    }

                }).start();

            } else {
                ZplayDebug.w(TAG, "initAllMediaAdapter media ConfigResult providers is empty", onoff);
            }
        } catch (Exception e) {

        }
    }
    
    private void initMediaAdapter(YumiProviderBean provider){
        provider.setGlobal(handleGlobal);
        YumiBaseMediaLayer tempAdapter = YumiMediaAdapterFactory.getFactory().buildMediaAdapter(mActivity, provider, innerListener);
        if (tempAdapter != null) {
            tempAdapter.setControl(YumiMediaControl.this);
            addLifeycle(tempAdapter);
            tempAdapter.prepareMedia(mediaRid);
            ZplayDebug.w(TAG, "initAllMediaAdapter init adapter providerID:" + provider.getProviderID(), onoff);
        } else {
            ZplayDebug.w(TAG, "initAllMediaAdapter adapter is null , check reflect exception providerID:" + provider.getProviderID(), onoff);
            mediaFrequency.cutDownProvider(provider);
        }
    }

    public int getMediaRemainRewards() {
        int times = SharedpreferenceUtils.getInt(mContext, YumiConstants.SP_FILENAME, YumiConstants.SP_KEY_INCENTIVED_REMAIN_TIMES, incentived);
        ZplayDebug.v(TAG, "media remain rewards " + times, onoff);
        return times;
    }

    /**
     * <p>
     * If your platform set different channel in one application , you can set
     * this Id to distinguish.
     * 
     * @param channelID
     *            The Id on platform. If you need distinguish different channel
     *            , configure it first. The channel Id is composed of number or
     *            English character.
     */
    public final void setChannelID(String channelID) {
        this.channelID = channelID != null ? channelID.trim() : "";
    }

    /**
     * <p>
     * Set the different version of your application, you can distinguish by
     * version if you set up . This version can use any String which is composed
     * of number and English character. Not limit the package version name , if
     * and only if this String equals your platform configure is accepted.
     * 
     * @param versionName
     *            : the version name on platform if you need distinguish
     *            different version . You can configure this id on platform.
     */
    public final void setVersionName(String versionName) {
        this.versionName = versionName != null ? versionName.trim() : "";
    }

    /**
     * <p>
     * If you require the status of the incentive media request and result. ,
     * you need invoke this method and set {@link IYumiMediaListener}. You can
     * get the incentive media request success or failed, the incentive media
     * show or close and get incentive.
     * 
     * @param mediaListener
     *            The listener instance. If you don't require callback , can be
     *            null, but you can't get the incentive. Highly recommend to set
     *            this listener.
     */
    public final void setMediaEventListner(IYumiMediaListener mediaListener) {
        this.mediaListener = mediaListener;
    }

    /**
     * <p>
     * Invoke this method , the Yumi SDK will request incentive media auto. When
     * the incentive media prepared , you can invoke {{@link #showMedia()} to
     * play media. You can get the request status by {@link IYumiMediaListener}
     * 
     * @see #setMediaEventListner(IYumiMediaListener)
     */
    public final void requestYumiMedia() {
        if (!isInit()) {
            if (!NullCheckUtils.isNotNull(yumiID)) {
                ZplayDebug.e(TAG, " yumiID can not be null", onoff);
                return;
            }
            if (!YumiManifestReaderUtils.hasMediaRegisterNecessary(mContext)) {
                ZplayDebug.w(TAG, "Missing necessary activity or service in manifest.xml", onoff);
                return;
            }
            if (mConn == null) {
                bindService();
            }
        }
    }

    /**
     * <p>
     * Play incentive media immediately when incentive media prepared.
     * 
     * @return {@link MediaStatus} of current incentive media .
     * 
     * @see MediaStatus#NOT_PREPARED
     * @see MediaStatus#ON_SHOW
     * @see MediaStatus#REACH_MAX_REWARD
     */
    public final MediaStatus showMedia() {
        try {
            if (PreparedProviderId == null) {
                return MediaStatus.NOT_PREPARED;
            }
            List<YumiBaseMediaLayer> adpaterList = YumiMediaAdapterFactory.getFactory().getIsMediaAdapterList();
            for (YumiBaseMediaLayer tempAdapter : adpaterList) {
                if (tempAdapter != null && tempAdapter.isMediaPreparedNoReport() && tempAdapter.getProviderId().equals(PreparedProviderId)) {
                    ZplayDebug.d(TAG, tempAdapter.getProvider().getProviderName() + " is Prepared and showMedia", onoff);
                    MediaStatus mStatus = tempAdapter.showMedia();
                    PreparedProviderId = null;
                    return mStatus;
                }
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "media method showMedia error", e, onoff);
        }
        return MediaStatus.NOT_PREPARED;
    }
    
    /**
     * 询问是有有视频转变完成
     * @return PreparedProviderId
     */
    public synchronized boolean isMediaPrepared() {
        try {
            if (mediaFrequency != null) {
                if (mediaFrequency.isCutDownAll()) {
                    ZplayDebug.w(TAG, "media has no avalid providers", onoff);
                    return false;
                }
                while (true) {
                    final YumiProviderBean nextProvider = mediaFrequency.getNextProvider();
                    if (nextProvider != null) {
                        final YumiBaseMediaLayer tempAdapter = YumiMediaAdapterFactory.getFactory().GetMediaAdapter(mActivity, nextProvider, innerListener);
                        if (tempAdapter != null && tempAdapter.isMediaPrepared()) {
                            ZplayDebug.d(TAG, nextProvider.getProviderName() + " is Prepared", onoff);
                            reportRound(LayerErrorCode.CODE_SUCCESS, tempAdapter.getPid(), tempAdapter.getProvider());
                            PreparedProviderId=tempAdapter.getProviderId();
                            return true;
                        } else {
                            ZplayDebug.d(TAG, nextProvider.getProviderName() + " is Prepared Failed", onoff);
                        }
                    } else {
                        mHandler.sendEmptyMessage(NEXT_ROUND_MEDIA);
                        break;
                    }
                }
            } else {
                ZplayDebug.w(TAG, "media frequency is null  check the local config", onoff);
            }
            reportRound(LayerErrorCode.CODE_FAILED, "", null);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "media method isMediaPrepared error : ", e, onoff);
        }
        return false;
    }

    /**
     * 上报论数
     * @param code
     * @param pid   成功传pid，轮空传空字符串
     */
    private final void reportRound(LayerErrorCode code, String pid, YumiProviderBean provider) {
        if (pid != null && !"".equals(pid) && provider != null) {
            AdListBean bean = new AdListBean(LayerType.TYPE_MEDIA.getType(), YumiConstants.ACTION_REPORT_ROUND, code.getCode(), "SDK", pid, provider.getProviderID(), provider.getKeyID(), null);
            List<AdListBean> beans = new ArrayList<>();
            beans.add(bean);
            YumiIntentSender.reportRoundSuccessEvent(mContext, yumiID, versionName, channelID, getConfigResult().getPlanTime() + "", getConfigResult().getOptimization() + "", LayerType.TYPE_MEDIA,
                    mediaRid, pid, getConfigResult().getTrans(), beans);
        } else {
            AdListBean bean = new AdListBean(LayerType.TYPE_MEDIA.getType(), YumiConstants.ACTION_REPORT_ROUND, code.getCode(), "SDK", "", "", "", null);
            List<AdListBean> beans = new ArrayList<>();
            beans.add(bean);
            YumiIntentSender.reportRoundFailedEvent(mContext, yumiID, versionName, channelID, getConfigResult().getPlanTime() + "", getConfigResult().getOptimization() + "", LayerType.TYPE_MEDIA,
                    mediaRid, getConfigResult().getTrans(), beans);
        }
    }

    private void bindService() {
        mConn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                homeService = null;
                mConn = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ZplayDebug.i(TAG, "bind service success", onoff);
                homeService = ((YumiAdsEventService.ServiceBinder) service).getService();
                if (homeService != null) {
                    getLocalConfigAndRequestAsynchronous();
                }
            }
        };
        YumiIntentSender.bindService(mContext, mConn, LayerType.TYPE_MEDIA);
    }

    /**
     * <p>
     * Cooperate the activity lifecycle , invoke when activity on resume.
     * 
     * @see Activity#onResume()
     */
    //TODO activityLifecycle remove
//    public final void onResume() {
////        if (NullCheckUtils.isNotEmptyCollection(lifecycle)) {
////            for (IYumiActivityLifecycleListener cycle : lifecycle) {
////                cycle.onActivityResume();
////            }
////        }
//    }

    /**
     * <p>
     * Cooperate the activity lifecycle , invoke when activity on pause.
     * 
     * @see Activity#onPause()
     */
    //TODO activityLifecycle remove
//    public final void onPause() {
////        if (NullCheckUtils.isNotEmptyCollection(lifecycle)) {
////            for (IYumiActivityLifecycleListener cycle : lifecycle) {
////                cycle.onActivityPause();
////            }
////        }
//    }

    /**
     * <p>
     * Cooperate the activity lifecycle , invoke when activity on destroy .
     * 
     * @see Activity#onDestroy()
     */
    public final void onDestory() {
        cancelHandlerMessage(mHandler, NEXT_ROUND_MEDIA);
        if (NullCheckUtils.isNotEmptyCollection(lifecycle)) {
            for (IYumiActivityLifecycleListener cycle : lifecycle) {
                cycle.onActivityDestroy();
            }
            lifecycle.clear();
        }
        YumiReceiverUtils.unregisterReceiver(mContext, nr);
        YumiMediaAdapterFactory.getFactory().clearAdapterObtain();
        if (mConn != null || homeService != null) {
            YumiIntentSender.unbindService(mContext, mConn);
        }
        YumiInstanceFactory.releaseMediaControlInstance(yumiID);
    }

    private void cancelHandlerMessage(Handler handler, int... whats) {
        for (int what : whats) {
            if (handler != null && handler.hasMessages(what)) {
                handler.removeMessages(what);
            }
        }
    }

    private void getLocalConfigAndRequestAsynchronous() {
        if (YumiConstants.getSdkVersionInt() > 1010 && NetworkStatusHandler.isNetWorkAvaliable(mContext)) {// TODO new version
            if (getConfigResult() == null && homeService!=null) {
                setConfigResult(homeService.getResultBeanByLocalConfig(YumiConstants.SP_KEY_LAST_MEDIA_CONFIG, YumiConstants.ASSETS_MEDIA_OFFLINE_CONFIG));
            }
            createMediaFrequency();
            initAllMediaAdapter();
        }
        requestMediaConfigFromServer();
    }

}
