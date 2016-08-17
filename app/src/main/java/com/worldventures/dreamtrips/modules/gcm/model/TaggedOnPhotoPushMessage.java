package com.worldventures.dreamtrips.modules.gcm.model;

public class TaggedOnPhotoPushMessage extends UserPushMessage {

   public final String photoUid;

   public TaggedOnPhotoPushMessage(AlertWrapper alertWrapper, PushType type, int notificationId, int notificationsCount, int requestsCount, int userId, String photoUid) {
      super(alertWrapper, type, notificationId, notificationsCount, requestsCount, userId);
      this.photoUid = photoUid;
   }
}
