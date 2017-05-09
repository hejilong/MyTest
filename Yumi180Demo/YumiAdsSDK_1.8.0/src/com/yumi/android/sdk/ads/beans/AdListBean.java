package com.yumi.android.sdk.ads.beans;

public class AdListBean
{
	
	private String adType;
	private String action;
	private String result;
	private String interfaceType;
	private String pid;
	private String providerID;
	private String eventTime;
	private String keyID;
	private ClickArea clickArea;
	private LPArea lPArea; //展示区域
	private int templateID;
	
    public AdListBean(String adType, String action, String result, String interfaceType, String pid, String providerID, String keyID, ClickArea clickArea) {
        this.adType = adType;
        this.action = action;
        this.result = result;
        this.interfaceType = interfaceType;
        this.pid = pid;
        this.providerID = providerID;
        this.eventTime = String.valueOf(System.currentTimeMillis());
        this.keyID = keyID;
        this.clickArea = clickArea;
    }
    
//    public AdListBean(String adType, String action, String result, String interfaceType, String pid, String providerID, String keyID, ClickArea clickArea,LPArea lPArea) {
//        this.adType = adType;
//        this.action = action;
//        this.result = result;
//        this.interfaceType = interfaceType;
//        this.pid = pid;
//        this.providerID = providerID;
//        this.eventTime = String.valueOf(System.currentTimeMillis());
//        this.keyID = keyID;
//        this.clickArea = clickArea;
//        this.lPArea = lPArea;
//    }
    
    public AdListBean(String adType, String action, String result, String interfaceType, String pid, String providerID, String keyID, ClickArea clickArea,LPArea lPArea, int templateID) {
        this.adType = adType;
        this.action = action;
        this.result = result;
        this.interfaceType = interfaceType;
        this.pid = pid;
        this.providerID = providerID;
        this.eventTime = String.valueOf(System.currentTimeMillis());
        this.keyID = keyID;
        this.clickArea = clickArea;
        this.lPArea = lPArea;
        this.templateID = templateID;
    }
	
	public String getAdType()
	{
		return adType;
	}
	public String getAction()
	{
		return action;
	}
	public String getResult()
	{
		return result;
	}
	public String getInterfaceType()
	{
		return interfaceType;
	}
	public String getPid()
	{
		return pid;
	}
	public String getProviderID()
	{
		return providerID;
	}

	public String getEventTime()
	{
		return eventTime;
	}
	
	public String getKeyID()
	{
		return keyID;
	}

	public ClickArea getClickArea()
	{
		return clickArea;
	}
	
	public LPArea getLPArea()
    {
        return lPArea;
    }

	public int getTemplateID()
	{
		return templateID;
	}
	
}
