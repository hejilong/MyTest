package com.yumi.android.sdk.ads.activity;


import java.io.File;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.utils.DeepLinkUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;

public final class YumiBrowserActivity extends Activity {

	private static final String TAG = "WebActivity";
	private WebView web;
	private ProgressBar progress;
	private boolean jump;
	private boolean onoff = true;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		String stringExtra = getIntent().getStringExtra("url");
		jump = getIntent().getBooleanExtra("302", true);
		progress = new ProgressBar(this);	
		web = new WebView(this);
		int[] display = PhoneInfoGetter.getDisplayMetrics(this);
		web.setLayoutParams(new LayoutParams(display[0], display[1]));
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		web.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		web.setDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype, long contentLength) {
				ZplayDebug.i(TAG, "webactivity download", onoff);
				//TODO google play 去权限版判断   下载跳转系统浏览器
				try {
                    if (YumiConstants.IS_GOOGLEPLAY_VERSION) {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        YumiBrowserActivity.this.startActivity(intent);
                    } else {
                        requestDownloadManager(url);
                        Intent downloadBrocast = new Intent();
                        downloadBrocast.setAction(YumiConstants.ACTION_DOWNLOAD_BEGIN);
                        sendBroadcast(downloadBrocast);
                    }
				} catch (Exception e) {
					ZplayDebug.e(TAG, "", e, onoff);
				}
			}
		});
		web.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				ZplayDebug.d(TAG, "override url is " + url, onoff);
				//支持deepLink
				if (DeepLinkUtils.isDeepLink(url)) {
					final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					Log.e("sss", "DeepLinkUtils.deviceCanHandleIntent = "+DeepLinkUtils.deviceCanHandleIntent(getApplicationContext(), intent));
					if (DeepLinkUtils.deviceCanHandleIntent(getApplicationContext(), intent)) {
						Log.e("sss", "deeplink = 1");
						startActivity(intent);
					}
					return true;
				} else {
//					return super.shouldOverrideUrlLoading(view, url);
					view.loadUrl(url);
					return true;
				}
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				if (progress != null) {
					progress.setVisibility(View.GONE);
				}
				if (!jump) {
					finish();
				}
				super.onPageFinished(view, url);
			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				ZplayDebug.d(TAG, "page error " + description + " url " + failingUrl, onoff);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});
		web.loadUrl(stringExtra);
		addContentView(web, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		addContentView(progress, new FrameLayout.LayoutParams(100, 100, Gravity.CENTER));
		}
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	private void requestDownloadManager(String url) {
		DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		Request request = new Request(Uri.parse(url));
		request.setVisibleInDownloadsUi(true);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
			File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
			String path = url.substring(url.lastIndexOf("/")+1);
			if (path.contains("?")) {
				path = path.substring(0, path.indexOf("?"));
			}
			if (externalFilesDir == null) {
				ZplayDebug.w(TAG, "externalFilesDir is null", onoff);
				File externalStorageDirectory = Environment.getExternalStorageDirectory();
				if (externalStorageDirectory != null) {
					File file = new File(externalStorageDirectory, Environment.DIRECTORY_DOWNLOADS);
					file.mkdirs();
					if (file != null) {
//						request.setDestinationInExternalFilesDir(getApplicationContext(), file.getAbsolutePath(), path);
						request.setDestinationUri(Uri.fromFile(file));
					}
				}else {
					ZplayDebug.w(TAG, "Environment dir is null", onoff);
				}
			}else {
				request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, path);
			}
			if (android.os.Build.VERSION.SDK_INT >11) {
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			}else {
				request.setShowRunningNotification(true);
			}
				dm.enqueue(request);
			} catch (Exception e) {
				ZplayDebug.e(TAG, "", e, onoff);
			}
		}else {
			ZplayDebug.w(TAG, "external storage is invalid", onoff);
		}
	}
	

	@Override
	protected final void onDestroy() {
		ZplayDebug.v(TAG, "activity destroy", onoff);
		if (web != null) {
			ViewGroup parent = (ViewGroup) web.getParent();
			parent.removeAllViews();
			web.destroy();
		}
		super.onDestroy();
	}
}
