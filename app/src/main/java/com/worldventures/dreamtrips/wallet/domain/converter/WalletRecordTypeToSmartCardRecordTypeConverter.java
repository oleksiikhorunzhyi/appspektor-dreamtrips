package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.record.RecordType;

import io.techery.mappery.MapperyContext;

public class WalletRecordTypeToSmartCardRecordTypeConverter implements Converter<RecordType, io.techery.janet.smartcard.model.Record.CardType> {

   @Override
   public Class<RecordType> sourceClass() {
      return RecordType.class;
   }

   @Override
   public Class<io.techery.janet.smartcard.model.Record.CardType> targetClass() {
      return io.techery.janet.smartcard.model.Record.CardType.class;
   }

   @Override
   public io.techery.janet.smartcard.model.Record.CardType convert(MapperyContext mapperyContext, RecordType cardType) {
      switch (cardType) {
         case CREDIT:
            return io.techery.janet.smartcard.model.Record.CardType.CREDIT;
         case DEBIT:
            return io.techery.janet.smartcard.model.Record.CardType.DEBIT;
         case PREFERENCE:
            return io.techery.janet.smartcard.model.Record.CardType.PREFERENCE;
         default:
            return io.techery.janet.smartcard.model.Record.CardType.FINANCIAL;
      }
   }
}
