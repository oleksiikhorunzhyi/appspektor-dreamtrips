package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.AttributeMap;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseCardDetailsAction extends WalletAnalyticsAction {

   @AttributeMap final Map<String, String> attributeMap = new HashMap<>();

   public void fillRecordDetails(Record record) {
      if (record.financialService() == null) return;

      final String cardType = "Payment";
      String paymentCardType, paymentCardIssuer;

      switch (record.financialService()) {
         case AMEX:
            paymentCardIssuer = "American Express";
            paymentCardType = "American Express";
            break;
         case VISA:
            paymentCardIssuer = record.bankName();
            paymentCardType = "Visa";
            break;
         case MASTERCARD:
            paymentCardIssuer = record.bankName();
            paymentCardType = "MasterCard";
            break;
         case DISCOVER:
            paymentCardIssuer = "Discover";
            paymentCardType = "Discover";
            break;
         default:
            paymentCardIssuer = "Unknown";
            paymentCardType = "Unknown";
            break;
      }

      attributeMap.put("paycardtype", paymentCardType);
      attributeMap.put("paycardissuer", paymentCardIssuer);
      attributeMap.put("cardtype", cardType);
   }
}
