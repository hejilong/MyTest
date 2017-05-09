package com.yumi.android.sdk.ads.mediation.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yumi.android.sdk.ads.beans.YumiGlobalBean;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.beans.YumiResultBean;
import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.factory.YumiBannerAdapterFacotry;
import com.yumi.android.sdk.ads.factory.YumiInterstitialAdapterFactory;
import com.yumi.android.sdk.ads.factory.YumiMediaAdapterFactory;
import com.yumi.android.sdk.ads.mediation.data.MediationStatus;
import com.yumi.android.sdk.ads.mediation.data.NetworkStatus;
import com.yumi.android.sdk.ads.mediation.views.NetworkDetailView;
import com.yumi.android.sdk.ads.mediation.views.NetworkListView;
import com.yumi.android.sdk.ads.publish.YumiMedia;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.publish.enumbean.ProviderID;
import com.yumi.android.sdk.ads.request.ConfigInfoRequest;
import com.yumi.android.sdk.ads.request.ConfigInfoRequest.ConfigRequestCallback;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;

/**
 * 调试模式主Activity
 * @author hejilong  2016-11-16
 *
 */
public class MediationTestActivity extends Activity {

    private static final String TAG = "MediationTestActivity";
    private static final boolean onoff = true;

    protected static final int REQ_TYPE_SDK = 1;

    NetworkListView networkListView = null;
    private NetworkDetailView networkDetailView;
//    private MediationStatus mediationStatus;
    private String yumiId = "";
    private String channelID = "";
    private String versionName = "";

    //请求返回的配置
    private YumiResultBean resultBanner;
    private YumiResultBean resultCp;
    private YumiResultBean resultMedia;

    //配置是否请求完成
    private boolean bannerRequested = false;
    private boolean cpRequested = false;
    private boolean mediaRequested = false;

    /**
     * 适配器排重过滤容器
     */
    private HashMap<String, NetworkStatus> filtratemap = new HashMap<String, NetworkStatus>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        yumiId = intentGetStringExtra(getIntent(), "yumiId");
        channelID = intentGetStringExtra(getIntent(), "channelID");
        versionName = intentGetStringExtra(getIntent(), "versionName");
        requestConfigFromServer();

        TextView loadingView = new TextView(this);
        this.networkListView = new NetworkListView(this);

        loadingView.setText("Searching for third party ADnetwork adapters");
        loadingView.setTextColor(-16777216);

        loadingView.setBackgroundColor(-1);
        this.networkListView.setBackgroundColor(-1);

