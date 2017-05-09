package com.yumi.android.sdk.ads.layer.web;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import  com.yumi.android.sdk.ads.self.ui.ResFactory;
import  com.yumi.android.sdk.ads.self.utils.display.WindowSizeUtils;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.YumiBaseBannerLayer;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.YumiIntentSender;
import com.yumi.android.sdk.ads.utils.YumiLayerSizeCalculater;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public abstract class YumiWebBannerLayer extends YumiBaseBannerLayer {

	public static final boolean onoff = true;

	private static final String TAG = "YumiWebBannerLayer";
	protected FrameLayout web;
    protected WebView webview;
	protected float[] upPoint = new float[2];
	protected float[] downPoint = new float[2];
	protected int webWidth;
	protected int webHeight;
	protected boolean bannerPageError;
	private Activity activity;
	protected YumiWebBannerLayer(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		super(activity, provider);
		mInnerListener = innerListener;
		this.activity = activity;
	}

	@SuppressLint("SetJavaScriptEnabled")
	protected final void createWebview(final OnClickListener onClick){
	    web = new FrameLayout(getContext());
	    webview = new WebView(getContext()){
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
		webview.setBackgroundColor(Color.TRANSPARENT);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		webview.getSettings().setDefaultTextEncodingName("UTF-8");
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.setVerticalScrollBarEnabled(false);
		webview.setHorizontalScrollBarEnabled(false);
		webview.setWebViewClient(createViewClient());
		
		FrameLayout.LayoutParams paramns = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        web.addView(webview, paramns);

        ImageView iv_flag = new ImageView(getContext());
        String providerName = null;
        try
        {
            providerName = getProvider().getProviderName();
            providerName = providerName.toLowerCase();
        } catch (Exception e)
        {
            ZplayDebug.e(TAG, "", e, onoff);
        }
        if ("gdtmob".equals(providerName))
        {
            Drawable drawable_flag = ResFactory.getDrawableByAssets("yumiad_flag_gdt", getContext());
            iv_flag.setImageDrawable(drawable_flag);
        }else{
            Drawable drawable_flag = ResFactory.getDrawableByAssets("yumiad_flag", getContext());
            iv_flag.setImageDrawable(drawable_flag);
        }
        FrameLayout.LayoutParams param_iv_flag = new FrameLayout.LayoutParams(
                WindowSizeUtils.dip2px(getContext(), 28), WindowSizeUtils.dip2px(getContext(), 14));
        param_iv_flag.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        web.addView(iv_flag, param_iv_flag);
		
	}
	

	@Override
	public void init() {
		
	}
	
	protected final void addJSInterface(Object obj, String name){
		if (webview != null) {
		    webview.addJavascriptInterface(obj, name);
		}
	}
	
	protected final void requestWebActivity(String url, boolean jump){
		YumiIntentSender.requestWebActivity(getContext(), url, jump);
	}
	
	protected final void requestSystemBrowser(String url){
		YumiIntentSender.requestSystemBrowser(getContext(), url);
	}
	
	protected final void calculateWebSize() {
		int[] calculateLayerSize = YumiLayerSizeCalculater.calculateLayerSize(getActivity(), bannerSize,isMatchWindowWidth);
		webWidth = calculateLayerSize[0];
		webHeight = calculateLayerSize[1];
	}
	
	protected final void loadData(String html) {
		bannerPageError = false;
		if (webview != null) {
		    webview.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
		}
	}
	
    protected final void loadUrl(String url) {
        bannerPageError = false;
        if (webview != null) {
            webview.loadUrl(url);
        }
    }

	protected final WebViewClient createViewClient(){
		WebViewClient client = new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webLayerClickedAndRequestBrowser(url);
                return true;
//				//支持deepLink
//				if (DeepLinkUtils.isDeepLink(url)) {
//					final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//					Log.e("sss", "DeepLinkUtils.deviceCanHandleIntent = "+DeepLinkUtils.deviceCanHandleIntent(activity, intent));
//					if (DeepLinkUtils.deviceCanHandleIntent(activity, intent)) {
//						Log.e("sss", "deeplink = 1");
//						activity.startActivity(intent);
//					}
//					return true;
//				} else {
////					return super.shouldOverrideUrlLoading(view, url);
//					view.loadUrl(url);
//					return true;
//				}
				
			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				bannerPageError = true;
				ZplayDebug.v(TAG, "banner page has error  " + errorCode + " description " + description, onoff);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				if (!bannerPageError) {
					webLayerPrepared(web);
				}else {
					layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
				}
				super.onPageFinished(view, url);
			}
			
		};
		return client;
	};
	
	protected abstract void webLayerClickedAndRequestBrowser(String url);
	
	protected abstract void webLayerPrepared(View view);
	
	protected abstract void calculateRequestSize();

	@Override
	protected final void layerClicked(float x, float y)
	{
		webViewWidth = web.getWidth();
		webViewHeight = web.getHeight();
		super.layerClicked(x, y);
	}
	
	
}
