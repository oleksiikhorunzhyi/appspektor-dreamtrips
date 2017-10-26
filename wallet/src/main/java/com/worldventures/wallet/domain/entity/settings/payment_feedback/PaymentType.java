package com.worldventures.wallet.domain.entity.settings.payment_feedback;

public enum PaymentType {
   MAG_STRIPE_SWIPE("Mag stripe swipe"), WIRELESS_MAGNETIC_SWIPE("Wireless magnetic swipe");

   private String type;

   PaymentType(String type) {
      this.type = type;
   }

   public String type() {
      return type;
   }
}
