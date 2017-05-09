package com.yumi.android.sdk.ads.receiver;

import java.io.File;
import java.net.URI;

import com.yumi.android.sdk.ads.constants.YumiConstants;
import com.yumi.android.sdk.ads.observer.DownloadWatched;
import com.yumi.android.sdk.ads.utils.NullCheckUtils;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;


public final class DownloadReceiver extends BroadcastReceiver {

	private static final boolean onoff = true;
	private static final String TAG = "DownloadReceiver";
	private DownloadWatched watched;

	public DownloadReceiver(DownloadWatched watched){
		this.watched = watched;
	}
	
	@Override
	public final void onReceive(Context context, Intent intent) {

		if (intent != null) {
			if (intent.getAction().equals(YumiConstants.ACTION_DOWNLOAD_BEGIN)) {
				ZplayDebug.d(TAG, "download begin", onoff);
				watched.notifyDownload();
			}
			if (intent.getAction().equals(
					DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			    ZplayDebug.d(TAG, "down load complete", onoff);
				long longExtra = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				DownloadManager manager = (DownloadManager) context
						.getSystemService(Context.DOWNLOAD_SERVICE);
				try {
					DownloadManager.Query query = new Query();
					query.setFilterById(longExtra);
					Cursor c = manager.query(query);
					if (c != null && c.moveToNext()) {
						int downloadStatus = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
						if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
							
							String path = "";
							try
							{
								String local_uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
								URI create = URI.create(local_uri);
								File file = new File(create);
								path = file.getAbsolutePath();
							} catch (Exception e)
							{
							}
							watched.notifyDownloadComplete(path);
							/*
							 * 使用 ZplayAd manifest 注册的 receiver 进行安装. 
							 */
							String url = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
							if (NullCheckUtils.isNotNull(url)) {
								installApp(context, url);
							}
						}
					}
				} catch (Exception e) {
					ZplayDebug.e(TAG, "", e, onoff);
					
				}
			}
		}
	}
	
	private final void installApp(Context context, String app) {
//		Uri uri = Uri.fromFile(new File(app));
		Uri uri = Uri.parse(app);
		// 创建Intent意图
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// 设置Uri和类型
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		// 执行意图进行安装
		context.startActivity(intent);
	}

}
