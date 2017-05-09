package com.yumi.android.sdk.ads.utils.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class AdContainer extends FrameLayout {

    private IAdContainerOnWindowFocusChanged iListener;

    public AdContainer(Context context) {
        super(context);
    }
    
    public AdContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public AdContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public AdContainer(Context context, IAdContainerOnWindowFocusChanged listener) {
        super(context);
        iListener = listener;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setListener(IAdContainerOnWindowFocusChanged listener) {
        iListener = listener;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (iListener != null) {
            iListener.onWindowFocusChanged(hasWindowFocus);
        }
    }

    /**
     * 广告父容器onWindowFocusChanged回调
     * @author Administrator
     *
     */
    public interface IAdContainerOnWindowFocusChanged {
        public void onWindowFocusChanged(boolean hasWindowFocus);
    }
}
