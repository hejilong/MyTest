package com.yumi.android.sdk.ads.publish;

public class YumiDebug {

	private static boolean DEBUG = false;
	
	public static void runInDebugMode(boolean flag){
		YumiDebug.DEBUG = flag;
		com.yumi.android.sdk.ads.self.utils.ZplayDebug.setDebugMode(flag);
		com.yumi.android.sdk.ads.selfmedia.utils.ZplayDebug.setDebugMode(flag);
	}
	
	public static boolean isDebugMode(){
		return DEBUG;
	}
}
