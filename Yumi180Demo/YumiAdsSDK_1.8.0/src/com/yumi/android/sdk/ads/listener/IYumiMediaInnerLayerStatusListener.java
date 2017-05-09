package com.yumi.android.sdk.ads.listener;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;

public interface IYumiMediaInnerLayerStatusListener extends
		IYumiInnerLayerStatusListener {
	public void onLayerIncentived(YumiProviderBean provider, LayerType type);
	public void onLayerCanGetReward(YumiProviderBean provider, LayerType type, int remain);
	public void onLayoutDownload();
}
