package com.yumi.android.sdk.ads.mediation.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.YumiManifestReaderUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PackageInfoGetter;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;

import android.content.Context;

/**
 * 调试模式下检查三方sdk组件是否注册工具类
 * 
 * @author hejilong 2016-11-21
 *
 */
public class NetworkManifestReaderUtils {

	private static final String TAG = "YumiManifestReaderUtils";
	private static final boolean onoff = true;
	private static Map<String, String[]> adaptertActivityList = new HashMap<String, String[]>();

	static {

		adaptertActivityList.put(key.admob, value.admob);
		adaptertActivityList.put(key.inmobi, value.inmobi);
		adaptertActivityList.put(key.chartboost, value.chartboost);
		adaptertActivityList.put(key.gdtmob, value.gdtmob);
		adaptertActivityList.put(key.baidu, value.baidu);
		adaptertActivityList.put(key.facebook, value.facebook);
		adaptertActivityList.put(key.leadbolt,value.leadbolt);
		adaptertActivityList.put(key.vungle, value.vungle);
		adaptertActivityList.put(key.Adcolony, value.adcolony);
		adaptertActivityList.put(key.unity, value.unity);
		adaptertActivityList.put(key.applovin, value.applovin);
		adaptertActivityList.put(key.startapp,value.startapp);
		adaptertActivityList.put(key.mopub,value.mopub);
		adaptertActivityList.put(key.xiaomi, value.xiaomi);
	}

	private static final class key {
		private static final String admob = YumiDes3Util.decodeDes3("IhxZiRF4064=");
		private static final String inmobi = YumiDes3Util.decodeDes3("CY8bomZYMP4=");
		private static final String chartboost = YumiDes3Util.decodeDes3("/S8MNf8gVlYqMcjhftg5zg==");
		private static final String gdtmob = YumiDes3Util.decodeDes3("xKghtsQCAMY=");
		private static final String baidu = YumiDes3Util.decodeDes3("elg7V+wB2WQ=");
		private static final String facebook = YumiDes3Util.decodeDes3("jvNsr470pWToN1QakyYNkA==");
		private static final String leadbolt = YumiDes3Util.decodeDes3("UcPaqQiHqokenvHPyXNaoA==");
		private static final String vungle = YumiDes3Util.decodeDes3("BZ63uQkJw44=");
		private static final String Adcolony = YumiDes3Util.decodeDes3("JHNKWtuhhWmPNjZ63hKtOg==");
		private static final String unity = YumiDes3Util.decodeDes3("2F7gZ4hQEdo=");
		private static final String applovin = YumiDes3Util.decodeDes3("4BDmG+U7fnKmiyqJru0KvA==");
		private static final String startapp = YumiDes3Util.decodeDes3("fD5hMlQsjyosTPumUcL7Pg==");
		private static final String mopub = YumiDes3Util.decodeDes3("tn4HpDt8KFs=");
		private static final String xiaomi = YumiDes3Util.decodeDes3("ou0piLgnYZ4=");
	}

	private static final class value {
		private static final String[] admob = new String[] {
				YumiDes3Util.decodeDes3("JXfmRoOwzPiBtDQbYfULL3phUCCUCDCmaJL1yLRkwPXW0nY9fVEwLw==") };
		private static final String[] inmobi = new String[] {
				YumiDes3Util.decodeDes3("GdswTvF1E7kjuQBuVPZiSKo9pWZ4XMLnyzsS8ofzaS2IhmIF/EqllQ==") };
		private static final String[] chartboost = new String[] {
				YumiDes3Util.decodeDes3("aGwpcJFe2yILvu/6Emnyxq9Nqejtluo5H63vx+kc01vnGfX/TW1tBA==") };
		private static final String[] gdtmob = new String[] {
				YumiDes3Util.decodeDes3("jZNdVBaRmdRyojFFMEAP4IXrmmYK4BDe") };
		private static final String[] baidu = new String[] {
				YumiDes3Util.decodeDes3("+7LXCQBB68TdV8ca/pgwNeaM/3KLa5kyBPZxMK8FHdA=") };
		private static final String[] leadbolt = new String[] {
				YumiDes3Util.decodeDes3("bxpXlUhEN+03A3dL1eoued7oHWRRnvbY9jISMXbsb7Tpwl+8w55AebFjLxrR mO9y") };
        private static final String[] applovin = new String[] {
                YumiDes3Util.decodeDes3("EaOP6at1N1YVV/gjpSTbYJkZmHsekPxO63JV4S2uaMJKrbTbrCogre19yUIc 2wTf7kXs6sa8Ilw="),
                YumiDes3Util.decodeDes3("EaOP6at1N1YVV/gjpSTbYJkZmHsekPxOT/mJU/TOOW9CrdHAGDc1C5hRY9PB i39gJ/7pPYfWjAM=") };
		private static final String[] mopub = new String[] {
				YumiDes3Util.decodeDes3("VyyVyNl2hbZiUCutRuVbVkUhveHqiHCxO3MwEcWgUir4dWDTnsu6FA=="),
				YumiDes3Util.decodeDes3("VyyVyNl2hbZiUCutRuVbVr98fTcFXOxe/5W33LfnN1D2b7M84HhZcg=="),
				YumiDes3Util.decodeDes3("VyyVyNl2hbYP5Zzs6bXoUYz0EhNZWhubvr1d2KVTV+4="),
				YumiDes3Util.decodeDes3("VyyVyNl2hbZiUCutRuVbVr98fTcFXOxeQknXF/R+iPjIV5KM7lVamN2ArAaz T2rz") };
		//171修改
		private static final String[] startapp = new String[] {
            YumiDes3Util.decodeDes3("GsWJB2YofBRLl/0Y5Hj7qjrxsBk/AEVc4+DVWVtPeCjliWw62gWIwsUKxqY0 xicqtlQMS4m0+/4="),
            YumiDes3Util.decodeDes3("GsWJB2YofBRLl/0Y5Hj7qjrxsBk/AEVc4+DVWVtPeCgycAGW6DY2ZDtQ0W+u vHEIo9/NTJYt4FsW+dUeOOkre5HIY9IMjgTQ"),
            YumiDes3Util.decodeDes3("GsWJB2YofBRLl/0Y5Hj7qjrxsBk/AEVc4+DVWVtPeCgycAGW6DY2ZDtQ0W+u vHEIAoFOU1ALXvlNT/q0QQ/7R5bREUdGH3HL") };
		private static final String[] facebook = new String[] {
			YumiDes3Util.decodeDes3("qHJH18pRKk6cEQOb+z4NwDlQbNaRJbZob/pws5Kw3rc6e8qW2ecHm2nTDOgj Cnbg") };
		private static final String[] adcolony = new String[] {
            YumiDes3Util.decodeDes3("AtbpS8bDklbOGQa8Cku4Md4pw1QkAleLUQUDBeqjHQee8M7Nrls3v7O4FgGm BNgV"),
            YumiDes3Util.decodeDes3("AtbpS8bDklbOGQa8Cku4Md4pw1QkAleL2ndgEW2pwSCIKx1m3Fu8MQ==") };
		private static final String[] xiaomi = new String[] {
			YumiDes3Util.decodeDes3("swJShuqkCKWTm3i+qE3zVhBNDEasdnmcKr7PGdZfVGo=") };
		private static final String[] vungle = new String[] {
            YumiDes3Util.decodeDes3("R3sre5OUD4OTk01u2cKDBEPJiAab4R77WVJH1C5HNQ2PYQHGHpXfSDtP5Ms0 67d0"),
            YumiDes3Util.decodeDes3("R3sre5OUD4OTk01u2cKDBDjCLItgxFZ1A7wO/Mvo5xw4gnhLOTE9rG9lPsh9 LJ+V") };
        private static final String[] unity = new String[] { 
            YumiDes3Util.decodeDes3("nRk5V7LuAM+r9wK3pL6PgrscfUaF4LG3kXLqr7dg8wJuLkQGHkGT6Q=="),
            YumiDes3Util.decodeDes3("nRk5V7LuAM+r9wK3pL6PgrscfUaF4LG3D3E1rRtG6f+HI5THkGMmRni3xK9K ZIxc") };
	}

