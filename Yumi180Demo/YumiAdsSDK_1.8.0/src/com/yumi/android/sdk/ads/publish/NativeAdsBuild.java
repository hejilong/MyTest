package com.yumi.android.sdk.ads.publish;

import com.yumi.android.sdk.ads.beans.Template;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.utils.SharedpreferenceUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.PhoneInfoGetter;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

public class NativeAdsBuild {

    public static final boolean onoff = true;
    private static final String TAG = "NativeAdsBuild";
    
    /**
	 * 默认图片广告模板
	 */
	private static final String AD_IMAGE_DEFAULT= 
			"<!DOCTYPE HTML>"+
		    "<html>"+
		    "<head>"+
		    "<meta charset=\"utf-8\">"+
		    "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">"+
		    "<meta name=\"format-detection\" content=\"telephone=no\">"+
		    "<meta name=\"format-detection\" content=\"email=no\">"+
		    "<meta name=\"format-detection\" content=\"address=no;\">"+
		    "<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">"+
		    "<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"default\">"+
		    "<meta name=\"wap-font-scale\" content=\"no\">"+
		    "<title>APP广告</title>"+
		    "<style>"+
		    "body{background-color:rgba(0,0,0,0.5);}"+
		    "img{ width:100%; position:fixed; left: 0; bottom:0; top:0; right:0; margin: auto; padding:0; }"+
		    ".main_bg{position:absolute;width:100%;height:100%;}"+
		    ".flag{position:absolute;bottom:4px;left:0;}"+
		    ".flag img{height:16px;width:30px;z-index:100;}"+
		    "</style>"+
		    "</head>"+
		    "<body>"+
		    "<div class=\"main_bg\">"+
		        "<a href=\"zflag_aTagUrl\"><img src=\"zflag_imageUrl\" /></a>"+
		    "</div>"+
		    "<a href=\"\" class=\"flag\" ><img class=\"flag\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACIAAAAQCAYAAABz9a1kAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyFpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIChXaW5kb3dzKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpBQkNCRjZBRTlBNjExMUU1QTkyNTk4QTkzRjMyNDI2QyIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDpBQkNCRjZBRjlBNjExMUU1QTkyNTk4QTkzRjMyNDI2QyI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOkFCQ0JGNkFDOUE2MTExRTVBOTI1OThBOTNGMzI0MjZDIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkFCQ0JGNkFEOUE2MTExRTVBOTI1OThBOTNGMzI0MjZDIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+PULWlgAAAhdJREFUeNq0Vj1PIlEUnRmeDA74ETGhI5HKil/gF1GDIfbb2dnuH7A0VlpZaudW2xuyRis1ak1lKDRaCRECK7Iz8Aa9x7xHRpwPWfEmN5OZe/Pueeede9+oyWRS6bEJ4VFypgzWOPkzeVV415yFdPIUuaF8n6HemPAE+Q25hYDmADHtBSJCViwWf+E5QFCGqKk7gaT8jkEPh/XMwvxmoVDYdwPjBNonaCZqKyppBHqYclschf1WSafT6ybZZHwicXF5tROU57PULRDF3SKxqIFzVHIr2YPOS8fUVC2iaSG907EtvHPO62AKOQCBvDeKKecwn/+xmsv95na7LvMCgMSZly4YY29FbNuu/TPN0gf5c25ZrZYlAef/HK054wDTByvDLKhFAeKhVL73igPISnZ5D4y5MSdAB7HCvjwnGs/NOrF3d3p2vuWVMzc7swH2glTLvzK4sEseNSynnmQM7ODIcIwBGuEA0CQf7e0Y7DC7vLSLRQJ70ENPw5FIwhn3UwCAVJxAZNsuLWa2W612DdR/lp2j45Of/0lshYmZn5DdI4cX7c6s1f+WAyh9Z7Jlu0yFhsZ6u8fFcCJVVVx6csQzMAIwENdnQWCgjcRiSZs6BSzK7+Hw0HiIOuip0bh/rFRLHpfgNe4bKVJLfEihdj8syM5pt3mxFzztqSw35cFE99JTfX4DDNFN6oAuuRfBQNPtN+BVgAEA00skRimfV18AAAAASUVORK5CYII=\" /></a>"+
		    "</body>"+
		    "</html>";
	private static final String BANNER_IMAGETEXT_DEFAULT_PHONE= "<!doctype html>"
            +"<html>"
            + "<meta charset=\"utf-8\">"
            + "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">"
            + "<head>"
            + "<style>"
            + "*{margin:0;padding:0}a{position:fixed;left:0;top:0;width:100%;height:100%;text-decoration:none;overflow:hidden;background:#fff}.wrap{position:absolute;left:0;top:50%;width:100%;overflow:hidden}#icon{position:absolute;left:5px;top:5px}.box{width:100%;box-sizing:border-box}.title{font-weight:bold;color:#333;overflow:hidden}.textWrap{overflow:hidden}.text{overflow:hidden;color:#666}.starBox{overflow:hidden;vertical-align:middle}.star{display:inline-block;margin-right:2px;background-size:100% 100%;background-repeat:no-repeat;background-image:url(\"http://static.zplay.cn/wap/ad/star.jpg\")}.load{position:absolute;text-align:center;color:#fff;font-weight:bold;border-radius:5px;font-style:normal;border-width:1px;border-style:solid;border-top-color:#a6e304;border-right-color:#61af01;border-bottom-color:#368302;border-left-color:#70b801;background:-webkit-linear-gradient(#90c900,#419e02);background:-0-linear-gradient(#90c900,#419e02);background:-moz-linear-gradient(#90c900,#419e02);background:-webkit-linear-gradient(#90c900,#419e02);background:linear-gradient(#90c900,#419e02)}"
            + "</style>"
            + "</head>"
            + "<body>"
            + "<a href=\"zflag_aTagUrl\">"
            + "<div class=\"wrap\">"
            + " <img id=\"icon\" src=\"zflag_iconUrl\" alt=\"\" />"
            + "<div class=\"box\">"
            + "<h3 class=\"title\">zflag_title</h3>"
            + "<div class=\"textWrap\">"
            + "<div class=\"textBox\">"
            + "<p class=\"text\">zflag_desc</p>"
            + "</div>"
            + "</div>"
            + "<div class=\"starBox\"><i class=\"star\"></i><i class=\"star\"></i><i class=\"star\"></i><i class=\"star\"></i><i class=\"star\"></i></div>"
            + "</div>"
            + "<span class=\"load\">点击查看</span>"
            + "</div>"
            + "</a>"
            + "<script type=\"text/javascript\">"
            + "window.onload=function(){var wrap=document.querySelector(\".wrap\"),box=document.querySelector(\".box\"),textWrap=document.querySelector(\".textWrap\"),text=document.querySelector(\".text\"),title=document.querySelector(\".title\"),icon=document.getElementById(\"icon\"),starBox=document.querySelector(\".starBox\"),star=starBox.getElementsByTagName(\"i\"),load=document.querySelector(\".load\");wrap.style.marginTop=\"-25px\";wrap.style.height=\"50px\";icon.style.width=\"40px\";icon.style.height=\"40px\";box.style.paddingLeft=\"52px\";box.style.paddingRight=\"75px\";title.style.fontSize=\"13px\";title.style.height=\"20px\";title.style.lineHeight=\"20px\";textWrap.style.fontSize=\"12px\";textWrap.style.height=\"14px\";textWrap.style.lineHeight=\"14px\";starBox.style.height=\"16px\";starBox.style.lineHeight=\"16px\";load.style.width=\"60px\";load.style.height=\"36px\";load.style.lineHeight=\"36px\";load.style.top=\"7px\";load.style.right=\"5px\";load.style.fontSize=\"12px\";text.style.maxHeight=\"28px\";h=14;for(var i=0;i<star.length;i++){star[i].style.width=\"12px\";star[i].style.height=\"12px\"}var oDiv=document.querySelector(\".textBox\");startmove(h,20,oDiv)};function startmove(h,speed,oDiv){var time;oDiv.innerHTML+=oDiv.innerHTML;oDiv.style.marginTop=0;function scrolling(){if(parseInt(oDiv.style.marginTop)%h!=0){oDiv.style.marginTop=parseInt(oDiv.style.marginTop)-1+\"px\";if(Math.abs(parseInt(oDiv.style.marginTop))>=oDiv.offsetHeight/2){oDiv.style.marginTop=0}}else{clearInterval(time);setTimeout(start,5000)}}function start(){time=setInterval(scrolling,speed);oDiv.style.marginTop=parseInt(oDiv.style.marginTop)-1+\"px\"}setTimeout(start,5000)};"
            + "</script>"
            + "</body>" + "</html>";
    
