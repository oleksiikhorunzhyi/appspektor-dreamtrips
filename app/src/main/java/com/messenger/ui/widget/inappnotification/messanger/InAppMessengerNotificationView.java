package com.messenger.ui.widget.inappnotification.messanger;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.widget.inappnotification.BaseInAppNotificationView;

public abstract class InAppMessengerNotificationView extends BaseInAppNotificationView {

    public abstract void setTitle(String title);

    public abstract void setText(String text);

    public InAppMessengerNotificationView(Context context) {
        super(context);
    }

    public InAppMessengerNotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InAppMessengerNotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}