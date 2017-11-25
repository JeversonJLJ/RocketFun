package com.jljsoluctions.rocketfun.Class;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.webkit.WebView;


/**
 * Created by jever on 23/11/2017.
 */

public class CustomWebView extends WebView {

    public CustomWebView(Context context) {
        super(context);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_MOVE)
            requestDisallowInterceptTouchEvent(true);
        else
            requestDisallowInterceptTouchEvent(false);

        return super.onTouchEvent(event);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (clampedY)
            requestDisallowInterceptTouchEvent(false);
        if (clampedX)
            requestDisallowInterceptTouchEvent(false);
    }


}
