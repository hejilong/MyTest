package com.yumi.android.sdk.ads.constants;

import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * 动态加载适配器列表
 * 
 * @author hjl
 * 
 */
public class AdapterLoaderList {

	private static final String TAG = "YumiBaseAdapterFactory";
	private static final String[] mJarNames = new String[] {
			"zplay_adapter_q.jar", "zplay_adapter_ab.jar","zplay_adapter_q.jar" };
	private static final String[] mAdapterNames = new String[] { "gdtmob",
			"admob","gdtnative" };

	/**
	 * 返回三方名称对应适配器jar包文件名
	 * 
	 * @param providerName
	 * @return
	 */
	public static String getJarName(String providerName) {
		try {
			if (providerName == null || providerName.length() == 0) {
				return null;
			}
			for (int i = 0; i < mAdapterNames.length; i++) {
				if (providerName.equals(mAdapterNames[i])) {
					return mJarNames[i];
				}
			}
		} catch (Exception e) {
			ZplayDebug.e(TAG, "get AdapterLoaderList error : " + e, true);
		}
		return null;
	}
}
