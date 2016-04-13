package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.messenger.notification.MessengerNotificationFactory;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.gcm.model.NewImagePushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewLocationPushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewMessagePushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewUnsupportedMessage;
import com.worldventures.dreamtrips.modules.gcm.model.PushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.TaggedOnPhotoPushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.UserPushMessage;

import java.util.Random;

import de.greenrobot.event.EventBus;
import rx.schedulers.Schedulers;
import timber.log.Timber;

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

    public void updateNotificationCount(PushMessage data) {
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

    public void notifyNewMessageReceived(NewMessagePushMessage message) {
        Notification notification = notificationFactoryHolder.getMessengerNotificationFactory().createNewMessage(message);
        notificationManager.notify(MessengerNotificationFactory.MESSENGER_TAG, 0, notification);
    }

    public void notifyNewImageMessageReceived(NewImagePushMessage message) {
        notificationFactoryHolder.getMessengerNotificationFactory()
                .createNewImageMessage(message)
                .subscribeOn(Schedulers.io())
                .subscribe(notification -> {
                    notificationManager.notify(MessengerNotificationFactory.MESSENGER_TAG, new Random().nextInt(), notification);
                }, e -> Timber.w(e, "Failed with creation of image message notification"));
    }

    public void notifyNewLocationMessageReceived(NewLocationPushMessage message) {
        Notification notification = notificationFactoryHolder.getMessengerNotificationFactory()
                .createNewLocationMessage(message);
        notificationManager.notify(MessengerNotificationFactory.MESSENGER_TAG, 0, notification);
    }

    public void notifyUnsupportedMessageReceived(NewUnsupportedMessage newUnsupportedMessage){
        Notification notification = notificationFactoryHolder.getMessengerNotificationFactory()
                .createUnsupportedMessage(newUnsupportedMessage);
        notificationManager.notify(MessengerNotificationFactory.MESSENGER_TAG, 0, notification);
    }

    public void cancel(String tag) {
        notificationManager.cancel(tag, 0);
    }

    public void cancel(int id) {
        notificationManager.cancel(id);
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }
}
