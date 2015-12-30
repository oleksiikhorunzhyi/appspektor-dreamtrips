package com.messenger.ui.widget.inappnotification;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.messenger.ui.widget.OnSwipeGestureListener;
import com.worldventures.dreamtrips.R;

public abstract class BaseInAppNotificationView extends FrameLayout {

    protected InAppNotificationViewListener listener;

    public void setListener(InAppNotificationViewListener listener){
        this.listener = listener;
    }

    public BaseInAppNotificationView(Context context) {
        super(context);
        initialize();
    }

    public BaseInAppNotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public BaseInAppNotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseInAppNotificationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    protected void initialize(){
        // does not work currently, prevents close button click being processed
//        setOnTouchListener(new OnSwipeGestureListener(getContext()){
//            @Override
//            public void onSwipeTop() {
//                if (listener != null){
//                    listener.onCloseSwipe();
//                }
//            }
//
//            @Override
//            public void onSingleTap(){
//                if (listener!=null){
//                    listener.onClick();
//                }
//            }
//        });

        View closeBtn = findViewById(R.id.in_app_notif_close_btn);
        if (closeBtn != null){
            closeBtn.setOnClickListener(v -> {
                if (listener != null){
                    listener.onCloseClick();
                }
            });
        }
    };
}
