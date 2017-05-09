package com.yumi.android.sdk.ads.listener;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;

public interface IYumiInnerLayerStatusListener {

	public void onLayerPrepared(YumiProviderBean provider, LayerType type);
	public void onLayerPreparedFailed(YumiProviderBean provider, LayerType type, LayerErrorCode error);
	public void onLayerExposure(YumiProviderBean provider, LayerType type);
	public void onLayerCLicked(YumiProviderBean provider, LayerType type, float x, float y);
	public void onLayerClosed(YumiProviderBean provider, LayerType type);
}
