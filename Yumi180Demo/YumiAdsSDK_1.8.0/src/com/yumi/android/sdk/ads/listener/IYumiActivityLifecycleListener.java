package com.yumi.android.sdk.ads.listener;

public interface IYumiActivityLifecycleListener {

	/**
	 * <p> Same as Activity lifecycle method onPause().
	 */
	public void onActivityPause();

	/**
	 * <p> Same as Activity lifecycle method onResume().
	 */
	public void onActivityResume();

	/**
	 * <p> Same as Activity lifecycle method onDestry().
	 */	
	public void onActivityDestroy();
	
	/**
	 * <p> Same as Activity lifecycle method onBackPressed().
	 */
	public boolean onActivityBackPressed();
}