        setContentView(loadingView);
    }

    public void showNetworkDetails(NetworkStatus networkStatus) {
        this.networkDetailView = new NetworkDetailView(MediationTestActivity.this, networkStatus);
        setContentView(this.networkDetailView);
    }

    /**
     * 获取配置完成准备开始展示平台列表
     */
    private void requestConfigComplete() {
        if (bannerRequested && cpRequested && mediaRequested) {
            MediationTestActivity.this.networkListView.setStatus(MediationTestActivity.this, new MediationStatus(getnetworkStatausList()));
            MediationTestActivity.this.setContentView(MediationTestActivity.this.networkListView);
        }
    }

    /**
     * 组装第一页列表实体
     * @return
     */
    private List<NetworkStatus> getnetworkStatausList() {
        List<NetworkStatus> list = new ArrayList<NetworkStatus>();
        try {
            if (resultBanner != null && resultBanner.getProviders() != null && resultBanner.getProviders().size() > 0) {
                addFiltratemap(resultBanner.getProviders(), 1);
            }
            if (resultCp != null && resultCp.getProviders() != null && resultCp.getProviders().size() > 0) {
                addFiltratemap(resultCp.getProviders(), 2);
            }
            if (resultMedia != null && resultMedia.getProviders() != null && resultMedia.getProviders().size() > 0) {
                addFiltratemap(resultMedia.getProviders(), 3);
            }

            for (NetworkStatus ns : filtratemap.values()) {
                list.add(ns);
            }
        } catch (Exception e) {
        	ZplayDebug.e(TAG, e.getMessage(), e, onoff);
        }
        return list;
    }

    /**
     * 平台排重过滤
     * @param list
     * @param adType
     */
    private void addFiltratemap(List<YumiProviderBean> list, int adType) {
        for (YumiProviderBean providerBean : list) {
            if (providerBean.getReqType() == REQ_TYPE_SDK && !"yumimobi".equals(providerBean.getProviderName())) {
                NetworkStatus ns = filtratemap.get(providerBean.getProviderName());

                ZplayDebug.d(TAG, "addFiltratemap ProviderID :" + providerBean.getProviderID() + " " + providerBean.getKey1(), onoff);

                if (ns == null) {
                    ns = new NetworkStatus(providerBean.getProviderName(), providerBean.getProviderID());
                    filtratemap.put(providerBean.getProviderName(), ns);
                }
                if (adType == 1) {
                    YumiGlobalBean global = new YumiGlobalBean(resultBanner, yumiId, channelID, versionName);
                    providerBean.setGlobal(global);
                    ns.setProviderBeanBanner(providerBean);
                    if (checkAdapter(providerBean, 1)) {
                        ns.setAdapterStatus(NetworkStatus.STATUS_SUCCEED);
                    }
                } else if (adType == 2) {
                    YumiGlobalBean global = new YumiGlobalBean(resultCp, yumiId, channelID, versionName);
                    providerBean.setGlobal(global);
                    ns.setProviderBeanCp(providerBean);

                    if (checkAdapter(providerBean, 2)) {
                        ns.setAdapterStatus(NetworkStatus.STATUS_SUCCEED);
                    }
                } else if (adType == 3) {
                    YumiGlobalBean global = new YumiGlobalBean(resultMedia, yumiId, channelID, versionName);
                    providerBean.setGlobal(global);
                    ns.setProviderBeanMedia(providerBean);
                    if (checkAdapter(providerBean, 3)) {
                        ns.setAdapterStatus(NetworkStatus.STATUS_SUCCEED);
                    }
                }
                //检查该平台是否已经展示过广告，设置状态
                ns.setLocalStatus(MediationStatus.getProviderIsPrepared(providerBean.getProviderName()));
            }
        }
    }

    /**
     * 获取广告配置
     */
    private final void requestConfigFromServer() {
        if (yumiId == null || "".equals(yumiId)) {
            ZplayDebug.e(TAG, "request Config YumiID is empty", onoff);
            return;
        }
        ZplayDebug.i(TAG, "request Config YumiID " + yumiId + " channelID " + channelID + " versionName " + versionName, onoff);
        if (NetworkStatusHandler.isNetWorkAvaliable(MediationTestActivity.this)) {
            requestConfig(yumiId, channelID, versionName, LayerType.TYPE_BANNER, YumiConstants.SP_KEY_LAST_BANNER_CONFIG, new ConfigRequestCallback() {
                @Override
                public void onReqeustDone(YumiResultBean result) {
                    if (result != null) {
                        if (result.getResult() == 0) {
                            resultBanner = result;
                        } else {
                            ZplayDebug.d(TAG, "get BANNER config failed by " + result.getResult(), onoff);
                        }
                        bannerRequested = true;
                        requestConfigComplete();
                    }
                }
            });

            requestConfig(yumiId, channelID, versionName, LayerType.TYPE_INTERSTITIAL, YumiConstants.SP_KEY_LAST_BANNER_CONFIG, new ConfigRequestCallback() {
                @Override
                public void onReqeustDone(YumiResultBean result) {
                    if (result != null) {
                        if (result.getResult() == 0) {
                            resultCp = result;
                        } else {
                            ZplayDebug.d(TAG, "get INTERSTITIAL config failed by " + result.getResult(), onoff);
                        }
                    }
                    cpRequested = true;
                    requestConfigComplete();
                }
            });

            requestConfig(yumiId, channelID, versionName, LayerType.TYPE_MEDIA, YumiConstants.SP_KEY_LAST_BANNER_CONFIG, new ConfigRequestCallback() {
                @Override
                public void onReqeustDone(YumiResultBean result) {
                    if (result != null) {
                        if (result.getResult() == 0) {
                            resultMedia = result;
                        } else {
                            ZplayDebug.d(TAG, "get MEDIA config failed by " + result.getResult(), onoff);
                        }
                    }
                    mediaRequested = true;
                    requestConfigComplete();
                }
            });

        } else {
            ZplayDebug.w(TAG, "Invalid network", onoff);
        }
    }

    public final void requestConfig(String yumiID, String channelID, String versionName, LayerType type, String spkey, ConfigRequestCallback callback) {
        ConfigInfoRequest configInfoRequest = new ConfigInfoRequest(getApplicationContext(), yumiID, channelID, versionName, type, spkey, callback);
        configInfoRequest.requestConfig();
    }

    private String intentGetStringExtra(Intent intent, String key) {
        String value = intent.getStringExtra(key);
        return value == null ? "" : value;
    }

    /**
     * 捕获返回键
     */
    @Override
    public void onBackPressed() {
        if (this.networkDetailView != null) {
            this.networkDetailView = null;
//            setContentView(this.networkListView);
            requestConfigComplete();
        } else {
            super.onBackPressed();
        }
    }
    
    /**
     * 检查适配器是否已添加
     * @param provider
     * @param adType
     * @return
     */
    private boolean checkAdapter(YumiProviderBean provider, int adType) {
        try {
        	String namedes3 = ProviderID.getProviderNameDes3(provider.getProviderID());
        	if (namedes3.equals(ProviderID.P20001) || namedes3.equals(ProviderID.P30001)) {
        		return true;
            }
            Class<?> clazz = (Class<?>) Class.forName(getPackageNameByProviderType(provider, adType));
            if (clazz != null) {
                return true;
            }

        } catch (Exception e) {

        }
        return false;
    }

