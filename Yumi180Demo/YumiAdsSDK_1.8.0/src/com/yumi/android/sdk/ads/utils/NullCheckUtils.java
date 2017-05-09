package com.yumi.android.sdk.ads.utils;

import java.util.Collection;
import java.util.Map;

public final class NullCheckUtils {

	public static final boolean isNotNull(String str){
		if (str != null && str.length() > 0 && !"null".equals(str)) {
			return true;
		}
		return false;
	}
	
	public static final boolean isNotEmptyCollection(Collection<?> collection){
		if (collection != null && collection.size() > 0) {
			return true;
		}
		return false;
	}
	
	public static final boolean isNotEmptyMap(Map<?, ?> map){
		if (map != null && map.size() > 0) {
			return true;
		}
		return false;
	}
}
