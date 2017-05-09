package com.yumi.android.sdk.ads.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yumi.android.sdk.ads.beans.AdListBean;
import com.yumi.android.sdk.ads.beans.Template;
import com.yumi.android.sdk.ads.beans.YumiGlobalBean;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.beans.YumiResultBean;
import com.yumi.android.sdk.ads.constants.YumiAPIList;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.factory.YumiInstanceFactory;
import com.yumi.android.sdk.ads.factory.YumiInterstitialAdapterFactory;
import com.yumi.android.sdk.ads.layer.YumiBaseInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiActivityLifecycleListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.publish.listener.IYumiInterstititalListener;
import com.yumi.android.sdk.ads.receiver.NetworkReceiver;
import com.yumi.android.sdk.ads.request.ConfigInfoRequest.ConfigRequestCallback;
import com.yumi.android.sdk.ads.service.YumiAdsEventService;
import com.yumi.android.sdk.ads.utils.CheckSelfPermissionUtils;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.SharedpreferenceUtils;
import com.yumi.android.sdk.ads.utils.XiaoMiSDKUtil;
import com.yumi.android.sdk.ads.utils.YumiIntentSender;
import com.yumi.android.sdk.ads.utils.YumiManifestReaderUtils;
import com.yumi.android.sdk.ads.utils.YumiReceiverUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.encrypt.YumiSignUtils;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

/**
 * 
 * <p>
 * Create a new {@link YumiInterstitialControl} with YumiID. You can integrate
 * interstitial ad into you project.
 * <p>
 * You can invoke {@link #setVersionName(String)} or invoke
 * {@link #setChannelID(String)}} to distinguish different application version ,
 * channel or both of them.
 * <p>
 * If you require the status of the interstitial request and result. You need
 * set the {@link IYumiInterstititalListener} by invoke
 * {@link #setInterstitialEventListner(IYumiInterstititalListener)}.
 * 
 * @author Mikoto
 *
 */
public final class YumiInterstitialControl extends Control
{

	private static final String TAG = "YumiInterstitial";
	private static final int NEXT_ROUND_INTERSTITIAL = 0x004;
	protected static final int NEXT_PROVIDER = 0x005;
	/**
	 * 插屏加载有效时长
	 */
    private static final int NEXT_ROUND_INTERSTITIAL_ETIME = 0x006;

	private String versionName = "";
	private String channelID = "";
//	private String yumiID = "";
//	private Activity mActivity;
//	private Context mContext;
	private final IYumiInnerLayerStatusListener innerListener;
	private IYumiInterstititalListener interstitialListener;
	private YumiBaseInterstitialLayer currentInterstitialAdapter;
	private Frequency interstitialFrequency;
	private Set<IYumiActivityLifecycleListener> lifecycle;
	private YumiAdsEventService homeService;
	private ServiceConnection mConn;
	private boolean needDelayToGetNext;
	private boolean showInterstitialWhenPreparedAuto;
	private NetworkReceiver nr;
	private String interstitialRid = "";
	
    private boolean flag_newRound;

