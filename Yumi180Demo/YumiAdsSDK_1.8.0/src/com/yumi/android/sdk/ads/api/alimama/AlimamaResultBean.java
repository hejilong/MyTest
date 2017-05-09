package com.yumi.android.sdk.ads.api.alimama;

import java.util.ArrayList;

public class AlimamaResultBean {

    private String status; //返回状态 ok: 成功    failure:失败 
    private String errcode; 
    private int tid; //渲染模板 id
    private int adtype; //广告位类型  banner：6  ,全屏/插屏：15 ,焦点图:43  ,开屏:9 ,信息流：12 
    private ArrayList<String> impressionReportUrl=new ArrayList<String>(); //创意展示监测 
    private ArrayList<String> clickReportUrl=new ArrayList<String>(); //创意点击监测  
    private ArrayList<String> downloadReportUrl=new ArrayList<String>(); //下载完成监测  
    
    private int mediaType; //物料类型   1- 图片  ,2- 图文   ,3- html5 片段,4-  html5 url ,5  -视频  
    private int w;//素材尺寸，宽 单位：像素 
    private int h;//素材尺寸，高 单位：像素
    private int eventId;//交互动作   1-打开网页  ,2-下载应用  ,3-拨打电话  ,4-播放视频  ,5-播放音频  ,6-打开地图 
    
    private String img_url;//图片url 
    private String title;//广告语 
    private String h5_snippet;//html5 片段创意 
    private String h5_url ;//html5 url 创意  
    private String click_url  ;//点击url 
    private String download_url ;//下载url 
    private String ad_words ;//副标题 
    
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getErrcode() {
        return errcode;
    }
    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }
    
    public int getTid() {
        return tid;
    }
    public void setTid(int tid) {
        this.tid = tid;
    }
    public int getAdtype() {
        return adtype;
    }
    public void setAdtype(int adtype) {
        this.adtype = adtype;
    }

    public ArrayList<String> getImpressionReportUrl() {
        return impressionReportUrl;
    }
    public void setImpressionReportUrl(ArrayList<String> impressionReportUrl) {
        this.impressionReportUrl = impressionReportUrl;
    }
    public ArrayList<String> getClickReportUrl() {
        return clickReportUrl;
    }
    public void setClickReportUrl(ArrayList<String> clickReportUrl) {
        this.clickReportUrl = clickReportUrl;
    }
    public ArrayList<String> getDownloadReportUrl() {
        return downloadReportUrl;
    }
    public void setDownloadReportUrl(ArrayList<String> downloadReportUrl) {
        this.downloadReportUrl = downloadReportUrl;
    }
    public int getMediaType() {
        return mediaType;
    }
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }
    public int getW() {
        return w;
    }
    public void setW(int w) {
        this.w = w;
    }
    public int getH() {
        return h;
    }
    public void setH(int h) {
        this.h = h;
    }
    public int getEventId() {
        return eventId;
    }
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    public String getImg_url() {
        return img_url;
    }
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getH5_snippet() {
        return h5_snippet;
    }
    public void setH5_snippet(String h5_snippet) {
        this.h5_snippet = h5_snippet;
    }
    public String getH5_url() {
        return h5_url;
    }
    public void setH5_url(String h5_url) {
        this.h5_url = h5_url;
    }
    public String getClick_url() {
        return click_url;
    }
    public void setClick_url(String click_url) {
        this.click_url = click_url;
    }
    public String getDownload_url() {
        return download_url;
    }
    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }
    public String getAd_words() {
        return ad_words;
    }
    public void setAd_words(String ad_words) {
        this.ad_words = ad_words;
    }

    
    
}
