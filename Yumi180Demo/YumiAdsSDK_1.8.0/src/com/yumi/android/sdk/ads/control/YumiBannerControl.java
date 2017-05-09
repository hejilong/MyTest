package com.yumi.android.sdk.ads.control;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.R.bool;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yumi.android.sdk.ads.animation.AnimationHolder;
import com.yumi.android.sdk.ads.beans.AdListBean;
import com.yumi.android.sdk.ads.beans.Template;
import com.yumi.android.sdk.ads.beans.YumiGlobalBean;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.beans.YumiResultBean;
import com.yumi.android.sdk.ads.constants.YumiAPIList;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.factory.YumiBannerAdapterFacotry;
import com.yumi.android.sdk.ads.factory.YumiInstanceFactory;
import com.yumi.android.sdk.ads.layer.YumiBaseBannerLayer;
import com.yumi.android.sdk.ads.listener.IYumiActivityLifecycleListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.publish.listener.IYumiBannerListener;
import com.yumi.android.sdk.ads.receiver.NetworkReceiver;
import com.yumi.android.sdk.ads.request.ConfigInfoRequest.ConfigRequestCallback;
import com.yumi.android.sdk.ads.service.YumiAdsEventService;
import com.yumi.android.sdk.ads.utils.CheckSelfPermissionUtils;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.SharedpreferenceUtils;
import com.yumi.android.sdk.ads.utils.XiaoMiSDKUtil;
import com.yumi.android.sdk.ads.utils.YumiIntentSender;
import com.yumi.android.sdk.ads.utils.YumiLayerSizeCalculater;
import com.yumi.android.sdk.ads.utils.YumiManifestReaderUtils;
import com.yumi.android.sdk.ads.utils.YumiReceiverUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.utils.encrypt.YumiSignUtils;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;
import com.yumi.android.sdk.ads.utils.views.AdContainer;
import com.yumi.android.sdk.ads.utils.views.AdContainer.IAdContainerOnWindowFocusChanged;

/**
 * <p>
 * Create a new {@link YumiBannerControl} with YumiID. You can integrate banner
 * ad into you project.
 * <p>
 * You can invoke {@link #setVersionName(String)} or invoke
 * {@link #setChannelID(String)} to distinguish different application version ,
 * channel or both of them.
 * <p>
 * You need set a {@link ViewGroup} as banner container, and you need to set the
 * container size with {@link #setBannerContainer(ViewGroup, AdSize)}.
 * <p>
 * If you require the status of the banner request and result. You need set the
 * {@link IYumiBannerListener} by invoke
 * {@link #setBannerEventListener(IYumiBannerListener)}.
 * 
 * @author Mikoto
 *
 */
public final class YumiBannerControl extends Control implements IAdContainerOnWindowFocusChanged
{

	private static final String TAG = "YumiBannerControl";

	public static final int CHANGE_BANNER_VIEW = 0x009;
	private static final int NEXT_ROUND_BANNER = 0x101;
	private static final int NEXT_PROVIDER = 0x102;
	private static final int HANDLER_ANIM_END = 0x103;

	private String versionName = "";
	private String channelID = "";
	private ViewGroup developerCntainer;//开发者设置的广告父容器
	private AdContainer container;    //广告控件父容器
	// private String yumiID;
	// private Activity mActivity;
	// private Context mContext;
	private final IYumiInnerLayerStatusListener innerListner;
	private IYumiBannerListener bannerListener;
	private YumiBaseBannerLayer lastBannerAdapter;
	private YumiBaseBannerLayer currentBannerAdapter;
	private Frequency bannerFrequency;
	private Set<IYumiActivityLifecycleListener> lifecycle = null;
	private boolean pauseBannerRequest = false;
	private boolean requestBannerInPause = false;
	private boolean canRequestBanner = false;
	private boolean activityInPause = false;
	private AdSize adSize = AdSize.BANNER_SIZE_AUTO;
	private boolean isMatchWindowWidth=false;
	private int[] calculateLayerSize;
	private YumiAdsEventService homeService;
	private TranslateAnimation out;
	private TranslateAnimation in;
	private boolean addedCancelBtn = false;
	private boolean needDelayToGetNext;
	private boolean isInvokeDismiss = false;
	private NetworkReceiver nr;
	private ServiceConnection mConn;
	private String bannerRid = "";
	
