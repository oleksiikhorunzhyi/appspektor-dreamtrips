package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

public abstract class BaseCardDetailsAction extends WalletAnalyticsAction {

   @Attribute("paycardtype") String paycardType = "Unknown";
   @Attribute("paycardissuer") String paycardIssuer = "Unknown";
   @Attribute("cardtype") final String cardtype = "Payment";

   public void fillPaycardInfo(BankCard bankCard) {
      if (bankCard.issuerInfo().financialService() == null) return;
      switch (bankCard.issuerInfo().financialService()) {
         case AMEX:
            paycardIssuer = "American Express";
            paycardType = "American Express";
            break;
         case VISA:
            paycardIssuer = bankCard.issuerInfo().bankName();
            paycardType = "Visa";
            break;
         case MASTERCARD:
            paycardIssuer = bankCard.issuerInfo().bankName();
            paycardType = "MasterCard";
            break;
         case DISCOVER:
            paycardIssuer = "Discover";
            paycardType = "Discover";
            break;
      }
   }
}
