package com.yumi.android.sdk.ads.utils.assets;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Map;

import android.content.Context;


/**
 * config是指assets目录下的文件{@linkplain ConstantsHolder#FILE_CONFIG}
 * 
 * @author glzlaohuai
 * @date 2013-4-18
 */
public final class ConfigValueHandler {
	
	public final static String FILE_CONFIG = "ZplayConfig.xml";
	public final static String KEY_CHANNEL = "ChannelID";
	public final static String KEY_GAMEID = "GameID";
	public final static String NODE_CHANNEL_GAMEID = "infos";
	public final static String KEY_USE_MM_CHANNEL = "USE_MM_CHANNEL";

	// 保存着channel以及gameID文件的解析结果的缓存
	private static SoftReference<Map<String, String>> XMLCache;
	
	/**
	 * 构造{@linkplain #XMLCache}
	 * 
	 * @param context
	 */
	private static void buildCache(Context context) {
		if (XMLCache == null || XMLCache.get() == null) {
			Map<String, Object> data = null;
			try {
				data = XMLParser.paraserXML(context.getAssets().open(
						FILE_CONFIG));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			// 保存着channel以及gameID的map结构
			@SuppressWarnings("unchecked")
			Map<String, String> infosMap = (Map<String, String>) data
					.get(NODE_CHANNEL_GAMEID);
			XMLCache = new SoftReference<Map<String, String>>(infosMap);
		}
	}

	/**
	 * 获取渠道（2013-4-19 11:41:55修改）
	 * 开始设计是从manifest中的meta处获取，但是因为设计到孟宪国的批量打包工具，所以此处按照以前的设计来获取
	 * 
	 * 2013-11-27 12:03:38
	 * 修改，因为可恶的mm改变了策略，任何修改以后的包都不能再使用mm进行正常的计费，所以，要想分渠道来统计计费数据
	 * ，只能使用mm的渠道来进行统计，所以
	 * ，这里将channelid改变为可以配置，如果要使用mm计费，那么在配置文件中将字段USE_MM_CHANNEl设置为1 否则就就标注为0
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getChannel(Context context) {
		String channel = null;
		if (useMMChannel(context)) {
			InputStream inputStream = RawFileInputStreamReader.readRawFile(
					context, "mmiap.xml");
			if (inputStream == null) {
				channel = "000000";
			} else {
				Map<String, Object> data = XMLParser.paraserXML(inputStream);
				channel = (String) ((Map<String, Object>) (data.get("data")))
						.get("channel");
			}
		} else {
			buildCache(context);
			if (XMLCache != null && XMLCache.get() != null) {
				return XMLCache.get().get(KEY_CHANNEL).trim();
			}
		}
		return channel;
	}

	/**
	 * 获取gameID(2013-4-19 11:53:36修改)
	 * 开始设计是从manifest中的meta处获取，但是因为设计到孟宪国的批量打包工具，所以此处按照以前的设计来获取
	 * 
	 * @param context
	 * @return
	 */
	public static String getGameID(Context context) {
		buildCache(context);
		if (XMLCache != null && XMLCache.get() != null) {
			return XMLCache.get().get(KEY_GAMEID).trim();
		}
		return null;
	}

	/**
	 * 是否使用mm配置的渠道
	 * 
	 * @param context
	 * @return
	 */
	private static boolean useMMChannel(Context context) {
		buildCache(context);
		if (XMLCache != null && XMLCache.get() != null) {
			String mm = XMLCache.get().get(KEY_USE_MM_CHANNEL);
			if (mm != null && mm.length() > 0) {
				return mm.trim().equals("1") ? true : false;
			}
		}
		return false;
	}
	
}
