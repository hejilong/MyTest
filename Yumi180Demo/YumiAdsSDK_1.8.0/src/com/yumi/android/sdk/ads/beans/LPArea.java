package com.yumi.android.sdk.ads.beans;

/**
 * 展示区域
 * (当前广告位的左上角坐标及真实像素的宽高)
 * @author Administrator
 *
 */
public class LPArea {
    
    private String width;
    private String height;
    private String showX;
    private String showY;

    public LPArea()
    {
    }
    
    public LPArea(String width, String height, String showX, String showY)
    {
        this.width = width;
        this.height = height;
        this.showX = showX;
        this.showY = showY;
    }

    public String getwidth()
    {
        return width;
    }

    public String getheight()
    {
        return height;
    }

    public String getshowX()
    {
        return showX;
    }

    public String getshowY()
    {
        return showY;
    }
}