    private static final String BANNER_IMAGETEXT_DEFAULT_PAD= "<!doctype html>"
            +"<html>"
            + "<meta charset=\"utf-8\">"
            + "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">"
            + "<head>"
            + "<style>"
            + "*{margin:0;padding:0}a{position:fixed;left:0;top:0;width:100%;height:100%;text-decoration:none;overflow:hidden;background:#fff}.wrap{position:absolute;left:0;top:50%;width:100%;overflow:hidden}#icon{position:absolute;left:5px;top:5px}.box{width:100%;box-sizing:border-box}.title{font-weight:bold;color:#333;overflow:hidden}.textWrap{overflow:hidden}.text{overflow:hidden;color:#666}.starBox{overflow:hidden;vertical-align:middle}.star{display:inline-block;margin-right:2px;background-size:100% 100%;background-repeat:no-repeat;background-image:url(\"http://static.zplay.cn/wap/ad/star.jpg\")}.load{position:absolute;text-align:center;color:#fff;font-weight:bold;border-radius:5px;font-style:normal;border-width:1px;border-style:solid;border-top-color:#a6e304;border-right-color:#61af01;border-bottom-color:#368302;border-left-color:#70b801;background:-webkit-linear-gradient(#90c900,#419e02);background:-0-linear-gradient(#90c900,#419e02);background:-moz-linear-gradient(#90c900,#419e02);background:-webkit-linear-gradient(#90c900,#419e02);background:linear-gradient(#90c900,#419e02)}"
            + "</style>"
            + "</head>"
            + "<body>"
            + "<a href=\"zflag_aTagUrl\">"
            + "<div class=\"wrap\">"
            + " <img id=\"icon\" src=\"zflag_iconUrl\" alt=\"\" />"
            + "<div class=\"box\">"
            + "<h3 class=\"title\">zflag_title</h3>"
            + "<div class=\"textWrap\">"
            + "<div class=\"textBox\">"
            + "<p class=\"text\">zflag_desc</p>"
            + "</div>"
            + "</div>"
            + "<div class=\"starBox\"><i class=\"star\"></i><i class=\"star\"></i><i class=\"star\"></i><i class=\"star\"></i><i class=\"star\"></i></div>"
            + "</div>"
            + "<span class=\"load\">点击查看</span>"
            + "</div>"
            + "</a>"
            + "<script type=\"text/javascript\">"
            + "window.onload=function(){var wrap=document.querySelector(\".wrap\"),box=document.querySelector(\".box\"),textWrap=document.querySelector(\".textWrap\"),text=document.querySelector(\".text\"),title=document.querySelector(\".title\"),icon=document.getElementById(\"icon\"),starBox=document.querySelector(\".starBox\"),star=starBox.getElementsByTagName(\"i\"),load=document.querySelector(\".load\");wrap.style.marginTop=\"-45px\";wrap.style.height=\"90px\";icon.style.width=\"80px\";icon.style.height=\"80px\";box.style.paddingLeft=\"92px\";box.style.paddingRight=\"130px\";title.style.fontSize=\"24px\";title.style.height=\"30px\";title.style.lineHeight=\"30px\";textWrap.style.fontSize=\"20px\";textWrap.style.height=\"35px\";textWrap.style.lineHeight=\"35px\";starBox.style.height=\"24px\";starBox.style.lineHeight=\"24px\";load.style.width=\"110px\";load.style.height=\"60px\";load.style.lineHeight=\"60px\";load.style.top=\"15px\";load.style.right=\"8px\";load.style.fontSize=\"32px\";text.style.maxHeight=\"70px\";h=35;for(var i=0;i<star.length;i++){star[i].style.width=\"20px\";star[i].style.height=\"20px\"}var oDiv=document.querySelector(\".textBox\");startmove(h,20,oDiv)};function startmove(h,speed,oDiv){var time;oDiv.innerHTML+=oDiv.innerHTML;oDiv.style.marginTop=0;function scrolling(){if(parseInt(oDiv.style.marginTop)%h!=0){oDiv.style.marginTop=parseInt(oDiv.style.marginTop)-1+\"px\";if(Math.abs(parseInt(oDiv.style.marginTop))>=oDiv.offsetHeight/2){oDiv.style.marginTop=0}}else{clearInterval(time);setTimeout(start,5000)}}function start(){time=setInterval(scrolling,speed);oDiv.style.marginTop=parseInt(oDiv.style.marginTop)-1+\"px\"}setTimeout(start,5000)};"
            + "</script>"
            + "</body>" + "</html>";
    
    
    
    
    /**
	 * 插屏默认竖屏模板
	 */
	private static final String INTERSTITIAL_IMAGETEXT_PORTRAIT_DEFAULT =
	        "<!DOCTYPE html>"+
	        "<html>"+
	        "<head>"+
	        "<meta charset=\"UTF-8\">"+
	        "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">"+
	        "<title>广告样式</title>"+
	        "<style type=\"text/css\">*{margin:0;padding:0}body{background:#fff}.aBtn{position:fixed;top:0;left:0;overflow:hidden;box-sizing:border-box;width:100%;height:100%;text-decoration:none}.wrap{position:absolute;top:0;left:0;overflow:hidden;width:100%;height:100%;background:#fff}.imgBox{font-size:0}.infoBox{margin-top:.06rem;width:100%;text-align:center;font-size:0}.infoIcon{width:.15rem}.infoText,.infoTitle{overflow:hidden;width:70%;max-height:.1rem;color:#333;font-size:.035rem;font-family:Simsun;line-height:.05rem}.infoTitle{margin:.02rem auto 0;font-weight:700}.infoText{margin:0 auto .01rem}.starBox{display:inline-block;height:.02rem;vertical-align:middle}.star{display:inline-block;margin-right:.002rem;width:.02rem;height:.02rem;background-size:100% 100%;background-repeat:no-repeat}.starAll{background-image:url(http://static.zplay.cn/wap/img/ad/star_all.png)}.starHalf{background-image:url(http://static.zplay.cn/wap/img/ad/star_half.png)}.number{display:inline-block;color:#666;vertical-align:middle;font-weight:700;font-size:.017rem;font-family:Arial}.free{display:inline-block;color:#ed9307;vertical-align:middle;font-weight:700;font-size:.017rem;font-family:Simsun}.btnBox{position:absolute;bottom:.04rem;left:0;width:100%;text-align:center;font-size:0}.getBtn{display:inline-block;width:60%}</style>"+
	        "<script type=\"text/javascript\" src=\"http://static.zplay.cn/wap/js/ad/fixedDirection.js\"></script>"+
	        "</head>"+
	        "<body>"+
	        "<div class=\"wrap\">"+
	            "<div class=\"imgBox\">"+
	                "<img id=\"adImg\" src=\"zflag_imageUrl\" width=\"100%\"/>"+
	            "</div>"+
	            "<div class=\"infoBox\"><img src=\"zflag_iconUrl\" class=\"infoIcon\" />"+
	               "<div class=\"infoTitle\">zflag_title</div>"+
	                "<div class=\"infoText\">zflag_desc</div>"+
	                "<div class=\"infoStar\">"+
	                 "<div class=\"starBox\" id=\"starBox\" number=\"zflag_star\"></div><span class=\"number\">zflag_NumberS</span>"+
	                "</div>"+
	            "</div>"+
	            "<div class=\"btnBox\">"+
	                "<img src=\"http://static.zplay.cn/wap/img/ad/check_btn.png\" class=\"getBtn\"/>"+
	            "</div>"+
	        "</div>"+
	        "<a href=\"zflag_aTagUrl\" class=\"aBtn\"></a>"+
	        "<script type=\"text/javascript\">window.resetFontSize=function(){window.oWrap=document.querySelector(\".wrap\");" +
	        "var dw=zflag_width," +
	        "dh=zflag_height," +
	        "ww=dw<dh?dw:dh,wh=dw>dh?dw:dh;document.querySelector(\"html\").style.fontSize=wh+\"px\"};window.onload=function(){resetFontSize();fixedDirection(oWrap,true);window.onresize=function(){fixedDirection(oWrap,true)};var starBox=document.getElementById(\"starBox\"),number=starBox.getAttribute(\"number\"),star=\"\";for(var i=0;i<parseInt(number);i++){star+='<i class=\"star starAll\"></i>'}if(number.indexOf(\".\")>0){star+='<i class=\"star starHalf\"></i>'}starBox.innerHTML=star};</script>"+
	        "</body>"+
	        "</html>";
	/**
	 * 插屏默认横屏模板
	 */
	private static final String INTERSTITIAL_IMAGETEXT_LANDSCAPE_DEFAULT =
	        "<!DOCTYPE html>"+
	        "<html>"+
	        "<head>"+
	        "<meta charset=\"UTF-8\">"+
	        "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">"+
	        "<title>广告样式</title>"+
	        "<style type=\"text/css\">"+
	        "*{margin:0;padding:0}body{background:#343434;font-size:12px}.aBtn{position:fixed;left:0;top:0;width:100%;height:100%;text-decoration:none;overflow:hidden;box-sizing:border-box}.wrap{width:50%;padding:0 15px;margin:.05rem auto;background-color:#fff;height:.9rem;overflow:hidden}.bg_left{position:absolute;top:0;left:0;height:100%}.bg_right{position:absolute;top:0;right:0;height:100%}.infoBox{margin-top:.03rem}.infoIcon{width:20%;margin-right:2%}.info{display:inline-block;width:74%;vertical-align:top}.infoTitle{font-size:.045rem;font-weight:bold;line-height:.065rem;height:.13rem;overflow:hidden;font-family:\"Simsun\";color:#333}.starBox{display:inline-block;height:.04rem;vertical-align:middle;line-height:.04rem;overflow:hidden}.star{display:inline-block;width:.04rem;height:.04rem;margin-right:.005rem;background-size:100% 100%;background-repeat:no-repeat}.starAll{background-image:url(\"http://static.zplay.cn/wap/img/ad/star_all.png\")}.starHalf{background-image:url(\"http://static.zplay.cn/wap/img/ad/star_half.png\")}.number{color:#666;display:inline-block;font-size:.02rem;font-weight:bold;vertical-align:middle;font-family:'Arial'}.free{color:#ed9307;display:inline-block;font-family:\"Simsun\";font-size:.02rem;font-weight:bold;vertical-align:middle}.imgBox{margin:.02rem 0;vertical-align:middle}.imgBox img{vertical-align:top}.botBox{height:.12rem;overflow:hidden;vertical-align:middle}.infoText{height:.12rem;font-size:.04rem;line-height:.06rem;overflow:hidden;display:inline-block;width:65%;padding-right:5%;vertical-align:middle}.clickbtn{width:30%;vertical-align:middle}"+
	        "</style>"+
	        "</head>"+
	        "<body>"+
	        "<img class=\"bg_left\" src=\"http://static.zplay.cn/wap/img/ad/bg_left.jpg\" />"+
	        "<img class=\"bg_right\" src=\"http://static.zplay.cn/wap/img/ad/bg_right.jpg\" />"+
	        "<div class=\"wrap\">"+
	            "<div class=\"infoBox\">"+
	                "<img src=\"zflag_iconUrl\" class=\"infoIcon\" />"+
	                "<div class=\"info\">"+
	                    "<div class=\"infoTitle\">zflag_title</div>"+
	                    "<div class=\"infoStar\">"+
	                        "<div class=\"starBox\" id=\"starBox\" number=\"zflag_star\"></div><span class=\"number\">zflag_NumberS</span>"+
	                    "</div>"+
	                "</div>"+
	            "</div>"+
	            "<div class=\"imgBox\">"+
	                "<img id=\"adImg\" src=\"zflag_imageUrl\" width=\"100%\"/>"+
	            "</div>"+
	            "<div class=\"botBox\">"+
	                "<div class=\"infoText\">zflag_desc</div><img class=\"clickbtn\" src=\"http://static.zplay.cn/wap/img/ad/check_btn_h.jpg\" />"+
	            "</div>"+
	        "</div>"+
	        "<a href=\"zflag_aTagUrl\" class=\"aBtn\"></a>"+
	        "<script type=\"text/javascript\">"+
	        "window.resetFontSize=function(){window.oWrap=document.querySelector(\".wrap\");"+
	        "var dw=zflag_width,"+
	        "dh=zflag_height,"+
	        "wh=dw<dh?dw:dh;document.querySelector(\"html\").style.fontSize=wh+\"px\"};window.onload=function(){resetFontSize();var starBox=document.getElementById(\"starBox\"),number=starBox.getAttribute(\"number\"),star=\"\";for(var i=0;i<parseInt(number);i++){star+='<i class=\"star starAll\"></i>'}if(number.indexOf(\".\")>0){star+='<i class=\"star starHalf\"></i>'}starBox.innerHTML=star};"+
	        "</script>"+
	        "</body>"+
	        "</html>";




