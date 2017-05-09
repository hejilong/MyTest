package com.yumi.android.demo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yumi.android.MActivity;
import com.yumi.android.sdk.ads.publish.YumiBanner;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.listener.IYumiBannerListener;
import com.yumi.android180.R;

public class BannerActivity extends MActivity {

	private FrameLayout bannerContainer;
	private YumiBanner banner;
	private IYumiBannerListener bannerListener;
	private TextView text;
	

    private FrameLayout bannerContainer2;
    private YumiBanner banner2;
	
	private Button btn_coveringBanner,btn_requestBanner2;
	private FrameLayout coveringView;
	private EditText etxt_yumiid;
	
	@Override
	public void initView() {
		setContentView(R.layout.activity_banner);
		setAcTitle("Banner_Code");
		text = (TextView) findViewById(R.id.textView2);
		coveringView=(FrameLayout) findViewById(R.id.coveringView);
		btn_coveringBanner=(Button) findViewById(R.id.btn_coveringBanner);
		btn_coveringBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(coveringView.getVisibility()==View.VISIBLE)
                {
                    coveringView.setVisibility(View.GONE);
                }else
                {
                    coveringView.setVisibility(View.VISIBLE);
                }
            }
        });
		        
		/*
		 * First step: 
		 *  create banner container , this container is a viewgroup, and add the container into your activity content view.
		 */
		bannerContainer2 = new FrameLayout(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER;
		addContentView(bannerContainer2, params);
		
		bannerContainer=(FrameLayout) findViewById(R.id.banner_container);
		
		etxt_yumiid=(EditText) findViewById(R.id.etxt_yumiid);
		btn_requestBanner2=(Button) findViewById(R.id.btn_requestBanner2);
		btn_requestBanner2.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String yumiID2=etxt_yumiid.getText().toString();
                if(yumiID2!=null){
                banner2 = new YumiBanner(BannerActivity.this,yumiID2, true);
//              banner2 = new YumiBanner(BannerActivity.this,getStringConfig("yumiID"), true);
                //setBannerContainer
                banner2.setBannerContainer(bannerContainer2, AdSize.BANNER_SIZE_AUTO,isMatchWindowWidth);
                //setChannelID . (Recommend)
                banner2.setChannelID(channelStr);
//                banner2.setDefaultChannelAndVersion(getApplicationContext());
                //setVersionName . (Recommend)
                banner2.setVersionName(versionStr);
                //setBannerEventListener. (Recommend)
                banner2.setBannerEventListener(bannerListener);
                //requestYumiBanner. (Require)
                banner2.requestYumiBanner();
                }
            }
        });
	}
	
	
	@Override
	public void setListener() {
		/*
		 * Second step:
		 * create IYumiBannerListener to get the banner request statue.
		 */
		bannerListener = new IYumiBannerListener() {

			@Override
			public void onBannerPreparedFailed(LayerErrorCode errorCode) {
				Log.e("mikoto", "on banner prepared failed " + errorCode);
				setInfo("on banner prepared failed " + errorCode);
			}

			@Override
			public void onBannerPrepared() {
				Log.e("mikoto", "on banner prepared");
				setInfo("on banner prepared");
			}

			@Override
			public void onBannerExposure() {
				Log.e("mikoto", "on banner exposure");
				setInfo("on banner exposure");
			}

			@Override
			public void onBannerClosed() {
				Log.e("mikoto", "on banner close ");
				
				setInfo("on banner close");
			}

			@Override
			public void onBannerClicked() {
				Log.e("mikoto", "on banner clicked ");
				setInfo("on banner clicked");
			}
		};

	}

	@Override
	public void onActivityCreate() {
		/*
		 * Thrid step :
		 * create YumiBanner instance by activity and your YumiID.
		 */
		banner = new YumiBanner(BannerActivity.this, getStringConfig("yumiID"), true);
		//setBannerContainer
		banner.setBannerContainer(bannerContainer, AdSize.BANNER_SIZE_AUTO,isMatchWindowWidth);
		//setChannelID . (Recommend)
		banner.setChannelID(channelStr);
//		banner.setDefaultChannelAndVersion(getApplicationContext());
		//setVersionName . (Recommend)
		banner.setVersionName(versionStr);
		//setBannerEventListener. (Recommend)
		banner.setBannerEventListener(bannerListener);
		//requestYumiBanner. (Require)
		banner.requestYumiBanner();
		
		Log.e("mikoto", "Local ip address is " + getLocalIpAddress());
	}

	
	@Override
	protected void onDestroy() {
		if (banner != null) {
			banner.onDestroy();
		}
		if (banner2 != null) {
		    banner2.onDestroy();
		}
		super.onDestroy();
	}
	
	private void setInfo(final String msg){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (text != null) {
					text.append(msg + "\n");
				}
				
			}
		});
	}
	
	public String getLocalIpAddress() {
	     try {
	          for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
	en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
	enumIpAddr.hasMoreElements();) {
	InetAddress inetAddress = enumIpAddr.nextElement();
	if (!inetAddress.isLoopbackAddress()) {
	   return inetAddress.getHostAddress();
	}
	}
	   }
	} catch (SocketException ex) {
	   Log.e("mikoto", ex.toString());
	}
	return null;
	}
}
