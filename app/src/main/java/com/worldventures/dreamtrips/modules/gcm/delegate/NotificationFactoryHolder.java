package com.worldventures.dreamtrips.modules.gcm.delegate;

import com.messenger.notification.MessengerNotificationFactory;
import com.worldventures.dreamtrips.modules.friends.notification.FriendNotificationFactory;

public class NotificationFactoryHolder {

   private FriendNotificationFactory friendNotificationFactory;
   private PhotoNotificationFactory photoNotificationFactory;
   private MessengerNotificationFactory messengerNotificationFactory;
   private MerchantNotficationFactory merchantNotficationFactory;

   public NotificationFactoryHolder(FriendNotificationFactory friendNotificationFactory,
         PhotoNotificationFactory photoNotificationFactory, MessengerNotificationFactory messengerNotificationFactory,
         MerchantNotficationFactory merchantNotficationFactory) {
      this.friendNotificationFactory = friendNotificationFactory;
      this.photoNotificationFactory = photoNotificationFactory;
      this.messengerNotificationFactory = messengerNotificationFactory;
      this.merchantNotficationFactory = merchantNotficationFactory;

   }

   FriendNotificationFactory getFriendNotificationFactory() {
      return friendNotificationFactory;
   }

   PhotoNotificationFactory getPhotoNotificationFactory() {
      return photoNotificationFactory;
   }

   MessengerNotificationFactory getMessengerNotificationFactory() {
      return messengerNotificationFactory;
   }

   MerchantNotficationFactory getMerchantNotifcationFactory() {
      return merchantNotficationFactory;
   }
}