	/**
     * 原生Banner默认
     * @param iconurl
     * @param title
     * @param desc
     * @param aTagUrl
     * @param context
     * @return
     */
	public static String getImageTextAdHtml(String iconurl,String title, String desc, String aTagUrl, Context context)
	{
        ZplayDebug.d(TAG, "getImageTextAdHtml [iconurl:"+iconurl+"] [title:"+title+"] [desc:"+desc+"] [aTagUrl:"+aTagUrl+"]", onoff);
	    int deviceType = PhoneInfoGetter.getDeviceType(context);
        if (deviceType==1)
        {
            String html = bannerAD(BANNER_IMAGETEXT_DEFAULT_PAD, iconurl, title,desc, aTagUrl);
            return html;
        }else
        {
            String html = bannerAD(BANNER_IMAGETEXT_DEFAULT_PHONE, iconurl, title,desc, aTagUrl);
            return html;
        }
	}
	
	/**
	 * 原生图片默认
	 * @param imageUrl 大图地址
	 * @param aTagUrl 跳转链接
	 * @return
	 */
	public static String getImageAdHtml(String imageUrl, String aTagUrl)
	{
		String adHtml = AD_IMAGE_DEFAULT
		.replace("zflag_aTagUrl", aTagUrl)
		.replace("zflag_imageUrl", imageUrl);
		return adHtml;
	}
	
