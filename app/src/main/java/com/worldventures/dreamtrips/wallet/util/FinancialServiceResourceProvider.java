package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;

import io.techery.janet.smartcard.model.Record;

public class FinancialServiceResourceProvider {

   @StringRes
   public int obtainFinancialServiceType(Record.FinancialService financialService) {
      switch (financialService) {
         case VISA:
            return R.string.wallet_card_financial_service_visa;
         case MASTERCARD:
            return R.string.wallet_card_financial_service_master_card;
         case DISCOVER:
            return R.string.wallet_card_financial_service_discover;
         case AMEX:
            return R.string.wallet_card_financial_service_amex;
         default:
            throw new IllegalStateException("Incorrect Financial Service");
      }
   }

}
