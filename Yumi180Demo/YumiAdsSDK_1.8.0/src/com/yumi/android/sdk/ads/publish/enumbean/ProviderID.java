package com.yumi.android.sdk.ads.publish.enumbean;

import java.lang.reflect.Field;

import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;

public class ProviderID
{
//	public static final String P10001 = "Adcolony";
//	public static final String P10002 = "Admob";
//	public static final String P10003 = "Adview";
//	public static final String P10004 = "Appcoach";
//	public static final String P10005 = "Applovin";
//	public static final String P10006 = "Chartboost";
//	public static final String P10007 = "Facebook";
//	public static final String P10008 = "Flurry";
//	public static final String P10009 = "iAd";
//	public static final String P10010 = "Inmobi";
//	public static final String P10011 = "Leadbolt";
//	public static final String P10012 = "Loopme";
//	public static final String P10013 = "Millennial";
//	public static final String P10014 = "Mopub";
//	public static final String P10015 = "Smaato";
//	public static final String P10016 = "Startapp";
//	public static final String P10017 = "Supersonic";
//	public static final String P10018 = "Tapjoy";
//	public static final String P10019 = "Unity";
//	public static final String P10020 = "Vpon";
//	public static final String P10021 = "Vungle";
//	public static final String P10022 = "Baidu";
//	public static final String P10023 = "Chancead";
//	public static final String P10024 = "dianru";
//	public static final String P10025 = "Domob";
//	public static final String P10026 = "Gdtmob";
//	public static final String P10027 = "Ifly";
//	public static final String P10028 = "Mogo";
//	public static final String P10029 = "Yima";
//	public static final String P20001 = "Yumi";
//	public static final String P30001 = "Yumimobi";
//	public static final String P10032 = "Gdtnative";
//	public static final String P10033 = "Inmobinative";			//120 +API
//	public static final String P10034 = "Youmi";					//120
//	public static final String P10040 = "Mobisagenative";			//120
//	public static final String P10041 = "Pubnative";				//120
//	public static final String P10043 = "Sohu";					//120
//	public static final String P10044 = "Xiaomi";					//140
//	public static final String P10045 = "Alimama";					//151
	
	
	/** Adcolony */
	public static final String P10001 = "XAaWYuqGNl30gJRhY4OB4g==";
	/** Admob */
	public static final String P10002 = "Th5KzbWqeT4=";
	/** Adview */
	public static final String P10003 = "6fiP6nD+1Bs=";
	/** Appcoach */
	public static final String P10004 = "cJplli8NFn01RrFwZb5idA==";
	/** Applovin */
	public static final String P10005 = "E25qjgALfAdhuvbDyBX3Zg==";
	/** Chartboost */
	public static final String P10006 = "qdfKJoFkwczCshiCjK4c/g==";
	/** Facebook */
	public static final String P10007 = "eaMyCp9eKaJw9xcWS6CiBA==";
	/** Flurry */
	public static final String P10008 = "IT+HNYBV6Nw=";
	/** iAd */
	public static final String P10009 = "RVE0ZmVvHk4=";
	/** Inmobi */
	public static final String P10010 = "Uw65gW1Ggt4=";
	/** Leadbolt */
	public static final String P10011 = "tsGzCC9oILeVnbMwwNMaOQ==";
	/** Loopme */
	public static final String P10012 = "OFf2J1gErGc=";
	/** Millennial */
	public static final String P10013 = "cThdtM2bctWoEb3pCkqP+w==";
	/** Mopub */
	public static final String P10014 = "XsAniG0WNaw=";
	/** Smaato */
	public static final String P10015 = "Y13xpJCDG0g=";
	/** Startapp */
	public static final String P10016 = "y3DOLNMb9D201MfwgkRFRQ==";
	/** Supersonic */
	public static final String P10017 = "7QfRKUgusFNxdlDqX1bvUg==";
	/** Tapjoy */
	public static final String P10018 = "0cXjmJQvr2M=";
	/** Unity */
	public static final String P10019 = "e7dgrC22i88=";
	/** Vpon */
	public static final String P10020 = "YZbtXk47pXc=";
	/** Vungle */
	public static final String P10021 = "xns7TovWbgY=";
	/** Baidu */
	public static final String P10022 = "IJzMKmd4tj8=";
	/** Chancead */
	public static final String P10023 = "CdeQ1MwonqKduN7PcEGw0A==";
	/** Dianru */
	public static final String P10024 = "RACdScOlv8Q=";
	/** Domob */
	public static final String P10025 = "Tg8caS7MR3Y=";
	/** Gdtmob */
	public static final String P10026 = "KuyeJcLUwHo=";
	/** Ifly */
	public static final String P10027 = "IPgOlZ6V+Vs=";
	/** Mogo */
	public static final String P10028 = "IAI8BdMpuxA=";
	/** Yima */
	public static final String P10029 = "bSK0QH1eWZc=";
	/** Yumi */
	public static final String P20001 = "NEaT/6xSzPI=";
	/** Yumimobi */
	public static final String P30001 = "IX86hdqvlH4ifnoBAvQQxA==";
	/** Gdtnative */
	public static final String P10032 = "koiNmn/ofGWMfNUiLTV4Xw==";
	/** Inmobinative */
	public static final String P10033 = "gNhpxC0ga9PAa3d2ONobXg==";
	/** Youmi */
	public static final String P10034 = "A6lW4IudYv4=";
	/** Mobisagenative */
	public static final String P10040 = "rdDSQVQ9zfqdYxa2FsDb1A==";
	/** Pubnative */
	public static final String P10041 = "I4Jell2Ygd1KULE1QBwtGQ==";
	/** Sohu */
	public static final String P10043 = "1iStJiUuuO0=";
	/** Xiaomi */
	public static final String P10044 = "Tdv4gIN/0uQ=";
	/** Alimama */
	public static final String P10045 = "3YzS8xz0sDc=";
	/** Facebooknative 181增加*/
	public static final String P10049 = "eaMyCp9eKaLHzzrbuHYbDQ==";
	/** Admobnative */
    public static final String P10039 = "r/SxJrzVGLxTASf19hJ7tA==";
	
	private ProviderID(){}
	
	public static final String getProviderNameByID(String providerID)
	{
		String providerName = "default";
		try
		{
			providerName = getProviderNameDes3(providerID);
			return YumiDes3Util.decodeDes3(providerName);
		} catch (Exception e)
		{
			return providerName;
		}
	}
	
	public static final String getProviderNameDes3(String providerID)
	{
		String providerName = "default";
		try
		{
			providerID = getFlow5(providerID);//TODO 1.2.0 ID取后5位
			Class<?> clazz = ProviderID.class;
			Field idf = clazz.getField("P"+providerID);
			providerName = (String) idf.get(null);
			return providerName;
		} catch (Exception e)
		{
			return providerName;
		}
	}
	
	public static final String getFlow5(String str)
	{
		return str.substring(str.length()-5, str.length());
	}
	
}
