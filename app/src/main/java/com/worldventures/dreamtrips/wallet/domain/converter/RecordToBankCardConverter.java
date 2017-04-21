package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.FinancialService;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;

import java.util.Map;

import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.wallet.domain.converter.RecordFields.BANK_CARD_CATEGORY;
import static com.worldventures.dreamtrips.wallet.domain.converter.RecordFields.BANK_NAME_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.RecordFields.TYPE_CARD_FIELD;

public class RecordToBankCardConverter implements Converter<Record, BankCard> {

   @Override
   public Class<BankCard> targetClass() {
      return BankCard.class;
   }

   @Override
   public Class<Record> sourceClass() {
      return Record.class;
   }

   @Override
   public BankCard convert(MapperyContext mapperyContext, Record record) {
      final Map<String, String> metadata = record.metadata();

      final ImmutableRecordIssuerInfo.Builder recordIssuerInfoBuilder = ImmutableRecordIssuerInfo.builder();
      if (metadata.containsKey(BANK_NAME_FIELD)) {
         recordIssuerInfoBuilder.bankName(metadata.get(BANK_NAME_FIELD));
      }
      if (metadata.containsKey(TYPE_CARD_FIELD)) {
         recordIssuerInfoBuilder.cardType(BankCard.CardType.valueOf(metadata.get(TYPE_CARD_FIELD)));
      }
      Card.Category category = Card.Category.BANK;
      if (metadata.containsKey(BANK_CARD_CATEGORY)) {
         category = Card.Category.valueOf(metadata.get(BANK_CARD_CATEGORY));
      }
      recordIssuerInfoBuilder.financialService(mapperyContext.convert(record.financialService(), FinancialService.class));
      final Integer recordId = record.id();
      return ImmutableBankCard.builder()
            .id(recordId != null ? String.valueOf(recordId) : null)
            .number(record.cardNumber())
            .expDate(record.expDate())
            .cvv(record.cvv())
            .track1(record.t1())
            .track2(record.t2())
            .cardNameHolder(record.firstName() + " " + record.middleName() + " " + record.lastName()) //// TODO: 11/28/16 use Record.cardNameHolder after sdk will updated !!!
            .nickName(record.title())
            .issuerInfo(recordIssuerInfoBuilder.build())
            .addressInfo(fetchAddressInfoFromRecord(record))
            .category(category)
            .cardHolderFirstName(record.firstName())
            .cardHolderLastName(record.lastName())
            .cardHolderMiddleName(record.middleName())
            .build();
   }

   private AddressInfo fetchAddressInfoFromRecord(Record record) {
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
