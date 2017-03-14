package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
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
      //todo: no use getOrDefault, for support < Java 8
      final String bankName = metadata.get(BANK_NAME_FIELD);

      final Integer recordId = record.id();
      return ImmutableRecord.builder()
            .id(recordId != null ? String.valueOf(recordId) : null)
            .number(record.cardNumber())
            .expDate(record.expDate())
            .cvv(record.cvv())
            .track1(record.t1())
            .track2(record.t2())
            // TODO: 11/28/16 use Record.cardNameHolder after sdk will updated !!!
            .cardNameHolder(String.format("%s %s %s", record.firstName(), record.middleName(), record.lastName()))
            .nickName(record.title())
            .bankName(bankName == null ? "" : bankName)
            .financialService(mapperyContext.convert(record.financialService(), FinancialService.class))
            .recordType(mapperyContext.convert(record.cardType(), RecordType.class))
            .addressInfo(fetchAddressInfoFromRecord(record))
            .cardHolderFirstName(record.firstName())
            .cardHolderLastName(record.lastName())
            .cardHolderMiddleName(record.middleName())
            .build();
   }

   private AddressInfo fetchAddressInfoFromRecord(io.techery.janet.smartcard.model.Record record) {
      ImmutableAddressInfo.Builder addressInfoBuilder = ImmutableAddressInfo.builder();
      if (record.streetName() != null) {
         addressInfoBuilder.address1(record.streetName());
      }
      if (record.country() != null) {
         addressInfoBuilder.address2(record.country());
      }
      if (record.city() != null) {
         addressInfoBuilder.city(record.city());
      }
      if (record.state() != null) {
         addressInfoBuilder.state(record.state());
      }
      if (record.zipCode() != null) {
         addressInfoBuilder.zip(record.zipCode());
      }
      return addressInfoBuilder.build();
   }
}
