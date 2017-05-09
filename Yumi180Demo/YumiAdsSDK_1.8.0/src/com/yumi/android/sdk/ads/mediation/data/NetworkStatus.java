package com.yumi.android.sdk.ads.mediation.data;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;

/**
 * 三方sdk状态实体
 * @author  hejilong  2016-11-16
 *
 */
public class NetworkStatus {
    private int localStatus = STATUS_NOTYET;
    private int adapterStatus = STATUS_NOTYET;
    private String name;
    private String providerId;
    private YumiProviderBean providerBeanBanner = null;
    private YumiProviderBean providerBeanCp = null;
    private YumiProviderBean providerBeanMedia = null;

    public static final int STATUS_SUCCEED = 0;
    public static final int STATUS_NOTYET = 1;

    public NetworkStatus(String name, String providerId) {
        this.name = name;
        this.providerId = providerId;
    }

    public String getName() {
        return this.name;
    }

    public String getNameUpperCase() {
        try {
           return this.name.substring(0, 1).toUpperCase()+this.name.substring(1).toLowerCase();

        } catch (Exception e) {
        }
        return this.name;
    }
    
    public String getProviderId() {
        return this.providerId;
    }

    public YumiProviderBean getProviderBeanBanner() {
        return providerBeanBanner;
    }

    public void setProviderBeanBanner(YumiProviderBean providerBeanBanner) {
        this.providerBeanBanner = providerBeanBanner;
    }

    public YumiProviderBean getProviderBeanCp() {
        return providerBeanCp;
    }

    public void setProviderBeanCp(YumiProviderBean providerBeanCp) {
        this.providerBeanCp = providerBeanCp;
    }

    public YumiProviderBean getProviderBeanMedia() {
        return providerBeanMedia;
    }

    public void setProviderBeanMedia(YumiProviderBean providerBeanMedia) {
        this.providerBeanMedia = providerBeanMedia;
    }

    public int getLocalStatus() {
        return this.localStatus;
    }

    public void setLocalStatus(int localStatus) {
        this.localStatus = localStatus;
    }

    public int getAdapterStatus() {
        return this.adapterStatus;
    }

    public boolean getNetworkStatus() {
        return (this.localStatus == STATUS_SUCCEED) && (this.adapterStatus == STATUS_SUCCEED);
    }

    public void setAdapterStatus(int adapterStatus) {
        this.adapterStatus = adapterStatus;
    }

}