	private final Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case NetworkReceiver.HANDLER_NETWORK_CHANGE:
				if (getConfigResult() != null && interstitialFrequency != null)
				{
					requestInterstitialLayer();
				} else
				{
					getLocalConfigAndRequestAsynchronous();
				}
				break;
            case NEXT_ROUND_INTERSTITIAL_ETIME:
			case NEXT_ROUND_INTERSTITIAL:
                flag_newRound = true;
				interstitialFrequency.toNextRound();
				updateInterstitialRid();
				requestInterstitialLayer();
				break;
			case NEXT_PROVIDER:
				requestInterstitialLayer();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * <p>
	 * Create the {@link YumiInterstitialControl} instance with activity and
	 * YumiId to integrate interstitial ad on your application.
	 * 
	 * @param activity
	 *            The activity you want to exposure the interstitial
	 * @param yumiID
	 *            The prime Id to use Yumi SDK. you can get the ID from Yumi
	 *            platform. The yumiID can not be null or empty string;
	 * 
	 */
	public YumiInterstitialControl(Activity activity, String yumiID, boolean auto)
	{
		super(activity, yumiID, auto);
		CheckSelfPermissionUtils.CheckSelfPermissionYumi(activity);
		this.innerListener = new IYumiInnerLayerStatusListener()
		{

			@Override
			public void onLayerPreparedFailed(YumiProviderBean provider, LayerType type, LayerErrorCode error)
			{
				/*
				 * if (interstitialListener != null) {
				 * interstitialListener.onInterstitialPreparedFailed(error); }
				 */
				if (error == LayerErrorCode.ERROR_OVER_RETRY_LIMIT)
				{
					ZplayDebug.d(TAG, "interstitial frequency cut down provider " + provider.getProviderName(), onoff);
					interstitialFrequency.cutDownProvider(provider);
				}
				mHandler.sendEmptyMessage(NEXT_PROVIDER);
			}

			@Override
			public void onLayerPrepared(YumiProviderBean provider, LayerType type)
			{
				needDelayToGetNext = false;
				if (interstitialListener != null)
				{
					interstitialListener.onInterstitialPrepared();
				}
				if (showInterstitialWhenPreparedAuto && currentInterstitialAdapter != null)
				{
					currentInterstitialAdapter.showInterstitialLayer(mActivity);
					showInterstitialWhenPreparedAuto = false;
				}else{
                    //插屏加载有效时长 单位为s,未设置为0,超过时间插屏预加载失效重新请求。
				    if(provider.getInterstitialsEtime()>0)
				    {
                      ZplayDebug.d(TAG, "interstitial NEXT_ROUND_INTERSTITIAL_ETIME InterstitialsEtime=" + provider.getInterstitialsEtime(), onoff);
                      mHandler.sendEmptyMessageDelayed(NEXT_ROUND_INTERSTITIAL_ETIME, provider.getInterstitialsEtime() * 1000 );
				    }
				}
			}

			@Override
			public void onLayerExposure(YumiProviderBean provider, LayerType type)
			{
				if (interstitialListener != null)
				{
					interstitialListener.onInterstitialExposure();
				}
			}

			@Override
			public void onLayerClosed(YumiProviderBean provider, LayerType type)
			{
				if (interstitialListener != null)
				{
					interstitialListener.onInterstitialClosed();
				}
				if (isAuto())
				{
					mHandler.sendEmptyMessage(NEXT_ROUND_INTERSTITIAL);
					currentInterstitialAdapter = null;
				}
				cancelInterstitialDelayShown();
			}

			@Override
			public void onLayerCLicked(YumiProviderBean provider, LayerType type, float x, float y)
			{
				if (interstitialListener != null)
				{
					interstitialListener.onInterstitialClicked();
				}
			}
		};
		registerNetworkReceiver();
	}

	private void registerNetworkReceiver()
	{
		nr = new NetworkReceiver(mHandler, mContext.getApplicationContext());
		YumiReceiverUtils.registerNetworkReceiver(mContext, nr);
	}

