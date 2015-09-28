package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.worldventures.dreamtrips.modules.friends.notification.FriendNotificationFactory;
import com.worldventures.dreamtrips.modules.friends.notification.UserNotificationData;

public class NotificationDelegate {

    private final Context context;
    private final NotificationManager notificationManager;
    //
    private final NotificationDataParser dataParser;
    //
    private final FriendNotificationFactory friendNotificationFactory;

    public NotificationDelegate(Context context, NotificationDataParser userDataParser, FriendNotificationFactory friendNotificationFactory) {
        this.context = context;
        this.dataParser = userDataParser;
        this.friendNotificationFactory = friendNotificationFactory;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notifyFriendRequestAccepted(Bundle bundle) {
        UserNotificationData userData = dataParser.parseUserData(bundle);
        Notification notification = friendNotificationFactory.createFriendRequestAccepted(userData);
        notificationManager.notify(userData.userId, notification);
    }

    public void notifyFriendRequestReceived(Bundle data) {
        UserNotificationData userData = dataParser.parseUserData(data);
        Notification notification = friendNotificationFactory.createFriendRequestReceived(userData);
        notificationManager.notify(userData.userId, notification);
    }

    public void cancel(int id) {
        notificationManager.cancel(id);
    }

}