	/**
	 * 原生插屏默认
	 * @param context
	 * @param title   标题
	 * @param desc 描述
	 * @param iconUrl 图标地址
     * @param imageUrl 大图地址
     * @param aTagUrl 跳转链接
	 * @param star 星星数（1-5）
	 * @param number 下载数（传 0 不显示）
	 * @return
	 */
	public static String getImageAdHtml(Context context, String title, String desc, String iconUrl,
			String imageUrl, String aTagUrl, int star, int number)
	{
		Configuration mConfiguration = context.getResources().getConfiguration(); // 获取设置的配置信息
		int ori = mConfiguration.orientation; // 获取屏幕方向
		if (ori == Configuration.ORIENTATION_LANDSCAPE)
		{
			return interstitialAd(INTERSTITIAL_IMAGETEXT_LANDSCAPE_DEFAULT, context, title, desc, iconUrl, imageUrl, aTagUrl, star, number);
		}else{
			return interstitialAd(INTERSTITIAL_IMAGETEXT_PORTRAIT_DEFAULT, context, title, desc, iconUrl, imageUrl, aTagUrl, star, number);
		}
		
	}
	
	/**
	 * 原生Banner图文动态模板
	 * @param iconurl
	 * @param title
	 * @param desc
	 * @param aTagUrl
	 * @param context
	 * @param provider
	 * @return
	 */
	public static String getTemplateBanner(String iconurl,String title, String desc, String aTagUrl, Context context, YumiProviderBean provider)
	{
	    ZplayDebug.d(TAG, "getImageTextAdHtml [iconurl:"+iconurl+"] [title:"+title+"] [desc:"+desc+"] [aTagUrl:"+aTagUrl+"]", onoff);
	    
	    int screenMode = 1;
	    Template template = provider.getTemplate(screenMode);
	    if (template == null)
		{
	    	screenMode = getScreenMode(context);
	    	template = provider.getTemplate(screenMode);
		}
		if (template!=null)
		{
			int id = template.getId();
			String t_html = SharedpreferenceUtils.getString(context, "template_" + id, "template", "");
			//TODO 使用已保存模板或默认模板
			if (t_html!=null && !"".equals(t_html))
			{
				ZplayDebug.v(TAG, "调用已保存模板模板", onoff);
				String html = bannerAD(t_html, iconurl, title,desc, aTagUrl);
				provider.setUseTemplateMode(screenMode);
				return html;
			}else{
				ZplayDebug.v(TAG, "当前没有已保存模板，调用默认模板", onoff);
				String html = getImageTextAdHtml(iconurl, title,desc, aTagUrl, context);
				return html;
			}
		}else{
			//使用默认模板
			ZplayDebug.v(TAG, "直接调用默认模板", onoff);
			String html = getImageTextAdHtml(iconurl, title,desc, aTagUrl, context);
			return html;
		}
	}

