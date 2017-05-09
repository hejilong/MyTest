package com.yumi.android.sdk.ads.listener;

import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

public interface IYumiAPIRequestListener {

	public void onAPIRequestDone(String data, LayerErrorCode error);
	
}
