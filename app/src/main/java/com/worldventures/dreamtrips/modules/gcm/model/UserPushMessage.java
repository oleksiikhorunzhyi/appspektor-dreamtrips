package com.worldventures.dreamtrips.modules.gcm.model;

public class UserPushMessage extends PushMessage {

   public final int userId;

   public UserPushMessage(AlertWrapper alertWrapper, PushType type, int notificationId, int notificationsCount, int requestsCount, int userId) {
      super(alertWrapper, type, notificationId, notificationsCount, requestsCount);
      this.userId = userId;
   }

}
