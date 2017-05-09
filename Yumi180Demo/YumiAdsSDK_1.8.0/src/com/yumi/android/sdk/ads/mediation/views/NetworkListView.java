package com.yumi.android.sdk.ads.mediation.views;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yumi.android.sdk.ads.mediation.activity.MediationTestActivity;
import com.yumi.android.sdk.ads.mediation.data.MediationStatus;
import com.yumi.android.sdk.ads.mediation.data.NetworkStatus;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

/**
 * SDK列表视图
 * @author  hejilong  2016-11-16
 *
 */
public class NetworkListView extends ScrollView
{
    private static final boolean onoff = true;
    MediationStatus status = null;
    MediationTestActivity testActivity = null;
    LinearLayout list;
    
    public NetworkListView(Context context)
    {
      super(context);
      this.list = new LinearLayout(context);
      this.list.setOrientation(1);
      
      setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
      
      FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -2);
      


      addView(this.list, lp);
    }
    
    /**
     * 设置三方sdk列表视图
     * @param testActivity
     * @param mediationStatus
     */
    public void setStatus(final MediationTestActivity testActivity, MediationStatus mediationStatus)
    {
      this.list.removeAllViews();
      
      this.status = mediationStatus;
      this.testActivity = testActivity;
      for (final NetworkStatus networkStatus : mediationStatus.networkStatusList) {
//        if ((networkStatus.getAdapterStatus() != 0) || (networkStatus.getRemoteStatus() != 0) || (networkStatus.getLocalStatus() != 0))
//        {
          FrameLayout liFrame = new FrameLayout(getContext());
          liFrame.setBackgroundColor(-5592406);
          liFrame.setPadding(0, 0, 0, 1);
          
          LinearLayout li = new LinearLayout(getContext());
          FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -2);

          lp.setMargins(0, 0, 0, 1);
          li.setLayoutParams(lp);
          li.setBackgroundColor(-1);
          
          TextView statusMark = new TextView(getContext());
//          statusMark.setText((networkStatus.getLocalStatus() == NetworkStatus.STATUS_SUCCEED) ? "on" : "off");  
//          statusMark.setBackgroundColor(networkStatus.getNetworkStatus() ? -16711936 : -65536);
          statusMark.setBackgroundColor(networkStatus.getLocalStatus() == NetworkStatus.STATUS_SUCCEED ? -16711936 : -65536);
          statusMark.setTextColor(-16777216);
          statusMark.getBackground().setAlpha(networkStatus.getNetworkStatus() ? 130 : 180);
          statusMark.setTextSize(12.0F);
          statusMark.setTypeface(null, 1);
          statusMark.setGravity(17);
          li.addView(statusMark, new LinearLayout.LayoutParams(WindowSizeUtils.dip2px(getContext(), 30), -1));
          
          TextView nameView = new TextView(getContext());
          nameView.setText(networkStatus.getNameUpperCase());
          ZplayDebug.v("MediationTestActivity", "adding network named: " + networkStatus.getName(),onoff);
          nameView.setTextSize(16.0F);
          nameView.setTextColor(-16777216);
          int px = (int)TypedValue.applyDimension(1, 14.0F, getContext().getResources().getDisplayMetrics());
          nameView.setPadding(px, px, px, px);
          li.addView(nameView);
          
          liFrame.addView(li);
          this.list.addView(liFrame);
          liFrame.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View view)
            {
              testActivity.showNetworkDetails(networkStatus);
            }
          });
//        }
      }
      this.status = mediationStatus;
    }
  }
