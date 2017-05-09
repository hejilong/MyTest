package com.yumi.android.sdk.ads.utils.network;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于构建{@linkplain WebTaskHandler}在执行时候需要的参数
 * 
 * @author glzlaohuai
 * @version 2014-6-12
 */
public final class WebParamsMapBuilder {

	public static final int SINGLE_VALUE = 0;
	public static final int KEY_VALUE_PAIR = 1;

	/**
	 * 单个值形式
	 * 
	 * @param url
	 * @param value
	 * @return
	 */
	public static Map<String, Object> buildParams(String url, String value) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", url);
		map.put("value", value);
		map.put("type", SINGLE_VALUE);
		return map;
	}

	/**
	 * 键值对的形式
	 * 
	 * @param url
	 * @param keys
	 * @param values
	 * @return
	 */
	public static Map<String, Object> buildParams(String url, String[] keys,
			String[] values) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", url);
		map.put("keys", keys);
		map.put("values", values);
		map.put("type", KEY_VALUE_PAIR);
		return map;
	}
}
