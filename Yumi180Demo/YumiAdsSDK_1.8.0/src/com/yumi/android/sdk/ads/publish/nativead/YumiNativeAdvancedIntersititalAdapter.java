package com.yumi.android.sdk.ads.publish.nativead;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.YumiBaseInterstitialLayer;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.YumiIntentSender;
import com.yumi.android.sdk.ads.utils.YumiLayerSizeCalculater;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

/**
 * Created by Administrator on 2017/3/24.
 */

public abstract class YumiNativeAdvancedIntersititalAdapter extends   YumiBaseInterstitialLayer {

    public static final boolean onoff = true;

    private static final String TAG = "YumiNativeAdvancedIntersititalAdapter";
    protected Dialog interstitialDialog;
    private FrameLayout interstitialFrame;
    private FrameLayout interstitialBaseFrame;
    protected boolean interstitialReady;

    protected YumiNativeAdvancedIntersititalAdapter(Activity activity,YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected final void onShowInterstitialLayer(Activity activity) {
        createInterstitialDialog(activity);
        interstitialDialog.show();
        NativeLayerOnShow();
    }

    private void createInterstitialDialog(Activity activity) {
        if (interstitialDialog == null) {
            interstitialDialog = new Dialog(activity,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

            interstitialBaseFrame=new FrameLayout(activity);
            interstitialBaseFrame.addView(interstitialFrame, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            interstitialBaseFrame.setBackgroundColor(Color.parseColor("#b2000000"));

            interstitialDialog.setContentView(interstitialBaseFrame, new FrameLayout.LayoutParams(android.widget.FrameLayout.LayoutParams.MATCH_PARENT, android.widget.FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
            interstitialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    NativeLayerDismiss();
                }
            });
            if ( getProvider().getGlobal().canManualCancel()) {
                addCancelBtn();
            }
        }
    }

    protected final void loadData(View view) {
        if (!interstitialReady) {
            createInterstitialContentLayout(view, getProvider()
                    .getGlobal().canManualCancel());
        }
        interstitialReady = true;
        NativeLayerPrepared(view);
    }

    protected final void requestWebActivity(String url, boolean jump){
        YumiIntentSender.requestWebActivity(getContext(), url, jump);
    }

    protected final void requestSystemBrowser(String url){
        YumiIntentSender.requestSystemBrowser(getContext(), url);
    }

    protected final void createInterstitialContentLayout(View view, boolean canManualCancel) {
        if (interstitialFrame == null) {
            interstitialFrame = new FrameLayout(getContext());
        }else {
            interstitialFrame.removeAllViews();
        }
        interstitialFrame.addView(view);
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
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (interstitialDialog != null && interstitialDialog.isShowing()) {
                    interstitialDialog.dismiss();
                }
            }
        });
        interstitialBaseFrame.addView(cancel, params);
    }

    @Override
    protected void onPrepareInterstitial() {
        interstitialReady = false;
        onPreparedNativeInterstitial();
    }

    protected abstract void onPreparedNativeInterstitial();

    @Override
    protected final boolean isInterstitialLayerReady() {
        return interstitialReady;
    }

    protected abstract void NativeLayerPrepared(View view);

    protected abstract void NativeLayerOnShow();

    protected abstract void calculateRequestSize();

    protected abstract void NativeLayerDismiss();
    
    @Override
    protected final void layerClicked(float x, float y)
    {
        webViewWidth = interstitialFrame.getWidth();
        webViewHeight =interstitialFrame.getHeight();
        super.layerClicked(x, y);
    }
}
