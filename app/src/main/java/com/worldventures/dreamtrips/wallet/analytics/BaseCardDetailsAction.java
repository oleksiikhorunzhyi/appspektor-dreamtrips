package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

public abstract class BaseCardDetailsAction extends WalletAnalyticsAction {

   @Attribute("paycardtype") String paycardType = "Unknown";
   @Attribute("paycardissuer") String paycardIssuer = "Unknown";
   @Attribute("cardtype") final String cardtype = "Payment";

   public void fillPaycardInfo(Record record) {
      if (record.financialService() == null) return;
      switch (record.financialService()) {
         case AMEX:
            paycardIssuer = "American Express";
            paycardType = "American Express";
            break;
         case VISA:
            paycardIssuer = record.bankName();
            paycardType = "Visa";
            break;
         case MASTERCARD:
            paycardIssuer = record.bankName();
            paycardType = "MasterCard";
            break;
         case DISCOVER:
            paycardIssuer = "Discover";
            paycardType = "Discover";
            break;
      }
   }
}