	private static Map<String, String[]> adaptertServiceList = new HashMap<String, String[]>();

	static {
		adaptertServiceList.put(YumiDes3Util.decodeDes3("CY8bomZYMP4="), new String[] { YumiDes3Util.decodeDes3("GdswTvF1E7lT/rYaWm34iq2TR2v58l7UAngd+QqauX6rqs2InK8bCULumtSw Mt6Gv3IRhFtvuSo=") });
		adaptertServiceList.put(YumiDes3Util.decodeDes3("xKghtsQCAMY="), new String[] { YumiDes3Util.decodeDes3("jZNdVBaRmdRRVDHsyX4cR3qEDP2Q+2TUTwezfus4dkc=") });
		adaptertServiceList.put(YumiDes3Util.decodeDes3("fD5hMlQsjyosTPumUcL7Pg=="), new String[] { YumiDes3Util.decodeDes3("GsWJB2YofBRLl/0Y5Hj7qjrxsBk/AEVcj8+NN/IU2415HqfO9BMNECDWqxfR PFc0Q9KnXwxa4qyKDrXRJqPENA=="),YumiDes3Util.decodeDes3("GsWJB2YofBRLl/0Y5Hj7qjrxsBk/AEVcj8+NN/IU2415HqfO9BMNEHENiB3r yZq0p8KyOkMAwnrj6MNwoiYE02hQKVK7hB99") });
	}

	/**
	 * 是否注册相应平台组件
	 * 
	 * @param context
	 * @param providerName
	 *            平台名称
	 * @return
	 */
	public final static boolean hasRegisterNecessary(Context context, NetworkStatus nStatus) {
		boolean hasActivity = true;
		boolean hasService = true;
		try {
			final String providerName = nStatus.getName();
			if ("yumi".equals(providerName)) {
				if (nStatus.getProviderBeanMedia() == null) {
					return YumiManifestReaderUtils.hasRegisterNecessary(context);
				} else if (nStatus.getProviderBeanBanner() == null && nStatus.getProviderBeanCp() == null) {
					return YumiManifestReaderUtils.hasMediaRegisterNecessary(context);
				} else {
					if (!YumiManifestReaderUtils.hasRegisterNecessary(context)
							|| !YumiManifestReaderUtils.hasMediaRegisterNecessary(context)) {
						return false;
					} else {
						return true;
					}
				}
			}
			Set<String> registerActivity = PackageInfoGetter.getRegisterActivity(context);
			if (NullCheckUtils.isNotEmptyCollection(registerActivity)) {
				String[] mActicitys = adaptertActivityList.get(providerName);
				if (mActicitys != null && mActicitys.length > 0) {
					for (String s : mActicitys) {
						if (!registerActivity.contains(s)) {
							return false;
						}
					}
				}
			}
			Set<String> registerService = PackageInfoGetter.getRegisterService(context);
			if (NullCheckUtils.isNotEmptyCollection(registerService)) {
				String[] mServices = adaptertServiceList.get(providerName);
				if (mServices != null && mServices.length > 0) {
					for (String s : mServices) {
						if (!registerService.contains(s)) {
							return false;
						}
					}
				}
			}
			return hasActivity && hasService;
		} catch (Exception e) {
			ZplayDebug.e(TAG, "", e, onoff);
			return true;
		}
	}

}
