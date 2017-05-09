package com.yumi.android.sdk.ads.api.alimama;

public enum AlimamaMediaTypeEnum {

    TYPE_IMAGE(1),
    TYPE_IMAGE_TEXT(2),
    TYPE_HTML(3),
    TYPE_HTML_URL(4),
    TYPE_VIDEO(5);
    
    private int type ;
    private AlimamaMediaTypeEnum(int type){
        this.type = type;
    }
    
    public int getType(){
        return type;
    }
    
}
