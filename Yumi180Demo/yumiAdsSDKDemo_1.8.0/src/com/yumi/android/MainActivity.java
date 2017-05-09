package com.yumi.android;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.yumi.android.anaylsis.AnaylsisActivity;
import com.yumi.android.demo.BannerActivity;
import com.yumi.android.demo.BannerActivity_MT;
import com.yumi.android.demo.InterstitialActivity;
import com.yumi.android.demo.InterstitialActivity_MT;
import com.yumi.android.demo.MediaActivity;
import com.yumi.android.sdk.ads.publish.YumiCheckPermission;
import com.yumi.android.sdk.ads.publish.YumiDebug;
import com.yumi.android.sdk.ads.publish.YumiDebugging;
import com.yumi.android180.R;

public class MainActivity extends MActivity implements OnClickListener, OnCheckedChangeListener
{
	
	private Button btn_banner_a;
	private Button btn_banner_m;
	private Button btn_interstitial_a;
	private Button btn_interstitial_m;
	private Button btn_media;
	private Button btn_splash;
	private Button btn_startDebugging;
	private EditText channel;
	private EditText version;
	private EditText yumiID;
	private CheckBox debug;
	private CheckBox cb_isMatchWindowWidth;
	private boolean isMatchWindowWidth;

    private Button anaylsis;
	
	@Override
	public void initView()
	{
		setContentView(R.layout.activity_main);
		btn_banner_a = (Button) findViewById(R.id.btn_banner_a);
		btn_banner_m = (Button) findViewById(R.id.btn_banner_m);
		btn_interstitial_a = (Button) findViewById(R.id.btn_interstitial_a);
		btn_interstitial_m = (Button) findViewById(R.id.btn_interstitial_m);
		btn_media = (Button) findViewById(R.id.btn_media_a);
		btn_splash = (Button) findViewById(R.id.btn_splash);
		btn_startDebugging = (Button) findViewById(R.id.btn_startDebugging);
		
		channel = (EditText) findViewById(R.id.channel);
		channel.clearFocus();
		version = (EditText) findViewById(R.id.version);
		version.clearFocus();
		yumiID = (EditText) findViewById(R.id.yumiID);
		yumiID.clearFocus();
		debug = (CheckBox) findViewById(R.id.debug);
		cb_isMatchWindowWidth = (CheckBox) findViewById(R.id.cb_isMatchWindowWidth);
		
		channel.setText(getStringConfig("channel"));
		version.setText(getStringConfig("version"));
//		yumiID.setText(getStringConfig("yumiID","ec3510685ed2b02ffabf542f07f1628e"));
		yumiID.setText(getStringConfig("yumiID","20748f5c16382811ea46a6058277ae1f"));
//		yumiID.setText(getStringConfig("yumiID","a92412bd1995ee7bb268f8143582587c"));
		boolean isdebug = getBooleanConfig("debug");
		debug.setChecked(isdebug);
		isMatchWindowWidth = getBooleanConfig("isMatchWindowWidth");
		cb_isMatchWindowWidth.setChecked(isMatchWindowWidth);
		YumiDebug.runInDebugMode(isdebug);
		YumiCheckPermission.runInCheckPermission(true);
		
        anaylsis = (Button) findViewById(R.id.btn_anaylsis);
	}

	@Override
	public void setListener()
	{
		btn_banner_a.setOnClickListener(this);
		btn_banner_m.setOnClickListener(this);
		btn_interstitial_a.setOnClickListener(this);
		btn_interstitial_m.setOnClickListener(this);
		btn_media.setOnClickListener(this);
		btn_splash.setOnClickListener(this);
		btn_startDebugging.setOnClickListener(this);
		debug.setOnCheckedChangeListener(this);
		cb_isMatchWindowWidth.setOnCheckedChangeListener(this);

        anaylsis.setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton b, boolean flag)
	{
		if (b == debug)
		{
			sp.edit().putBoolean("debug", flag).commit();
			YumiDebug.runInDebugMode(flag);
		}
		if (b == cb_isMatchWindowWidth)
		{
		    sp.edit().putBoolean("isMatchWindowWidth", flag).commit();
		    isMatchWindowWidth=flag;
		}
	}
	
	@Override
	public void onClick(View v)
	{
		String cha = channel.getText().toString();
		String ver = version.getText().toString();
		String yid = yumiID.getText().toString();
		
		sp.edit().putString("channel", cha).commit();
		sp.edit().putString("version", ver).commit();
		sp.edit().putString("yumiID", yid).commit();
		
		if(v.getId()==R.id.btn_startDebugging)
		{
		    YumiDebugging.startDebugging(MainActivity.this, yid);
		    return;
		}
		Intent intent = new Intent();
		switch (v.getId())
		{
		case R.id.btn_banner_a:
			intent.setClass(MainActivity.this, BannerActivity.class);
		    intent.putExtra("isMatchWindowWidth", isMatchWindowWidth);
			break;
		case R.id.btn_banner_m:
			intent.setClass(MainActivity.this, BannerActivity_MT.class);
	        intent.putExtra("isMatchWindowWidth", isMatchWindowWidth); 
			break;
		case R.id.btn_interstitial_a:
			intent.setClass(MainActivity.this, InterstitialActivity.class);
			break;
		case R.id.btn_interstitial_m:
			intent.setClass(MainActivity.this, InterstitialActivity_MT.class);
			break;
		case R.id.btn_media_a:
			intent.setClass(MainActivity.this, MediaActivity.class);
			break;
		case R.id.btn_splash:
			intent.setClass(MainActivity.this, SplashTestActivity.class);
			break;
		 case R.id.btn_anaylsis:
	            intent.setClass(MainActivity.this, AnaylsisActivity.class);
	            break;
		default:
			break;
		}
		intent.putExtra("channel", cha);
		intent.putExtra("version", ver);
		startActivity(intent);
	}

	@Override
	public void onActivityCreate() {
	}

	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

}
