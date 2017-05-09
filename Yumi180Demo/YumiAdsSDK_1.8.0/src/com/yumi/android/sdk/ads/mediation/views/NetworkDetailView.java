package com.yumi.android.sdk.ads.mediation.views;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.control.YumiBannerControl;
import com.yumi.android.sdk.ads.factory.YumiBannerAdapterFacotry;
import com.yumi.android.sdk.ads.factory.YumiInterstitialAdapterFactory;
import com.yumi.android.sdk.ads.factory.YumiMediaAdapterFactory;
import com.yumi.android.sdk.ads.layer.YumiBaseBannerLayer;
import com.yumi.android.sdk.ads.layer.YumiBaseInterstitialLayer;
import com.yumi.android.sdk.ads.layer.YumiBaseMediaLayer;
import com.yumi.android.sdk.ads.listener.IYumiInnerLayerStatusListener;
import com.yumi.android.sdk.ads.listener.IYumiMediaInnerLayerStatusListener;
import com.yumi.android.sdk.ads.mediation.data.NetworkManifestReaderUtils;
import com.yumi.android.sdk.ads.mediation.data.NetworkStatus;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerType;
import com.yumi.android.sdk.ads.publish.enumbean.MediaStatus;
import com.yumi.android.sdk.ads.receiver.NetworkReceiver;
import com.yumi.android.sdk.ads.utils.YumiLayerSizeCalculater;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;
import com.yumi.android.sdk.ads.utils.encrypt.YumiSignUtils;

/**
 * sdk详情页
 * @author  hejilong  2016-11-16
 *
 */
public class NetworkDetailView extends LinearLayout {

    private static final String TAG = "Mediation NetworkDetailView";
    private static final boolean onoff = true;
    
    private final NetworkStatus networkStatus;
    RadioGroup adFormatSelect;
    private RadioButton toggleCp;
    private RadioButton toggleMedia;
    private RadioButton toggleBanner;
    Button btnFetch;
    Button btnShow;
    LogView logView;
    private Button btnBannerShow;
    private Button btnBannerHide;
    private FrameLayout bannerFrame;
    private Activity mActivity;
    private LinearLayout controlFrame;
    private FrameLayout bannerContainer;
    private TextView adapterStartedText;
    private TextView adapterPresentText;
    
    private  YumiBaseBannerLayer tempBannerAdapter;
    private  YumiBaseInterstitialLayer tempCpAdapter;
    private  YumiBaseMediaLayer tempMediaAdapter;
    
