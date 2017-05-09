package com.yumi.android.sdk.ads.utils;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import  com.yumi.android.sdk.ads.self.ui.MyExplorer;
import  com.yumi.android.sdk.ads.self.ui.MyExplorer.MyWebViewClient;
import  com.yumi.android.sdk.ads.self.ui.MyExplorer.OnHandleClose;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.observer.DownloadObserver;
import com.yumi.android.sdk.ads.observer.DownloadWatched;
import com.yumi.android.sdk.ads.receiver.DownloadReceiver;

/**
 * 内部浏览器
 * 
 * @author hjl
 * 
 */
public class YumiInlayBrowserUtils {

	private final static String TAG = "YumiInlayBrowserUtils";
	private final static boolean onoff = true;
	private static MyExplorer webView;
	private static Activity mContext;
	private static Dialog dialog;

	private static DownloadReceiver receiver;
	private static DownloadObserver observer;
	private static final DownloadWatched watched = new DownloadWatched();

	private static boolean hasRegister = false;

	/**
	 * 打开内置浏览器
	 * 
	 * @param context
	 * @param url
	 * @param onDismissListener
	 */
	public static void openBrowser(final Activity context, String url,
			OnDismissListener onDismissListener) {
		mContext = context;
		dialog = buildFullDialog(context);

		FrameLayout contentView = new FrameLayout(context);
		webView = new MyExplorer(context);

		FrameLayout.LayoutParams params_web = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(webView, params_web);

		dialog.setContentView(contentView);
		if (onDismissListener != null) {
			dialog.setOnDismissListener(onDismissListener);
		}
		webView.setWebViewClient(new MyWebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//支持deepLink
				if (DeepLinkUtils.isDeepLink(url)) {
					final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					Log.e("sss", "DeepLinkUtils.deviceCanHandleIntent = "+DeepLinkUtils.deviceCanHandleIntent(context, intent));
					if (DeepLinkUtils.deviceCanHandleIntent(context, intent)) {
						Log.e("sss", "deeplink = 1");
						context.startActivity(intent);
					}
					return true;
				} else {
//					return super.shouldOverrideUrlLoading(view, url);
					view.loadUrl(url);
					return true;
				}
			}

			@Override
			public void onScaleChanged(WebView arg0, float arg1, float arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReceivedSslError(WebView arg0, SslErrorHandler arg1,
					SslError arg2) {
//			    final SslErrorHandler sslHadnler = arg1;
//			    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                String message = "SSL Certificate error.";
//                    switch (arg2.getPrimaryError()) {
//                        case SslError.SSL_UNTRUSTED:
//                            message = "The certificate authority is not trusted.";
//                            break;
//                        case SslError.SSL_EXPIRED:
//                            message = "The certificate has expired.";
//                            break;
//                        case SslError.SSL_IDMISMATCH:
//                            message = "The certificate Hostname mismatch.";
//                            break;
//                        case SslError.SSL_NOTYETVALID:
//                            message = "The certificate is not yet valid.";
//                            break;
//                    }
//                    message += " Do you want to continue anyway?";
//
//                    builder.setTitle("SSL Certificate Error");
//                    builder.setMessage(message);
//                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        sslHadnler.proceed();
//                    }
//                });
//                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        sslHadnler.cancel();
//                    }
//                });
//                final AlertDialog dialog = builder.create();
//                dialog.show();
			}

			@Override
			public void onReceivedHttpAuthRequest(WebView arg0,
					HttpAuthHandler arg1, String arg2, String arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReceivedError(WebView arg0, int arg1, String arg2,
					String arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageStarted(WebView arg0, String arg1, Bitmap arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageFinished(WebView arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadResource(WebView arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFormResubmission(WebView arg0, Message arg1,
					Message arg2) {
				// TODO Auto-generated method stub

			}
		});
		webView.setmOnHandleClose(new OnHandleClose() {
			@Override
			public void onCloseClick() {
				dialogDestroy();
			}
		});
		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				requestDownloadManager(mContext, url);
				Intent downloadBrocast = new Intent();
				downloadBrocast.setAction(YumiConstants.ACTION_DOWNLOAD_BEGIN);
				mContext.sendBroadcast(downloadBrocast);
				dialogDestroy();
			}
		});
		webView.loadUrl(url);
		registerObserver();
		dialog.show();
	}

	/**
	 * 构建dialog
	 * 
	 * @param activity
	 * @return
	 */
	public static Dialog buildFullDialog(Activity activity) {
		boolean isfullScreen = isFullScreen(activity);
		Dialog dialog = null;
		if (isfullScreen) {
			dialog = new Dialog(activity,
					android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		} else {
			dialog = new Dialog(activity,
					android.R.style.Theme_Translucent_NoTitleBar);
		}
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	public static boolean isFullScreen(Activity activity) {
		return ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0);
	}

	/**
	 * 处理下载请求
	 * 
	 * @param context
	 * @param url
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	private static void requestDownloadManager(Activity context, String url) {
		DownloadManager dm = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		Request request = new Request(Uri.parse(url));
		request.setVisibleInDownloadsUi(true);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				File externalFilesDir = context
						.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
				String path = url.substring(url.lastIndexOf("/") + 1);
				if (path.contains("?")) {
					path = path.substring(0, path.indexOf("?"));
				}
				if (externalFilesDir == null) {
					ZplayDebug.w(TAG, "externalFilesDir is null", onoff);
					File externalStorageDirectory = Environment
							.getExternalStorageDirectory();
					if (externalStorageDirectory != null) {
						File file = new File(externalStorageDirectory,
								Environment.DIRECTORY_DOWNLOADS);
						file.mkdirs();
						if (file != null) {
							// request.setDestinationInExternalFilesDir(getApplicationContext(),
							// file.getAbsolutePath(), path);
							request.setDestinationUri(Uri.fromFile(file));
						}
					} else {
						ZplayDebug.w(TAG, "Environment dir is null", onoff);
					}
				} else {
					request.setDestinationInExternalFilesDir(
							context.getApplicationContext(),
							Environment.DIRECTORY_DOWNLOADS, path);
				}
				if (android.os.Build.VERSION.SDK_INT > 11) {
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				} else {
					request.setShowRunningNotification(true);
				}
				dm.enqueue(request);
			} catch (Exception e) {
				ZplayDebug.e(TAG, "", e, onoff);
			}
		} else {
			ZplayDebug.w(TAG, "external storage is invalid", onoff);
		}
	}

	/**
	 * 下载完成广播接受者
	 */
	private static void registerObserver() {
		if (observer == null) {
			observer = new DownloadObserver() {
				@Override
				public void onDownloadComplete(String path) {
				}

				@Override
				public void onDownload() {
				}
			};
			ZplayDebug.i(TAG, "build new observer and register to watched ",
					onoff);
			watched.registerObserver(observer);
			ZplayDebug.i(TAG, "register download receiver", onoff);
			receiver = new DownloadReceiver(watched);
			YumiReceiverUtils.registerDownloadReceiver(mContext, receiver);
			hasRegister = true;
		}
	}

	/**
	 * 销毁
	 */
	private static void dialogDestroy() {
		if (webView != null) {
			webView.destroy();
		}
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	/**
	 * 生命周期方法destroy中调用（重要）
	 */
	public static void callOnActivityDestroy() {
		try {
			dialogDestroy();
			if (hasRegister && receiver != null) {
				YumiReceiverUtils.unregisterReceiver(mContext, receiver);
				hasRegister = false;
			}
			ZplayDebug.i(TAG, "unregister download receiver", onoff);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