	/**
	 * 原生插屏图文动态模板
	 * @param context
	 * @param title
	 * @param desc
	 * @param iconUrl
	 * @param imageUrl
	 * @param aTagUrl
	 * @param star
	 * @param number
	 * @param provider
	 * @return
	 */
	public static String getTemplateInterstitial(Context context, String title, String desc, String iconUrl,
			String imageUrl, String aTagUrl, int star, int number, YumiProviderBean provider)
	{
		
		int screenMode = 1;
	    Template template = provider.getTemplate(screenMode);
	    if (template == null)
		{
	    	screenMode = getScreenMode(context);
	    	template = provider.getTemplate(screenMode);
		}
		if (template!=null)
		{
			int id = template.getId();
			String t_html = SharedpreferenceUtils.getString(context, "template_" + id, "template", "");
//			long time = template.getTime();
//			long lastTime = SharedpreferenceUtils.getLong(context, "template_" + id, "time", -1);
//			if (lastTime!=time)
//			{
//				downloadTemplate(id, time, context);
//			}
			if (t_html!=null && !"".equals(t_html))
			{
				//TODO 使用已保存模板
				ZplayDebug.v(TAG, "调用已保存模板模板="+t_html, onoff);
				String html = interstitialAd(t_html, context, title, desc, iconUrl, imageUrl, aTagUrl, star, number);
				ZplayDebug.v(TAG, "html="+html, onoff);
				provider.setUseTemplateMode(screenMode);
				return html;
			}else
			{
				//使用默认模板
				ZplayDebug.v(TAG, "当前没有已保存模板，调用默认模板", onoff);
				String html = getImageAdHtml(context, title, desc, iconUrl, imageUrl, aTagUrl, star, number);
				return html;
			}
		}else{
			//使用默认模板
			ZplayDebug.v(TAG, "直接调用默认模板", onoff);
			String html = getImageAdHtml(context, title, desc, iconUrl, imageUrl, aTagUrl, star, number);
			return html;
		}
		
		
		
	}

	
	/**
	 * 套用Banner模板
	 * @param iconurl
	 * @param title
	 * @param desc
	 * @param aTagUrl
	 * @return
	 */
	private static String bannerAD(String t_html, String iconurl,String title, String desc, String aTagUrl){
	    String banner = t_html
	    		.replace("zflag_aTagUrl", aTagUrl)
	    		.replace("zflag_iconUrl", iconurl)
	    		.replace("zflag_title", title)
	    		.replace("zflag_desc", desc);
	    ZplayDebug.w(TAG, "bannerHtml = " + banner, onoff);
	    return banner;
	}

