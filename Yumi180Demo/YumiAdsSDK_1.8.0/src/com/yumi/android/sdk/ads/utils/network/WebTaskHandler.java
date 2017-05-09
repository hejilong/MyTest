package com.yumi.android.sdk.ads.utils.network;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.network.WebMethodHandler.ResultObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;


public final class WebTaskHandler extends
		AsyncTask<Map<String, Object>, Integer, Map<String, Object>> {

//	private final static String TAG = "WebTaskHandler";
	private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(20);
	
	private Context context;
	private WebTask webTask;
	private boolean isByPost = NetworkConfig.getWebAccessMethodInDefaultState();
	private boolean isLoadingDialogShow = NetworkConfig
			.isLoadingTipsShouldShowInDefaultState();
//	private String loadingTipsResName = NetworkConfig
//			.getDefaultLoadingTipsResName();
	private ProgressDialog loadingDialog = null;
	private boolean isLoadingDialogCancelable = NetworkConfig
			.isLoadingDialogCancelable();
	private boolean isTaskCancelWhenDialogCancel = NetworkConfig
			.isExecuteCancelWhenLoadingDialogCanceled();

	private String charset = "UTF-8";

	private Map<String, String> headers;

	public WebTaskHandler(Context context, WebTask webTask) {
		this.context = context;
		this.webTask = webTask;
	}

	public WebTaskHandler(Context context, WebTask webTask, boolean isByPost, boolean isLoadingDialogShow){
		this.context = context;
		this.webTask = webTask;
		this.isByPost = isByPost;
		this.isLoadingDialogShow = isLoadingDialogShow;
	}
	
	public WebTaskHandler(Context context, WebTask webTask, boolean isByPost, boolean isLoadingDialogShow, String charset){
		this.context = context;
		this.webTask = webTask;
		this.isByPost = isByPost;
		this.isLoadingDialogShow = isLoadingDialogShow;
		this.charset  = charset;
	}
	
	public WebTaskHandler(Context context, WebTask webTask, boolean isByPost,
			boolean isLoadingDialogShow, String loadingTipsResName,
			boolean isLoadingDialogCancelable,
			boolean isTaskCancelWhenDialogCancel) {
		this.context = context;
		this.webTask = webTask;

		this.isByPost = isByPost;
		this.isLoadingDialogShow = isLoadingDialogShow;
		this.isLoadingDialogCancelable = isLoadingDialogCancelable;
//		this.loadingTipsResName = loadingTipsResName;
		this.isTaskCancelWhenDialogCancel = isTaskCancelWhenDialogCancel;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		ZplayDebug.V(TAG, "task信息：[post方式请求:" + isByPost + ",是否展示loading:"
//				+ isLoadingDialogShow + ",loading是否可以取消："
//				+ isLoadingDialogCancelable + ",loading取消之后是否停止execute："
//				+ isTaskCancelWhenDialogCancel + ",loadingTipsResName:"
//				+ loadingTipsResName + "]");
		if (isLoadingDialogShow && context instanceof Activity) {
			loadingDialog = new ProgressDialog(context);
			String loadingTips = "数据加载中...";
			loadingDialog.setCanceledOnTouchOutside(false);
			loadingDialog.setCancelable(isLoadingDialogCancelable);
			loadingDialog.setMessage(loadingTips);

			if (isTaskCancelWhenDialogCancel) {
				loadingDialog.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						WebTaskHandler.this.cancel(true);
					}
				});
			}
			loadingDialog.show();
		}
	}

	protected Map<String, Object> doInBackground(Map<String, Object>... params) {
		Map<String, Object> urlParamsMap = params[0];
		ResultObject resultObject = null;
		// 从map中获取接口地址以及参数
		String url = (String) urlParamsMap.get("url");
		String[] keys = (String[]) urlParamsMap.get("keys");
		String[] values = (String[]) urlParamsMap.get("values");
		String value = (String) urlParamsMap.get("value");
		int type = (Integer) urlParamsMap.get("type");

		if (isByPost) {
			if (type == WebParamsMapBuilder.SINGLE_VALUE) {
				resultObject = WebMethodHandler.accessWebByPost(context, url,
						value, charset, headers);
			}
			if (type == WebParamsMapBuilder.KEY_VALUE_PAIR) {
				resultObject = WebMethodHandler.accessWebByPost(context, url,
						keys, values, charset, headers);
			}
		} else {
			resultObject = WebMethodHandler.accessWebByGet(context, url, keys,
					values, charset, headers);
		}
		// 状态
		int status = resultObject.getStatus();
		String data = resultObject.getData();
		String errorMsg = resultObject.getErrorMsg();

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", status);
		if (data != null) {
			result.put("data", data);
		}
		result.put("msg", errorMsg);
		return result;
	}

	@Override
	protected void onPostExecute(Map<String, Object> result) {
		super.onPostExecute(result);
		// dismiss掉dialog，如果有dialog正在展示
		if (isLoadingDialogShow && loadingDialog != null
				&& loadingDialog.isShowing()) {
			loadingDialog.dismiss();
			loadingDialog = null;
		}
		String data = (String) result.get("data");
		String errorMsg = (String) result.get("msg");
		if (webTask != null) {
//			ZplayDebug.V(TAG, "resoponse is " + data  + " / " + errorMsg);
			webTask.doTask(data, errorMsg);
		}
	}
	
	public void setHeaders(Map<String, String> headers){
		this.headers = headers;
	}
	
	public void cancelTask(){
		if (getStatus() != Status.FINISHED) {
			cancel(true);
		}
	}
	
	public  AsyncTask<Map<String, Object>, Integer, Map<String, Object>> executeOnPool(Map<String, Object>... params){
		if (PhoneInfoGetter.getAndroidSDK() >= 11) {
			return executeOnExecutor(THREAD_POOL, params);
		}else {
			return execute(params);
		}
	}
}