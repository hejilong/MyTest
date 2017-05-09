package com.yumi.android.sdk.ads.utils.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * 从流中读取数据
 * 
 * @author laohuai
 */
public final class StreamParser {

//	/**
//	 * 从流中读取数据构造成一个String返回
//	 * 
//	 * @param is
//	 * @return
//	 */
//	public static String parseStream1(InputStream is) {
//		return parseStream(is, "UTF-8");
//	}
	
	/**
	 * 从流中读取数据构造成一个String返回
	 * @param is
	 * @param charset
	 * @return
	 */
	public static String parseStream(InputStream is, String charset) {
		String result = null;
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				sb.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		result = sb.toString();
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
