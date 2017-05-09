package com.yumi.android.sdk.ads.api.alimama;

import com.yumi.android.sdk.ads.api.alimama.AlimamaApiRequest.IAlimamaAPIRequestListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.web.YumiWebInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.YumiInlayBrowserUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

import android.app.Activity;
import android.webkit.WebView;

public class AlimamaInterstitialAdapter extends YumiWebInterstitialLayer {
    private static final String TAG = "ALiMaMaInterstitialAdapter";
    private AlimamaApiRequest req;
    private YumiProviderBean mProvider;
    private Activity mContext;
    private int reqWidth;
    private int reqHeight;
    private String adSize;

    public AlimamaInterstitialAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
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
        closeOnResume();
    }

    @Override
    public boolean onActivityBackPressed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void onPreparedWebInterstitial() {
        if (req != null) {
            ZplayDebug.d(TAG, "Alimama api request new interstitial", onoff);
            calculateRequestSize();
            req.requestApi(getProvider().getKey1(), getProvider().getGlobal().getReqIP(), adSize);
        }
    }

    @Override
    protected void webLayerClickedAndRequestBrowser(String url) {
        ZplayDebug.d(TAG, "ALiMaMa api Interstitial clicked", onoff);
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
    protected void webLayerPrepared(WebView view) {
        ZplayDebug.d(TAG, "ALiMaMa api Interstitial prepared", onoff);
        layerPrepared();
    }

    @Override
    protected void webLayerOnShow() {
        ZplayDebug.d(TAG, "ALiMaMa api interstitial shown", onoff);
        layerExposure();
        if (webview != null && isInterstitialLayerReady()) {
            ZplayDebug.d(TAG, "ALiMaMa instertitial call js method", onoff);
            webview.loadUrl("javascript:show()");
        } 
        if(req!=null)
        {
            req.reportImpression();
        }
    }

    @Override
    protected void calculateRequestSize() {
        if (WindowSizeUtils.isPortrait(getContext())) {
            reqWidth = 500;
            reqHeight = 600;
            adSize = "500x600";
        } else {
            reqWidth = 600;
            reqHeight = 500;
            adSize = "600x500";
        }
    }

    @Override
    protected void webLayerDismiss() {
        layerClosed();
    }

    @Override
    public void init() {
        ZplayDebug.i(TAG, "ALiMaMa api init key : " + getProvider().getKey1(), onoff);
        calculateRequestSize();
        if (req == null) {
            req = new AlimamaApiRequest(getContext(), new IAlimamaAPIRequestListener() {

                @Override
                public void onAPIRequestDone(AlimamaResultBean data, LayerErrorCode error) {
                    if (data != null) {
                        //成功 有广告返回 
                        calculateWebSize(reqWidth, reqHeight);
                        createWebview(null);
                        parseResult(data);
                    } else if (error != null) {
                        ZplayDebug.d(TAG, "ALiMaMa api interstitial failed " + error, onoff);
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
        if (data.getAdtype() != 15) {
            ZplayDebug.d(TAG, "ALiMaMa api Interstitial Adtype not Interstitial is :" + data.getAdtype(), onoff);
            layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
            return;
        }
        if (data.getMediaType() == AlimamaMediaTypeEnum.TYPE_HTML.getType()) {
            loadData(data.getH5_snippet());
        } else if (data.getMediaType() == AlimamaMediaTypeEnum.TYPE_HTML_URL.getType()) {
            loadUrl(data.getH5_url());
        } else if (data.getMediaType() == AlimamaMediaTypeEnum.TYPE_IMAGE.getType()) {
            String html = XMLRendering.getCpImageHtml(data.getImg_url(), data.getClick_url(),null);
            ZplayDebug.d(TAG, "ALiMaMa api Interstitial parseResult MediaType is TYPE_IMAGE  HTML:" + html, onoff);
            loadData(html);
        } else if (data.getMediaType() == AlimamaMediaTypeEnum.TYPE_IMAGE_TEXT.getType()) {
            String html = XMLRendering.getCpIconTextHtml(data.getImg_url(), data.getTitle(), data.getAd_words(), data.getClick_url(), null);
            loadData(html);
        } else {
            ZplayDebug.d(TAG, "ALiMaMa api Interstitial MediaType is " + data.getMediaType(), onoff);
            layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
        }
    }

}
