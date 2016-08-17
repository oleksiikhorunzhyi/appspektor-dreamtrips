package com.worldventures.dreamtrips.modules.gcm.model;

import com.google.gson.annotations.SerializedName;

public class NewImagePushMessage extends NewMessagePushMessage {
   @SerializedName("image_url") private final String imageUrl;

   public NewImagePushMessage(AlertWrapper alertWrapper, String imageUrl, int notificationId, int notificationsCount, int requestsCount, String conversationId, int unreadConversationsCount) {
      super(alertWrapper, PushType.NEW_IMG_MESSAGE, notificationId, notificationsCount, requestsCount, conversationId, unreadConversationsCount);
      this.imageUrl = imageUrl;
   }

   public String getImageUrl() {
      return imageUrl;
   }


}
