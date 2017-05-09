package com.yumi.android.sdk.ads.utils.network;
/**
 * 保存程序运行期间需要的配置信息
 * 
 * @author laohuai
 * 
 */
public final class NetworkConfig {
	// 默认的编码方式
	private static String defaultCharset = "utf-8";
	// 超时时间设置
	private static int connectTimeout = 30000;
	// 读取超时
	private static int readTimeout = 30000;

//	private static boolean isVVisible = true;
//	private static boolean isDVisible = true;
//	private static boolean isIVisible = true;
//	private static boolean isWVisible = true;
//	private static boolean isEVisible = true;
//
//	private static String defaultLogDir = "/zplay/sdk/log/";
//
//	private static String defaultFailedJson = "{}";
//
//	private static String defaultSPFileName = "com.e7studio.android.e7appsdk";

	private static boolean isShouldPostOrNotInDefaultState = true;

	// 在使用WebTaskHandler执行网络请求时候是否展示loading提示
	private static boolean isLoadingTipsShow = true;
	// 默认的loading提示内容
//	private static String defaultLoadingTipsResName = "app_name";
	// loading提示框是否可以被取消
	private static boolean isLoadingDialogCancelable = true;
	// loading对话框取消掉之后，是否需要取消掉WebTaskHandler的执行
	private static boolean isExecuteCancelWhenLoadingDialogCanceled = true;

	public static void setDefaultCharset(String defaultCharset) {
		NetworkConfig.defaultCharset = defaultCharset;
	}

	public static String getDefaultCharset() {
		return defaultCharset;
	}

	public static void setDefaultConnectTimeOut(int connectTimeOut) {
		NetworkConfig.connectTimeout = connectTimeOut;
	}

	public static int getDefaultConnectTimeOut() {
		return connectTimeout;
	}

	public static void setReadTimeOut(int readTimeout) {
		NetworkConfig.readTimeout = readTimeout;
	}

	public static int getReadTimeOut() {
		return NetworkConfig.readTimeout;
	}

//	public static void setVVisible(boolean isVVisible) {
//		APPConfig.isVVisible = isVVisible;
//	}
//
//	public static boolean isVVisible() {
//		return APPConfig.isVVisible;
//	}

//	public static void setDVisible(boolean isDVisible) {
//		APPConfig.isDVisible = isDVisible;
//	}
//
//	public static boolean isDVisible() {
//		return APPConfig.isDVisible;
//	}

//	public static void setIVisible(boolean isIVisible) {
//		APPConfig.isIVisible = isIVisible;
//	}
//
//	public static boolean isIVisible() {
//		return APPConfig.isIVisible;
//	}

//	public static void setWVisible(boolean isWVisible) {
//		APPConfig.isWVisible = isWVisible;
//	}
//
//	public static boolean isWVisible() {
//		return APPConfig.isWVisible;
//	}

//	public static void setEVisible(boolean isEVisible) {
//		APPConfig.isEVisible = isEVisible;
//	}
//
//	public static boolean isEVisible() {
//		return APPConfig.isEVisible;
//	}

//	public static void setLogDir(String logDir) {
//		APPConfig.defaultLogDir = logDir;
//	}
//
//	public static String getLogDir() {
//		return APPConfig.defaultLogDir;
//	}

//	public static void setDefaultFailedJSON(String jsonString) {
//		APPConfig.defaultFailedJson = jsonString;
//	}
//
//	public static String getDefaultFailedJSON() {
//		return APPConfig.defaultFailedJson;
//	}

//	public static void setDefaultSPFileName(String spFileName) {
//		APPConfig.defaultSPFileName = spFileName;
//	}
//
//	public static String getDefaultSPFileName() {
//		return APPConfig.defaultSPFileName;
//	}

	public static void setWebAccessMethod(boolean isByPost) {
		NetworkConfig.isShouldPostOrNotInDefaultState = isByPost;
	}

	public static boolean getWebAccessMethodInDefaultState() {
		return NetworkConfig.isShouldPostOrNotInDefaultState;
	}

	public static void setLoadingTipsShouldShowOrNotInDefaultState(
			boolean isLodingTipsShouldShowOrNot) {
		NetworkConfig.isLoadingTipsShow = isLodingTipsShouldShowOrNot;
	}

	public static boolean isLoadingTipsShouldShowInDefaultState() {
		return NetworkConfig.isLoadingTipsShow;
	}

//	public static void setDefaultLoadingTipsResName(String loadingTipsResName) {
//		NetworkConfig.defaultLoadingTipsResName = loadingTipsResName;
//	}
//
//	public static String getDefaultLoadingTipsResName() {
//		return NetworkConfig.defaultLoadingTipsResName;
//	}

	public static void setIsLoadingDialogCancelable(
			boolean isLoadingDialogCancelable) {
		NetworkConfig.isLoadingDialogCancelable = isLoadingDialogCancelable;
	}

	public static boolean isLoadingDialogCancelable() {
		return NetworkConfig.isLoadingDialogCancelable;
	}

	public static void setIsExecuteCancelWhenDialogCanceled(
			boolean isShouldCancelOrNot) {
		NetworkConfig.isExecuteCancelWhenLoadingDialogCanceled = isShouldCancelOrNot;
	}

	public static boolean isExecuteCancelWhenLoadingDialogCanceled() {
		return NetworkConfig.isExecuteCancelWhenLoadingDialogCanceled;
	}

}
