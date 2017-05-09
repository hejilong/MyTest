package com.yumi.android.sdk.ads.ensure;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.selfmedia.ads.media.MediaAD;
import com.yumi.android.sdk.ads.selfmedia.ads.media.MediaADListener;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class YumiMediaAdapter extends YumiCustomerMediaAdapter
{

	private static final String TAG = "YumiMediaAdapter";
	private MediaAD mediaAD;

    private static final int RETRY_REQUEST = 0x001;
    private static final int RETRY_REQUEST_TIMES=30*1000;
	
    public YumiMediaAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener)
	{
		super(activity, provider);
		mInnerListener = innerListener;
	}
	
    private final Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case RETRY_REQUEST:
                if (mediaAD != null) {
                    mediaAD.requestMedia();
                    ZplayDebug.v(TAG, "Yumi mediaAD is RETRY_REQUEST", onoff);
                }
                break;
            default:
                break;
            }
        };
    };

	@Override
	public void onActivityPause()
	{
//		if (mediaAD!=null)
//		{
//			mediaAD.pause();
//		}
	}

	@Override
	public void onActivityResume()
	{
//		if (mediaAD!=null)
//		{
//			mediaAD.resume();
//		}
	}

	@Override
	protected void onPrepareMedia()
	{
		if (mediaAD!=null)
		{
			mediaAD.requestMedia();
		}
	}

	@Override
	protected void onShowMedia()
	{
		if (mediaAD!=null)
		{
			mediaAD.showMedia();
		}
	}

	@Override
	protected boolean isMediaReady()
	{
		if (mediaAD!=null)
		{
			return mediaAD.isReady();
		}
		return false;
	}

	@Override
	public void init()
	{
		ZplayDebug.i(TAG, "appkey : " + getProvider().getKey1(), onoff);
		ZplayDebug.i(TAG, "locationID : " + getProvider().getKey2(), onoff);
		ZplayAdExtra.getHolder().initSelfMediaSDK(getActivity(), getProvider().getKey1(), getProvider().getKey2());
		mediaAD = new MediaAD(getActivity(), getProvider().getKey2(),  getProvider().getKey1(), new MediaADListener()
		{
			
			@Override
			public void onMediaReward(String arg0, String arg1, String arg2)
			{
                ZplayDebug.v(TAG, "Yumi mediaAD is onMediaReward ", onoff);
			}
			
			@Override
			public void onMediaRequestFailed(String arg0)
			{
                ZplayDebug.v(TAG, "Yumi mediaAD is onMediaRequestFailed "+arg0, onoff);
				layerPreparedFailed(getErrorCode(arg0));
				if(!mHandler.hasMessages(RETRY_REQUEST))
				{
			    	mHandler.sendEmptyMessageDelayed(RETRY_REQUEST, RETRY_REQUEST_TIMES);
				}
			}
			
			@Override
			public void onMediaRequest(String arg0)
			{
                ZplayDebug.v(TAG, "Yumi mediaAD is onMediaRequest "+arg0, onoff);
			}
			
			@Override
			public void onMediaPlay()
			{
                ZplayDebug.v(TAG, "Yumi mediaAD is onMediaPlay ", onoff);
				layerExposure();
                layerMediaStart();
                mHandler.sendEmptyMessage(RETRY_REQUEST);
			}
			
			@Override
			public void onMediaDismiss(String msg)
			{
                ZplayDebug.v(TAG, "Yumi mediaAD is onMediaDismiss ", onoff);
				layerClosed();
                layerMediaEnd();
                layerIncentived();
			}

			@Override
			public void onMediaDownload()
			{
                ZplayDebug.v(TAG, "Yumi mediaAD is onMediaDownload ", onoff);
				downloadMedia();
			}

			@Override
			public void onMediaPageClick()
			{
                ZplayDebug.v(TAG, "Yumi mediaAD is onMediaPageClick ", onoff);
				layerClicked();
			}

            @Override
            public void onMediaPrepared() {
                ZplayDebug.v(TAG, "Yumi mediaAD is onMediaPrepared ", onoff);
                layerPrepared();
            }
		});
	}

	@Override
	protected void callOnActivityDestroy()
	{
		if (mediaAD!=null)
		{
			mediaAD.destroy();
		}
	}

	private LayerErrorCode getErrorCode(String errorMsg)
    {
        if ("无填充".equals(errorMsg)) {
            return LayerErrorCode.ERROR_NO_FILL;
        }else{
            return LayerErrorCode.ERROR_INTERNAL;
        }
    }
	
}
