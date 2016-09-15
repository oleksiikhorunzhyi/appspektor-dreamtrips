package com.messenger.ui.inappnotifications;

import android.app.Activity;

import com.messenger.ui.widget.inappnotification.BaseInAppNotificationView;

public interface AppNotification {
   void show(Activity activity, BaseInAppNotificationView view, final InAppNotificationEventListener listener);

   void dismissForActivity(Activity activity);
}
