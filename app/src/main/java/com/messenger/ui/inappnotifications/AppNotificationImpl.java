package com.messenger.ui.inappnotifications;

import android.app.Activity;

import com.messenger.ui.inappnotifications.appmsg.AppMsg;
import com.messenger.ui.widget.inappnotification.BaseInAppNotificationView;
import com.messenger.ui.widget.inappnotification.InAppNotificationViewListener;
import com.worldventures.dreamtrips.App;

public class AppNotificationImpl implements AppNotification {

   private static final int SHOWING_DURATION = 3000;
   private static final int PRIORITY_NORMAL = 0;
   private int currentMessagePriority = PRIORITY_NORMAL;

   public AppNotificationImpl(App app) {
   }

   @Override
   public void show(Activity activity, BaseInAppNotificationView view, final InAppNotificationEventListener listener) {
      final AppMsg appMsg = AppMsg.showCustomView(activity, view, SHOWING_DURATION, currentMessagePriority--);
      appMsg.show();

      view.setListener(new InAppNotificationViewListener() {
         @Override
         public void onClick() {
            appMsg.cancel();
            if (listener != null) {
               listener.onClick();
            }
         }

         @Override
         public void onCloseClick() {
            appMsg.cancel();
            if (listener != null) {
               listener.onClose();
            }
         }

         @Override
         public void onCloseSwipe() {
            appMsg.cancel();
            if (listener != null) {
               listener.onClose();
            }
         }
      });
   }

   @Override
   public void dismissForActivity(Activity activity) {
      AppMsg.cancelAll(activity);
   }
}
