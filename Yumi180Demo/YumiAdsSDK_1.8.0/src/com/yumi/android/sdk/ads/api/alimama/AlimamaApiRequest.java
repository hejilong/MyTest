package com.yumi.android.sdk.ads.api.alimama;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yumi.android.sdk.ads.api.ApiRequest;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.utils.encrypt.Encrypter;
import com.yumi.android.sdk.ads.self.utils.encrypt.YumiDes3Util;
import com.yumi.android.sdk.ads.utils.json.JsonResolveUtils;
import com.yumi.android.sdk.ads.utils.location.LocationHandler;
import com.yumi.android.sdk.ads.utils.network.NetworkStatusHandler;
import com.yumi.android.sdk.ads.utils.network.WebParamsMapBuilder;
import com.yumi.android.sdk.ads.utils.network.WebTask;
import com.yumi.android.sdk.ads.utils.network.WebTaskHandler;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AlimamaApiRequest extends ApiRequest {
    private static final String TAG = "ALiMaMaApiRequest";
    /**http://afpapi.alimama.com/api*/
    private static final String API_URL = "nAGqWZw6f01UAEVjOCjRuv7TornDaxMPqERsN/62q/o=";
    private Context context;
    private IAlimamaAPIRequestListener listener;
    private WebTaskHandler task;
    private AlimamaResultBean resultData;
    
    private static final String[] REQ_KEY = new String[] { 
        "aid",
        "net",
        "netp",
        "mnc",
        "adnm",
        "apvc",
        "apvn", 
        "ip", 
        "ict", 
        "lt", 
        "c",
        "ct",
        "extdata",
        "bn", 
        "mn", 
        "os",
        "osv",
        "mcc",
        "sz",
        "rs", 
        "mac", 
        "imei", 
        "imei_enc",
        "dpr"};

    public AlimamaApiRequest(Context context, IAlimamaAPIRequestListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @SuppressWarnings("unchecked")
    public void requestApi(String aid, String ip,String sz) {
//        JSONObject request = new JSONObject();
//        String mcc = "";
        Map<String, Object> params = WebParamsMapBuilder
                .buildParams(
                        YumiDes3Util.decodeDes3(API_URL),
                        REQ_KEY,
                        buildRepValues(aid, ip, sz));
        if (task != null) {
            task.cancelTask();
        }
        task = new WebTaskHandler(context, new WebTask() {

            @Override
            public void doTask(String data, String msg) {
                if (NullCheckUtils.isNotNull(data)) {
                    Log.d(TAG, "data = " + data.toString());
                    dealWithData(data);
                }
            }
        }, false, false);
        task.executeOnPool(params);
    }

    private String[] buildRepValues(String aid, String ip,String sz) {
       
        try {
            String mcc = "";
            Location loc = LocationHandler.getLocHandler().getLastKnownLocation(context);
            int[] metrics = PhoneInfoGetter.getDisplayMetrics(context);
            TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simOperator = tel.getSimOperator();
            if (simOperator != null && simOperator.length() > 3) {
                mcc = simOperator.substring(0, 3);
            }

             String[] values = new String[] {
                aid,
                getNetValue() + "",
                getConn() + "",
                getOperator(context),
                getApplicationName(context),
                getVersion(context),
                context.getPackageName(),
                ip,
                null == loc ? "" : loc.getLongitude() + "," + loc.getLatitude(),
                isPortrait() + "",
                "1",
                "",
                "",
                PhoneInfoGetter.getBrand(),
                PhoneInfoGetter.getModel(),
                "Android",
                PhoneInfoGetter.getSysVersion(),
                mcc,
                sz,
                metrics.clone()[0] + "*" + metrics.clone()[1],
                PhoneInfoGetter.getMAC(context),
                PhoneInfoGetter.getIMEI(context),
                Encrypter.doMD5Encode(PhoneInfoGetter.getIMEI(context)),
                "1.0"                            
              };
            return values;

        } catch (Exception e) {
            ZplayDebug.e(TAG, "Alimama requestApi build parameter error :", e, onoff);
        }
        return null;
    }
    
    private String getConn() {
        String connectedNetName = NetworkStatusHandler.getConnectedNetName(context);
        String conn = "";
        if (NullCheckUtils.isNotNull(connectedNetName)) {
            if (connectedNetName.equalsIgnoreCase("wifi")) {
                conn = "wifi";
            }
            if (connectedNetName.equalsIgnoreCase("2g")) {
                conn = "2g";
            }
            if (connectedNetName.equalsIgnoreCase("3g")) {
                conn = "3g";
            }
            if (connectedNetName.equalsIgnoreCase("4g")) {
                conn = "4g";
            }
        }
        return conn;
    }

    public String getOperator(Context context) {
        try {
            String imsi = PhoneInfoGetter.getIMSI(context);
            if (imsi != null && imsi.length() > 0) {
                if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
                    return "00";
                }
                if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
                    return "01";
                }
                if (imsi.startsWith("46003") || imsi.startsWith("46005") || imsi.startsWith("46011")) {
                    return "03";
                }
                if (imsi.startsWith("46020")) {
                    return "04";
                }
            }
        } catch (Exception e) {

        }
        return "05";
    }

    public String getNetValue() {
        try {
            NetworkInfo ninfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (ninfo == null) {
                return "unknown";
            }
            if (ninfo.getState() == NetworkInfo.State.UNKNOWN) {
                return "unknown";
            } else if (ninfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return "wifi";
            } else if (ninfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return "cell";
            }
        } catch (Exception e) {

        }
        return "unknown";
    }

    private int isPortrait() {
        int lt = 0;
        if (WindowSizeUtils.isPortrait(context)) {
            lt = 1;
        } else {
            lt = 2;
        }
        return lt;
    }


    public void onDestroy() {
        if (task != null) {
            task.cancel(true);
        }
    }

    public String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
            return applicationName;
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        return "";
    }
    
    public static String getVersion(Context context)//获取版本号  
    {  
        try {  
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return pi.versionName;  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
            return "";  
        }  
    } 
    
    public interface IAlimamaAPIRequestListener {
        public void onAPIRequestDone(AlimamaResultBean data, LayerErrorCode error);
        
    }
    
    private void dealWithData(String data) {
        try {
            AlimamaResultBean aliBean=new AlimamaResultBean();
            JSONObject json = new JSONObject(data);
            String status = JsonResolveUtils.getStringFromJson(json, "status", "");
            aliBean.setStatus(status);
            if (!"ok".equals(status)) {//返回错误
                String errcode = JsonResolveUtils.getStringFromJson(json, "errcode", "");
                aliBean.setErrcode(errcode);
                ZplayDebug.i(TAG, "Alimama result [status]:"+status +" [errcode]:"+errcode, onoff);
                if ("204".equals(errcode)) {
                    requestThirdApiFailed(LayerErrorCode.ERROR_NO_FILL);
                } else {
                    requestThirdApiFailed(LayerErrorCode.ERROR_INTERNAL);
                }
                return;
            } else {
                JSONArray adArray = json.getJSONArray("ad");
                JSONObject adObj=adArray.getJSONObject(0);
                aliBean.setTid(adObj.optInt("tid"));
                JSONObject set = adObj.getJSONObject("set");
                aliBean.setAdtype(JsonResolveUtils.getIntFromJson(set, "atype", -1));
                JSONArray creativeArray = adObj.getJSONArray("creative");
                JSONObject creative = creativeArray.getJSONObject(0);
                
                JSONArray impressionArray=creative.getJSONArray("impression");
                for(int i=0;i<impressionArray.length();i++){
                    aliBean.getImpressionReportUrl().add(impressionArray.optString(i,""));
                }
                
                JSONArray clickArray=creative.getJSONArray("click");
                for(int i=0;i<clickArray.length();i++){
                    aliBean.getClickReportUrl().add(clickArray.optString(i,""));
                } 
                
                JSONArray downloadArray=creative.getJSONArray("download");
                for(int i=0;i<downloadArray.length();i++){
                    aliBean.getDownloadReportUrl().add(downloadArray.optString(i,""));
                }
                
                JSONObject media=creative.getJSONObject("media");
                aliBean.setMediaType(media.optInt("type",-1));
                aliBean.setW(media.optInt("w",-1));
                aliBean.setH(media.optInt("h",-1));
                aliBean.setEventId(media.optInt("event",-1));
                aliBean.setImg_url(media.optString("img_url",""));
                aliBean.setTitle(media.optString("title",""));
                aliBean.setH5_snippet(media.optString("h5_snippet",""));
                aliBean.setH5_url(media.optString("h5_url",""));
                aliBean.setClick_url(media.optString("click_url",""));
                aliBean.setDownload_url(media.optString("download_url",""));
                aliBean.setAd_words(media.optString("ad_words",""));
                listener.onAPIRequestDone(aliBean, null);
                resultData=aliBean;
            }

        } catch (JSONException e) {
            ZplayDebug.e(TAG, "Alimama result json parse error :", e, onoff);
            requestThirdApiFailed(LayerErrorCode.ERROR_INTERNAL);
        }
    }
    
    private void requestThirdApiFailed(LayerErrorCode error) {
        listener.onAPIRequestDone(null, error);
    }
    
    
    public void reportImpression() {
        if (resultData != null) {
            report(resultData.getImpressionReportUrl());
        }
    }

    public void reportClick() {
        if (resultData != null) {
            report(resultData.getClickReportUrl());
        }
    }
    
    private void report(ArrayList<String> urls) {
        if (urls != null && urls.size() > 0) {
            // 上报统计服务器
            for (String url : urls) {
                final String reportUrl = url;
                reportThird(reportUrl);
            }
        } else {
            ZplayDebug.e(TAG, "alimama 第三方上报地址为空", onoff);
        }
    }
    
    /**
     * 上报第三方
     * 
     * @param thirdurl
     * @param entity
     */
    private void reportThird(final String thirdurl) {
//        ZplayDebug.v(TAG, "alimama 准备上报第三方监播", onoff);
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(thirdurl);
//                    ZplayDebug.v(TAG, "alimama 第三方监播地址:" + thirdurl, onoff);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setInstanceFollowRedirects(true);
                    conn.setConnectTimeout(5000);
                    conn.setDoInput(false);
                    conn.getContentLength();
                    conn.disconnect();
                } catch (Exception e) {
                    String err = Log.getStackTraceString(e);
                    ZplayDebug.e(TAG, "alimama 第三方监播异常:" + err, onoff);
                }
            }
        };
        new Thread(run).start();
    }