	private void requestInterstitialLayer()
	{
		// showInterstitialWhenPreparedAuto = false;
		if (interstitialFrequency != null)
		{
			if (interstitialFrequency.isCutDownAll())
			{
				ZplayDebug.w(TAG, "interstitial has no avalid providers ", onoff);
				currentInterstitialAdapter = null;
				return;
			}
			YumiProviderBean nextProvider = interstitialFrequency.getNextProvider();
			if (nextProvider != null)
			{
				nextProvider.setGlobal(new YumiGlobalBean(getConfigResult(), yumiID, channelID, versionName));
				YumiBaseInterstitialLayer tempAdapter = YumiInterstitialAdapterFactory.getFactory()
				        .buildInterstitialAdapter(mActivity, nextProvider, innerListener);
				if (tempAdapter != null)
				{
					if (currentInterstitialAdapter != null)
					{
						currentInterstitialAdapter.onRoundFinished();
					}
					currentInterstitialAdapter = tempAdapter;
					currentInterstitialAdapter.setControl(this);
					addLifeycle(currentInterstitialAdapter);
                    if (flag_newRound)
                    {
                        XiaoMiSDKUtil.trackRequest();
                        flag_newRound = false;
                    }
					currentInterstitialAdapter.prepareInterstitialLayer(interstitialRid);
				} else
				{
					// next adapter is null , maybe cannot find third sdk jar.
					// or reflect exception
					ZplayDebug.e(TAG, "adapter is null , check reflect exception", onoff);
					mHandler.sendEmptyMessage(NEXT_PROVIDER);
					interstitialFrequency.cutDownProvider(nextProvider);
				}
				needDelayToGetNext = true;
			} else
			{
				// next provider is null mean this round finished;
				if (isAuto())
				{
					// 自动模式下轮空后请求下一轮，非自动模式不请求
					mHandler.sendEmptyMessageDelayed(NEXT_ROUND_INTERSTITIAL,
					        needDelayToGetNext ? getConfigResult().getInterval() * 1000 : 0L);
				}

				//上报轮空
				AdListBean bean = new AdListBean(LayerType.TYPE_INTERSTITIAL.getType(),
				        YumiConstants.ACTION_REPORT_ROUND, LayerErrorCode.CODE_FAILED.getCode(),
				        "SDK", "", "", "", null);
				List<AdListBean> beans = new ArrayList<>();
				beans.add(bean);
				YumiIntentSender.reportRoundFailedEvent(mContext, yumiID, versionName, channelID,
				        getConfigResult().getPlanTime() + "", getConfigResult().getOptimization() + "",
				        LayerType.TYPE_INTERSTITIAL, interstitialRid, getConfigResult().getTrans(), beans);
				if (interstitialListener != null)
				{
					interstitialListener.onInterstitialPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
				}
			}
		} else
		{
			ZplayDebug.w(TAG, "interstitial frequency is null  check the local config", onoff);
		}
	}

	private void addLifeycle(YumiBaseInterstitialLayer cycle)
	{
		if (lifecycle == null)
		{
			lifecycle = new HashSet<IYumiActivityLifecycleListener>();
		}
		lifecycle.add(cycle);
	}

	private void requestInterstitialConfigFromServer()
	{
		ZplayDebug.i(TAG, "interstitial request service YumiID " + yumiID + " channelID " + channelID + " versionName "
		        + versionName, onoff);
		if (NetworkStatusHandler.isNetWorkAvaliable(mContext))
		{
            if(homeService == null)
            {
                return;
            }
			homeService.requestConfig(yumiID, channelID, versionName, LayerType.TYPE_INTERSTITIAL,
			        YumiConstants.SP_KEY_LAST_INTERSTITIAL_CONFIG, new ConfigRequestCallback()
			        {

				        @Override
				        public void onReqeustDone(YumiResultBean result)
				        {
					        if (result != null)
					        {
						        if (result.getResult() == 0)
						        {
						        	String logUrl = result.getLogUrl();
						        	if (logUrl!=null && !"".equals(logUrl) && !"null".equals(logUrl)) {
										YumiAPIList.setEVENT_REPORT_HOST_RESET(logUrl);
									}
							        setConfigResult(result);
							        setInit();//TODO 初始化成功
							        downloadTemplate(result);
							        updateInterstitialRid();
							        createInterstitialFrequency();
							        requestInterstitialLayer();
						        } else
						        {
							        ZplayDebug.d(TAG, "get config failed by " + result.getResult(), onoff);
						        }
					        }
				        }
			        });
		} else
		{
			ZplayDebug.w(TAG, "Invalid network", onoff);
		}
	}

	private void updateInterstitialRid()
	{
		interstitialRid = YumiSignUtils.getTrackerID(mContext, "r");
		ZplayDebug.d(TAG, "interstitial update tracker id " + interstitialRid, onoff);
	}