//    private static final String REFLECT_BANNER_NAME_SDK = "com.yumi.android.sdk.ads.adapter.%s.%sBannerAdapter";
//    private static final String YUMI_BANNER_SDK = "com.yumi.android.sdk.ads.ensure.YumiBannerAdapter";

//    private static final String REFLECT_INTERSITITAL_NAME_SDK = "com.yumi.android.sdk.ads.adapter.%s.%sInterstitialAdapter";
//    private static final String YUMI_INTERSITITAL_SDK = "com.yumi.android.sdk.ads.ensure.YumiInterstitialAdapter";

//    public static final String REFLECT_MEDIA_NAME_SDK = "com.yumi.android.sdk.ads.adapter.%s.%sMediaAdapter";
//    private static final String YUMI_MEDIA_SDK = "com.yumi.android.sdk.ads.ensure.YumiMediaAdapter";

    private final String getPackageNameByProviderType(YumiProviderBean provider, int adType) {
        String name = provider.getProviderName();
//        if (name.equals("yumimobi") || name.equals("yumi")) {
//            if (adType == 1) {
//                return YUMI_BANNER_SDK;
//            } else if (adType == 2) {
//                return YUMI_INTERSITITAL_SDK;
//            } else if (adType == 3) {
//                return YUMI_MEDIA_SDK;
//            }
//        }
        if (provider.getReqType() == REQ_TYPE_SDK) {

            if (adType == 1) {
                return String.format(YumiDes3Util.decodeDes3(YumiBannerAdapterFacotry.REFLECT_BANNER_NAME_SDK), name.toLowerCase(Locale.ENGLISH), name);
            } else if (adType == 2) {
                return String.format(YumiDes3Util.decodeDes3(YumiInterstitialAdapterFactory.REFLECT_INTERSITITAL_NAME_SDK), name.toLowerCase(Locale.ENGLISH), name);
            } else if (adType == 3) {
                return String.format(YumiDes3Util.decodeDes3(YumiMediaAdapterFactory.REFLECT_MEDIA_NAME_SDK), name.toLowerCase(Locale.ENGLISH), name);
            }
        }
        throw new RuntimeException("unavailable provider request type");
    }

//    private final String uppercaseFirstWorld(String providerName) {
//        char[] charArray = providerName.toCharArray();
//        if (charArray[0] >= 97 && charArray[0] <= 122) {
//            charArray[0] -= 32;
//        }
//        return String.valueOf(charArray);
//    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(this.networkDetailView !=null)
        {
            this.networkDetailView.onDestroy();
        }
    }
}
