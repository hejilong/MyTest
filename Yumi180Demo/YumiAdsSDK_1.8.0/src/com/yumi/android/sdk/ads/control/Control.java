package com.yumi.android.sdk.ads.control;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import com.yumi.android.sdk.ads.beans.AdListBean;
import com.yumi.android.sdk.ads.beans.Template;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.beans.YumiResultBean;
import com.yumi.android.sdk.ads.constants.YumiAPIList;
import com.yumi.android.sdk.ads.utils.SharedpreferenceUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.Activity;
import android.content.Context;

public abstract class Control
{
	
	private static final String TAG = "Control";
	protected static final boolean onoff = true;
	protected Activity mActivity;
	protected Context mContext;
	protected String yumiID = "";
	private boolean auto = true;
	private boolean isinit = false;
	private YumiResultBean configResult;                                
	
	public YumiResultBean getConfigResult()
	{
		return configResult;
	}
	public void setConfigResult(YumiResultBean configResult)
	{
		this.configResult = configResult;
	}

	private List<AdListBean> eventMerge;

	public List<AdListBean> getEventMerge()
	{
		if (eventMerge == null)
		{
			eventMerge = new ArrayList<>();
		}
		return eventMerge;
	}
	
	
	public Control(Activity activity, String yumiID, boolean auto)
	{
		this.mActivity = activity;
		this.mContext = activity.getApplicationContext();
		this.yumiID = yumiID.trim();
		this.auto = auto;
	}
	
	public boolean isAuto()
	{
		return Control.this.auto;
	}
	
	public boolean isInit()
	{
		return Control.this.isinit;
	}
	
	public void setInit()
	{
		Control.this.isinit = true;
	}
	
	/**
	 * 下载模板
	 * @param result
	 */
	protected void downloadTemplate(YumiResultBean result)
	{
        List<YumiProviderBean> providers = result.getProviders();
        if (providers != null)
		{
			for (int i = 0; i < providers.size(); i++)
			{
				YumiProviderBean providerBean = providers.get(i);
				List<Template> templates = providerBean.getTemplates();
				for (int j = 0; j < templates.size(); j++)
				{
					Template template = templates.get(j);
					long time = template.getTime();
					int id = template.getId();
					long lastTime = SharedpreferenceUtils.getLong(mContext, "template_" + id, "time", -1);
					if (lastTime!=time)
					{
						downloadTemplate(id, time, mContext);
					}
				}
			}
		}
	}
	
	private static void downloadTemplate(final int id, final long time, final Context context)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String templateUrl = YumiAPIList.TEMPLATE_DETAIL_URL();
				Random random = new Random();
				int ram = random.nextInt(1024);
				try
				{
					ZplayDebug.d(TAG, "开始下载新模板", onoff);
					URL url = new URL(templateUrl+"?ids="+id+"&r="+ram);
					URLConnection conn = url.openConnection();
					InputStream is = conn.getInputStream();
					int len = 0;
					byte[] buffer = new byte[1024];
					StringBuffer sb = new StringBuffer();
					while((len = is.read(buffer))!=-1)
					{
						sb.append(new String(buffer, 0, len, "UTF-8"));
					}
					String templateDetail = sb.toString();
					JSONObject jobj_templateDetail = new JSONObject(templateDetail);
					if (jobj_templateDetail.getInt("errcode")==200)
					{
						JSONObject jobj_data = jobj_templateDetail.getJSONObject("data");
						JSONObject jobj_htmlList = jobj_data.getJSONObject("htmlList");
						JSONObject jobj_id = jobj_htmlList.getJSONObject(id+"");
						String html = jobj_id.getString("html");
						html = URLDecoder.decode(html, "UTF-8");
						SharedpreferenceUtils.saveLong(context, "template_" + id, "time", time);
						SharedpreferenceUtils.saveString(context, "template_" + id, "template", html);
						ZplayDebug.d(TAG, "新模板已保存，ID：" + id, onoff);
					}
				} catch (Exception e)
				{
					ZplayDebug.e(TAG, e.getMessage(), e, onoff);
				}
			}
		}).start();
	}
	
}
