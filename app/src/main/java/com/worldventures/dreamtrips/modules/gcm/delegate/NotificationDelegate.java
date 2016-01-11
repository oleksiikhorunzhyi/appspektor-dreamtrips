package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.gcm.model.PushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.TaggedOnPhotoPushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.UserPushMessage;

import de.greenrobot.event.EventBus;

public class NotificationDelegate {

    private final EventBus bus;
    private final SnappyRepository repository;
    private final NotificationManager notificationManager;
    //
    private final NotificationFactoryHolder notificationFactoryHolder;

    public NotificationDelegate(Context context, EventBus bus, SnappyRepository repository, NotificationFactoryHolder notificationFactoryHolder) {
        this.bus = bus;
        this.repository = repository;
        this.notificationFactoryHolder = notificationFactoryHolder;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void updateNotificationCount(PushMessage data){
        repository.saveBadgeNotificationsCount(data.alertWrapper.badge);
        repository.saveCountFromHeader(SnappyRepository.EXCLUSIVE_NOTIFICATIONS_COUNT, data.notificationsCount);
        repository.saveCountFromHeader(SnappyRepository.FRIEND_REQUEST_COUNT, data.requestsCount);
        bus.post(new HeaderCountChangedEvent());
    }

    public void notifyFriendRequestAccepted(UserPushMessage message) {
        Notification notification = notificationFactoryHolder.getFriendNotificationFactory().createFriendRequestAccepted(message);
        notificationManager.notify(message.userId, notification);
    }

    public void notifyFriendRequestReceived(UserPushMessage message) {
        Notification notification = notificationFactoryHolder.getFriendNotificationFactory().createFriendRequestReceived(message);
        notificationManager.notify(message.userId, notification);
    }

    public void notifyTaggedOnPhoto(TaggedOnPhotoPushMessage message) {
        Notification notification = notificationFactoryHolder.getPhotoNotificationFactory().createTaggedOnPhoto(message);
        notificationManager.notify(message.userId, notification);
    }

    public void cancel(int id) {
        notificationManager.cancel(id);
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }

}
