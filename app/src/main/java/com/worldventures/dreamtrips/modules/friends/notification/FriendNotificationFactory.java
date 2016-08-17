package com.worldventures.dreamtrips.modules.friends.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationFactory;
import com.worldventures.dreamtrips.modules.gcm.model.UserPushMessage;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

public class FriendNotificationFactory extends NotificationFactory {

   private final RouteCreator<Integer> routeCreator;

   public FriendNotificationFactory(Context context, RouteCreator<Integer> profileRouteCreator) {
      super(context);
      this.routeCreator = profileRouteCreator;
   }

   public Notification createFriendRequestAccepted(UserPushMessage data) {
      String message = context.getString(R.string.notification_message_friend_accepted, TextUtils.join(" ", data.alertWrapper.alert.locArgs));
      NotificationCompat.Builder notification = createFriendNotification(data.userId, data.notificationId, message);
      return notification.build();
   }

   public Notification createFriendRequestReceived(UserPushMessage data) {
      String message = context.getString(R.string.notification_message_friend_request, TextUtils.join(" ", data.alertWrapper.alert.locArgs));
      //
      NotificationCompat.Builder notification = createFriendNotification(data.userId, data.notificationId, message);
      // respond actions
      PendingIntent acceptIntent = createFriendIntent(data.userId, new UserBundle(new User(data.userId), data.notificationId, true), true);
      NotificationCompat.Action acceptAction = new NotificationCompat.Action(R.drawable.ic_action_accept, context.getString(R.string.friend_accept), acceptIntent);
      //
      Intent rejectDataIntent = new Intent(context, FriendRejectActionReceiver.class);
      rejectDataIntent.putExtra(ComponentPresenter.EXTRA_DATA, new UserBundle(new User(data.userId), data.notificationId));
      PendingIntent rejectIntent = PendingIntent.getBroadcast(context, 1, rejectDataIntent, PendingIntent.FLAG_ONE_SHOT);
      NotificationCompat.Action rejectAction = new NotificationCompat.Action(R.drawable.ic_action_cancel, context.getString(R.string.friend_reject), rejectIntent);
      //
      notification.addAction(acceptAction);
      notification.addAction(rejectAction);

      return notification.build();
   }

   /**
    * Shows a push notification with ability to open user profile
    *
    * @param userId         userId to get Route for navigation, and also unique id for the push
    * @param notificationId UserPresenter need notification to mark actual notification
    * @param message        actual message that will be shown in notification
    */
   private NotificationCompat.Builder createFriendNotification(int userId, int notificationId, String message) {
      PendingIntent intent = createFriendIntent(userId, new UserBundle(new User(userId), notificationId), false);
      return super.createNotification().setContentIntent(intent).setContentText(message);
   }

   private PendingIntent createFriendIntent(int userId, UserBundle userBundle, boolean forAction) {
      Intent resultIntent = new Intent(context, ComponentActivity.class);
      //set args to pending intent
      Bundle args = new Bundle();
      args.putSerializable(ComponentPresenter.ROUTE, routeCreator.createRoute(userId));
      args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG, ToolbarConfig.Builder.create()
            .visible(false)
            .build());
      args.putParcelable(ComponentPresenter.EXTRA_DATA, userBundle);
      resultIntent.putExtra(ComponentPresenter.COMPONENT_EXTRA, args);
      //
      TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
      stackBuilder.addParentStack(ComponentActivity.class);
      stackBuilder.addNextIntent(resultIntent);
      //
      return stackBuilder.getPendingIntent(0, forAction ? PendingIntent.FLAG_ONE_SHOT : PendingIntent.FLAG_CANCEL_CURRENT);
   }

}
