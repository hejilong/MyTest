package com.yumi.android.sdk.ads.utils.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public final class ParamsBuilder {
	/**
	 * 构造形如"?params1=values1&params2=values2..."形式的字符串
	 * 
	 * @param keys
	 *            参数名
	 * @param values
	 *            参数值
	 * @return
	 */
	public static String buildGetParams(String[] keys, String[] values) {
		StringBuffer sb = new StringBuffer();
		if (keys != null && keys.length > 0) {
			sb.append("?");
			// sb2中数据应该是URLEncode以后的"?a=1&b=2&c=3"
			for (int i = 0; i < keys.length; i++) {
				try {
					sb.append(keys[i] + "="
							+ URLEncoder.encode(values[i], "utf-8") + "&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	
	/**
     * 构造形如"?params1=values1&params2=values2..."形式的字符串, 参数value不编码
     * 
     * @param keys
     *            参数名
     * @param values
     *            参数值
     * @return
     */
    public static String buildGetParamsNotEncode(String[] keys, String[] values) {
        StringBuffer sb = new StringBuffer();
        if (keys != null && keys.length > 0) {
            sb.append("?");
            // sb2中数据应该是URLEncode以后的"?a=1&b=2&c=3"
            for (int i = 0; i < keys.length; i++) {
                try {
                    sb.append(keys[i] + "=" + values[i] + "&");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

	/**
	 * post方式请求网络
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public static List<NameValuePair> buildPostParams(String[] keys,
			String[] values) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (int i = 0; i < keys.length; i++) {
			BasicNameValuePair nameValuePair = new BasicNameValuePair(keys[i],
					values[i]);
			params.add(nameValuePair);
		}
		return params;
	}
}
