package com.worldventures.dreamtrips.modules.gcm.model;

import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

public class PushMessage {

   @SerializedName("aps") public final AlertWrapper alertWrapper;
   public final PushType type;
   public final int notificationId;
   public final int notificationsCount;
   public final int requestsCount;

   public PushMessage(AlertWrapper alertWrapper, PushType type, int notificationId, int notificationsCount, int requestsCount) {
      this.alertWrapper = alertWrapper;
      this.type = type;
      this.notificationId = notificationId;
      this.notificationsCount = notificationsCount;
      this.requestsCount = requestsCount;
   }

   public static class AlertWrapper {
      public final Alert alert;
      public final int badge;

      public AlertWrapper(Alert alert, int badge) {
         this.alert = alert;
         this.badge = badge;
      }
   }

   public static class Alert {
      @SerializedName("loc-key") public final String locKey;
      @SerializedName("loc-args") public final List<String> locArgs;

      public Alert(String locKey, List<String> locArgs) {
         this.locKey = locKey;
         this.locArgs = locArgs;
      }
   }

   public enum PushType {

      ACCEPT_REQUEST, SEND_REQUEST, BADGE_UPDATE, TAGGED_ON_PHOTO, NEW_MESSAGE, NEW_IMG_MESSAGE,
      NEW_LOC_MESSAGE, UNSUPPORTED_MESSAGE, MERCHANT_REWARD_POINTS, UNKNOWN;

      public static PushType of(String type) {
         PushType result = Queryable.from(PushType.values()).firstOrDefault(element -> element.name()
               .equalsIgnoreCase(type));
         return result == null ? UNKNOWN : result;
      }
   }
}
