package com.yumi.android.sdk.ads.layer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;

import com.yumi.android.sdk.ads.beans.AdListBean;
import com.yumi.android.sdk.ads.beans.ClickArea;
import com.yumi.android.sdk.ads.beans.LPArea;
import com.yumi.android.sdk.ads.beans.Template;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.beans.YumiResultBean;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.control.Control;
import com.yumi.android.sdk.ads.listener.IYumiActivityLifecycleListener;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.mediation.data.MediationStatus;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.utils.XiaoMiSDKUtil;
import com.yumi.android.sdk.ads.utils.YumiIntentSender;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.encrypt.YumiSignUtils;

public abstract class YumiBaseLayer implements IYumiActivityLifecycleListener
{

	private static final boolean onoff = false;
	private static final int NON_RESPONSE = 0x01;
	private static final String TAG = "YumiBaseLayer";

	private String rid = "";
	private String pid = "";
	YumiProviderBean invariantProvider;
	YumiProviderBean mProvider;
	Activity mActivity;
	Context mContext;
	public IYumiInnerLayerStatusListener mInnerListener;
	int failedTimes = 0;
	boolean needCallbackInnerListener;
	private Control control;
	public boolean isOutTime;
	
	protected int webViewWidth;
	protected int webViewHeight;
	
    private ViewGroup developerContainer;//开发者设置的广告父容器

    protected boolean isMediation=false; //是否是调试模式调用

	public final void setControl(Control control)
	{
		this.control = control;
	}

	protected final Activity getActivity()
	{
		return mActivity;
	}

	protected final Context getContext()
	{
		return mContext;
	}

	public final YumiProviderBean getProvider()
	{
		return mProvider;
	}

	
	public boolean getIsMediation() {
        return isMediation;
    }

    public void setIsMediation(boolean isMediation) {
        this.isMediation = isMediation;
    }
    
    public IYumiInnerLayerStatusListener getmInnerListener() {
        return mInnerListener;
    }

    public void setmInnerListener(IYumiInnerLayerStatusListener mInnerListener) {
        synchronized (mInnerListener) {
            this.mInnerListener = mInnerListener;
            needCallbackInnerListener = true;
        }
    }


    public ViewGroup getDeveloperCntainer() {
        return developerContainer;
    }

    public void setDeveloperCntainer(ViewGroup developerCntainer) {
        this.developerContainer = developerCntainer;
    }


