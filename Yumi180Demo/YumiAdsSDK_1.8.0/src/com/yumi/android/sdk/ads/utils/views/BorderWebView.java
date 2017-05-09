package com.yumi.android.sdk.ads.utils.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

/**
 * 可以设置白色边框的webview
 * @author Administrator
 *
 */
public class BorderWebView extends WebView {
    
    //边框宽度 px
    private final int stroke_width = 4;  
    //是否添加边框
    private boolean haveStroke=true;

    public BorderWebView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    public BorderWebView(Context context,Boolean haveStroke) {
        super(context);
        this.haveStroke=haveStroke;
    }
    
    public BorderWebView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
    
    public BorderWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
//        //获取控件需要重新绘制的区域
        if (haveStroke) {
            Rect rect = canvas.getClipBounds();
            rect.bottom--;
            rect.right--;
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(stroke_width);
            canvas.drawRect(rect, paint);
        }
    }  

}