    private final Handler mHandlerBanner = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case NetworkReceiver.HANDLER_NETWORK_CHANGE:
                break;
            case YumiBannerControl.CHANGE_BANNER_VIEW:
                replaceBannerView((View) msg.obj);
                break;
            default:
                break;
            }
        };
    };
    
    public NetworkDetailView(Context context,NetworkStatus networkStatus)
    {
      super(context);
      mActivity=(Activity) context;
      this.networkStatus = networkStatus;
      setBackgroundColor(-1);
      setOrientation(1);
      constructChildren();
    }
    
    /**
     * 构建主要窗体
     */
    private void constructChildren()
    {
      controlFrame = new LinearLayout(getContext());
      controlFrame.setOrientation(1);
      
      TextView title = new TextView(getContext());
      title.setText(this.networkStatus.getNameUpperCase());
      title.setTextColor(-16777216);
      title.setTextSize(24.0F);
      controlFrame.addView(title);
      LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
      titleParams.setMargins(0, 0, 0, WindowSizeUtils.dip2px(getContext(), 12));
      
      addView(buildStatusNotifications(), new LinearLayout.LayoutParams(-1, -2));
      this.adFormatSelect = new RadioGroup(getContext());
      this.adFormatSelect.setOrientation(1);
      
      boolean cpEnabled = (networkStatus.getAdapterStatus()==networkStatus.STATUS_SUCCEED) && (networkStatus.getProviderBeanCp() != null);
      this.toggleCp = new RadioButton(getContext());
      this.toggleCp.setText("Interstitial");
      this.toggleCp.setEnabled(cpEnabled);
      this.toggleCp.setTextColor(cpEnabled ? -16777216 : -7829368);
      
      boolean mediaEnabled = (networkStatus.getAdapterStatus()==networkStatus.STATUS_SUCCEED) && (networkStatus.getProviderBeanMedia() != null);
      this.toggleMedia = new RadioButton(getContext());
      this.toggleMedia.setText("Rewarded Video");
      this.toggleMedia.setEnabled(mediaEnabled);
      this.toggleMedia.setTextColor(mediaEnabled ? -16777216 : -7829368);
      
      boolean bannerEnabled =(networkStatus.getAdapterStatus()==networkStatus.STATUS_SUCCEED) && (networkStatus.getProviderBeanBanner() != null);
      this.toggleBanner = new RadioButton(getContext());
      this.toggleBanner.setText("Banner");
      this.toggleBanner.setEnabled(bannerEnabled);
      this.toggleBanner.setTextColor(bannerEnabled ? -16777216 : -7829368);
      
      this.adFormatSelect.addView(this.toggleCp);
      this.adFormatSelect.addView(this.toggleMedia);
      this.adFormatSelect.addView(this.toggleBanner);
      controlFrame.addView(this.adFormatSelect);
      
      final LinearLayout actionContainer = new LinearLayout(getContext());
      actionContainer.setOrientation(0);
      this.btnFetch = new Button(getContext());
      this.btnFetch.setText("Fetch");
      this.btnFetch.setOnClickListener(new View.OnClickListener()
      {
            public void onClick(View view) {
                if (toggleCp.isChecked()) {
                    requestCpLayer(networkStatus.getProviderBeanCp());
                } else if (toggleMedia.isChecked()) {
                    requestMediaLayer(networkStatus.getProviderBeanMedia());
                }
            }
      });
      this.btnShow = new Button(getContext());
      this.btnShow.setText("Show");
      this.btnShow.setOnClickListener(new View.OnClickListener()
      {
            public void onClick(View view) {
                if (toggleCp.isChecked()) {
                    showCp();
                } else if (toggleMedia.isChecked()) {
                    showMedia();
                }
            }
      });
      final LinearLayout btnActionContainer = new LinearLayout(getContext());
      actionContainer.setOrientation(0);
      
      this.btnBannerShow = new Button(getContext());
      this.btnBannerShow.setText("Show");
      this.btnBannerShow.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View v)
        {
            requestBannerLayer(networkStatus.getProviderBeanBanner());
        }
      });
      this.btnBannerHide = new Button(getContext());
      this.btnBannerHide.setText("Hide");
      this.btnBannerHide.setOnClickListener(new View.OnClickListener()
      {
         public void onClick(View v) {
            if (bannerContainer != null) {
                bannerContainer.setVisibility(View.GONE);
            }
         }
      });
      LinearLayout.LayoutParams weightParams = new LinearLayout.LayoutParams(0, -2);
      weightParams.weight = 1.0F;
      actionContainer.addView(this.btnFetch, weightParams);
      actionContainer.addView(this.btnShow, weightParams);
      
      btnActionContainer.addView(this.btnBannerShow, weightParams);
      btnActionContainer.addView(this.btnBannerHide, weightParams);
      
      actionContainer.setVisibility(8);
      btnActionContainer.setVisibility(8);
      controlFrame.addView(actionContainer);
      controlFrame.addView(btnActionContainer);
      
      this.bannerFrame = new FrameLayout(getContext());
      controlFrame.addView(this.bannerFrame, new LinearLayout.LayoutParams(-1, -2));
      
      this.adFormatSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
      {
        public void onCheckedChanged(RadioGroup group, int checkedId)
        {
          boolean isBanner = NetworkDetailView.this.toggleBanner.isChecked();
          actionContainer.setVisibility(isBanner ? 8 : 0);
          btnActionContainer.setVisibility(isBanner ? 0 : 8);
        }
      });
      for (RadioButton b : Arrays.asList(new RadioButton[] { this.toggleCp, this.toggleMedia, this.toggleBanner })) {
        if (b.isEnabled())
        {
          b.setChecked(true);
          break;
        }
      }
      this.logView = new LogView(getContext());
      addView(controlFrame, new LinearLayout.LayoutParams(-1, -2, 0.0F));
      addView(this.logView, new LinearLayout.LayoutParams(-1, 0, 1.0F));
      
    }
    
    /**
     * 构建提醒
     * @return
     */
    private LinearLayout buildStatusNotifications()
    {
      LinearLayout list = new LinearLayout(getContext());
      list.setOrientation(1);
      LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(-1, -2);
      

      adapterPresentText = generateStatusLine();
      if (this.networkStatus.getAdapterStatus()==networkStatus.STATUS_SUCCEED)
      {
        adapterPresentText.setText("SDK Available");
        adapterPresentText.setBackgroundColor(-16711936);
      }
      else
      {
        adapterPresentText.setText("SDK Missing");
        adapterPresentText.setBackgroundColor(-65536);
      }
      list.addView(adapterPresentText, blockParams);
      
        TextView configurationPresentText = generateStatusLine();
        if (NetworkManifestReaderUtils.hasRegisterNecessary(mActivity, this.networkStatus))
        {
          configurationPresentText.setText("Configuration present");
          configurationPresentText.setBackgroundColor(-16711936);
        }
        else
        {
          configurationPresentText.setText("Configuration not present");
          configurationPresentText.setBackgroundColor(-65536);
        }
        list.addView(configurationPresentText, blockParams);
      
      adapterStartedText = generateStatusLine();
      if (this.networkStatus.getLocalStatus() == networkStatus.STATUS_SUCCEED)
      {
        adapterStartedText.setText("SDK Started Successfully");
        adapterStartedText.setBackgroundColor(-16711936);
      }
      else
      {
        adapterStartedText.setText("SDK Failed to start or No_fill");
        adapterStartedText.setBackgroundColor(-65536);
      }
      list.addView(adapterStartedText, blockParams);
      
      return list;
    }
    
    /**
     * 重新设置提醒
     */
    private void setAdapterStartedText()
    {
        if (adapterStartedText != null) {
            if (this.networkStatus.getLocalStatus() == networkStatus.STATUS_SUCCEED) {
                adapterStartedText.setText("SDK Started Successfully");
                adapterStartedText.setBackgroundColor(-16711936);
            } else {
                adapterStartedText.setText("SDK Failed to start or No_fill");
                adapterStartedText.setBackgroundColor(-65536);
            }
        }
    }
    
    /**
     * 生成提醒控件
     * @return
     */
    private TextView generateStatusLine()
    {
      int padding = WindowSizeUtils.dip2px(getContext(), 6);
      TextView view = new TextView(getContext());
      view.setTextSize(16.0F);
      view.setTypeface(Typeface.DEFAULT_BOLD);
      view.setPadding(padding, padding, padding, padding);
      view.setTextColor(-1);
      return view;
    }
    
    
    /**
     * 请求banner
     * @param providerBean
     */
    private final void requestBannerLayer(YumiProviderBean providerBean) {
        if (providerBean != null) {
            tempBannerAdapter = YumiBannerAdapterFacotry.getFactory().buildBannerAdapter(mActivity, providerBean,null);
            if (tempBannerAdapter != null) {
                tempBannerAdapter.setmInnerListener(new IYumiInnerLayerStatusListener() {
                    
                    @Override
                    public void onLayerPreparedFailed(YumiProviderBean provider, LayerType type, LayerErrorCode error) {
                        logView.addMessage("banner onPreparedFailed LayerErrorCode:"+error);
                    }
                    
                    @Override
                    public void onLayerPrepared(YumiProviderBean provider, LayerType type) {
                        logView.addMessage("banner onPrepared");
                    }
                    
                    @Override
                    public void onLayerExposure(YumiProviderBean provider, LayerType type) {
                        logView.addMessage("banner onExposure");
                        networkStatus.setLocalStatus(NetworkStatus.STATUS_SUCCEED);
                        setAdapterStartedText();
//                        MediationStatus.addPreparedAdpaterList(provider.getProviderID());
                    }
                    
                    @Override
                    public void onLayerClosed(YumiProviderBean provider, LayerType type) {
                        logView.addMessage("banner onClosed");
                    }
                    
                    @Override
                    public void onLayerCLicked(YumiProviderBean provider, LayerType type, float x, float y) {
                        logView.addMessage("banner onCLicked");
                    }
                });
                tempBannerAdapter.setIsMediation(true);
                tempBannerAdapter.prepareBannerLayer(AdSize.BANNER_SIZE_AUTO, mHandlerBanner, YumiSignUtils.getTrackerID(mActivity, "r"), false, YumiLayerSizeCalculater.calculateLayerSize(mActivity, AdSize.BANNER_SIZE_AUTO, false));
            } else {
            }
        }
    }
    
    
    /**
     * 展示banner
     * @param newBanner
     */
    private final void replaceBannerView(View newBanner)
    {
        
        if(bannerContainer==null)
        {
            bannerContainer = new FrameLayout(mActivity);
//            bannerContainer.setBackgroundColor(Color.BLUE);
            int[] calculateLayerSize = YumiLayerSizeCalculater.calculateLayerSize(mActivity, AdSize.BANNER_SIZE_AUTO,true);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(calculateLayerSize[0], calculateLayerSize[1]);
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.topMargin=50;
            controlFrame.addView(bannerContainer, params);
            ZplayDebug.e(TAG, "replaceBannerView calculateLayerSize[0]:"+calculateLayerSize[0]+ "  calculateLayerSize[1]:"+calculateLayerSize[1], onoff);
        }
            bannerContainer.setVisibility(View.VISIBLE);
            bannerContainer.removeAllViews();
            bannerContainer.addView(newBanner,0);
    }
    
    /**
     * 请求插屏
     * @param providerBean
     */
    private final void requestCpLayer(YumiProviderBean providerBean) {
        if (providerBean != null) {
            tempCpAdapter = YumiInterstitialAdapterFactory.getFactory().buildInterstitialAdapter(mActivity, providerBean,null);
            if (tempCpAdapter != null) {
                tempCpAdapter.setmInnerListener(new IYumiInnerLayerStatusListener() {
                    
                    @Override
                    public void onLayerPreparedFailed(YumiProviderBean provider, LayerType type, LayerErrorCode error) {
                        logView.addMessage("Interstitial onPreparedFailed LayerErrorCode:"+error);
                    }
                    
                    @Override
                    public void onLayerPrepared(YumiProviderBean provider, LayerType type) {
                        logView.addMessage("Interstitial onPrepared");
                    }
                    
                    @Override
                    public void onLayerExposure(YumiProviderBean provider, LayerType type) {
                        logView.addMessage("Interstitial onExposure");
                        networkStatus.setLocalStatus(NetworkStatus.STATUS_SUCCEED);
                        setAdapterStartedText();
//                        MediationStatus.addPreparedAdpaterList(provider.getProviderID());
                    }
                    
                    @Override
                    public void onLayerClosed(YumiProviderBean provider, LayerType type) {
                        logView.addMessage("Interstitial onClosed");
                    }
                    
                    @Override
                    public void onLayerCLicked(YumiProviderBean provider, LayerType type, float x, float y) {
                        logView.addMessage("Interstitial onCLicked");
                    }
                });
                tempCpAdapter.setIsMediation(true);
                tempCpAdapter.prepareInterstitialLayer(YumiSignUtils.getTrackerID(mActivity, "r"));
            } else {
            }
        }
    }
    
    /**
     * show 插屏
     */
    private void showCp()
    {
        if (tempCpAdapter != null)
        {
            tempCpAdapter.showInterstitialLayer(mActivity);
        }
    }

    /**
     * 请求视频
     * @param providerBean
     */
    private final void requestMediaLayer(YumiProviderBean providerBean) {
        if (providerBean != null) {
            tempMediaAdapter = YumiMediaAdapterFactory.getFactory().buildMediaAdapter(mActivity, providerBean,null);
            if (tempMediaAdapter != null) {
                tempMediaAdapter.setmInnerListener(new IYumiMediaInnerLayerStatusListener() {
                    
                    @Override
                    public void onLayerPreparedFailed(YumiProviderBean provider, LayerType type, LayerErrorCode error) {
                        logView.addMessage("Media onPreparedFailed LayerErrorCode:"+error);
                    }
                    
                    @Override
                    public void onLayerPrepared(YumiProviderBean provider, LayerType type) {
                        logView.addMessage("Media onPrepared");
                    }
                    
                    @Override
                    public void onLayerExposure(YumiProviderBean provider, LayerType type) {
                        logView.addMessage("Media onExposure");
                        networkStatus.setLocalStatus(NetworkStatus.STATUS_SUCCEED);
                        setAdapterStartedText();
//                        MediationStatus.addPreparedAdpaterList(provider.getProviderID());
                    }
                    
                    @Override
                    public void onLayerClosed(YumiProviderBean provider, LayerType type) {
                        logView.addMessage("Media onClosed");
                    }
                    
                    @Override
                    public void onLayerCLicked(YumiProviderBean provider, LayerType type, float x, float y) {
                        logView.addMessage("Media onCLicked");
                    }

                    @Override
                    public void onLayerIncentived(YumiProviderBean provider, LayerType type) {
                        logView.addMessage("Media onIncentived");
                    }

                    @Override
                    public void onLayerCanGetReward(YumiProviderBean provider, LayerType type, int remain) {
                        logView.addMessage("Media onCanGetReward");
                    }

                    @Override
                    public void onLayoutDownload() {
                        logView.addMessage("Media onLayoutDownload");
                    }
                });
                tempMediaAdapter.setIsMediation(true);
                tempMediaAdapter.prepareMedia(YumiSignUtils.getTrackerID(mActivity, "r"));
            } else {
            }
        }
    }
    
    /**
     * show 视频
     */
    private void showMedia()
    {
        if (tempMediaAdapter != null)
        {
            MediaStatus ms=tempMediaAdapter.showMedia();
        }
    }
    
    public void onDestroy() {
        if (tempBannerAdapter != null) {
            tempBannerAdapter.onActivityDestroy();
        }
        if (tempCpAdapter != null) {
            tempCpAdapter.onActivityDestroy();
        }
        if (tempMediaAdapter != null) {
            tempMediaAdapter.onActivityDestroy();
        }
    }
}