	/**
	 * 套用插屏模板
	 * @param t_html
	 * @param context
	 * @param title
	 * @param desc
	 * @param iconUrl
	 * @param imageUrl
	 * @param aTagUrl
	 * @param star
	 * @param number
	 * @return
	 */
	private static String interstitialAd(String t_html, Context context, String title, String desc, String iconUrl,
			String imageUrl, String aTagUrl, int star, int number)
	{
		final Activity activity = (Activity) context;
		int[] screen = WindowSizeUtils.getRealSize(activity);
		int width = WindowSizeUtils.px2dip(activity, screen[0]);
		int height = WindowSizeUtils.px2dip(activity, screen[1]);
		// ZplayDebug.e(TAG, "getImageAdHtml :[width]" + width
		// +"[height]"+height, onoff);
		String NumberS = number > 0 ? "（" + number + "）" : "";
		star = star > 5 ? 5 : star; // 星星不能大于5
		
		String interstitial = t_html
				.replace("zflag_iconUrl", iconUrl)
				.replace("zflag_title", title)
				.replace("zflag_star", String.valueOf(star))
				.replace("zflag_NumberS", NumberS)
				.replace("zflag_imageUrl", imageUrl)
				.replace("zflag_desc", desc)
				.replace("zflag_aTagUrl", aTagUrl)
				.replace("zflag_width", String.valueOf(width))
				.replace("zflag_height", String.valueOf(height));
		ZplayDebug.w(TAG, "interstitialHtml = " + interstitial, onoff);
		return interstitial;
	}

