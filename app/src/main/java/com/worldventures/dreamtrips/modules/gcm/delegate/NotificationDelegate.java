package com.worldventures.dreamtrips.modules.gcm.delegate;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.messenger.notification.MessengerNotificationFactory;
import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.gcm.model.MerchantPushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewImagePushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewLocationPushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewMessagePushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.NewUnsupportedMessage;
import com.worldventures.dreamtrips.modules.gcm.model.PushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.TaggedOnPhotoPushMessage;
import com.worldventures.dreamtrips.modules.gcm.model.UserPushMessage;

import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NotificationDelegate {

   private final NotificationCountEventDelegate notificationCountEventDelegate;
   private final SnappyRepository repository;
   private final NotificationManager notificationManager;
   //
   private final NotificationFactoryHolder notificationFactoryHolder;

   public NotificationDelegate(Context context, NotificationCountEventDelegate notificationCountEventDelegate, SnappyRepository repository, NotificationFactoryHolder notificationFactoryHolder) {
      this.notificationCountEventDelegate = notificationCountEventDelegate;
      this.repository = repository;
      this.notificationFactoryHolder = notificationFactoryHolder;
      this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
   }

   public void updateNotificationCount(PushMessage data) {
      repository.saveBadgeNotificationsCount(data.alertWrapper.badge);
      repository.saveNotificationsCount(data.notificationsCount);
      repository.saveFriendRequestsCount(data.requestsCount);
      notificationCountEventDelegate.post(null);
   }

   public void notifyFriendRequestAccepted(UserPushMessage message) {
      Notification notification = notificationFactoryHolder.getFriendNotificationFactory()
            .createFriendRequestAccepted(message);
      notificationManager.notify(message.userId, notification);
   }

   public void notifyFriendRequestReceived(UserPushMessage message) {
      Notification notification = notificationFactoryHolder.getFriendNotificationFactory()
            .createFriendRequestReceived(message);
      notificationManager.notify(message.userId, notification);
   }

   public void notifyTaggedOnPhoto(TaggedOnPhotoPushMessage message) {
      Notification notification = notificationFactoryHolder.getPhotoNotificationFactory().createTaggedOnPhoto(message);
      notificationManager.notify(message.userId, notification);
   }

   public void notifyNewMessageReceived(NewMessagePushMessage message) {
      Notification notification = notificationFactoryHolder.getMessengerNotificationFactory().createNewMessage(message);
      notificationManager.notify(MessengerNotificationFactory.MESSENGER_TAG, MessengerNotificationFactory.MESSAGE_NOTIFICATION_ID, notification);
   }

   public void notifyNewImageMessageReceived(NewImagePushMessage message) {
      notificationFactoryHolder.getMessengerNotificationFactory()
            .createNewImageMessage(message)
            .subscribeOn(Schedulers.io())
            .subscribe(notification -> {
               notificationManager.notify(MessengerNotificationFactory.MESSENGER_TAG, MessengerNotificationFactory.MESSAGE_NOTIFICATION_ID, notification);
            }, e -> Timber.w(e, "Failed with creation of image message notification"));
   }

   public void notifyNewLocationMessageReceived(NewLocationPushMessage message) {
      Notification notification = notificationFactoryHolder.getMessengerNotificationFactory()
            .createNewLocationMessage(message);
      notificationManager.notify(MessengerNotificationFactory.MESSENGER_TAG, MessengerNotificationFactory.MESSAGE_NOTIFICATION_ID, notification);
   }

   public void notifyMerchantMessageReceived(MerchantPushMessage message) {
      Notification notification = notificationFactoryHolder.getMerchantNotifcationFactory()
            .createMerchantNotification(message);
      notificationManager.notify(message.getMerchantName().hashCode(), notification);
   }

   public void notifyUnsupportedMessageReceived(NewUnsupportedMessage newUnsupportedMessage) {
      Notification notification = notificationFactoryHolder.getMessengerNotificationFactory()
            .createUnsupportedMessage(newUnsupportedMessage);
      notificationManager.notify(MessengerNotificationFactory.MESSENGER_TAG, MessengerNotificationFactory.MESSAGE_NOTIFICATION_ID, notification);
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
