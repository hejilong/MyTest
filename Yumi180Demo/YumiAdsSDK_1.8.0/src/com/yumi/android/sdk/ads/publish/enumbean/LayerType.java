package com.yumi.android.sdk.ads.publish.enumbean;

public enum LayerType {

	
	TYPE_BANNER("2"),
	TYPE_INTERSTITIAL("3"),
	TYPE_MEDIA("5"),
	TYPE_SPLASH("6"),
	TYPE_OFFERWALL("7"),
	TYPE_STREAM("8");
	
	private String type ;
	private LayerType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
	
}
