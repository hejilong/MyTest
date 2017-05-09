package com.yumi.android.demo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yumi.android.MActivity;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.YumiDebug;
import com.yumi.android.sdk.ads.publish.YumiMedia;
import com.yumi.android.sdk.ads.publish.enumbean.MediaStatus;
import com.yumi.android.sdk.ads.publish.listener.IYumiMediaListener;
import com.yumi.android.sdk.ads.receiver.NetworkReceiver;
import com.yumi.android180.R;

/**
 * Created by Administrator on 2017/4/24.
 */

public class TestMediaActivity extends MActivity implements View.OnClickListener {
    private TextView info;
    private Button show, isPrepared,remain;
    private YumiMedia media;
    private IYumiMediaListener mediaListener;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (media.isMediaPrepared()) {
                        Log.e("mikoto", "media prepared ");
                        setInfo("media prepared");
                        MediaStatus showMedia = media.showMedia();
                        Toast.makeText(TestMediaActivity.this, "media show status " + showMedia.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("mikoto", "media prepared failed");
                        setInfo("media prepared failed");
                        showTimer();
                    }
                    break;
                default:
                    break;
            }
        };
    };
    @Override
    public void initView() {
        setContentView(R.layout.activity_media);
        setAcTitle("Meida");
        show = (Button) findViewById(R.id.showMedia);
        show.setOnClickListener(this);
        show.setVisibility(View.GONE);
        isPrepared = (Button) findViewById(R.id.isPrepared);
        isPrepared.setOnClickListener(this);
        remain = (Button) findViewById(R.id.remain);
        remain.setOnClickListener(this);
        remain.setVisibility(View.GONE);

        info = (TextView) findViewById(R.id.mediainfo);
//        showTimer();

        YumiDebug.runInDebugMode(true);
    }

    @Override
    public void setListener() {
        /*
         * First step :
         * Create IYumiMediaListener to get media request statue.
         */
        mediaListener = new IYumiMediaListener() {

            @Override
            public void onMediaIncentived() {
                Log.e("mikoto", "on media  incentived ");
                setInfo("on media incentived ");
            }

            @Override
            public void onMediaExposure() {
                Log.e("mikoto", "on media exposure ");
                setInfo("on media exposure ");
            }

            @Override
            public void onMediaClosed() {
                Log.e("mikoto", "on media closed  ");
                setInfo("on media closed ");

            }

            @Override
            public void onMediaClicked() {
                Log.e("mikoto", "on media clicked ");
                setInfo("on media clicked ");
            }

        };
    }

    @Override
    public void onActivityCreate() {
        /*
         * Second step :
         * Create YumiMedia instance by activity and your YumiID.
         */
        media = new YumiMedia(TestMediaActivity.this, "ec3510685ed2b02ffabf542f07f1628e");
        //setMediaEventListener .  (Require)
        media.setMediaEventListner(mediaListener);
//        //setChannelID .  (Recommend)
//        media.setChannelID(channelStr);
//        //setVersionName . (Recommend)
//        media.setVersionName(versionStr);
        //requestYumiMedia . (Require)
        media.requestYumiMedia();
    }

    /*
     * Third step :
     * Invoke showMedia method when you want exposure media ads.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.showMedia) {
            if (media != null) {
                MediaStatus showMedia = media.showMedia();
                Toast.makeText(TestMediaActivity.this, "media show status " + showMedia.toString(), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.isPrepared) {
            if (media != null) {
                if (media.isMediaPrepared()) {
                    Log.e("mikoto", "media prepared ");
                    setInfo("media prepared");
                    MediaStatus showMedia = media.showMedia();
                    Toast.makeText(TestMediaActivity.this, "media show status " + showMedia.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("mikoto", "media prepared failed");
                    setInfo("media prepared failed");
                    showTimer();
                }
            }
        } else if (v.getId() == R.id.remain) {
            if (media != null) {
                Log.e("mikoto","media MediaRemainRewards is "+media.getMediaRemainRewards());
                setInfo("media MediaRemainRewards is "+media.getMediaRemainRewards());
            }
        }
    }


    @Override
    protected void onDestroy() {
        Log.e("mikoto", "media activity destroy");
        if (media != null) {
            media.onDestory();
        }
        super.onDestroy();
    }

    private void setInfo(final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (info != null) {
                    info.append(msg + "\n");
                }

            }
        });
    }

    private void showTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    while (true) {
                        Thread.sleep(5000);
                        Message mesage = new Message();
                        mesage.what = 1;
                        mHandler.sendMessage(mesage);
//                    }
                } catch (Exception e) {
                    Log.e("TestMediaActivity", "showTimer error :", e);
                }
            }
        }).start();
    }
}
