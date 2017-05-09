package com.yumi.android.sdk.ads.factory;

import com.yumi.android.sdk.ads.api.alimama.AlimamaBannerAdapter;
import com.yumi.android.sdk.ads.api.baidu.BaiduBannerAdapter;
import com.yumi.android.sdk.ads.api.chancead.ChanceadBannerAdapter;
import com.yumi.android.sdk.ads.api.gdtmob.GdtmobBannerAdapter;
import com.yumi.android.sdk.ads.api.ifly.IflyBannerAdapter;
import com.yumi.android.sdk.ads.api.inmobi.InmobiBannerAdapter;
import com.yumi.android.sdk.ads.api.mogo.MogoBannerAdapter;
import com.yumi.android.sdk.ads.api.smaato.SmaatoBannerAdapter;
import com.yumi.android.sdk.ads.api.sohu.SohuBannerAdapter;
import com.yumi.android.sdk.ads.api.ym.YMBannerAdapter;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.ensure.YumiBannerAdapter;
import com.yumi.android.sdk.ads.layer.YumiBaseBannerLayer;
import com.yumi.android.sdk.ads.layer.web.YumiWebBannerLayer;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.ProviderID;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;

import android.app.Activity;

public final class YumiBannerAdapterFacotry extends
		YumiBaseAdapterFactory<YumiBaseBannerLayer> {
	
	/**com.yumi.android.sdk.ads.adapter.%s.%sBannerAdapter*/
	public static final String REFLECT_BANNER_NAME_SDK = "hOXEFgFMzvOT/NRmeglm/mQqjQrnfPefzPGMZaXEfrzCIQdsyLDbY5Yx1B5D /9EEakafMl1jDeM=";
//	private static final String REFLECT_BANNER_NAME_API = "com.yumi.android.sdk.ads.api.%s.%sBannerAdapter";
//	private static final String YUMI_SDK = "com.yumi.android.sdk.ads.ensure.YumiBannerAdapter";
	
	private static class YumiBannerAdapterFactoryHolder{
		private static final YumiBannerAdapterFacotry BANNER_FACTORY = new YumiBannerAdapterFacotry();
	}
	
	private YumiBannerAdapterFacotry(){
		
	}
	
	public final static YumiBannerAdapterFacotry getFactory(){
		return YumiBannerAdapterFactoryHolder.BANNER_FACTORY;
	}
	
	public final YumiBaseBannerLayer buildBannerAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener){
		return getAdapterInstance(activity, provider, innerListener);
	}
	
	@Override
	protected final String getPackageNameByProviderType(YumiProviderBean provider) {
		String name = provider.getProviderName();
		if (provider.getReqType() == REQ_TYPE_SDK) {
			return String.format(YumiDes3Util.decodeDes3(REFLECT_BANNER_NAME_SDK), name.toLowerCase(), name);
		}
		if (provider.getReqType() == REQ_TYPE_CUSTOMER) {
			return provider.getProviderName();
		}
		throw new RuntimeException("unavailable provider request type");
	}

	@Override
	protected YumiBaseBannerLayer getLayerByProvider(Activity activity, YumiProviderBean provider,
			IYumiInnerLayerStatusListener innerListener) {
		String ProID = ProviderID.getFlow5(provider.getProviderID());
		YumiWebBannerLayer adapter = null;
		if ("10045".equals(ProID)) //alimama
		{
			adapter = new AlimamaBannerAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10022".equals(ProID)) //baidu
		{
			adapter = new BaiduBannerAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10023".equals(ProID)) //chancead
		{
			adapter = new ChanceadBannerAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10026".equals(ProID)) //gdtmob
		{
			adapter = new GdtmobBannerAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10027".equals(ProID)) //ifly
		{
			adapter = new IflyBannerAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10010".equals(ProID)) //inmobi
		{
			adapter = new InmobiBannerAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10028".equals(ProID)) //mogo
		{
			adapter = new MogoBannerAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10015".equals(ProID)) //smaato
		{
			adapter = new SmaatoBannerAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10043".equals(ProID)) //sohu 
		{
			adapter = new SohuBannerAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10034".equals(ProID)) //youmi
		{
			adapter = new YMBannerAdapter(activity, provider, innerListener);
			adapter.init();
		}
		return adapter;
	}

	@Override
	protected YumiBaseBannerLayer getSelfLayer(Activity activity, YumiProviderBean provider,
			IYumiInnerLayerStatusListener innerListener) {
		YumiBannerAdapter adapter = new YumiBannerAdapter(activity, provider, innerListener);
		adapter.init();
		return adapter;
	}
	
}
