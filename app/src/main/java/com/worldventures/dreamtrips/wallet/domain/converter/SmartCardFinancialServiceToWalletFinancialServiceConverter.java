package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.record.FinancialService;

import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;

public class SmartCardFinancialServiceToWalletFinancialServiceConverter implements Converter<Record.FinancialService, FinancialService> {

   @Override
   public Class<Record.FinancialService> sourceClass() {
      return Record.FinancialService.class;
   }

   @Override
   public Class<FinancialService> targetClass() {
      return FinancialService.class;
   }

   @Override
   public FinancialService convert(MapperyContext mapperyContext, Record.FinancialService recordFinancialService) {
      FinancialService financialService = null;
      switch (recordFinancialService) {
         case VISA:
            financialService = FinancialService.VISA;
            break;
         case MASTERCARD:
            financialService = FinancialService.MASTERCARD;
            break;
         case DISCOVER:
            financialService = FinancialService.DISCOVER;
            break;
         case AMEX:
            financialService = FinancialService.AMEX;
            break;
         case GENERIC:
            financialService = FinancialService.GENERIC;
            break;
      }
      return financialService;
   }
}