//  try {   
//  Location loc = LocationHandler.getLocHandler().getLastKnownLocation(context);
//  int[] metrics = PhoneInfoGetter.getDisplayMetrics(context);
//  TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//  String simOperator = tel.getSimOperator();
//  if (simOperator != null && simOperator.length() > 3) {
//      mcc = simOperator.substring(0, 3);
//  }
//  request.put("aid", aid);
//  request.put("net", getNetValue() + "");
//  request.put("netp", getConn() + "");
//  request.put("mnc", getOperator(context));
//  request.put("adnm", getApplicationName(context));
//  request.put("apvc", getVersion(context));
//  request.put("apvn", context.getPackageName());
//  request.put("ip", ip);
//  request.put("ict", null == loc ? "" : loc.getLongitude() + "," + loc.getLatitude());
//  request.put("lt", isPortrait() + "");
//  request.put("c", "1");
//  request.put("ct", "");
//  request.put("extdata", "");
//  request.put("bn", PhoneInfoGetter.getBrand());
//  request.put("mn", PhoneInfoGetter.getModel());
//  request.put("os", "Android");
//  request.put("osv", PhoneInfoGetter.getSysVersion());
//  request.put("mcc", mcc);
//  request.put("sz", sz);
//  request.put("rs", metrics.clone()[0] + "*" + metrics.clone()[1]);
//  request.put("mac", PhoneInfoGetter.getMAC(context));
//  request.put("imei", PhoneInfoGetter.getIMEI(context));
//  request.put("imei_enc", Encrypter.doMD5Encode(PhoneInfoGetter.getIMEI(context)));
//  request.put("dpr", "1.0");
//} catch (JSONException e) {
//  ZplayDebug.e(TAG, "Alimama requestApi build parameter error :", e, onoff);
//}
//Map<String, Object> params = WebParamsMapBuilder.buildParams(API_URL, request.toString());
        
}
