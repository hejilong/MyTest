package com.yumi.android.sdk.ads.mediation.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 状态类
 * @author  hejilong  2016-11-16
 *
 */
public class MediationStatus {
    
    //已展示过广告的三方SDK ID列表
    public static  List<String> preparedProviderNameList=new ArrayList<String>();
    //调试模式三方SDK集合
    public final List<NetworkStatus> networkStatusList;
    public MediationStatus(List<NetworkStatus> networkStatusList)
    {
      this.networkStatusList = networkStatusList;
    }
    
    public static List<String> getPreparedAdpaterList() {
        return preparedProviderNameList;
    }
    
    public static void addPreparedAdpaterList(String providerName) {
        try {
            if (!MediationStatus.preparedProviderNameList.contains(providerName)) {
                MediationStatus.preparedProviderNameList.add(providerName);
            }
        } catch (Exception e) {
        }
    }
    
    public static int getProviderIsPrepared(String providerName) {
        try {
            if (MediationStatus.preparedProviderNameList.contains(providerName)) {
                return NetworkStatus.STATUS_SUCCEED;
            }
        } catch (Exception e) {
        }
        return NetworkStatus.STATUS_NOTYET;
    }
}
