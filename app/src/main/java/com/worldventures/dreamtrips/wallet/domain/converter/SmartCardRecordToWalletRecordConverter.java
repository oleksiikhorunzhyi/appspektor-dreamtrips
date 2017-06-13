package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.record.FinancialService;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.domain.entity.record.RecordType;

import java.util.Map;

import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.wallet.domain.converter.RecordFields.BANK_NAME_FIELD;

public class SmartCardRecordToWalletRecordConverter implements Converter<io.techery.janet.smartcard.model.Record, Record> {

   @Override
   public Class<io.techery.janet.smartcard.model.Record> sourceClass() {
      return io.techery.janet.smartcard.model.Record.class;
   }

   @Override
   public Class<Record> targetClass() {
      return Record.class;
   }

   @Override
   public Record convert(MapperyContext mapperyContext, io.techery.janet.smartcard.model.Record record) {
      final Map<String, String> metadata = record.metadata();
      //no use getOrDefault, for support < Java 8
      final String bankName = metadata.get(BANK_NAME_FIELD);

      final Integer recordId = record.id();
      return ImmutableRecord.builder()
            .id(recordId != null ? String.valueOf(recordId) : null)
            .number(record.cardNumber())
            .expDate(record.expDate())
            .cvv(record.cvv())
            .track1(record.t1())
            .track2(record.t2())
            .nickName(record.title())
            .bankName(bankName == null ? "" : bankName)
            .financialService(mapperyContext.convert(record.financialService(), FinancialService.class))
            .recordType(mapperyContext.convert(record.cardType(), RecordType.class))
            .cardHolderFirstName(record.firstName())
            .cardHolderLastName(record.lastName())
            .cardHolderMiddleName(record.middleName())
            .version(record.version())
            .build();
   }
}