    private Handler nonResponseHanlder = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == NON_RESPONSE)
			{
				isOutTime = true;
				onRequestNonResponse();
			}
		};
	};

	protected YumiBaseLayer(Activity activity, YumiProviderBean provider)
	{
		this.mActivity = activity;
		this.mContext = activity.getApplicationContext();
		this.invariantProvider = new YumiProviderBean(provider);
		this.mProvider = provider;
		this.isOutTime = false;
		// init(); Constructor don't call method need override
	}

	final void setYumiInnerListener()
	{
		synchronized (mInnerListener)
		{
			ZplayDebug.v(TAG, "Thread id " + Thread.currentThread().getId() + " invoke setYumiInnerListener ", onoff);
			needCallbackInnerListener = true;
		}
	}

	private final void removeYumiInnerListener()
	{
		synchronized (mInnerListener)
		{
			ZplayDebug.v(TAG, "Thread id " + Thread.currentThread().getId() + " invoke removeYumiInnerListener ",
			        onoff);
			needCallbackInnerListener = false;
		}
	}

	final void sendNonResoponseHandler()
	{
		if (nonResponseHanlder != null)
		{
			int outTime = invariantProvider.getOutTime();
			ZplayDebug.d(TAG, "超时时间:" + outTime, true);
			if (invariantProvider.getOutTime() > 0)
			{
				nonResponseHanlder.sendEmptyMessageDelayed(NON_RESPONSE, invariantProvider.getOutTime() * 1000);
			}
		}
	};

	private final void cancelNonResponseHandler()
	{
		if (nonResponseHanlder != null && nonResponseHanlder.hasMessages(NON_RESPONSE))
		{
			ZplayDebug.i(TAG, "cancel non response handler", onoff);
			nonResponseHanlder.removeMessages(NON_RESPONSE);
		}
	}

	final void layerShowOpportReport(LayerType layerType, LayerErrorCode errorCode)
	{
		if (!isOutTime && !isMediation)
		{
			AdListBean bean = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_OPPORT,
			        errorCode.getCode(), geReqType(), pid, getProvider().getProviderID(), getProvider().getKeyID(), null);
			List<AdListBean> beans = new ArrayList<>();
			beans.add(bean);
			YumiIntentSender.reportEvent(mContext, mProvider, YumiConstants.ACTION_REPORT_OPPORT, layerType, errorCode,
			        rid, pid, control.getConfigResult().getTrans(), beans);
		}
	}

	// TODO 需要合并上报项
	final void layerRequestReport(LayerType layerType)
	{
		if (!isOutTime && !isMediation)
		{
			if (layerType == LayerType.TYPE_BANNER)
			{
				AdListBean bean = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_REQUEST,
				        LayerErrorCode.CODE_SUCCESS.getCode(), geReqType(), pid, getProvider().getProviderID(),
				        getProvider().getKeyID(), null);
				List<AdListBean> beans = this.control.getEventMerge();
				beans.add(bean);
			} else
			{
				AdListBean bean = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_REQUEST,
				        LayerErrorCode.CODE_SUCCESS.getCode(), geReqType(), pid, getProvider().getProviderID(),
				        getProvider().getKeyID(), null);
				List<AdListBean> beans = new ArrayList<>();
				beans.add(bean);
				YumiResultBean configResult = control.getConfigResult();
				String trans = configResult.getTrans();
				YumiIntentSender.reportEvent(mContext, invariantProvider, YumiConstants.ACTION_REPORT_REQUEST,
				        layerType, LayerErrorCode.CODE_SUCCESS, rid, pid, trans, beans);
			}
		}
	}

	final void layerIncentivedReport()
	{
		if (!isOutTime && !isMediation)
		{
			AdListBean bean = new AdListBean(LayerType.TYPE_MEDIA.getType(), YumiConstants.ACTION_REPORT_REWARD,
			        LayerErrorCode.CODE_SUCCESS.getCode(), geReqType(), pid, getProvider().getProviderID(),
			        getProvider().getKeyID(), null);
			List<AdListBean> beans = new ArrayList<>();
			beans.add(bean);
			YumiIntentSender.reportEvent(mContext, invariantProvider, YumiConstants.ACTION_REPORT_REWARD,
			        LayerType.TYPE_MEDIA, LayerErrorCode.CODE_SUCCESS, rid, pid, control.getConfigResult().getTrans(), beans);
		}
	}

	
	final void layerClicked(LayerType layerType, float x, float y)
	{
		synchronized (mInnerListener)
		{
			if (!isOutTime && layerType != LayerType.TYPE_MEDIA && !isMediation)
			{
				ZplayDebug.d(TAG, "Thread id " + Thread.currentThread().getId() + " " + mProvider.getProviderName()
				        + " layerClicked", onoff);
				int templateID = 0;
				int useTemplateMode = getProvider().getUseTemplateMode();
            	if (useTemplateMode>=0)
				{
            		Template template = getProvider().getTemplate(useTemplateMode);
    				if (template!=null)
    				{
    					templateID = template.getId();
    				}
				}
				AdListBean bean = new AdListBean(
						layerType.getType(),
						YumiConstants.ACTION_REPORT_CLICK,
						LayerErrorCode.CODE_SUCCESS.getCode(),
						geReqType(),
						pid,
						getProvider().getProviderID(),
						getProvider().getKeyID(),
						new ClickArea(webViewWidth, webViewHeight, x, y),
						getContainerXYWH(),
						templateID);
				List<AdListBean> beans = new ArrayList<>();
				beans.add(bean);
				YumiIntentSender.reportEvent(mContext, invariantProvider, YumiConstants.ACTION_REPORT_CLICK, layerType,
				        LayerErrorCode.CODE_SUCCESS, rid, pid, control.getConfigResult().getTrans(), beans);
			}
			if (needCallbackInnerListener)
            {
                mInnerListener.onLayerCLicked(mProvider, layerType, x, y);
            }
		}
	}

	// TODO 需要合并上报项 终结
	final void layerExposure(LayerType layerType)
	{
	    XiaoMiSDKUtil.trackShow();
		synchronized (mInnerListener)
		{
			if (!isOutTime )
			{
			    getContainerXYWH();
				ZplayDebug.d(TAG, "Thread id " + Thread.currentThread().getId() + " " + mProvider.getProviderName()
				        + " layerExpourse ", onoff);
                if (!isMediation) {
    				int templateID = 0;
    				int useTemplateMode = getProvider().getUseTemplateMode();
                	if (useTemplateMode>=0)
    				{
                		Template template = getProvider().getTemplate(useTemplateMode);
        				if (template!=null)
        				{
        					templateID = template.getId();
        				}
    				}
                    if (layerType == LayerType.TYPE_BANNER) {
						List<AdListBean> beans = this.control.getEventMerge();
						AdListBean bean = new AdListBean(
								layerType.getType(),
								YumiConstants.ACTION_REPORT_EXPOSURE,
								LayerErrorCode.CODE_SUCCESS.getCode(),
								geReqType(),
								pid,
								getProvider().getProviderID(),
								getProvider().getKeyID(),
								null,
								getContainerXYWH(),
								templateID);
						beans.add(bean);
						AdListBean bean2 = new AdListBean(
								layerType.getType(),
								YumiConstants.ACTION_REPORT_ROUND,
								LayerErrorCode.CODE_SUCCESS.getCode(),
								geReqType(),
								pid,
								getProvider().getProviderID(),
								getProvider().getKeyID(),
								null);
						beans.add(bean2);

                        YumiIntentSender.reportEvent(mContext, invariantProvider, YumiConstants.ACTION_REPORT_EXPOSURE, layerType, LayerErrorCode.CODE_SUCCESS, rid, pid, control.getConfigResult()
                                .getTrans(), beans);
                        beans.clear();
                    } else if (layerType == LayerType.TYPE_INTERSTITIAL) {
						AdListBean bean = new AdListBean(
								layerType.getType(),
								YumiConstants.ACTION_REPORT_EXPOSURE,
								LayerErrorCode.CODE_SUCCESS.getCode(),
								geReqType(),
								pid,
								getProvider().getProviderID(),
								getProvider().getKeyID(),
								null,
								null,
								templateID);
	                       List<AdListBean> beans = new ArrayList<>();
                        beans.add(bean);
                        YumiIntentSender.reportEvent(mContext, invariantProvider, YumiConstants.ACTION_REPORT_EXPOSURE, layerType, LayerErrorCode.CODE_SUCCESS, rid, pid, control.getConfigResult()
                                .getTrans(), beans);
                    }
                }
				if (needCallbackInnerListener)
				{
					mInnerListener.onLayerExposure(mProvider, layerType);
					//调试模式记录该平台成功展示过广告
					if (mProvider.getReqType() == 1 && !"yumimobi".equals(mProvider.getProviderName())) {
					   MediationStatus.addPreparedAdpaterList(mProvider.getProviderName());
					}
				}
			}
		}
	}

	final void layerClosed(LayerType layerType)
	{
		synchronized (mInnerListener)
		{
            ZplayDebug.d(TAG, "Thread id " + Thread.currentThread().getId() + " " + mProvider.getProviderName() + " layerClosed ", onoff);
            mInnerListener.onLayerClosed(mProvider, layerType);
		}
	}

	// TODO 需要合并上报项
	final void layerPrepared(LayerType layerType, boolean isNewRound)
	{
	    XiaoMiSDKUtil.trackFetched();
		cancelNonResponseHandler();
		synchronized (mInnerListener)
		{
			if (!isOutTime)
			{
                ZplayDebug.d(TAG, "Thread id " + Thread.currentThread().getId() + " " + mProvider.getProviderName() + " layerPrepared ", onoff);
                failedTimes = 0;
                if (!isMediation) {
                    if (layerType == LayerType.TYPE_BANNER) {
                        List<AdListBean> beans = this.control.getEventMerge();
                        AdListBean bean = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_RESPONSE, LayerErrorCode.CODE_SUCCESS.getCode(), geReqType(), pid, getProvider()
                                .getProviderID(), getProvider().getKeyID(), null);
                        beans.add(bean);
                    } else if (layerType == LayerType.TYPE_INTERSTITIAL) {
                        List<AdListBean> beans = new ArrayList<>();
                        AdListBean bean = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_RESPONSE, LayerErrorCode.CODE_SUCCESS.getCode(), geReqType(), pid, getProvider()
                                .getProviderID(), getProvider().getKeyID(), null);
                        beans.add(bean);
                        AdListBean bean2 = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_ROUND, LayerErrorCode.CODE_SUCCESS.getCode(), geReqType(), pid, getProvider()
                                .getProviderID(), getProvider().getKeyID(), null);
                        beans.add(bean2);

                        YumiIntentSender.reportEvent(mContext, invariantProvider, YumiConstants.ACTION_REPORT_RESPONSE, layerType, LayerErrorCode.CODE_SUCCESS, rid, pid, control.getConfigResult()
                                .getTrans(), beans);
                        // YumiIntentSender.reportEvent(mContext, invariantProvider,
                        // YumiConstants.ACTION_REPORT_ROUND, layerType,
                        // LayerErrorCode.CODE_SUCCESS, rid, "", null);
                    }
                }
                if (needCallbackInnerListener && isNewRound) {
                    mInnerListener.onLayerPrepared(mProvider, layerType);
                }
			}
		}
	}
	
	private String geReqType()
	{
		int reqType = getProvider().getReqType();
		String type = "SDK";
		if (reqType == 2)
		{
			type = "API";
		}
		return type;
	}

	// TODO 需要合并上报项
	final void layerPreparedFailed(LayerType layerType, LayerErrorCode error, boolean isNewRound)
	{
		cancelNonResponseHandler();
		synchronized (mInnerListener)
		{
			ZplayDebug.d(TAG, "Thread id " + Thread.currentThread().getId() + " " + mProvider.getProviderName()
			        + " layerPreparedFailed " + error.getMsg(), onoff);
			if (error != LayerErrorCode.ERROR_INVALID_NETWORK)
			{
				if (errerNeedReport(error) && !isMediation)
				{
					if (layerType == LayerType.TYPE_BANNER)
					{
						List<AdListBean> beans = this.control.getEventMerge();
						AdListBean bean = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_RESPONSE,
						        error.getCode(), geReqType(), pid, getProvider().getProviderID(),
						        getProvider().getKeyID(), null);
						beans.add(bean);
					}  else if (layerType == LayerType.TYPE_INTERSTITIAL)
	                {
						List<AdListBean> beans = new ArrayList<>();
						AdListBean bean = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_RESPONSE,
						        error.getCode(), geReqType(), pid, getProvider().getProviderID(),
						        getProvider().getKeyID(), null);
						beans.add(bean);
						YumiIntentSender.reportEvent(mContext, invariantProvider, YumiConstants.ACTION_REPORT_RESPONSE,
						        layerType, error, rid, pid, control.getConfigResult().getTrans(), beans);
					}
				}
				if (needCallbackInnerListener && isNewRound)
				{
					failedTimes++;
					mInnerListener.onLayerPreparedFailed(mProvider, layerType, error);
				} else
				{
					Log.e("mikoto", "not new round");
				}
			} else
			{
				ZplayDebug.w(TAG, "no network", onoff);
			}
		}
        if (layerType != LayerType.TYPE_MEDIA) {
            removeYumiInnerListener();
        }
	}
	
    final void layerResponseReport(LayerType layerType,LayerErrorCode reeorCode) {
        if (!isMediation) {
            List<AdListBean> beans = new ArrayList<>();
            AdListBean bean = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_RESPONSE, reeorCode.getCode(), geReqType(), pid, getProvider().getProviderID(), getProvider().getKeyID(),
                    null);
            beans.add(bean);
            YumiIntentSender.reportEvent(mContext, invariantProvider, YumiConstants.ACTION_REPORT_RESPONSE, layerType, LayerErrorCode.CODE_SUCCESS, rid, pid, control.getConfigResult().getTrans(),
                    beans);
        }
    }
    
    final void layerStartReport(LayerType layerType) {
        if (!isMediation) {
            List<AdListBean> beans = new ArrayList<>();
            AdListBean bean = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_START, LayerErrorCode.CODE_SUCCESS.getCode(), geReqType(), pid, getProvider().getProviderID(),
                    getProvider().getKeyID(), null);
            beans.add(bean);
            YumiIntentSender.reportEvent(mContext, invariantProvider, YumiConstants.ACTION_REPORT_START, layerType, LayerErrorCode.CODE_SUCCESS, rid, pid, control.getConfigResult().getTrans(), beans);
        }
    }
    
    final void layerEndReport(LayerType layerType) {
        if (!isMediation) {
            List<AdListBean> beans = new ArrayList<>();
            AdListBean bean = new AdListBean(layerType.getType(), YumiConstants.ACTION_REPORT_END, LayerErrorCode.CODE_SUCCESS.getCode(), geReqType(), pid, getProvider().getProviderID(),
                    getProvider().getKeyID(), null);
            beans.add(bean);
            YumiIntentSender.reportEvent(mContext, invariantProvider, YumiConstants.ACTION_REPORT_END, layerType, LayerErrorCode.CODE_SUCCESS, rid, pid, control.getConfigResult().getTrans(), beans);
        }
    }
    
	private final boolean errerNeedReport(LayerErrorCode error)
	{
		if (error == LayerErrorCode.ERROR_INTERNAL || error == LayerErrorCode.ERROR_INVALID
		        || error == LayerErrorCode.ERROR_NO_FILL || error == LayerErrorCode.ERROR_NETWORK_ERROR
		        || error == LayerErrorCode.ERROR_OVER_RETRY_LIMIT || error == LayerErrorCode.ERROR_NON_RESPONSE)
		{
			return true;
		}
		return false;
	}

	final void notInActivityRound()
	{
		ZplayDebug.d(TAG, mProvider.getProviderName() + " finished activity round , and remove innerListener", onoff);
		removeYumiInnerListener();
	}

	void onDestroy()
	{
		cancelNonResponseHandler();
	}

	/**
	 * <p>
	 * Invoke when the implement instance created.
	 * <p>
	 * Override this method to init customer ad SDK, like create adEventListner
	 * or something of kind.
	 */
	protected abstract void init();

	/**
	 * <p>
	 * Release customer reference , invoke by onActivityDestroy
	 */
	protected abstract void callOnActivityDestroy();

	protected abstract void onRequestNonResponse();

	public final void setRID(String rid)
	{
		this.rid = rid;
		this.pid = YumiSignUtils.getTrackerID(getContext(), "p");
		ZplayDebug.d(TAG, "provider update tracker id " + this.pid, onoff);
	}

	@Override
	public final void onActivityDestroy()
	{
		callOnActivityDestroy();
		onDestroy();
	}
	
	
	/**
     * 获取父容器XY轴坐标和真实宽高
     * @return
     * 返回数组顺序：X 轴坐标，Y 轴坐标，宽度，高度
     */
    private LPArea getContainerXYWH() {
        try {
            int ContainerWidth = 0;
            int ContainerHeight = 0;
            final int[] location = new int[2];
            if (developerContainer != null) {
                developerContainer.getLocationOnScreen(location);
                ContainerWidth = developerContainer.getWidth();
                ContainerHeight = developerContainer.getHeight();
            }
            ZplayDebug.d(TAG, "getContainerXYWH developerCntainer  X_index:"+location[0]+"   Y_index:"+location[1]+"  ContainerWidth:"+ContainerWidth+"  ContainerHeight:"+ContainerHeight, onoff);
            
            return new LPArea(ContainerWidth+"",ContainerHeight+"",location[0]+"",location[1]+"");
        } catch (Exception e) {
            ZplayDebug.e(TAG, "getCntainerXYWH error :", e, onoff);
        }
        return null;
    }
    
    public String getPid() {
        return pid;
    }
}
