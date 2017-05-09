package com.yumi.android.sdk.ads.constants;

import com.yumi.android.sdk.ads.publish.YumiDebug;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;

public final class YumiAPIList {

	/**http://corn.yumimobi.com/*/
    private static final String INIT_CONFIG_HOST = YumiDes3Util.decodeDes3("3nuX3i0MR3PoJWUTuudF/3C8jBkr7vLcuK/Z1/XzHDg=");
    /**http://tracker1.yumimobi.com/*/
    private static final String EVENT_REPORT_HOST = YumiDes3Util.decodeDes3("lOWGYuUjaXVC6YHwjyaF28xmVeGWZ31UPuXqqLRCmHk=");
    private static String EVENT_REPORT_HOST_RESET = "";
    public static void setEVENT_REPORT_HOST_RESET(String eVENT_REPORT_HOST_RESET) {
		EVENT_REPORT_HOST_RESET = eVENT_REPORT_HOST_RESET;
	}

	/**http://testapi.yumimobi.com:8899/*/
    private static final String TEST_HOST = YumiDes3Util.decodeDes3("lOWGYuUjaXVj4BS45Oig9B/Jld7sk6l7KZbnhr9Y91dWfGmA198nSg==");

    /**api/2.0/mediation/adconfig.php*/
    private static final String INIT_CONFIG_URL = YumiDes3Util.decodeDes3("W73J9EZV8W3Vvf9RsQhyyApsxSFLOMWSBDUyxTfkyaw=");
    /**api/2.0/mediation/report.php*/
    private static final String EVENT_REPORY_URL = YumiDes3Util.decodeDes3("W73J9EZV8W3Vvf9RsQhyyK4WGp0NTigWo7+5onHNW3M=");
    /**api/2.0/mediation/adconfig_partner.php*/
    private static final String INIT_CONFIG_URL_PARTNER = YumiDes3Util.decodeDes3("W73J9EZV8W3Vvf9RsQhyyApsxSFLOMWS5x5d9mOv2KMGjlD10kAWzw==");
    /**api/2.0/mediation/report_partner.php*/
    private static final String EVENT_REPORY_URL_PARTNER = YumiDes3Util.decodeDes3("W73J9EZV8W3Vvf9RsQhyyK4WGp0NTigW8yA9Fdnk7lbfq6pFmp7ErQ==");
    /**api/2.0/mediation/template.php*/
    private static final String TEMPLATE_DETAIL_URL = YumiDes3Util.decodeDes3("W73J9EZV8W3Vvf9RsQhyyBuZynxW4DbcT//vjwhriZM=");

    public static final String TEMPLATE_DETAIL_URL()
    {
    	
    	if (YumiDebug.isDebugMode())
        {
            return TEST_HOST + TEMPLATE_DETAIL_URL;
        } else
        {
            return INIT_CONFIG_HOST + TEMPLATE_DETAIL_URL;
        }
    }
    
    public static final String INIT_CONFIG_URL()
    {
        if (YumiDebug.isDebugMode())
        {
            return TEST_HOST + INIT_CONFIG_URL;
        } else
        {
            boolean is_zhangyou = "10000".equals(YumiConstants.PARTNER_ID);
            return INIT_CONFIG_HOST + (is_zhangyou ? INIT_CONFIG_URL : INIT_CONFIG_URL_PARTNER);
        }
    }

    public static final String EVENT_REPORT_TEMP_URL()
    {
        boolean is_zhangyou = "10000".equals(YumiConstants.PARTNER_ID);
        boolean is_reset = EVENT_REPORT_HOST_RESET!=null && !"".equals(EVENT_REPORT_HOST_RESET);
        if (YumiDebug.isDebugMode())
        {
            return (is_reset ? EVENT_REPORT_HOST_RESET : TEST_HOST) + EVENT_REPORY_URL;
        } else
        {
        	return (is_reset ? EVENT_REPORT_HOST_RESET : EVENT_REPORT_HOST)
        			+(is_zhangyou ? EVENT_REPORY_URL : EVENT_REPORY_URL_PARTNER);
//        	if (is_reset) {
//        		return EVENT_REPORT_HOST_RESET
//                        + (is_zhangyou ? EVENT_REPORY_URL : EVENT_REPORY_URL_PARTNER);
//			}else{
//				return EVENT_REPORT_HOST
//						+ (is_zhangyou ? EVENT_REPORY_URL : EVENT_REPORY_URL_PARTNER);
//			}
        }
    }

	
}
