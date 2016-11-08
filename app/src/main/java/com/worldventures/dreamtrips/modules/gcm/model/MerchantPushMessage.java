package com.worldventures.dreamtrips.modules.gcm.model;


public class MerchantPushMessage extends PushMessage {

   private String points;
   private String merchantName;
   private String city;
   private String transactionDate;

   public MerchantPushMessage(AlertWrapper alertWrapper, PushType type, int notificationId, int notificationsCount,
         int requestsCount, String points, String merchantName, String city, String transactionDate) {
      super(alertWrapper, type, notificationId, notificationsCount, requestsCount);
      this.points = points;
      this.merchantName = merchantName;
      this.city = city;
      this.transactionDate = transactionDate;
   }

   public String getPoints() {
      return points;
   }

   public String getMerchantName() {
      return merchantName;
   }

   public String getCity() {
      return city;
   }

   public String getTransactionDate() {
      return transactionDate;
   }
}