	/**
	 * 获取屏幕状态并转化为与服务端匹配的数字
	 * @param context
	 * @return
	 */
	private static int getScreenMode(Context context)
	{
		Configuration mConfiguration = context.getResources().getConfiguration(); // 获取设置的配置信息
		int ori = mConfiguration.orientation; // 获取屏幕方向
		int screenMode = 0;
		if (ori == Configuration.ORIENTATION_LANDSCAPE)
		{
			ZplayDebug.v(TAG, "当前横屏", onoff);
			screenMode = 2;
		}else if (ori == Configuration.ORIENTATION_PORTRAIT){
			ZplayDebug.v(TAG, "当前竖屏", onoff);
			screenMode = 3;
		}
		return screenMode;
	}
	
	
//	/**
//	 * 下载模板
//	 * @param id
//	 * @param time
//	 * @param context
//	 */
//	private static void downloadTemplate(final int id, final long time, final Context context)
//	{
//		new Thread(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				String templateUrl = YumiAPIList.TEMPLATE_DETAIL_URL();
//				Random random = new Random();
//				int ram = random.nextInt(1024);
//				try
//				{
//					ZplayDebug.d(TAG, "开始下载新模板", onoff);
//					URL url = new URL(templateUrl+"?ids="+id+"&r="+ram);
//					URLConnection conn = url.openConnection();
//					InputStream is = conn.getInputStream();
//					int len = 0;
//					byte[] buffer = new byte[1024];
//					StringBuffer sb = new StringBuffer();
//					while((len = is.read(buffer))!=-1)
//					{
//						sb.append(new String(buffer, 0, len, "UTF-8"));
//					}
//					String templateDetail = sb.toString();
//					JSONObject jobj_templateDetail = new JSONObject(templateDetail);
//					if (jobj_templateDetail.getInt("errcode")==200)
//					{
//						JSONObject jobj_data = jobj_templateDetail.getJSONObject("data");
//						JSONObject jobj_htmlList = jobj_data.getJSONObject("htmlList");
//						JSONObject jobj_id = jobj_htmlList.getJSONObject(id+"");
//						String html = jobj_id.getString("html");
//						html = URLDecoder.decode(html, "UTF-8");
//						SharedpreferenceUtils.saveLong(context, "template_" + id, "time", time);
//						SharedpreferenceUtils.saveString(context, "template_" + id, "template", html);
//						ZplayDebug.d(TAG, "新模板已保存，ID：" + id, onoff);
//					}
//				} catch (Exception e)
//				{
//					ZplayDebug.e(TAG, e.getMessage(), e, onoff);
//				}
//			}
//		}).start();
//	}
	
}
