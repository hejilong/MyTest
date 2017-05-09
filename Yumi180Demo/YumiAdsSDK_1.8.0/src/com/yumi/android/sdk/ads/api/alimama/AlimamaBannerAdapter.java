package com.yumi.android.sdk.ads.api.alimama;

import android.app.Activity;
import android.view.View;

import com.yumi.android.sdk.ads.api.alimama.AlimamaApiRequest.IAlimamaAPIRequestListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebBannerLayer;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.NativeAdsBuild;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class AlimamaBannerAdapter extends YumiWebBannerLayer {
    private static final String TAG = "ALiMaMaBannerAdapter";
    private AlimamaApiRequest req;
    private YumiProviderBean mProvider;
    private Activity mContext;
    private int reqWidth = 0;
    private int reqHeight = 0;
    private String adSize;

    public AlimamaBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
        super(activity, provider, innerListener);
        mProvider = provider;
        mContext = activity;
    }

    @Override
    public void onActivityPause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActivityResume() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void webLayerClickedAndRequestBrowser(String url) {
        ZplayDebug.d(TAG, "ALiMaMa api banner clicked", onoff);
        if (mProvider != null && mProvider.getBrowserType().trim().equals("1")) {
            YumiInlayBrowserUtils.openBrowser(mContext, url, null);
        } else {
            requestSystemBrowser(url);
        }
        layerClicked(upPoint[0], upPoint[1]);
        if(req!=null)
        {
            req.reportClick();
        }
    }

    @Override
    protected void webLayerPrepared(View view) {
        ZplayDebug.d(TAG, "ALiMaMa api banner prepared", onoff);
        layerPrepared(view, false);
        ZplayDebug.d(TAG, "ALiMaMa api banner shown", onoff);
        layerExposure();
        if(req!=null)
        {
            req.reportImpression();
        }
    }

    @Override
    protected void calculateRequestSize() {
        if (reqWidth == 0 || reqHeight == 0) {
            if (bannerSize == AdSize.BANNER_SIZE_728X90) {
                reqWidth = 728;
                reqHeight = 90;
                adSize = "728x90";
            } else {
                reqWidth = 320;
                reqHeight = 50;
                adSize = "320x50";
            }
        }
    }

    @Override
    protected void onPrepareBannerLayer() {
        if (req != null) {
            ZplayDebug.d(TAG, "ALiMaMa api  request new banner", onoff);
            calculateRequestSize();
            req.requestApi(getProvider().getKey1(), getProvider().getGlobal().getReqIP(), adSize);
        }
    }

    @Override
	public void init() {
        if (req == null) {
            calculateRequestSize();
            req = new AlimamaApiRequest(getContext(), new IAlimamaAPIRequestListener() {

                @Override
                public void onAPIRequestDone(AlimamaResultBean data, LayerErrorCode error) {
                    if (data != null) {
                        calculateWebSize();
                        createWebview(null);
                        sendChangeViewBeforePrepared(web);
                        parseResult(data);
                    } else if (error != null) {
                        ZplayDebug.d(TAG, "ALiMaMa api banner failed " + error, onoff);
                        layerPreparedFailed(error);
                    }
                }
            });
        }
    }

    @Override
    protected void callOnActivityDestroy() {
        if (req != null) {
            req.onDestroy();
        }
    }

    public void parseResult(AlimamaResultBean data) {
        if (data.getAdtype() != 6) {
            ZplayDebug.d(TAG, "ALiMaMa api banner Adtype not banner is :" + data.getAdtype(), onoff);
            layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
            return;
        }
        if (data.getMediaType() == AlimamaMediaTypeEnum.TYPE_HTML.getType()) {
            loadData(data.getH5_snippet());
        } else if (data.getMediaType() == AlimamaMediaTypeEnum.TYPE_HTML_URL.getType()) {
            loadUrl(data.getH5_url());
        } else if (data.getMediaType() == AlimamaMediaTypeEnum.TYPE_IMAGE.getType()) {
            String html = NativeAdsBuild.getImageAdHtml(data.getImg_url(), data.getClick_url());
            loadData(html);
        } else if (data.getMediaType() == AlimamaMediaTypeEnum.TYPE_IMAGE_TEXT.getType()) {
            String html = NativeAdsBuild.getImageTextAdHtml(data.getImg_url(), data.getTitle(), data.getAd_words(), data.getClick_url(), mContext);
            loadData(html);
        } else {
            ZplayDebug.d(TAG, "ALiMaMa api banner MediaType is " + data.getMediaType(), onoff);
            layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
        }
    }

}
