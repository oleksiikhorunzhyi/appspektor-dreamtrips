package com.worldventures.dreamtrips.modules.gcm.model;

import com.google.gson.annotations.SerializedName;

public class NewLocationPushMessage extends NewMessagePushMessage {
   @SerializedName("ll") private final String latitudeLongitude;

   public NewLocationPushMessage(AlertWrapper alertWrapper, String latitudeLongitude, int notificationId, int notificationsCount, int requestsCount, String conversationId, int unreadConversationsCount) {
      super(alertWrapper, PushType.NEW_LOC_MESSAGE, notificationId, notificationsCount, requestsCount, conversationId, unreadConversationsCount);
      this.latitudeLongitude = latitudeLongitude;
   }

   public double[] getLatitudeLongitude() {
      String[] latLng = latitudeLongitude.split(",");
      double latitude = Double.parseDouble(latLng[0]);
      double longitude = Double.parseDouble(latLng[1]);
      return new double[]{latitude, longitude};
   }
}
