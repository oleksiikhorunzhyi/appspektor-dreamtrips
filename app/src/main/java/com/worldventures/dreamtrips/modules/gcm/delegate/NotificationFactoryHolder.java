package com.worldventures.dreamtrips.modules.gcm.delegate;

import com.messenger.notification.MessengerNotificationFactory;
import com.worldventures.dreamtrips.modules.friends.notification.FriendNotificationFactory;

public class NotificationFactoryHolder {

   private FriendNotificationFactory friendNotificationFactory;
   private PhotoNotificationFactory photoNotificationFactory;
   private MessengerNotificationFactory messengerNotificationFactory;

   public NotificationFactoryHolder(FriendNotificationFactory friendNotificationFactory, PhotoNotificationFactory photoNotificationFactory, MessengerNotificationFactory messengerNotificationFactory) {
      this.friendNotificationFactory = friendNotificationFactory;
      this.photoNotificationFactory = photoNotificationFactory;
      this.messengerNotificationFactory = messengerNotificationFactory;

   }

   public FriendNotificationFactory getFriendNotificationFactory() {
      return friendNotificationFactory;
   }

   public PhotoNotificationFactory getPhotoNotificationFactory() {
      return photoNotificationFactory;
   }

   public MessengerNotificationFactory getMessengerNotificationFactory() {
      return messengerNotificationFactory;
   }
}
