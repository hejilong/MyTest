package com.yumi.android.sdk.ads.api.baidu;

import java.util.List;

public final class XMLRendering
{

    private static final String NOTIFY_DIV_SIMPLE = "<img src=\"%s\" style=\"display:none;\">";

    public static String getBannerImageHtml(String imgUrl, String clickUrl, List<String> notifyUrls)
    {
        StringBuffer bannerBuild = new StringBuffer();
        bannerBuild.append("<!DOCTYPE HTML>");
        bannerBuild.append("<html>");
        bannerBuild.append("<head>");
        bannerBuild.append("<meta charset=\"utf-8\">");
        bannerBuild.append(
                "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">");
        bannerBuild.append("<title>APP广告</title>");
        bannerBuild.append("<style>");
        bannerBuild.append(
                "*{margin:0; padding:0} body{background-color:rgba(0,0,0,0.5); } a{position:fixed; left:0; top:0; width:100%; height:100%; text-decoration:none; overflow:hidden; text-align: center; vertical-align: middle; }#img{position: absolute; left:0; top:0; height:100%; width: 100%; vertical-align: middle; } #baidu{ width:15px; float: left; } #ad{ width:15px; float: left; margin:5px 0 0 1px; } span{ position: absolute; left:1px; bottom:1px; display: block; background-color:rgba(0,0,0,0.5); padding-right:3px; }");
        bannerBuild.append("</style>");
        bannerBuild.append("</head>");
        bannerBuild.append("<body>");
        bannerBuild.append("<a href=\"");
        bannerBuild.append(clickUrl);
        bannerBuild.append("\">");
        bannerBuild.append("<img id=\"img\" src=\"");
        bannerBuild.append(imgUrl);
        bannerBuild.append("\" border=\"0\" />");
        bannerBuild.append("<span>");
        bannerBuild.append(
                "<img id=\"baidu\" src=\"http://static.zplay.cn/wap/ad/baidu_icon.png\" />");
        bannerBuild.append("<img id=\"ad\" src=\"http://static.zplay.cn/wap/ad/baidu_ad.png\" />");
        bannerBuild.append("</span>");
        bannerBuild.append("</a>");
        if (notifyUrls != null && notifyUrls.size() > 0)
        {
            for (String url : notifyUrls)
            {
                bannerBuild.append(String.format(NOTIFY_DIV_SIMPLE, url));
            }
        }
        bannerBuild.append("</body>");
        bannerBuild.append("</html>");

        String html = bannerBuild.toString();
        return html;
    }

    public static String getBannerTextHtml(String title, String description, String clickUrl,
        List<String> notifyUrls)
    {
        StringBuffer bannerBuild = new StringBuffer();
        bannerBuild.append("<!DOCTYPE HTML>");
        bannerBuild.append("<html>");
        bannerBuild.append("<head>");
        bannerBuild.append("<meta charset=\"utf-8\">");
        bannerBuild.append(
                "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">");
        bannerBuild.append("<title>APP广告</title>");
        bannerBuild.append("<style>");
        bannerBuild.append(
                "*{margin:0;padding:0}a{position:fixed;left:0;top:0;width:100%;height:100%;text-decoration:none;overflow:hidden;color:#fff;background:#000 url(\"http://static.zplay.cn/wap/ad/banner1.gif\") no-repeat 0 center;background-size:100% 100%;box-sizing:border-box}.wrap{position:absolute;left:0;top:50%;width:100%;overflow:hidden}.box{width:100%;box-sizing:border-box}h3{font-weight:bold;overflow:hidden}.textWrap{overflow:hidden}p{overflow:hidden}i{position:absolute;text-align:center;color:#fff;font-weight:bold;border-radius:5px;font-style:normal;border-width:1px;border-style:solid;border-top-color:#a6e304;border-right-color:#61af01;border-bottom-color:#368302;border-left-color:#70b801;background:-webkit-linear-gradient(#90c900,#419e02);background:-0-linear-gradient(#90c900,#419e02);background:-moz-linear-gradient(#90c900,#419e02);background:-webkit-linear-gradient(#90c900,#419e02);background:linear-gradient(#90c900,#419e02)}#baidu{width:15px;float:left}#ad{width:15px;float:left;margin:5px 0 0 1px}span{position:absolute;left:1px;bottom:1px;display:block;background-color:rgba(0,0,0,0.5);padding-right:3px}");
        bannerBuild.append("</style>");
        bannerBuild.append("</head>");
        bannerBuild.append("<body>");
        bannerBuild.append("<a href=\"" + clickUrl + "\">");
        bannerBuild.append("<div class=\"wrap\">");
        bannerBuild.append("<div class=\"box\">");
        bannerBuild.append("<h3 class=\"title\">" + title + "</h3>");
        bannerBuild.append("<div class=\"textWrap\">");
        bannerBuild.append("<div class=\"textBox\">");
        bannerBuild.append("<p class=\"text\">" + description + "</p>");
        bannerBuild.append("</div>");
        bannerBuild.append("</div>");
        bannerBuild.append("</div>");
        bannerBuild.append("<i class=\"load\">点击查看</i>");
        bannerBuild.append("</div>");
        bannerBuild.append("<span>");
        bannerBuild.append(
                "<img id=\"baidu\" src=\"http://static.zplay.cn/wap/ad/baidu_icon.png\" />");
        bannerBuild.append("<img id=\"ad\" src=\"http://static.zplay.cn/wap/ad/baidu_ad.png\" />");
        bannerBuild.append("</span>");
        bannerBuild.append("</a>");
        if (notifyUrls != null && notifyUrls.size() > 0)
        {
            for (String url : notifyUrls)
            {
                bannerBuild.append(String.format(NOTIFY_DIV_SIMPLE, url));
            }
        }
        bannerBuild.append("<script type=\"text/javascript\">");
        bannerBuild.append(
                "window.onload=function(){var wrap=document.querySelector(\".wrap\"),box=document.querySelector(\".box\"),textWrap=document.querySelector(\".textWrap\"),text=document.querySelector(\".text\"),title=document.querySelector(\".title\"),load=document.querySelector(\".load\");if(window.innerHeight<90){wrap.style.marginTop=\"-20px\";wrap.style.height=\"40px\";box.style.paddingLeft=\"10px\";box.style.paddingRight=\"75px\";title.style.fontSize=\"14px\";title.style.height=\"20px\";title.style.lineHeight=\"20px\";textWrap.style.fontSize=\"12px\";textWrap.style.height=\"20px\";textWrap.style.lineHeight=\"20px\";text.style.maxHeight=\"40px\";load.style.width=\"60px\";load.style.height=\"28px\";load.style.lineHeight=\"28px\";load.style.top=\"5px\";load.style.right=\"5px\";load.style.fontSize=\"12px\";h=20}else{wrap.style.marginTop=\"-40px\";wrap.style.height=\"80px\";box.style.paddingLeft=\"10px\";box.style.paddingRight=\"75px\";title.style.fontSize=\"18px\";title.style.height=\"40px\";title.style.lineHeight=\"40px\";textWrap.style.fontSize=\"18px\";textWrap.style.height=\"30px\";textWrap.style.lineHeight=\"30px\";text.style.maxHeight=\"60px\";load.style.width=\"120px\";load.style.height=\"60px\";load.style.lineHeight=\"60px\";load.style.top=\"10px\";load.style.right=\"10px\";load.style.fontSize=\"24px\";h=30}var oDiv=document.querySelector(\".textBox\");startmove(h,20,oDiv)};function startmove(h,speed,oDiv){var time;oDiv.innerHTML+=oDiv.innerHTML;oDiv.style.marginTop=0;function scrolling(){if(parseInt(oDiv.style.marginTop)%h!=0){oDiv.style.marginTop=parseInt(oDiv.style.marginTop)-1+\"px\";if(Math.abs(parseInt(oDiv.style.marginTop))>=oDiv.offsetHeight/2){oDiv.style.marginTop=0}}else{clearInterval(time);setTimeout(start,5000)}}function start(){time=setInterval(scrolling,speed);oDiv.style.marginTop=parseInt(oDiv.style.marginTop)-1+\"px\"}setTimeout(start,5000)};");
        bannerBuild.append("</script>");
        bannerBuild.append("</body>");
        bannerBuild.append("</html>");
        String html = bannerBuild.toString();
        return html;
    }

    public static String getBannerIconTextHtml(String icon, String title, String description,
        String clickUrl, List<String> notifyUrls)
    {
        StringBuffer bannerBuild = new StringBuffer();
        bannerBuild.append("<!DOCTYPE HTML>");
        bannerBuild.append("<html>");
        bannerBuild.append("<head>");
        bannerBuild.append("<meta charset=\"utf-8\">");
        bannerBuild.append(
                "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">");
        bannerBuild.append("<title>APP广告</title>");
        bannerBuild.append("<style>");
        bannerBuild.append(
                "*{margin:0;padding:0}a{position:fixed;left:0;top:0;width:100%;height:100%;text-decoration:none;overflow:hidden;color:#fff;background:#000 url(\"http://static.zplay.cn/wap/ad/banner1.gif\") no-repeat 0 center;background-size:100% 100%;box-sizing:border-box}.wrap{position:absolute;left:0;top:50%;width:100%;overflow:hidden}#icon{position:absolute}.box{width:100%;box-sizing:border-box}h3{font-size:14px;font-weight:bold;height:20px;line-height:20px;overflow:hidden}.textWrap{height:20px;overflow:hidden;font-size:12px;line-height:20px}p{max-height:40px;overflow:hidden}i{position:absolute;right:10px;top:5px;width:60px;height:28px;line-height:28px;text-align:center;color:#fff;font-weight:bold;border-radius:5px;font-style:normal;font-size:12px;border-width:1px;border-style:solid;border-top-color:#a6e304;border-right-color:#61af01;border-bottom-color:#368302;border-left-color:#70b801;background:-webkit-linear-gradient(#90c900,#419e02);background:-0-linear-gradient(#90c900,#419e02);background:-moz-linear-gradient(#90c900,#419e02);background:-webkit-linear-gradient(#90c900,#419e02);background:linear-gradient(#90c900,#419e02)}#baidu{width:15px;float:left}#ad{width:15px;float:left;margin:5px 0 0 1px}span{position:absolute;left:1px;bottom:1px;display:block;background-color:rgba(0,0,0,0.5);padding-right:3px}");
        bannerBuild.append("</style>");
        bannerBuild.append("</head>");
        bannerBuild.append("<body>");
        bannerBuild.append("<a href=\"" + clickUrl + "\">");
        bannerBuild.append("<div class=\"wrap\">");
        bannerBuild.append("<img id=\"icon\" src=\"" + icon + "\" alt=\"\" />");
        bannerBuild.append("<div class=\"box\">");
        bannerBuild.append("<h3 class=\"title\">" + title + "</h3>");
        bannerBuild.append("<div class=\"textWrap\">");
        bannerBuild.append("<div class=\"textBox\">");
        bannerBuild.append("<p class=\"text\">" + description + "</p>");
        bannerBuild.append("</div>");
        bannerBuild.append("</div>");
        bannerBuild.append("</div>");
        bannerBuild.append("<i class=\"load\">点击查看</i>");
        bannerBuild.append("</div>");
        bannerBuild.append("<span>");
        bannerBuild.append(
                "<img id=\"baidu\" src=\"http://static.zplay.cn/wap/ad/baidu_icon.png\" />");
        bannerBuild.append("<img id=\"ad\" src=\"http://static.zplay.cn/wap/ad/baidu_ad.png\" />");
        bannerBuild.append("</span>");
        bannerBuild.append("</a>");

        if (notifyUrls != null && notifyUrls.size() > 0)
        {
            for (String url : notifyUrls)
            {
                bannerBuild.append(String.format(NOTIFY_DIV_SIMPLE, url));
            }
        }

        bannerBuild.append(
                "<script type=\"text/javascript\">window.onload=function(){var wrap=document.querySelector(\".wrap\"),box=document.querySelector(\".box\"),textWrap=document.querySelector(\".textWrap\"),text=document.querySelector(\".text\"),title=document.querySelector(\".title\"),icon=document.getElementById(\"icon\"),load=document.querySelector(\".load\");if(window.innerHeight<90){wrap.style.marginTop=\"-20px\";wrap.style.height=\"40px\";icon.style.width=\"40px\";icon.style.height=\"40px\";icon.style.left=\"10px\";icon.style.top=\"0px\";box.style.paddingLeft=\"60px\";box.style.paddingRight=\"75px\";title.style.fontSize=\"14px\";title.style.height=\"20px\";title.style.lineHeight=\"20px\";textWrap.style.fontSize=\"12px\";textWrap.style.height=\"20px\";textWrap.style.lineHeight=\"20px\";text.style.maxHeight=\"40px\";load.style.width=\"60px\";load.style.height=\"28px\";load.style.lineHeight=\"28px\";load.style.top=\"5px\";load.style.right=\"5px\";load.style.fontSize=\"12px\";h=20}else{wrap.style.marginTop=\"-40px\";wrap.style.height=\"80px\";icon.style.width=\"70px\";icon.style.height=\"70px\";icon.style.left=\"10px\";icon.style.top=\"5px\";box.style.paddingLeft=\"95px\";box.style.paddingRight=\"140px\";title.style.fontSize=\"18px\";title.style.height=\"40px\";title.style.lineHeight=\"40px\";textWrap.style.fontSize=\"18px\";textWrap.style.height=\"30px\";textWrap.style.lineHeight=\"30px\";text.style.maxHeight=\"60px\";load.style.width=\"120px\";load.style.height=\"60px\";load.style.lineHeight=\"60px\";load.style.top=\"10px\";load.style.right=\"10px\";load.style.fontSize=\"24px\";h=30}var oDiv=document.querySelector(\".textBox\");startmove(h,20,oDiv)};function startmove(h,speed,oDiv){var time;oDiv.innerHTML+=oDiv.innerHTML;oDiv.style.marginTop=0;function scrolling(){if(parseInt(oDiv.style.marginTop)%h!=0){oDiv.style.marginTop=parseInt(oDiv.style.marginTop)-1+\"px\";if(Math.abs(parseInt(oDiv.style.marginTop))>=oDiv.offsetHeight/2){oDiv.style.marginTop=0}}else{clearInterval(time);setTimeout(start,5000)}}function start(){time=setInterval(scrolling,speed);oDiv.style.marginTop=parseInt(oDiv.style.marginTop)-1+\"px\"}setTimeout(start,5000)};");
        bannerBuild.append("</script>");
        bannerBuild.append("</body>");
        bannerBuild.append("</html>");
        String html = bannerBuild.toString();

        return html;
    }

    public static String getCpImageHtml(String imgUrl, String clickUrl, List<String> notifyUrls)
    {
        StringBuffer bannerBuild = new StringBuffer();
        bannerBuild.append("<!DOCTYPE HTML>");
        bannerBuild.append("<html>");
        bannerBuild.append("<head>");
        bannerBuild.append("<meta charset=\"utf-8\">");
        bannerBuild.append(
                "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">");
        bannerBuild.append("<title>APP广告</title>");
        bannerBuild.append(
                "<style>*{margin:0;padding:0}body{background-color:rgba(0,0,0,0.5)}a{position:fixed;left:0;top:0;width:100%;height:100%;text-decoration:none;overflow:hidden;text-align:center;vertical-align:middle;border:4px solid #fff;box-sizing:border-box}#img{position:absolute;left:0;top:0;height:100%;width:100%;vertical-align:middle}#baidu{width:15px;float:left}#ad{width:15px;float:left;margin:5px 0 0 1px}span{position:absolute;left:1px;bottom:1px;display:block;background-color:rgba(0,0,0,0.5);padding-right:3px}");
        bannerBuild.append("</style>");
        bannerBuild.append("</head>");
        bannerBuild.append("<body>");
        bannerBuild.append("<a href=\"" + clickUrl + "\">");
        bannerBuild.append("<img id=\"img\" src=\"" + imgUrl + "\" border=\"0\" />");
        bannerBuild.append("<span>");
        bannerBuild.append(
                "<img id=\"baidu\" src=\"http://static.zplay.cn/wap/ad/baidu_icon.png\" />");
        bannerBuild.append("<img id=\"ad\" src=\"http://static.zplay.cn/wap/ad/baidu_ad.png\" />");
        bannerBuild.append("</span></a>");

        if (notifyUrls != null && notifyUrls.size() > 0)
        {
            for (String url : notifyUrls)
            {
                bannerBuild.append(String.format(NOTIFY_DIV_SIMPLE, url));
            }
        }
        bannerBuild.append("</body>" + "</html>");
        String html = bannerBuild.toString();
        return html;
    }

    public static String getCpTextHtml(String title, String description, String clickUrl,
        List<String> notifyUrls)
    {
        StringBuffer bannerBuild = new StringBuffer();
        bannerBuild.append("<!DOCTYPE HTML>");
        bannerBuild.append("<html>");
        bannerBuild.append("<head>");
        bannerBuild.append("<meta charset=\"utf-8\">");
        bannerBuild.append(
                "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">");
        bannerBuild.append("<title>APP广告</title>");
        bannerBuild.append("<style>");
        bannerBuild.append(
                "*{margin:0;padding:0}a{position:fixed;left:0;top:0;width:100%;height:100%;overflow:hidden;text-align:center;overflow:hidden;text-decoration:none;color:#000;font-family:\"microsoft yahei\";border:4px solid #fff;box-sizing:border-box;background:#ccd9e5 url(\"http://static.zplay.cn/wap/ad/chaping.jpg\") no-repeat;background-size:100%}em{display:inline-block;height:100%;width:0;vertical-align:middle}div{display:inline-block;width:100%;box-sizing:border-box;vertical-align:middle;text-align:center}#baidu{width:15px;height:15px;float:left}#ad{width:15px;height:8px;float:left;margin:5px 0 0 1px}span{position:absolute;height:15px;left:1px;bottom:1px;display:block;background-color:rgba(0,0,0,0.5);padding-right:3px}h3{line-height:40px;overflow:hidden;height:40px;font-size:24px;font-weight:bold;width:80%;margin:0 auto}p{max-height:60px;line-height:30px;font-size:18px;width:80%;margin:0 auto;overflow:hidden}");
        bannerBuild.append("</style>");
        bannerBuild.append("</head>");
        bannerBuild.append("<body>");
        bannerBuild.append("<a href=\"" + clickUrl + "\">");
        bannerBuild.append("<em></em><div><h3>" + title + "</h3>");
        bannerBuild.append("<p>" + description + "</p></div>");
        bannerBuild.append("<span>");
        bannerBuild.append("<img id=\"baidu\" src=\"http://static.zplay.cn/wap/ad/baidu_icon.png\" />");
        bannerBuild.append("<img id=\"ad\" src=\"http://static.zplay.cn/wap/ad/baidu_ad.png\" />");
        bannerBuild.append("</span>");
        bannerBuild.append("</a>");
        if (notifyUrls != null && notifyUrls.size() > 0)
        {
            for (String url : notifyUrls)
            {
                bannerBuild.append(String.format(NOTIFY_DIV_SIMPLE, url));
            }
        }
        bannerBuild.append("</body>");
        bannerBuild.append("</html>");
        String html = bannerBuild.toString();
        return html;
    }

    public static String getCpIconTextHtml(String icon, String title, String description,
        String clickUrl, List<String> notifyUrls)
    {
        StringBuffer bannerBuild = new StringBuffer();
        bannerBuild.append("<!DOCTYPE HTML>");
        bannerBuild.append("<html>");
        bannerBuild.append("<head>");
        bannerBuild.append("<meta charset=\"utf-8\">");
        bannerBuild.append(
                "<meta name=\"viewport\" content=\"initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width\">");
        bannerBuild.append("<title>APP广告</title>");
        bannerBuild.append(
                "<style>*{margin:0;padding:0}a{position:fixed;left:0;top:0;width:100%;height:100%;overflow:hidden;text-align:center;overflow:hidden;text-decoration:none;color:#000;font-family:\"microsoft yahei\";border:4px solid #fff;box-sizing:border-box;background:#ccd9e5 url(\"http://static.zplay.cn/wap/ad/chaping.jpg\") no-repeat;background-size:100%}img{width:32%;margin-bottom:20px}em{display:inline-block;height:100%;width:0;vertical-align:middle}#baidu{width:15px;height:15px;float:left}#ad{width:15px;height:8px;float:left;margin:5px 0 0 1px}span{position:absolute;height:15px;left:1px;bottom:1px;display:block;background-color:rgba(0,0,0,0.5);padding-right:3px}div{display:inline-block;width:100%;box-sizing:border-box;vertical-align:middle;text-align:center}h3{line-height:40px;overflow:hidden;height:40px;font-size:24px;font-weight:bold;width:80%;margin:0 auto}p{max-height:60px;line-height:30px;font-size:18px;width:80%;margin:0 auto;overflow:hidden}");
        bannerBuild.append("</style>");
        bannerBuild.append("</head>");
        bannerBuild.append("<body>");
        bannerBuild.append("<a href=\"" + clickUrl + "\">");
        bannerBuild.append("<em></em><div><img src=\"" + icon + "\" alt=\"\" />");
        bannerBuild.append("<h3>" + title + "</h3>");
        bannerBuild.append("<p>" + description + "</p></div>");
        bannerBuild.append("<span>");
        bannerBuild.append(
                "<img id=\"baidu\" src=\"http://static.zplay.cn/wap/ad/baidu_icon.png\" />");
        bannerBuild.append("<img id=\"ad\" src=\"http://static.zplay.cn/wap/ad/baidu_ad.png\" />");
        bannerBuild.append("</span>");
        bannerBuild.append("</a>");
        if (notifyUrls != null && notifyUrls.size() > 0)
        {
            for (String url : notifyUrls)
            {
                bannerBuild.append(String.format(NOTIFY_DIV_SIMPLE, url));
            }
        }
        bannerBuild.append("</body>");
        bannerBuild.append("</html>");
        String html = bannerBuild.toString();
        return html;
    }

}
