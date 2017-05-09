package com.yumi.android.sdk.ads.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.layer.YumiBaseLayer;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.publish.enumbean.ProviderID;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;

public abstract class YumiBaseAdapterFactory<T extends YumiBaseLayer> {

	protected static final boolean onoff = true;
	
	protected static final int REQ_TYPE_SDK = 1;
	protected static final int REQ_TYPE_API = 2;
	protected static final int REQ_TYPE_CUSTOMER = 3;
	protected Map<String, T> adapterObtain = new HashMap<String, T>();
	protected String TAG = "YumiBaseAdapterFactory";
	
	final T getAdapterInstance(Activity activity,
			YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		String key = buildObtainKey(provider);
		if (provider != null && NullCheckUtils.isNotNull(key)) {
			T t = adapterObtain.get(key);
			if (t != null) {
				t.isOutTime = false;
				t.setIsMediation(false);
				return t;
			} else {
				//API直接调用
				if (provider.getReqType() == REQ_TYPE_API)
				{
					t = getLayerByProvider(activity, provider, innerListener);
				}
				//自主直接调用
				else if(
						"20001".equals(ProviderID.getFlow5(provider.getProviderID()))
						|| "30001".equals(ProviderID.getFlow5(provider.getProviderID()))
						){
					t = getSelfLayer(activity, provider, innerListener);
				}else
				{
					t = reflectAdapterByProvider(activity, provider, innerListener);
				}
				if (t != null) {
					adapterObtain.put(buildObtainKey(provider), t);
				}
				return t;
			}
		}
		ZplayDebug.e(TAG, "provider is null  or provider  name  is  empty", onoff);
		return null;
	}

	@SuppressWarnings("unchecked")
	private T reflectAdapterByProvider(Activity activity,
			YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener) {
		T t = null;
		
		try
		{
			Class<T> clazz = null;
			clazz = (Class<T>) Class.forName(getPackageNameByProviderType(provider));
			Constructor<T> constructor = clazz
			        .getDeclaredConstructor(Activity.class, YumiProviderBean.class);
			constructor.setAccessible(true);
			t = constructor.newInstance(activity, provider);
			Class<?> superClazz = clazz.getSuperclass();
			while (superClazz != YumiBaseLayer.class)
			{
				superClazz = superClazz.getSuperclass();
			}
			Field inner = superClazz.getDeclaredField("mInnerListener");
			inner.setAccessible(true);
			inner.set(t, innerListener);
			Method init = clazz.getDeclaredMethod("init");
			init.setAccessible(true);
			init.invoke(t);
		}
		catch (ClassNotFoundException e)
		{
			ZplayDebug.w(
			        TAG,
			        "you request adapter , but you don't add the jar into your project , require jar is "
			                + provider.getProviderName(),
			        onoff);
			ZplayDebug.e(TAG, "", e, onoff);
		}
		catch (InstantiationException e)
		{
			ZplayDebug.e(TAG, "", e, onoff);
		}
		catch (IllegalAccessException e)
		{
			ZplayDebug.e(TAG, "", e, onoff);
		}
		catch (NoSuchMethodException e)
		{
			ZplayDebug.e(
			        TAG,
			        "no such method exception in class " + provider.getProviderName(),
			        onoff);
			ZplayDebug.e(TAG, "", e, onoff);
		}
		catch (IllegalArgumentException e)
		{
			ZplayDebug.e(TAG, "", e, onoff);
		}
		catch (InvocationTargetException e)
		{
			ZplayDebug.e(TAG, "", e, onoff);
			ZplayDebug.w(
			        TAG,
			        "you request adapter , but you don't add the jar into your project , require jar is "
			                + provider.getProviderName(),
			        onoff);
		}
		catch (NoSuchFieldException e)
		{
			ZplayDebug.e(TAG, "", e, onoff);
		}
		return t;
	}
	
	public String buildObtainKey(YumiProviderBean provider){
		if (provider != null && provider.getGlobal() != null) {
			StringBuilder builder = new StringBuilder("");
			builder.append(provider.getGlobal().getYumiID());
			builder.append(provider.getProviderName());
			builder.append(provider.getProviderID());
			return builder.toString();
		}
		return null;
	}
	

	protected final String uppercaseFirstWorld(String providerName) {
		char[] charArray = providerName.toCharArray();
		if (charArray[0] >= 97 && charArray[0] <= 122) {
			charArray[0] -= 32;
		}
		return String.valueOf(charArray);
	}

	public final void releaseFactory() {
		ZplayDebug.i(TAG, "factory release", onoff);
		if (adapterObtain != null) {
			adapterObtain.clear();
		}
	}

	protected abstract String getPackageNameByProviderType(YumiProviderBean provider);
	
	protected abstract T getLayerByProvider(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener);
	protected abstract T getSelfLayer(Activity activity, YumiProviderBean provider, IYumiInnerLayerStatusListener innerListener);

	public final void clearAdapterObtain(){
		if (NullCheckUtils.isNotEmptyMap(adapterObtain)) {
			Set<Entry<String,T>> entrySet = adapterObtain.entrySet();
			for (Entry<String, T> entry : entrySet) {
				entry.getValue().onActivityDestroy();
			}
			adapterObtain.clear();
		}
	}
	
}
