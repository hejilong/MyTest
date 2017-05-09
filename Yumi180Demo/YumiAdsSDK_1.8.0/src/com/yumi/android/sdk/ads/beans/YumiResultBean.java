package com.yumi.android.sdk.ads.beans;

import java.util.List;

public final class YumiResultBean {

	private int result;
	private String reqIP;
	private String cornID;
	private String uuid;
	private long spaceTime;
	private long planTime;
	private int interval;
	private int manualCancel;
	private int retryLimit;
	private int incentived;
	private int optimization;
	private String trans;
	private String logUrl;
	private int overlapRemove; //1.8.0 重叠移除 移除=>1 不移除=>0     描述 :如被遮盖，则移出当前广告位的广告控件
	
	private List<YumiProviderBean> providers;
	
	public int getResult()
	{
		return result;
	}
	public String getReqIP()
	{
		return reqIP;
	}
	public String getCornID()
	{
		return cornID;
	}
	public String getUuid()
	{
		return uuid;
	}
	public long getSpaceTime()
	{
		return spaceTime;
	}
	public long getPlanTime()
	{
		return planTime;
	}
	public int getInterval()
	{
		return interval;
	}
	public int getManualCancel()
	{
		return manualCancel;
	}
	public int getRetryLimit()
	{
		return retryLimit;
	}
	public int getIncentived()
	{
		return incentived;
	}
	public int getOptimization()
	{
		return optimization;
	}
	public List<YumiProviderBean> getProviders()
	{
		return providers;
	}
	public String getTrans()
	{
		return trans;
	}
	public String getLogUrl() {
		return logUrl;
	}
	
	public int getOverlapRemove()
    {
        return overlapRemove;
    }
	
	/*public final long getSpaceTime() {
		return spaceTime;
	}
	
	public final long getPlanTime() {
		return planTime;
	}
	
	public final String getCornId() {
		return cornId;
	}
	public final int getResult() {
		return result;
	}
	public final String getReqIP() {
		return reqIP;
	}
	public final int getInterval() {
		return interval;
	}
	public final int getManualCancel() {
		return manualCancel;
	}
	public final int getRetryLimit() {
		return retryLimit;
	}
	public final int getIncentived() {
		return incentived;
	}
	public final int getOptimization() {
		return optimization;
	}
	public final List<YumiProviderBean> getProviders() {
		return providers;
	}*/
}
