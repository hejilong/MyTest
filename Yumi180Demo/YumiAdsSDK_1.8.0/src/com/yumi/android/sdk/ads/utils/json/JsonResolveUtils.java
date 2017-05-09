package com.yumi.android.sdk.ads.utils.json;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yumi.android.sdk.ads.utils.ZplayDebug;

public final class JsonResolveUtils {


	private static final String TAG = "JsonResolveUtils";
	private static final boolean onoff = true;

	public static double getDoubleFromJson(JSONObject json, String key, double defaultVaule) {
		double result = defaultVaule;
		if (json != null) {
			try {
				if (json.has(key)) {
					result = json.getDouble(key);
				}
			} catch (JSONException e) {
				e.toString();
			}
		}
		return result;
	}

	public static boolean getBooleanFromJson(JSONObject json, String key) {
		boolean result = false;
		if (json != null) {
			try {
				if (json.has(key)) {
					result = json.getBoolean(key);
				}
			} catch (JSONException e) {
				e.toString();
			}
		}
		return result;
	}
	
	
	public static long getLongFromJson(JSONObject json, String key, long defaultValue) {
		long result = defaultValue;
		if (json != null) {
			try {
				if (json.has(key)) {
					result = json.getLong(key);
				}
			} catch (JSONException e) {
				e.toString();
			}
		}
		return result;
	}

	public static int getIntFromJson(JSONObject json, String key, int defaultValue) {
		int result = defaultValue;
		if (json != null) {
			try {
				if (json.has(key)) {
					result = json.getInt(key);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String getStringFromJson(JSONObject json, String key, String defaultValue) {
		String result = defaultValue;
		if (json != null) {
			try {
				if (json.has(key)) {
					result = json.getString(key);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static JSONArray getJsonArrayFromJson(JSONObject json, String key) {
		JSONArray array = null;
		if (json != null) {
			try {
				if (json.has(key)) {
					array = json.getJSONArray(key);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return array;
	}

	public static JSONObject getJsonObjectFromJson(JSONObject json, String key) {
		JSONObject obj = null;
		if (json != null) {
			try {
				if (json.has(key)) {
					obj = json.getJSONObject(key);
				}
			} catch (JSONException e) {
				ZplayDebug.e(TAG, "", e, onoff);
			}
		}
		return obj;
	}

	public static <T> T resolveJson2JaveBean(JSONObject json, Class<T> clazz) {
		T obj = null;
		if (json != null) {
			try {
				obj = clazz.newInstance();
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					String simpleName = field.getType().getName();
					if (simpleName.startsWith("[")) { // array
						
					} else if (simpleName.endsWith("List")){ // list
						Type type = field.getGenericType();
						if (type instanceof ParameterizedType) {
							Type[] rawType = ((ParameterizedType) type).getActualTypeArguments();
							if (rawType[0] != null && rawType[0] instanceof Class) {
								List<Object> list = null;
								JSONArray jsonArrayFromJson = JsonResolveUtils.getJsonArrayFromJson(json, field.getName());
								if (jsonArrayFromJson != null && jsonArrayFromJson.length() > 0) {
									list = new ArrayList<Object>();
									for (int i = 0; i < jsonArrayFromJson.length(); i++) {
										list.add(resolveJson2JaveBean(jsonArrayFromJson.getJSONObject(i), (Class<?>)rawType[0]));
									}
								}
								field.set(obj, list);
							}
						}
						
					}else if(simpleName.endsWith("Map"))
					{
						Type type = field.getGenericType();
						if (type instanceof ParameterizedType) {
							Type[] rawType = ((ParameterizedType) type).getActualTypeArguments();
							if (rawType[0] != null && rawType[0] instanceof Class) {
								Map<Object,Object> map = null;
								JSONObject jsonObjectFromJson = JsonResolveUtils.getJsonObjectFromJson(json, field.getName());
								if (jsonObjectFromJson != null)
								{
									map = new HashMap<>();
									Iterator<String> keys = jsonObjectFromJson.keys();
									while(keys.hasNext())
									{
										String key = keys.next();
										map.put(key, jsonObjectFromJson.getString(key));
									}
								}
								field.set(obj, map);
							}
						}
					}
					else {
						if (simpleName.endsWith("String")) {
							field.set(obj, JsonResolveUtils.getStringFromJson(json, field.getName(),""));
						} else if (simpleName.endsWith("int") || simpleName.endsWith("Integer")) {
							field.set(obj, JsonResolveUtils.getIntFromJson(json, field.getName(),0));
						} else if (simpleName.endsWith("long")) {
							field.set(obj, JsonResolveUtils.getLongFromJson(json, field.getName(),0L));
						} else if (simpleName.endsWith("double")) {
							field.set(obj, JsonResolveUtils.getDoubleFromJson(json, field.getName(),0.0d));
						}else if (simpleName.endsWith("boolean")) {
							field.set(obj, JsonResolveUtils.getBooleanFromJson(json, field.getName()));
						}else {
							field.set(obj, resolveJson2JaveBean(JsonResolveUtils.getJsonObjectFromJson(json, field.getName()), Class.forName(simpleName)));
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return obj;

	}

}
