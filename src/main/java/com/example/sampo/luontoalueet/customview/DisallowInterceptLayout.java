package com.example.sampo.luontoalueet.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * This class simply calls {@link android.view.ViewGroup#requestDisallowInterceptTouchEvent(boolean)}
 * in {@link android.view.ViewGroup#dispatchTouchEvent(MotionEvent)} and passes to it
 * {@link #disallowParentIntercept}.
 */
public class DisallowInterceptLayout extends FrameLayout {

    private boolean disallowParentIntercept = false;

    public DisallowInterceptLayout(Context context) {
        super(context);
    }

    public DisallowInterceptLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisallowInterceptLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DisallowInterceptLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (disallowParentIntercept) {
            final ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(disallowParentIntercept);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setDisallowParentIntercept(boolean disallowParentIntercept) {
        this.disallowParentIntercept = disallowParentIntercept;
    }

    public boolean willDisallowParentIntercept() {
        return disallowParentIntercept;
    }
}