package com.worldventures.wallet.service.command.reset;

public final class ResetOptions {

   private boolean withEnterPin = false;
   private boolean withPaymentCards = true;
   private boolean withUserSmartCardData = true;
   private boolean smartCardIsAvailable = true;

   private ResetOptions() {}

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

   public boolean isSmartCardIsAvailable() {
      return smartCardIsAvailable;
   }

   public static class Builder {

      private final ResetOptions resetOptions;

      public Builder() {
         resetOptions = new ResetOptions();
      }

      public Builder withEnterPin(boolean withEnterPin) {
         resetOptions.withEnterPin = withEnterPin;
         return this;
      }

      public Builder wipePaymentCards(boolean wiped) {
         resetOptions.withPaymentCards = wiped;
         return this;
      }

      public Builder wipeUserSmartCardData(boolean wiped) {
         resetOptions.withUserSmartCardData = wiped;
         return this;
      }

      public Builder smartCardIsAvailable(boolean smartCardIsAvailable) {
         resetOptions.smartCardIsAvailable = smartCardIsAvailable;
         return this;
      }

      public ResetOptions build() {
         return resetOptions;
      }
   }
}
