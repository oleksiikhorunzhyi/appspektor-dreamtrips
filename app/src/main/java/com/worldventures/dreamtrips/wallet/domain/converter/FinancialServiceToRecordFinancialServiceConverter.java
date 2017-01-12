package com.worldventures.dreamtrips.wallet.domain.converter;


import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.FinancialService;

import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;

public class FinancialServiceToRecordFinancialServiceConverter implements Converter<FinancialService, Record.FinancialService> {

   @Override
   public Class<FinancialService> sourceClass() {
      return FinancialService.class;
   }

   @Override
   public Class<Record.FinancialService> targetClass() {
      return Record.FinancialService.class;
   }

   @Override
   public Record.FinancialService convert(MapperyContext mapperyContext, FinancialService financialService) {
      Record.FinancialService recordFinancialService = null;
      switch (financialService) {
         case VISA:
            recordFinancialService = Record.FinancialService.VISA;
            break;
         case MASTERCARD:
            recordFinancialService = Record.FinancialService.MASTERCARD;
            break;
         case DISCOVER:
            recordFinancialService = Record.FinancialService.DISCOVER;
            break;
         case AMEX:
            recordFinancialService = Record.FinancialService.AMEX;
            break;
         case SAMPLE:
            recordFinancialService = Record.FinancialService.SAMPLE;
            break;
      }
      return recordFinancialService;
   }
}
