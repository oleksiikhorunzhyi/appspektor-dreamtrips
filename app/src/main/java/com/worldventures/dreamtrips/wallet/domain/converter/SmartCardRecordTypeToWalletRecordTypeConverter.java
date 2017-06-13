package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.record.RecordType;

import io.techery.mappery.MapperyContext;

public class SmartCardRecordTypeToWalletRecordTypeConverter implements Converter<io.techery.janet.smartcard.model.Record.CardType, RecordType> {

   @Override
   public Class<io.techery.janet.smartcard.model.Record.CardType> sourceClass() {
      return io.techery.janet.smartcard.model.Record.CardType.class;
   }

   @Override
   public Class<RecordType> targetClass() {
      return RecordType.class;
   }

   @Override
   public RecordType convert(MapperyContext mapperyContext, io.techery.janet.smartcard.model.Record.CardType cardType) {
      switch (cardType) {
         case CREDIT:
            return RecordType.CREDIT;
         case DEBIT:
            return RecordType.DEBIT;
         case PREFERENCE:
            return RecordType.PREFERENCE;
         case FINANCIAL:
         default:
            return RecordType.FINANCIAL;
      }
   }
}
