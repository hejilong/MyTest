package com.yumi.android.sdk.ads.mediation.views;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * log输出控件
 * @author  hejilong  2016-11-16
 *
 */
public class LogView extends ScrollView {
    StringBuilder log = new StringBuilder();
    TextView txtLog;
    
    public LogView(Context context)
    {
      super(context);
      this.txtLog = new TextView(getContext());
      this.txtLog.setTextColor(-16777216);
      addView(this.txtLog, new LinearLayout.LayoutParams(-1, -2));
    }
    
    public void addMessage(final String message)
    {
      ((Activity)getContext()).runOnUiThread(new Runnable()
      {
        public void run()
        {
          Log.d("MediationTestActivity", "adding log: " + message);
          SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
          Date date = new Date();
          LogView.this.log.append("[" + dateFormat.format(date) + "] " + message + "\n");
          LogView.this.txtLog.setText(LogView.this.log.toString());
        }
      });
    }
}
