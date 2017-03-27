package com.worldventures.dreamtrips.wallet.domain.entity;

public class FactoryResetOptions {

   private boolean withEnterPin = false;
   private boolean withPaymentCards = true;
   private boolean withUserSmartCardData = true;

   private FactoryResetOptions() {}

   public static Builder builder() {
      return new Builder();
   }

   public boolean isWithPaymentCards() {
      return withPaymentCards;
   }

   public boolean isWithUserSmartCardData() {
      return withUserSmartCardData;
   }

   public boolean isWithEnterPin() {
      return withEnterPin;
   }

   public static class Builder {

      private FactoryResetOptions options;

      public Builder() {
         options = new FactoryResetOptions();
      }

      public Builder withEnterPin(boolean withEnterPin) {
         options.withEnterPin = withEnterPin;
         return this;
      }

      public Builder wipePaymentCards(boolean wiped) {
         options.withPaymentCards = wiped;
         return this;
      }

      public Builder wipeUserSmartCardData(boolean wiped) {
         options.withUserSmartCardData = wiped;
         return this;
      }

      public FactoryResetOptions build() {
         return options;
      }
   }
}