	private boolean flag_newRound;
	
	private boolean isOverlapRemove=false;
	private boolean isOnDestroy=false;

	private final Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case NetworkReceiver.HANDLER_NETWORK_CHANGE:
				if (getConfigResult() != null && bannerFrequency != null)
				{
					requestBannerLayer();
				} else
				{
					getLocalConfigAndRequestAsynchronous();
				}
				break;
			case CHANGE_BANNER_VIEW:
				replaceBannerView((View) msg.obj);
				break;
			case NEXT_ROUND_BANNER:
                if (!getBanerContainerCovering()) {//判断广告容器是否被遮盖
                    flag_newRound = true;
                    if (bannerFrequency != null) {
                        bannerFrequency.toNextRound();
                        updateBannerRID();
                        requestBannerLayer();
                    }
                } else {
                    removeAdToNextRound();
                }
				break;
			case NEXT_PROVIDER:
				requestBannerLayer();
				break;
			case HANDLER_ANIM_END:
				removeOldBannerView((View) msg.obj);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * <p>
	 * Create the {@link YumiBannerControl} instance with activity and YumiId to
	 * integrate banner ad on your application.
	 * 
	 * @param activity
	 *            The activity you want to exposure the banner
	 * @param yumiID
	 *            The prime Id to use Yumi SDK. you can get the ID from Yumi
	 *            platform. The yumiID can not be null or empty string;
	 * 
	 */
	public YumiBannerControl(Activity activity, String yumiID, boolean auto)
	{
		super(activity, yumiID, auto);
		CheckSelfPermissionUtils.CheckSelfPermissionYumi(activity);
		this.innerListner = new IYumiInnerLayerStatusListener()
		{

			@Override
			public void onLayerPreparedFailed(YumiProviderBean provider, LayerType type, LayerErrorCode error)
			{
				/*
				 * if (bannerListener != null) {
				 * bannerListener.onBannerPreparedFailed(error); }
				 */
				if (error == LayerErrorCode.ERROR_OVER_RETRY_LIMIT)
				{
					ZplayDebug.d(TAG, "banner frequency cut down provider " + provider.getProviderName(), onoff);
					bannerFrequency.cutDownProvider(provider);
				}
				mHandler.sendEmptyMessage(NEXT_PROVIDER);
			}

			@Override
			public void onLayerPrepared(YumiProviderBean provider, LayerType type)
			{
				needDelayToGetNext = false;
				if (!isInvokeDismiss && developerCntainer != null && developerCntainer.getVisibility() != View.VISIBLE)
				{
				    developerCntainer.setVisibility(View.VISIBLE);
				}
				if (bannerListener != null)
				{
					bannerListener.onBannerPrepared();
				}
				if (isAuto())
				{
					mHandler.sendEmptyMessageDelayed(NEXT_ROUND_BANNER, getConfigResult().getInterval() * 1000);
				}
			}

			@Override
			public void onLayerExposure(YumiProviderBean provider, LayerType type)
			{
				if (bannerListener != null)
				{
					bannerListener.onBannerExposure();
				}
			}

			@Override
			public void onLayerClosed(YumiProviderBean provider, LayerType type)
			{
				if (bannerListener != null)
				{
					bannerListener.onBannerClosed();
				}
			}

			@Override
			public void onLayerCLicked(YumiProviderBean provider, LayerType type, float x, float y)
			{
				if (bannerListener != null)
				{
					bannerListener.onBannerClicked();
				}
			}
		};
		registerNetworkReceiver();
	}

