package com.yumi.android.sdk.ads.factory;

import com.yumi.android.sdk.ads.api.alimama.AlimamaInterstitialAdapter;
import com.yumi.android.sdk.ads.api.baidu.BaiduInterstitialAdapter;
import com.yumi.android.sdk.ads.api.chancead.ChanceadInterstitialAdapter;
import com.yumi.android.sdk.ads.api.gdtmob.GdtmobInterstitialAdapter;
import com.yumi.android.sdk.ads.api.ifly.IflyInterstitialAdapter;
import com.yumi.android.sdk.ads.api.inmobi.InmobiInterstitialAdapter;
import com.yumi.android.sdk.ads.api.mogo.MogoInterstitialAdapter;
import com.yumi.android.sdk.ads.api.smaato.SmaatoInterstitialAdapter;
import com.yumi.android.sdk.ads.api.sohu.SohuInterstitialAdapter;
import com.yumi.android.sdk.ads.api.ym.YMInterstitialAdapter;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.ensure.YumiInterstitialAdapter;
import com.yumi.android.sdk.ads.layer.YumiBaseInterstitialLayer;
import com.yumi.android.sdk.ads.layer.web.YumiWebInterstitialLayer;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.ProviderID;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;

import android.app.Activity;

public final class YumiInterstitialAdapterFactory extends
		YumiBaseAdapterFactory<YumiBaseInterstitialLayer> {
	/**com.yumi.android.sdk.ads.adapter.%s.%sInterstitialAdapter*/
	public static final String REFLECT_INTERSITITAL_NAME_SDK = "hOXEFgFMzvOT/NRmeglm/mQqjQrnfPefzPGMZaXEfrwH9KWAF3ZZXGujM1GO g450TY+q5P1MFTMrqv0PjKM1Qw==";
//	private static final String REFLECT_INTERSITITAL_NAME_API = "com.yumi.android.sdk.ads.api.%s.%sInterstitialAdapter";
//	private static final String YUMI_SDK = "com.yumi.android.sdk.ads.ensure.YumiInterstitialAdapter";
	
	private static class YumiInterstitialAdapterFactoryHolder{
		private static final YumiInterstitialAdapterFactory INTERSTITIAL_FACTORY = new YumiInterstitialAdapterFactory();
	}
	
	private YumiInterstitialAdapterFactory(){
		
	}
	
	public final static YumiInterstitialAdapterFactory getFactory(){
		return YumiInterstitialAdapterFactoryHolder.INTERSTITIAL_FACTORY;
	}
	
	
	public final YumiBaseInterstitialLayer buildInterstitialAdapter(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener){
		return getAdapterInstance(activity, provider, innerListener);
	}
	
	
	@Override
	protected final String getPackageNameByProviderType(YumiProviderBean provider) {
		String name = provider.getProviderName();
//		if (name.equals("yumimobi") || name.equals("yumi")) {
//			return YUMI_SDK;
//		}
		if (provider.getReqType() == REQ_TYPE_SDK) {
			return String.format(YumiDes3Util.decodeDes3(REFLECT_INTERSITITAL_NAME_SDK), name.toLowerCase(), name);
		}
//		if (provider.getReqType() == REQ_TYPE_API) {
//			return String.format(REFLECT_INTERSITITAL_NAME_API, name.toLowerCase(), name);
//		}
		if (provider.getReqType() == REQ_TYPE_CUSTOMER) {
			return provider.getProviderName();
		}
		throw new RuntimeException("unavailable provider request type");
	}
	
	@Override
	protected YumiBaseInterstitialLayer getLayerByProvider(Activity activity, YumiProviderBean provider,
			IYumiInnerLayerStatusListener innerListener) {
		String ProID = ProviderID.getFlow5(provider.getProviderID());
		YumiWebInterstitialLayer adapter = null;
		if ("10045".equals(ProID)) //alimama
		{
			adapter = new AlimamaInterstitialAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10022".equals(ProID)) //baidu
		{
			adapter = new BaiduInterstitialAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10023".equals(ProID)) //chancead
		{
			adapter = new ChanceadInterstitialAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10026".equals(ProID)) //gdtmob
		{
			adapter = new GdtmobInterstitialAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10027".equals(ProID)) //ifly
		{
			adapter = new IflyInterstitialAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10010".equals(ProID)) //inmobi
		{
			adapter = new InmobiInterstitialAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10028".equals(ProID)) //mogo
		{
			adapter = new MogoInterstitialAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10015".equals(ProID)) //smaato
		{
			adapter = new SmaatoInterstitialAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10043".equals(ProID)) //sohu 
		{
			adapter = new SohuInterstitialAdapter(activity, provider, innerListener);
			adapter.init();
		}
		else if ("10034".equals(ProID)) //youmi
		{
			adapter = new YMInterstitialAdapter(activity, provider, innerListener);
			adapter.init();
		}
		return adapter;
	}

	@Override
	protected YumiBaseInterstitialLayer getSelfLayer(Activity activity,YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener)
	{
		YumiInterstitialAdapter adapter = new YumiInterstitialAdapter(activity, provider, innerListener);
		adapter.init();
		return adapter;
	}

}
