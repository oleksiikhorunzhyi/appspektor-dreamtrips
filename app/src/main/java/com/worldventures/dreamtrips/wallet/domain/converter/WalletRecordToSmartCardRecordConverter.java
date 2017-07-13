package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

import java.util.HashMap;

import io.techery.janet.smartcard.model.ImmutableRecord;
import io.techery.mappery.MapperyContext;

public class WalletRecordToSmartCardRecordConverter implements Converter<Record, io.techery.janet.smartcard.model.Record> {

   public static final String BANK_NAME_FIELD = "bank_name";

   @Override
   public Class<Record> sourceClass() {
      return Record.class;
   }

   @Override
   public Class<io.techery.janet.smartcard.model.Record> targetClass() {
      return io.techery.janet.smartcard.model.Record.class;
   }

   @Override
   public io.techery.janet.smartcard.model.Record convert(MapperyContext mapperyContext, Record card) {
      HashMap<String, String> metadata = new HashMap<>(5);
      metadata.put(BANK_NAME_FIELD, card.bankName());

      ImmutableRecord.Builder recordBuilder = ImmutableRecord.builder()
            .id(parseCardId(card))
            .title(card.nickName())
            .cardNumber(card.number())
            .cvv(card.cvv())
            .expDate(card.expDate())
            .financialService(mapperyContext.convert(card.financialService(),
                  io.techery.janet.smartcard.model.Record.FinancialService.class))
            .t1(ProjectTextUtils.defaultIfEmpty(card.track1(), ""))
            .t2(ProjectTextUtils.defaultIfEmpty(card.track2(), ""))
            .t3(ProjectTextUtils.defaultIfEmpty(card.track3(), ""))
            .lastName(card.cardHolderLastName())
            .firstName(card.cardHolderFirstName())
            .middleName(card.cardHolderMiddleName())
            .cardType(mapperyContext.convert(card.recordType(), io.techery.janet.smartcard.model.Record.CardType.class))
            .version(card.version())
            .metadata(metadata);

      return recordBuilder.build();
   }

   private Integer parseCardId(Record card) {
      // RecordId is null until Record was not added on sc
      return card.id() != null ? Integer.parseInt(card.id()) : null;
   }
}
