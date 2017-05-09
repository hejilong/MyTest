package com.yumi.android.sdk.ads.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import android.app.Activity;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.ensure.YumiMediaAdapter;
import com.yumi.android.sdk.ads.layer.YumiBaseMediaLayer;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;

public final class YumiMediaAdapterFactory extends
		YumiBaseAdapterFactory<YumiBaseMediaLayer> {
	/**com.yumi.android.sdk.ads.adapter.%s.%sMediaAdapter*/
	public static final String REFLECT_MEDIA_NAME_SDK = "hOXEFgFMzvOT/NRmeglm/mQqjQrnfPefzPGMZaXEfrx12CQzBuzDD23r1unT YOWHSlyrzceXlfQ=";
//	public static final String REFLECT_MEDIA_NAME_API = "com.yumi.android.sdk.ads.api.%s.%sMediaAdapter";
//	private static final String YUMI_SDK = "com.yumi.android.sdk.ads.ensure.YumiMediaAdapter";
	
	private static class YumiMediaAdapterFactoryHolder{
		private static final YumiMediaAdapterFactory MEDIA_FACTORY = new YumiMediaAdapterFactory();
	}
	
	private YumiMediaAdapterFactory(){
		
	}
	
	public final static YumiMediaAdapterFactory getFactory(){
		return YumiMediaAdapterFactoryHolder.MEDIA_FACTORY;
	}
	
	public final YumiBaseMediaLayer buildMediaAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener){
		return getAdapterInstance(activity, provider , innerListener);
	}
	
	public final YumiBaseMediaLayer GetMediaAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener){
	    String key = buildObtainKey(provider);
        if (provider != null && NullCheckUtils.isNotNull(key)) {
            YumiBaseMediaLayer t = adapterObtain.get(key);
            if (t != null) {
                return t;
            } 
        }
        return null;
	}
	
	
	public final void UpdateRid(String rid)
	{
	    if (NullCheckUtils.isNotEmptyMap(adapterObtain)) {
            Set<Entry<String,YumiBaseMediaLayer>> entrySet = adapterObtain.entrySet();
            for (Entry<String, YumiBaseMediaLayer> entry : entrySet) {
                entry.getValue().setRID(rid);
            }
        }
	}
	
	public List<YumiBaseMediaLayer> getIsMediaAdapterList()
	{
	    List<YumiBaseMediaLayer> list=new ArrayList<YumiBaseMediaLayer>();
        try {
            if (NullCheckUtils.isNotEmptyMap(adapterObtain)) {
                Set<Entry<String, YumiBaseMediaLayer>> entrySet = adapterObtain.entrySet();
                for (Entry<String, YumiBaseMediaLayer> entry : entrySet) {
                    list.add(entry.getValue());
                }
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "getIsMediaAdapterList is error", e, onoff);
        }
	    return list;
	}
	
	@Override
	protected final String getPackageNameByProviderType(YumiProviderBean provider) {
		String name = provider.getProviderName();
		if (provider.getReqType() == REQ_TYPE_SDK) {
			return String.format(YumiDes3Util.decodeDes3(REFLECT_MEDIA_NAME_SDK), name.toLowerCase(), name);
		}
		if (provider.getReqType() == REQ_TYPE_CUSTOMER) {
			return provider.getProviderName();
		}
		throw new RuntimeException("unavailable provider request type");
	}

	@Override
	protected YumiBaseMediaLayer getLayerByProvider(Activity activity, YumiProviderBean provider,
			IYumiInnerLayerStatusListener innerListener) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected YumiBaseMediaLayer getSelfLayer(Activity activity, YumiProviderBean provider,
			IYumiInnerLayerStatusListener innerListener) {
		YumiMediaAdapter adapter = new YumiMediaAdapter(activity, provider, innerListener);
		adapter.init();
		return adapter;
	}
}
