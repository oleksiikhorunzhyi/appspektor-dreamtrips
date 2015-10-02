package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.friends.notification.FriendNotificationFactory;
import com.worldventures.dreamtrips.modules.gcm.model.PushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.UserPushMessage;

import de.greenrobot.event.EventBus;

public class NotificationDelegate {

    private final Context context;
    private final EventBus bus;
    private final SnappyRepository repository;
    private final NotificationManager notificationManager;
    //
    private final FriendNotificationFactory friendNotificationFactory;

    public NotificationDelegate(Context context, EventBus bus, SnappyRepository repository, FriendNotificationFactory friendNotificationFactory) {
        this.context = context;
        this.bus = bus;
        this.repository = repository;
        this.friendNotificationFactory = friendNotificationFactory;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void updateNotificationCount(PushMessage data){
        repository.saveNotificationsCount(data.alertWrapper.badge);
        bus.post(new HeaderCountChangedEvent());
    }

    public void notifyFriendRequestAccepted(UserPushMessage message) {
        Notification notification = friendNotificationFactory.createFriendRequestAccepted(message);
        notificationManager.notify(message.userId, notification);
    }

    public void notifyFriendRequestReceived(UserPushMessage message) {
        Notification notification = friendNotificationFactory.createFriendRequestReceived(message);
        notificationManager.notify(message.userId, notification);
    }

    public void cancel(int id) {
        notificationManager.cancel(id);
    }

}
