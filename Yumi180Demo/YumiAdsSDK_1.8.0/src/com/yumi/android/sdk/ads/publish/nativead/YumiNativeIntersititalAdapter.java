package com.yumi.android.sdk.ads.publish.nativead;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.YumiBaseInterstitialLayer;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import  com.yumi.android.sdk.ads.self.ui.ResFactory;
import com.yumi.android.sdk.ads.utils.YumiIntentSender;
import com.yumi.android.sdk.ads.utils.YumiLayerSizeCalculater;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.utils.views.BorderWebView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

public abstract class YumiNativeIntersititalAdapter extends
		YumiBaseInterstitialLayer {

	public static final boolean onoff = true;
	
	private static final String TAG = "YumiWebInterstitialLayer";
	protected FrameLayout web;
	protected BorderWebView webview;
	protected float[] upPoint = new float[2];
	protected float[] downPoint = new float[2];
	protected Dialog interstitialDialog;
	protected boolean interstitialPrepareDone;
	private FrameLayout interstitialFrame;
	private FrameLayout interstitialBaseFrame;
	private int webWidth;
	private int webHeight;
	protected boolean instertitialPageError;
	protected boolean interstitialReady;
//	private Activity activity;
	private String aTagUrl = "http://com.yumi.native/click";
    //webView是否添加边框
	protected boolean haveStroke=true;

	public String getaTagUrl()
	{
		return aTagUrl;
	}


	public void setaTagUrl(String aTagUrl)
	{
		this.aTagUrl = aTagUrl;
	}


	protected YumiNativeIntersititalAdapter(Activity activity,
			YumiProviderBean provider) {
		super(activity, provider);
//		this.activity = activity;
	}


	@Override
	protected final void onShowInterstitialLayer(Activity activity) {
			createInterstitialDialog(activity);
			interstitialDialog.show();
			webLayerOnShow();
	}

	private void createInterstitialDialog(Activity activity) {
		if (interstitialDialog == null) {
			interstitialDialog = new Dialog(activity,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

            interstitialBaseFrame=new FrameLayout(activity);
            interstitialBaseFrame.addView(interstitialFrame, new FrameLayout.LayoutParams(webWidth, webHeight, Gravity.CENTER));
            interstitialBaseFrame.setBackgroundColor(Color.parseColor("#b2000000"));

            interstitialDialog.setContentView(interstitialBaseFrame, new FrameLayout.LayoutParams(android.widget.FrameLayout.LayoutParams.MATCH_PARENT, android.widget.FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
			interstitialDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					webLayerDismiss();
				}
			});
			
            ZplayDebug.i(TAG, "createInterstitialDialog  webWidth=" + webWidth + "|| webHeight= " + webHeight, onoff);
		}
	}

	@SuppressLint({ "SetJavaScriptEnabled", "RtlHardcoded" })
	protected final void createWebview(final OnClickListener onClick) {
	    web = new FrameLayout(getContext());
	    webview = new BorderWebView(getContext(),haveStroke) {

			@Override
			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
				setMeasuredDimension(webWidth, webHeight);
			}
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					downPoint[0] = event.getX();
					downPoint[1] = event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					upPoint[0] = event.getX();
					upPoint[1] = event.getY();
					if (onClick != null) {
						onClick.onClick(webview);
					}
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return false;
				}
				return super.onTouchEvent(event);
			}
		};
		
		webview.setLayoutParams(new FrameLayout.LayoutParams(webWidth, webHeight,
				Gravity.CENTER));
		webview.setBackgroundColor(Color.TRANSPARENT);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		webview.getSettings().setDefaultTextEncodingName("UTF-8");
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.setHorizontalScrollBarEnabled(false);
		webview.setVerticalScrollBarEnabled(false);
		webview.setWebViewClient(createViewClient());
		
		FrameLayout.LayoutParams paramns = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        web.addView(webview, paramns);
        
        ImageView iv_flag = new ImageView(getContext());
        Drawable drawable_flag = ResFactory.getDrawableByAssets("yumiad_flag", getContext());
        iv_flag.setImageDrawable(drawable_flag);
        FrameLayout.LayoutParams param_iv_flag = new FrameLayout.LayoutParams(
                WindowSizeUtils.dip2px(getContext(), 28), WindowSizeUtils.dip2px(getContext(), 14));
        param_iv_flag.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        web.addView(iv_flag, param_iv_flag);
	}

	protected final void addJSInterface(Object obj, String name){
		if (webview != null) {
		    webview.addJavascriptInterface(obj, name);
		}
	}
	
	protected final void calculateWebSize(int widthDip, int heightDip) {
		int[] calculateLayerSize = YumiLayerSizeCalculater.calculateLayerSize(getContext(), widthDip, heightDip);
		webWidth = calculateLayerSize[0];
		webHeight = calculateLayerSize[1];
	}
	
	protected final void calculateWebSizePx(int widthDip, int heightDip) {
        webWidth = widthDip;
        webHeight = heightDip;
    }
	
	protected final void calculateWebSize(int widthDip, int heightDip, float scale){
		int[] calculateLayerSize = YumiLayerSizeCalculater.calculateLayerSize(getContext(), widthDip, heightDip);
		webWidth = (int) (calculateLayerSize[0] * scale);
		webHeight = (int) (calculateLayerSize[1] * scale);
	}

	protected final void loadData(String html) {
		if (webview != null) {
		    webview.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
		}
	}
	
	protected final void requestWebActivity(String url, boolean jump){
		YumiIntentSender.requestWebActivity(getContext(), url, jump);
	}
	
	protected final void requestSystemBrowser(String url){
		YumiIntentSender.requestSystemBrowser(getContext(), url);
	}
	
	protected final void createInterstitialContentLayout(WebView view,
			boolean canManualCancel) {
		if (interstitialFrame == null) {
			interstitialFrame = new FrameLayout(getContext());
		}else {
			interstitialFrame.removeAllViews();
		}
		interstitialFrame.addView(web);
		if (canManualCancel) {
			addCancelBtn();
		}
	}
	
	protected final void closeOnResume() {
		if (interstitialDialog != null && interstitialDialog.isShowing()) {
			interstitialDialog.dismiss();
		}
	}
	
	@SuppressLint("RtlHardcoded")
	private final void addCancelBtn() {
		ImageView cancel = new ImageView(getContext());
		cancel.setImageResource(android.R.drawable.presence_offline);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WindowSizeUtils.dip2px(getContext(), 30), WindowSizeUtils.dip2px(getContext(), 30));
		params.gravity = Gravity.RIGHT | Gravity.TOP;
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (interstitialDialog != null && interstitialDialog.isShowing()) {
					interstitialDialog.dismiss();
				}
			}
		});
		interstitialFrame.addView(cancel, params);
	}

	@Override
	protected void onPrepareInterstitial() {
		interstitialReady = false;
		onPreparedWebInterstitial();
	}

	protected abstract void onPreparedWebInterstitial();
	
	protected final WebViewClient createViewClient(){
		WebViewClient client = new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (getaTagUrl().equals(url))
				{
					webLayerClickedAndRequestBrowser(url);
					//支持deepLink 逻辑有问题
//					if (DeepLinkUtils.isDeepLink(url)) {
//						final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//						Log.e("sss", "DeepLinkUtils.deviceCanHandleIntent = "+DeepLinkUtils.deviceCanHandleIntent(activity, intent));
//						if (DeepLinkUtils.deviceCanHandleIntent(activity, intent)) {
//							Log.e("sss", "deeplink = 1");
//							activity.startActivity(intent);
//						}
//						return true;
//					} else {
////						return super.shouldOverrideUrlLoading(view, url);
//						view.loadUrl(url);
//						return true;
//					}
				}
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (!instertitialPageError) {
					if (!interstitialReady) {
						createInterstitialContentLayout(view, getProvider()
								.getGlobal().canManualCancel());
					}
					interstitialReady = true;
					webLayerPrepared(view);
				}else {
					layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
				}
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				instertitialPageError = true;
				ZplayDebug.v(TAG, "interstitial page has error  " + errorCode + " description " + description, onoff);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

		};
		return client;
	};
	
	@Override
	protected final boolean isInterstitialLayerReady() {
		return interstitialReady;
	}
	
	protected abstract void webLayerClickedAndRequestBrowser(String url);

	protected abstract void webLayerPrepared(WebView view);
	
	protected abstract void webLayerOnShow();

	protected abstract void calculateRequestSize();
	
	protected abstract void webLayerDismiss();
	
    @Override
    protected final void layerClicked(float x, float y) {
        webViewWidth = web.getWidth();
        webViewHeight = web.getHeight();
        super.layerClicked(x, y);
    }
	    
}