	private final void registerNetworkReceiver()
	{
		nr = new NetworkReceiver(mHandler, mContext.getApplicationContext());
		YumiReceiverUtils.registerNetworkReceiver(mContext, nr);
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
	 * SDK will set a {@link ViewGroup} as banner container, and you need assign
	 * {@link AdSize} and position to this container . Banner view will fill
	 * into the container and exposure .
	 * <p>
	 * You must invoke this method after your activity setContentView().
	 * 
	 * @param bannerSize
	 *            The banner size you can choose from {@link AdSize}.
	 * @param gravity
	 *            The banner position , use {@link android.view.Gravity} to set
	 *            bannerContainer exposure position.
	 * 
	 * @see AdSize#BANNER_SIZE_320X50
	 * @see AdSize#BANNER_SIZE_728X90
	 * @see AdSize#BANNER_SIZE_AUTO
	 */
	// public final void setBannerContainer(AdSize bannerSize, int gravity){
	// setDefaultSize(bannerSize);
	// int[] calculateLayerSize =
	// YumiLayerSizeCalculater.calculateLayerSize(mActivity, adSize);
	// if (this.container == null) {
	// this.container = new FrameLayout(mContext);
	// LayoutParams layoutParams = new LayoutParams(calculateLayerSize[0],
	// calculateLayerSize[1]);
	// layoutParams.gravity = gravity;
	// mActivity.addContentView(container, layoutParams);
	// }
	// }
	public final void setBannerContainer(ViewGroup layout, AdSize bannerSize, boolean isMatchWindowWidth)
	{
		this.developerCntainer = layout;
		this.container = new AdContainer(mContext, this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.isMatchWindowWidth=isMatchWindowWidth;
        setDefaultSize(bannerSize);
        calculateLayerSize = YumiLayerSizeCalculater.calculateLayerSize(mActivity, adSize,isMatchWindowWidth);
        // calculate layer size and set up developerCntainer
		LayoutParams layoutParams = developerCntainer.getLayoutParams();
		if (layoutParams != null)
		{
			layoutParams.width = calculateLayerSize[0];
			layoutParams.height = calculateLayerSize[1];
		} else
		{
			layoutParams = new LayoutParams(calculateLayerSize[0], calculateLayerSize[1]);
		}
		developerCntainer.setLayoutParams(layoutParams);
		developerCntainer.addView(container,params);
	}

	private final void addManualCancelButton()
	{
		if (!addedCancelBtn)
		{
			ImageView cancel = new ImageView(mContext);
			cancel.setImageResource(android.R.drawable.presence_offline);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WindowSizeUtils.dip2px(mContext, 20),
			        WindowSizeUtils.dip2px(mContext, 20));
			params.gravity = Gravity.RIGHT | Gravity.TOP;
			cancel.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					container.setVisibility(View.INVISIBLE);
					if (bannerListener != null)
					{
						bannerListener.onBannerClosed();
					}
				}
			});
			container.addView(cancel, params);
			addedCancelBtn = true;
		}

	}

	/**
	 * <p>
	 * If you require the status of the banner request and result. , you need
	 * invoke this method and set {@link IYumiBannerListener}. You can get the
	 * banner request success or failed, the banner shown or dismiss on screen,
	 * the banner ad click.
	 * 
	 * @param bannerListener
	 *            The listener instance. If you don't require callback , can be
	 *            null
	 */
	public final void setBannerEventListener(IYumiBannerListener bannerListener)
	{
		this.bannerListener = bannerListener;
	}

	/**
	 * <p>
	 * Invoke this method , the Yumi SDK will request banner auto, and show
	 * banner if there has ad to show. You can get the request status by
	 * {@link IYumiBannerListener}
	 * 
	 * @see #setBannerEventListener(IYumiBannerListener)
	 */
	public final void requestYumiBanner()
	{
	    flag_newRound = true;
		if (!isInit())
		{
			if (!NullCheckUtils.isNotNull(yumiID))
			{
				ZplayDebug.e(TAG, " yumiID can not be null", onoff);
				return;
			}
			if (developerCntainer == null)
			{
				ZplayDebug.w(TAG,
				        "empty banner container, if you need to exposure banner ads, make sure you have set the banner container",
				        onoff);
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
		}else{
			if (isAuto())
			{
				ZplayDebug.e(TAG, "Cannot invoke this method on auto mode!", onoff);
			}else{
				mHandler.sendEmptyMessage(NEXT_ROUND_BANNER);
			}
		}
		
	}

	private final void bindService()
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
				homeService = ((YumiAdsEventService.ServiceBinder) service).getService();
				ZplayDebug.i(TAG, "bind service success", onoff);
				if (homeService != null)
				{
					getLocalConfigAndRequestAsynchronous();
				}
			}
		};
		YumiIntentSender.bindService(mContext, mConn, LayerType.TYPE_BANNER);
	}

	private final void requestBannerConfigFromServer()
	{
		ZplayDebug.i(TAG,
		        "banner request service YumiID " + yumiID + " channelID " + channelID + " versionName " + versionName,
		        onoff);
		if (NetworkStatusHandler.isNetWorkAvaliable(mContext))
		{
		    if(homeService == null)
		    {
		        return;
		    }
			homeService.requestConfig(yumiID, channelID, versionName, LayerType.TYPE_BANNER,
			        YumiConstants.SP_KEY_LAST_BANNER_CONFIG, new ConfigRequestCallback()
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
							        setInit();
							        updateBannerRID();
							        createBannerFrequency();
							        downloadTemplate(result);
                                    isOverlapRemove = result.getOverlapRemove() == 1 ? true : false;
							        if (!getBanerContainerCovering()) { //判断广告容器是否被遮盖
					                        requestBannerLayer();
					                } else {
					                    removeAdToNextRound();
					                }
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

	private void updateBannerRID()
	{
		bannerRid = YumiSignUtils.getTrackerID(mContext, "r");
		ZplayDebug.d(TAG, "banner update round tracker id " + bannerRid, onoff);
	}

	private final void createBannerFrequency()
	{
		canRequestBanner = false;
		if (getConfigResult() != null)
		{
			List<YumiProviderBean> providers = getConfigResult().getProviders();
			if (NullCheckUtils.isNotEmptyCollection(providers))
			{
				bannerFrequency = new Frequency(providers, getConfigResult().getOptimization() == 1 ? true : false);
				ZplayDebug.v(TAG, "reflash new config , clear adapter obtain", onoff);
				YumiBannerAdapterFacotry.getFactory().clearAdapterObtain();
				ZplayDebug.v(TAG, "reflash new config , cancel  handler ", onoff);
				cancelHandlerMessage(mHandler, NEXT_PROVIDER, NEXT_ROUND_BANNER);
				canRequestBanner = true;
			}
		}
	}

	private final void setDefaultSize(AdSize bannerSize)
	{
		if (bannerSize == AdSize.BANNER_SIZE_AUTO)
		{
			if (WindowSizeUtils.isTablet(mActivity))
			{
				adSize = AdSize.BANNER_SIZE_728X90;
			} else
			{
				adSize = AdSize.BANNER_SIZE_320X50;
			}
		} else
		{
			adSize = bannerSize;
		}
	}

	private final void requestBannerLayer()
	{
		if (pauseBannerRequest)
		{
			requestBannerInPause = true;
			ZplayDebug.d(TAG, "request banner layer in pause", onoff);
		} else
		{
			if (bannerFrequency != null)
			{
				if (bannerFrequency.isCutDownAll())
				{
					ZplayDebug.w(TAG, "banner has no avalid providers ", onoff);
					return;
				}
				YumiProviderBean nextProvider = bannerFrequency.getNextProvider();
				if (nextProvider != null)
				{
					YumiGlobalBean global = new YumiGlobalBean(getConfigResult(), yumiID, channelID, versionName);
					nextProvider.setGlobal(global);
					YumiBaseBannerLayer tempAdapter = YumiBannerAdapterFacotry.getFactory()
					        .buildBannerAdapter(mActivity, nextProvider, innerListner);
					lastBannerAdapter = currentBannerAdapter;
					if (tempAdapter != null)
					{
						if (lastBannerAdapter != null)
						{
							lastBannerAdapter.onRoundFinished();
						}
						currentBannerAdapter = tempAdapter;
						currentBannerAdapter.setControl(this);
						currentBannerAdapter.setDeveloperCntainer(developerCntainer);
						addLifeycle(currentBannerAdapter);
						if (flag_newRound)
                        {
                            XiaoMiSDKUtil.trackRequest();
                            flag_newRound = false;
                        }
						currentBannerAdapter.prepareBannerLayer(adSize, mHandler, bannerRid,isMatchWindowWidth,calculateLayerSize);
					} else
					{
						// next adapter is null , maybe cannot find third sdk
						// jar. or reflect exception
						ZplayDebug.e(TAG, "adapter is null , check reflect exception", onoff);
						mHandler.sendEmptyMessage(NEXT_PROVIDER);
						bannerFrequency.cutDownProvider(nextProvider);
					}
					needDelayToGetNext = true;
				} else
				{
					// next provider is null mean this round finished;
					if (isAuto())
					{
						mHandler.sendEmptyMessageDelayed(NEXT_ROUND_BANNER,
								needDelayToGetNext ? getConfigResult().getInterval() * 1000 : 0L);
					}
					// TODO 需要合并上报项 终结
					AdListBean bean = new AdListBean(LayerType.TYPE_BANNER.getType(), YumiConstants.ACTION_REPORT_ROUND,
					        LayerErrorCode.CODE_FAILED.getCode(), "SDK", "", "", "", null);
					getEventMerge().add(bean);
					YumiIntentSender.reportRoundFailedEvent(mContext, yumiID, versionName, channelID,
					        getConfigResult().getPlanTime() + "", getConfigResult().getOptimization() + "", LayerType.TYPE_BANNER,
					        bannerRid, getConfigResult().getTrans(), getEventMerge());
					getEventMerge().clear();
					if (bannerListener != null)
					{
						bannerListener.onBannerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
					}
				}
			} else
			{
				ZplayDebug.w(TAG, "banner frequency is null  check the local config", onoff);
			}
		}
	}

	private final void addLifeycle(YumiBaseBannerLayer cycle)
	{
		if (lifecycle == null)
		{
			lifecycle = new HashSet<IYumiActivityLifecycleListener>();
		}
		lifecycle.add(cycle);
	}

	private final void replaceBannerView(View newBanner)
	{
		// use the new banner view to replace old view . destroy old view
		if (container != null && newBanner != null)
		{
			Map<String, TranslateAnimation> anims = AnimationHolder.getAnimManager()
			        .buildHorizontalAnimation(developerCntainer.getLayoutParams().width, 1500);
			if (container.getChildCount() > (getConfigResult().getManualCancel() == 1 ? 1 : 0))
			{
				final View child = container.getChildAt(0);
				if (!(child instanceof ImageView))
				{
					out = anims.get(AnimationHolder.KEY_OUTANIM);
					child.setAnimation(out);
					out.start();
					Message msg = Message.obtain();
					msg.what = HANDLER_ANIM_END;
					msg.obj = child;
					mHandler.sendMessageDelayed(msg, out.getDuration());
					ZplayDebug.i(TAG, "remove view is " + child, onoff);
				}
			}
			if (newBanner.getParent() != null)
			{
				((ViewGroup) newBanner.getParent()).removeView(newBanner);
			}
			ZplayDebug.i(TAG, "add new view is " + newBanner, onoff);
			container.addView(newBanner, 0);
			in = anims.get(AnimationHolder.KEY_INANIM);
			newBanner.setAnimation(in);
			in.start();
			if (getConfigResult().getManualCancel() == 1)
			{
				addManualCancelButton();
			}
			ZplayDebug.d(TAG, "container size is " + container.getChildCount(), onoff);
	        
		}
	}
	

	private final void removeOldBannerView(final View child)
	{
		if (container != null)
		{
			container.removeView(child);
			child.destroyDrawingCache();
		}
		if (container != null)
		{
			ZplayDebug.d(TAG, "container size is " + container.getChildCount() + " when remove old view ", onoff);
		}
	}

	/**
	 * <p>
	 * Hide banner and pause the banner request circulation at the same time.
	 * 
	 */
	public final void dismissBanner()
	{
		// 隐藏banner 需要暂停request
		isInvokeDismiss = true;
		if (developerCntainer != null)
		{
		    developerCntainer.setVisibility(View.GONE);
		}
		pauseRequest();
	}

	/**
	 * <p>
	 * Resume banner to visiable, and resume banner request circulation at the
	 * same time.
	 */
	public final void resumeBanner()
	{
		// 恢复banner 恢复request
		isInvokeDismiss = false;
		if (developerCntainer != null)
		{
		    developerCntainer.setVisibility(View.VISIBLE);
		}
		if (!activityInPause)
		{
			resumeRequest();
		}
	}

	private final void pauseRequest()
	{
		ZplayDebug.d(TAG, "pause banner request ", onoff);
		pauseBannerRequest = true;
	}

	private final void resumeRequest()
	{
		if (developerCntainer != null && developerCntainer.getVisibility() != View.VISIBLE)
		{
			return;
		}
		pauseBannerRequest = false;
		if (isAuto())
		{
			if (requestBannerInPause && canRequestBanner)
			{
				ZplayDebug.d(TAG, "resume banner request and need call request ", onoff);
				requestBannerLayer();
				requestBannerInPause = false;
			} else
			{
				ZplayDebug.d(TAG, "resume banner request not need request ::  call in pause : " + requestBannerInPause
						+ " can request :  " + canRequestBanner, onoff);
				if (!mHandler.hasMessages(NEXT_ROUND_BANNER) && !mHandler.hasMessages(NEXT_PROVIDER))
				{
					if (getConfigResult() != null)
					{
						ZplayDebug.e(TAG, "not need request and check handler and into next round", onoff);
						mHandler.sendEmptyMessageDelayed(NEXT_ROUND_BANNER, getConfigResult().getIncentived() * 1000);
					}
				}
			}
		}
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
//		activityInPause = false;
//		resumeRequest();
//		if (NullCheckUtils.isNotEmptyCollection(lifecycle))
//		{
//			for (IYumiActivityLifecycleListener cycle : lifecycle)
//			{
//				cycle.onActivityResume();
//			}
//		}
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
//		activityInPause = true;
//		pauseRequest();
//		if (NullCheckUtils.isNotEmptyCollection(lifecycle))
//		{
//			for (IYumiActivityLifecycleListener cycle : lifecycle)
//			{
//				cycle.onActivityPause();
//			}
//		}
//	}

	/**
	 * <p>
	 * Cooperate the activity lifecycle , invoke when activity on destroy.
	 * 
	 * @see Activity#onDestroy()
	 */
	public final void onDestroy()
	{
	    isOnDestroy=true;
		ZplayDebug.i(TAG, "yumi banner destroy ", onoff);
		cancelHandlerMessage(mHandler, NEXT_ROUND_BANNER, NEXT_PROVIDER);
		stopAnim(out, in);
		if (NullCheckUtils.isNotEmptyCollection(lifecycle))
		{
			for (IYumiActivityLifecycleListener cycle : lifecycle)
			{
				cycle.onActivityDestroy();
			}
			lifecycle.clear();
		}
		if (container != null)
		{
			ZplayDebug.i(TAG, "remove cantainer view on destroy", onoff);
			container.removeAllViews();
		}
		YumiReceiverUtils.unregisterReceiver(mContext, nr);
		YumiBannerAdapterFacotry.getFactory().releaseFactory();
		if (mConn != null)
		{
			ZplayDebug.i(TAG, "unbind service on destroy", onoff);
			YumiIntentSender.unbindService(mContext, mConn);
		}
		YumiInstanceFactory.releaseBannerControlInstance(yumiID);
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

	private final void stopAnim(TranslateAnimation... anims)
	{
		for (TranslateAnimation translateAnimation : anims)
		{
			if (translateAnimation != null)
			{
				translateAnimation.cancel();
			}
		}
	}

	private final void getLocalConfigAndRequestAsynchronous()
	{
		if (YumiConstants.getSdkVersionInt() > 1010 && NetworkStatusHandler.isNetWorkAvaliable(mContext))
		{// TODO
		 // new
		 // version
			if (getConfigResult() == null && homeService!=null)
			{
				setConfigResult(homeService.getResultBeanByLocalConfig(YumiConstants.SP_KEY_LAST_BANNER_CONFIG,
				        YumiConstants.ASSETS_BANNER_OFFLINE_CONFIG));
			}
			createBannerFrequency();
			requestBannerLayer();
		}
		requestBannerConfigFromServer();
	}

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if(hasWindowFocus)
        {
            activityInPause = false;
            resumeRequest();
            if (NullCheckUtils.isNotEmptyCollection(lifecycle))
            {
                for (IYumiActivityLifecycleListener cycle : lifecycle)
                {
                    cycle.onActivityResume();
                }
            }
        }else{
            activityInPause = true;
            pauseRequest();
            if (NullCheckUtils.isNotEmptyCollection(lifecycle))
            {
                for (IYumiActivityLifecycleListener cycle : lifecycle)
                {
                    cycle.onActivityPause();
                }
            }
        }
    }
    
    /**
     * 移除广告并等待下一轮
     */
    private void removeAdToNextRound()
    {
        if (!isOnDestroy) {
            ZplayDebug.e(TAG, "The AD container is covered,Wait for the next round of.", onoff);
            container.removeAllViews();
            mHandler.sendEmptyMessageDelayed(NEXT_ROUND_BANNER, getConfigResult().getInterval() * 1000);
        }
    }
    
    /**
     * 判断banner是否被遮挡
     * @return
     */
    private boolean getBanerContainerCovering() {
        if(!isOverlapRemove)
        {
            //服务端返回遮挡不删除的话直接返回 false 
            return false;
        }
        if (developerCntainer != null) {
            boolean isCovering=isViewCovered(developerCntainer);
//            ZplayDebug.v(TAG, "getBanerContainerCovering isCovering="+isCovering, onoff);
            return isCovering;
        }
        return false;
    }

    /**
     * 判断view是否被其他view遮挡
     * @param view
     * @return
     */
    public boolean isViewCovered(final View view) {
        try {
            View currentView = view;
            Rect currentViewRect = new Rect();
            boolean partVisible = currentView.getGlobalVisibleRect(currentViewRect);
            boolean totalHeightVisible = (currentViewRect.bottom - currentViewRect.top) >= view.getMeasuredHeight();
            boolean totalWidthVisible = (currentViewRect.right - currentViewRect.left) >= view.getMeasuredWidth();
            boolean totalViewVisible = partVisible && totalHeightVisible && totalWidthVisible;
            if (!totalViewVisible)//if any part of the view is clipped by any of its parents,return true
                return true;

            while (currentView.getParent() instanceof ViewGroup) {
                ViewGroup currentParent = (ViewGroup) currentView.getParent();
                if (currentParent.getVisibility() != View.VISIBLE)//if the parent of view is not visible,return true
                {
                    return true;
                }
                int start = indexOfViewInParent(currentView, currentParent);
                for (int i = start + 1; i < currentParent.getChildCount(); i++) {
                    Rect viewRect = new Rect();
                    view.getGlobalVisibleRect(viewRect);
                    View otherView = currentParent.getChildAt(i);
                    Rect otherViewRect = new Rect();
                    otherView.getGlobalVisibleRect(otherViewRect);
                    if (Rect.intersects(viewRect, otherViewRect) && otherView.getVisibility() == View.VISIBLE)//if view intersects its older brother(covered),return true
                    {
                        return true;
                    }
                }
                currentView = currentParent;
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "isViewCovered is error : ",e, onoff);
        }
        return false;
    }

    private int indexOfViewInParent(View view, ViewGroup parent) {
        int index;
        for (index = 0; index < parent.getChildCount(); index++) {
            if (parent.getChildAt(index) == view)
                break;
        }
        return index;
    }
}
