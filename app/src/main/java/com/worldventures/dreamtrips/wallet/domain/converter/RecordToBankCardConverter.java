package com.worldventures.dreamtrips.wallet.domain.converter;

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

public class RecordToBankCardConverter implements com.worldventures.dreamtrips.modules.mapping.converter.Converter<Record, BankCard> {

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

      ImmutableRecordIssuerInfo.Builder recordIssuerInfoBuilder = ImmutableRecordIssuerInfo.builder();
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
      recordIssuerInfoBuilder.financialService(record.financialService());
      int cvv = 0;
      if (record.cvv().length() > 0) {
         cvv = Integer.parseInt(record.cvv());
      }
      return ImmutableBankCard.builder()
            .id(String.valueOf(record.id()))
            .number(Long.parseLong(record.cardNumber()))
            .expDate(record.expDate())
            .cvv(cvv)
            .track1(record.t1())
            .track2(record.t2())
            .cardNameHolder("") //// TODO: 11/28/16 use Record.cardNameHolder after sdk will updated !!!
            .nickName(record.title())
            .issuerInfo(recordIssuerInfoBuilder.build())
            .addressInfo(ImmutableAddressInfo.builder()
                  .address1(record.streetName())
                  .address2(record.country())
                  .city(record.city())
                  .state(record.state())
                  .zip(record.zipCode())
                  .build())
            .category(category)
            .build();
   }
}
