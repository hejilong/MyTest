package com.yumi.android.sdk.ads.api.sohu;

import com.yumi.android.sdk.ads.utils.NullCheckUtils;


public final class XMLRendering {

	private static final String MODEL_IMG = "<!DOCTYPE html><html lang=\"en\">"
			+ "<head><meta charset=\"UTF-8\"><title>Document</title></head>"
			+ "<style type=\"text/css\">body,img,div{margin:0;padding:0;}body{width: 100%;height: 100%;}div{position: absolute;top:0;bottom:0;left:0;right:0;}img{width: 100%;height: 100%;display: block;}</style>"
			+ "<body><div>";
	private static final String MODEL_IMG_APPEDN = "<img src=\"%s\" alt=\"\">";
	private static final String MODEL_IMG_END = "</div></bod></html>";
	
	
	private static final String MODEL_TEXTIMG = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width\">"
			+ "<title>Document</title></head>"
			+ "<style type=\"text/css\">body,div,p,span,img,h3{margin: 0 auto;font-family: '宋体';}"
			+ ".div1{position: absolute;top:0%;bottom:0%;left: 0%;right: 0%;background: #efefef;}"
			+ ".duv1_div{width: 100%;height: 105px;background: url(http://imgcache.qq.com/gdt/cdn/api/static/image/sdk_popup.png) no-repeat;background-size: 100% 210px;}"
			+ ".spans{width: 105px;height: 105px;float: left;}"
			+ ".spans img{display: inline-block;margin-top: 12px;margin-left: 12px;width: 80px;height: 80px;}"
			+ ".mydiv2{height: 75px;width: 100px;padding-top: 30px;float: left;color: #fff;font-size: 16px;}"
			+ ".myp{padding:10px 12px;font-size: 14px;line-height: 20px;color: #404040;}"
			+ ".xiazai{width: 100%;height: 30px;position: absolute;bottom:40px;}"
			+ ".xiazai a{width: 100px;height: 32px;display: block;margin: 0 auto;border: 1px solid #36aa3e;border-left: 2px solid #36aa3e;border-right: 2px solid #36aa3e;background-color: #3eb246;"
			+ "background: linear-gradient(to bottom, #36ac3e 0%,#46b84e 100%);background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#36ac3e), color-stop(100%,#46b84e));"
			+ "background: -webkit-linear-gradient(top, #36ac3e 0%,#46b84e 100%);background: -o-linear-gradient(top, #36ac3e 0%,#46b84e 100%);"
			+ "background: -ms-linear-gradient(top, #36ac3e 0%,#46b84e 100%);border-radius: 4px;box-shadow: 0 1px 4px #c3f7c6,inset 0 1px 0 #c5c5c5;text-decoration: none;color: #fff;line-height: 32px;"
			+ "text-align: center;font-size: 14px;}"
			+ "@media screen and (max-width:320px){.xiazai{ bottom: 15px;}.myp{font-size: 12px}}</style>"
			+ "<body><div class=\"div1\"><div class=\"duv1_div\"><span class='spans'>";
	private static final String MODEL_TEXTIMG_APPEND = "<img src=\"%s\"></span>"
			+ "<div class=\"mydiv2\">%s</div></div>"
			+ "<p class=\"myp\">%s</p></div>";
	private static final String MODEL_TEXTIMG_END = "<div class=\"xiazai\"><a href=\"\">免费下载</a></div></body></html>";
	
	public static final String renderingImgXML(String imgUrl) {
		StringBuffer buffer = new StringBuffer("");
		if (NullCheckUtils.isNotNull(imgUrl)) {
			buffer.append(MODEL_IMG);
			buffer.append(String.format(MODEL_IMG_APPEDN, imgUrl));
			buffer.append(MODEL_IMG_END);
		}
		return buffer.toString();
	}

	public static final String renderingImgTextXML(String icon, String title, String desc) {
		StringBuffer buffer = new StringBuffer("");
		if (NullCheckUtils.isNotNull(icon) && NullCheckUtils.isNotNull(title) && NullCheckUtils.isNotNull(desc)) {
			buffer.append(MODEL_TEXTIMG);
			buffer.append(String.format(MODEL_TEXTIMG_APPEND, icon, title, desc));
			buffer.append(MODEL_TEXTIMG_END);
		}
		return buffer.toString();
	}

}
