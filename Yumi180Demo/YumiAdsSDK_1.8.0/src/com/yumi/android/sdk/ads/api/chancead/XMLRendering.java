package com.yumi.android.sdk.ads.api.chancead;

import java.util.List;



public final class XMLRendering {

	private static final String HEADER = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/>"+
			"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no,telephone=no\"/>" +
			"<meta name=\"format-detection\" content=\"telephone=no\"/>" +
			"<title></title><link rel=\"stylesheet\" type=\"text/css\" href=\"http://adservice.zplay.cn//static/css/plaque.css\"/>" +
			"</head>";
	
	private static final String BODY = "<body><div class=\"main_bg\"><a href=\"%s\">";
	private static final String BODY_IMG = "<img class=\"bgImg\"  src=\"%s\"/></a></div>";
	private static final String NOTIFY_DIV = "<div class=\"hidediv\">";
	private static final String NOTIFY_DIV_SIMPLE = "<img src=\"%s\" style=\"display:none;\">";
	private static final String NOTIFY_DIV_BY_SHOW_BEGIN = "<script>function show(){var img_box = document.createElement('div');img_box.style.display='none';img_box.innerHTML=\"";
	private static final String NOTIFIY_DIV_BY_SHOW_URLS = "<img src='%s' />";
	private static final String NOTIFY_DIV_BY_SHOW_END = "\";document.body.insertBefore(img_box);}</script>";
	private static final String END = "</div></body></html>";
	
	
	
	public final static String renderingImgXML(String imgUrl, String clickUrl , List<String> notifyUrls) {
		StringBuilder buffer = new StringBuilder("");
		if (imgUrl != null && imgUrl.length() > 0) {
			buffer.append(HEADER);
			buffer.append(String.format(BODY, clickUrl));
			buffer.append(String.format(BODY_IMG, imgUrl));
			buffer.append(NOTIFY_DIV);
			if (notifyUrls != null && notifyUrls.size() > 0) {
				for (String url : notifyUrls) {
					buffer.append(String.format(NOTIFY_DIV_SIMPLE, url));
				}
			}
			buffer.append(END);
		}
		return buffer.toString();
	}

	public final static String renderingImgInterstitialXML(String imgUrl, String clickUrl, List<String> notifyUrls){
		StringBuilder buffer = new StringBuilder("");
		if (imgUrl != null && imgUrl.length() > 0) {
			buffer.append(HEADER);
			buffer.append(String.format(BODY, clickUrl));
			buffer.append(String.format(BODY_IMG, imgUrl));
			buffer.append(NOTIFY_DIV_BY_SHOW_BEGIN);
			if (notifyUrls != null && notifyUrls.size() > 0) {
				for (String url : notifyUrls) {
					buffer.append(String.format(NOTIFIY_DIV_BY_SHOW_URLS, url));
				}
			}
			buffer.append(NOTIFY_DIV_BY_SHOW_END);
			buffer.append(END);
		}
		return buffer.toString();
	}
	
}
