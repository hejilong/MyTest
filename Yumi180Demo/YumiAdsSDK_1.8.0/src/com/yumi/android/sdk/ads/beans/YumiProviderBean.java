package com.yumi.android.sdk.ads.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.yumi.android.sdk.ads.publish.enumbean.ProviderID;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public final class YumiProviderBean {
	
//	private String providerName;
	private String providerID;
	private int ratio;
	private int priority;
//	private String key1;
//	private String key2;
//	private String key3;
	private String keyExtra;
	private int reqType;
	private int outTime;
	private String keyID;//1.2.0
	private String browserType;//1.2.0
	private String closeBtn;//1.8.0
	private String template;
	private int interstitialsEtime;//1.8.0
	private YumiGlobalBean global;
	private Map<String, String> keys;
	
	private int useTemplateMode;
	
	public YumiProviderBean(){}
	
	public YumiProviderBean(YumiProviderBean bean){
//		this.providerName = bean.getProviderName();
		this.providerID = bean.getProviderID();
		this.ratio = bean.getRatio();
		this.priority = bean.getPriority();
//		this.key1 = bean.getKey1();
//		this.key2 = bean.getKey2();
//		this.key3 = bean.getKey3();
		this.keyExtra = bean.getKeyExtra();
		this.reqType = bean.getReqType();
		this.outTime = bean.getOutTime();
		this.global = bean.getGlobal();
		this.keys = bean.getKeys();
	}
	
	public String getKeyID()
	{
		return keyID;
	}
	
	public String getBrowserType()
	{
		return browserType;
	}

	public final int getOutTime() {
		return outTime;
	}

	public YumiGlobalBean getGlobal() {
		return global;
	}

	public final int getRatio() {
		return ratio;
	}

	public final int getPriority() {
		return priority;
	}

//	public final String getKey1() {
//		if (NullCheckUtils.isNotNull(key1)) {
//			return key1.trim();
//		}
//		return key1;
//	}
//
//	public final String getKey2() {
//		if (NullCheckUtils.isNotNull(key2)) {
//			return key2.trim();
//		}
//		return key2;
//	}
//
//	public final String getKey3() {
//		if (NullCheckUtils.isNotNull(key3)) {
//			return key3.trim();
//		}
//		return key3;
//	}
	
	public final String getKey1() {
		String key = "";
		if (keys!=null && keys.size()>0)
		{
			key = keys.get("key1");
			if (key==null)
			{
				key = "";
			}
		}
//		ZplayDebug.v("YumiProviderBean", "key1="+key);
		return key;
	}

	public final String getKey2() {
		String key = "";
		if (keys!=null && keys.size()>0)
		{
			key = keys.get("key2");
			if (key==null)
			{
				key = "";
			}
		}
//		ZplayDebug.v("YumiProviderBean", "key2="+key);
		return key;
	}

	public final String getKey3() {
		String key = "";
		if (keys!=null && keys.size()>0)
		{
			key = keys.get("key3");
			if (key==null)
			{
				key = "";
			}
		}
//		ZplayDebug.v("YumiProviderBean", "key3="+key);
		return key;
	}

	public final String getKeyExtra() {
		if (NullCheckUtils.isNotNull(keyExtra)) {
			return keyExtra.trim();
		}
		return keyExtra;
	}

	public final int getReqType() {
		return reqType;
	}

	public final String getProviderName() {
		String providerNameByID = ProviderID.getProviderNameByID(providerID);
		return providerNameByID;
	}


	public int getInterstitialsEtime() {
		return interstitialsEtime;
	}

	public Map<String ,Integer> getCloseBtn() {
		Map<String ,Integer> closeBtnMap = new HashMap<String ,Integer>();
		try {
			if(closeBtn != null){
				JSONObject closeBtnObject = new JSONObject(closeBtn);
				int position = closeBtnObject.getInt("position");
				JSONObject picObject = closeBtnObject.getJSONObject("pic");
				int picW = picObject.getInt("w");
				int picH = picObject.getInt("h");
				JSONObject areaObject = closeBtnObject.getJSONObject("area");
				int areaW = areaObject.getInt("w");
				int areaH = areaObject.getInt("h");
				closeBtnMap.put("position", position);
				closeBtnMap.put("picW", picW);
				closeBtnMap.put("picH", picH);
				closeBtnMap.put("areaW", areaW);
				closeBtnMap.put("areaH", areaH);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return closeBtnMap;
	}

	public String getProviderID()
	{
		return providerID;
	}

	public void setProviderID(String providerID)
	{
		this.providerID = providerID;
	}

	public void setGlobal(YumiGlobalBean global) {
		this.global = global;
	}

//	public void setProviderName(String providerName) {
//		this.providerName = providerName;
//	}

	public void setRatio(int ratio) {
		this.ratio = ratio;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

//	public void setKey1(String key1) {
//		this.key1 = key1;
//	}
//
//	public void setKey2(String key2) {
//		this.key2 = key2;
//	}
//
//	public void setKey3(String key3) {
//		this.key3 = key3;
//	}

	public void setKeyExtra(String keyExtra) {
		this.keyExtra = keyExtra;
	}

	public void setReqType(int reqType) {
		this.reqType = reqType;
	}

	public void setOutTime(int outTime) {
		this.outTime = outTime;
	}
	
	private Map<String, String> getKeys()
	{
		return keys;
	}

	public Template getTemplate(int screenMode)
	{
		if (template!=null && !"".equals(template))
		{
			try
			{
				JSONObject templates_obj = new JSONObject(template);
				Template temp = new Template();
				JSONObject temp_obj = templates_obj.getJSONObject(String.valueOf(screenMode));
				temp.setScreenMode(screenMode);
				temp.setId(temp_obj.getInt("id"));
				temp.setTime(temp_obj.getLong("time"));
				return temp;
			} catch (JSONException e)
			{
				ZplayDebug.e("YumiProviderBean", "", e, true);
				return null;
			}
		}
		return null;
	}
	
	public List<Template> getTemplates()
	{
		List<Template> list = new ArrayList<Template>();
		if (template!=null && !"".equals(template))
		{
			try
			{
				JSONObject templates_obj = new JSONObject(template);
				Iterator<String> keys = templates_obj.keys();
				while(keys.hasNext())
				{
					String key = keys.next();
					Template temp = new Template();
					JSONObject temp_obj = templates_obj.getJSONObject(key);
					temp.setScreenMode(temp_obj.optInt("screenMode"));
					temp.setId(temp_obj.optInt("id"));
					temp.setTime(temp_obj.optLong("time"));
					list.add(temp);
				}
			} catch (JSONException e)
			{
				ZplayDebug.e("YumiProviderBean", "", e, true);
			}
		}
		return list;
	}

//	public boolean isTemplate()
//	{
//		return isTemplate;
//	}

	public int getUseTemplateMode()
	{
		return useTemplateMode;
	}

	public void setUseTemplateMode(int useTemplateMode)
	{
		this.useTemplateMode = useTemplateMode;
	}
	
}
