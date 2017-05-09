package com.yumi.android.sdk.ads.beans;

public final class YumiGlobalBean {

	private String yumiID;
	private String channelID;
	private String versionName;
	private int retryLimit;
	private int incentived;
	private String reqIP;
	private long planTime;

	public long getPlanTime() {
		return planTime;
	}

	private int optimization;
	private boolean canManualCancel;

	public YumiGlobalBean(YumiResultBean result, String yumiID,
			String channelID, String versionName) {
		this.yumiID = yumiID;
		this.channelID = channelID;
		this.versionName = versionName;
		this.reqIP = result.getReqIP();
		this.retryLimit = result.getRetryLimit();
		this.incentived = result.getIncentived();
		this.canManualCancel = result.getManualCancel() == 0 ? false : true;
		this.planTime = result.getPlanTime();
	}

	public YumiGlobalBean() {
		
	}
	
	public final int getOptimization() {
		return optimization;
	}

	public final boolean canManualCancel() {
		return canManualCancel;
	}

	public final int getIncentived() {
		return incentived;
	}

	public final String getReqIP() {
		return reqIP;
	}

	public final String getYumiID() {
		return yumiID;
	}

	public final String getChannelID() {
		return channelID;
	}

	public final String getVersionName() {
		return versionName;
	}

	public final int getRetryLimit() {
		return retryLimit;
	}

}