	private void createInterstitialFrequency()
	{
		if (getConfigResult() != null)
		{
			List<YumiProviderBean> providers = getConfigResult().getProviders();
			if (NullCheckUtils.isNotEmptyCollection(providers))
			{
				interstitialFrequency = new Frequency(getConfigResult().getProviders(),
				        getConfigResult().getOptimization() == 1 ? true : false);
				ZplayDebug.v(TAG, "reflash new config , clear adapter obtain", onoff);
				YumiInterstitialAdapterFactory.getFactory().clearAdapterObtain();
				ZplayDebug.v(TAG, "reflash new config , cancel  handler ", onoff);
				cancelHandlerMessage(mHandler, NEXT_PROVIDER, NEXT_ROUND_INTERSTITIAL,NEXT_ROUND_INTERSTITIAL_ETIME);
			}
		}
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
	public final void setChannelID(String channelID)
	{
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
	public final void setVersionName(String versionName)
	{
		this.versionName = versionName != null ? versionName.trim() : "";
	}

	/**
	 * <p>
	 * If you require the status of the interstitial request and result. , you
	 * need invoke this method and set {@link IYumiInterstititalListener}. You
	 * can get the interstitial request success or failed, the interstitial show
	 * or close , the interstitial ad click.
	 * 
	 * @param interstitialListener
	 *            The listener instance. If you don't require callback , can be
	 *            null
	 */
	public final void setInterstitialEventListener(IYumiInterstititalListener interstitialListener)
	{
		this.interstitialListener = interstitialListener;
	}

	/**
	 * <p>
	 * Invoke this method , the Yumi SDK will request interstitial auto. When
	 * the interstitial prepared , you can invoke {
	 * {@link #showInterstitial(boolean)} to show interstitial. You can get the
	 * request status by {@link IYumiInterstititalListener}
	 * 
	 * @see #setInterstitialEventListner(IYumiInterstititalListener)
	 */
	public final void requestYumiInterstitial()
	{
        flag_newRound = true;
		if (!isInit())
		{
			if (!NullCheckUtils.isNotNull(yumiID))
			{
				ZplayDebug.e(TAG, " yumiID can not be null", onoff);
				return;
			}
			if (!YumiManifestReaderUtils.hasRegisterNecessary(mContext))
			{
				ZplayDebug.w(TAG, "Missing necessary activity or service in manifest.xml", onoff);
				return;
			}
			if (mConn == null)
			{
				bindService();
			}
//			YumiInterstitialControl.this.isinit = true;TODO
		}else{
			if (isAuto())
			{
				ZplayDebug.e(TAG, "Cannot invoke this method on auto mode!", onoff);
			}else{
				mHandler.sendEmptyMessage(NEXT_ROUND_INTERSTITIAL);
			}
		}
	}
	
	/**
	 * <p>
	 * Show interstitial when the interstitial prepared .
	 * 
	 * @param delayToShowEnable
	 *            True means there is no prepared interstitial when you invoke
	 *            this method, allow to wait interstitial to load, and when the
	 *            interstitial prepared will show auto. And if you cannot to
	 *            wait the interstitial shown , you need return to your
	 *            application and don't hope the interstitial shown to disturb
	 *            your own function. You must invoke
	 *            {@link #cancelInterstitialDelayShown()}.
	 *            <p>
	 *            False means if there is interstitial prepared interstitial
	 *            will be show immediately, else doesn't show anything when you
	 *            invoke
	 * 
	 * @see #cancelInterstitialDelayShown()
	 */
	public final void showInterstitial(boolean delayToShowEnable)
	{
		if (currentInterstitialAdapter != null)
		{
			currentInterstitialAdapter.showInterstitialLayer(mActivity);
		}
		showInterstitialWhenPreparedAuto = delayToShowEnable;
		
		cancelHandlerMessage(mHandler,NEXT_ROUND_INTERSTITIAL_ETIME);
	}

	/**
	 * <p>
	 * When you invoke {@link #showInterstitial(boolean)} and passing true. You
	 * can invoke this method to cancel waiting. Even if the interstitial
	 * prepared soon , the interstitial doesn't shown.
	 * 
	 * @see #showInterstitial(boolean)
	 */
	public final void cancelInterstitialDelayShown()
	{
		showInterstitialWhenPreparedAuto = false;
	}

	// public final void changeCurrentActivity(Activity activity){
	// this.mActivity = activity;
	// }

	private void bindService()
	{
		mConn = new ServiceConnection()
		{

			@Override
			public void onServiceDisconnected(ComponentName name)
			{
				homeService = null;
				mConn = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				ZplayDebug.i(TAG, "bind service success", onoff);
				homeService = ((YumiAdsEventService.ServiceBinder) service).getService();
				if (homeService != null)
				{
					getLocalConfigAndRequestAsynchronous();
				}
			}
		};
		YumiIntentSender.bindService(mContext, mConn, LayerType.TYPE_INTERSTITIAL);
	}

	/**
	 * <p>
	 * Cooperate the activity lifecycle , invoke when activity on resume.
	 * 
	 * @see Activity#onResume()
	 */
    //TODO activityLifecycle remove
//	public final void onResume()
//	{
////		if (NullCheckUtils.isNotEmptyCollection(lifecycle))
////		{
////			for (IYumiActivityLifecycleListener cycle : lifecycle)
////			{
////				cycle.onActivityResume();
////			}
////		}
//	}

	/**
	 * <p>
	 * Cooperate the activity lifecycle , invoke when activity on pause.
	 * 
	 * @see Activity#onPause()
	 */
    //TODO activityLifecycle remove
//	public final void onPause()
//	{
////		if (NullCheckUtils.isNotEmptyCollection(lifecycle))
////		{
////			for (IYumiActivityLifecycleListener cycle : lifecycle)
////			{
////				cycle.onActivityPause();
////			}
////		}
//	}

	/**
	 * <p> 
	 * Cooperate the activity lifecycle , invoke when activity on destroy .
	 * 
	 * @see Activity#onDestroy()
	 */
	public final void onDestory()
	{
		cancelHandlerMessage(mHandler, NEXT_ROUND_INTERSTITIAL, NEXT_PROVIDER,NEXT_ROUND_INTERSTITIAL_ETIME);
		if (NullCheckUtils.isNotEmptyCollection(lifecycle))
		{
			for (IYumiActivityLifecycleListener cycle : lifecycle)
			{
				cycle.onActivityDestroy();
			}
			lifecycle.clear();
		}
		YumiReceiverUtils.unregisterReceiver(mContext, nr);
		YumiInterstitialAdapterFactory.getFactory().releaseFactory();
		if (mConn != null)
		{
			YumiIntentSender.unbindService(mContext, mConn);
		}
		YumiInstanceFactory.releaseInterstitialControlInstance(yumiID);
	}

	/**
	 * <p>
	 * Cooperate the activity lifecycle , invoke when press back .
	 * 
	 * @return
	 *         <li>If true, you need direct return.
	 *         <li>If false, invoke super.onBackPressed()
	 * 
	 *         <p>
	 *         Sample
	 * 
	 *         <pre class="prettyprint"> 
	 *         public void onBackPressed()
	 *         {
	 *             if (interstitial.onBackPressed())
	 *             {
	 *                 return;
	 *             }
	 *             super.onBackPressed();
	 *         }
	 *         </pre>
	 * 
	 * @see Activity#onBackPressed()
	 */
	public final boolean onBackPressed()
	{
		if (currentInterstitialAdapter != null)
		{
			return currentInterstitialAdapter.onActivityBackPressed();
		}
		return false;
	}

	private final void cancelHandlerMessage(Handler handler, int... whats)
	{
		for (int what : whats)
		{
			if (handler != null && handler.hasMessages(what))
			{
				handler.removeMessages(what);
			}
		}
	}

	private void getLocalConfigAndRequestAsynchronous()
	{
		if (YumiConstants.getSdkVersionInt() > 1010 && NetworkStatusHandler.isNetWorkAvaliable(mContext))
		{// TODO new version
			if (getConfigResult() == null && homeService!=null)
			{
				setConfigResult(homeService.getResultBeanByLocalConfig(YumiConstants.SP_KEY_LAST_INTERSTITIAL_CONFIG,
				        YumiConstants.ASSETS_INTERSTITIAL_OFFLINE_CONFIG));
			}
			createInterstitialFrequency();
			requestInterstitialLayer();
		}
		requestInterstitialConfigFromServer();
	}

}
