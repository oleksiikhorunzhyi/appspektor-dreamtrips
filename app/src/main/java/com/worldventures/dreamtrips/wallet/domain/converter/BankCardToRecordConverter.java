package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import java.util.HashMap;

import io.techery.janet.smartcard.model.ImmutableRecord;
import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.wallet.domain.converter.RecordFields.BANK_CARD_CATEGORY;
import static com.worldventures.dreamtrips.wallet.domain.converter.RecordFields.BANK_NAME_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.RecordFields.TYPE_CARD_FIELD;

public class BankCardToRecordConverter implements com.worldventures.dreamtrips.modules.mapping.converter.Converter<BankCard, Record> {

   @Override
   public Class<BankCard> sourceClass() {
      return BankCard.class;
   }

   @Override
   public Class<Record> targetClass() {
      return Record.class;
   }

   @Override
   public Record convert(MapperyContext mapperyContext, BankCard card) {
      AddressInfo addressInfo = card.addressInfo();
      HashMap<String, String> metadata = new HashMap<>(5);
      metadata.put(TYPE_CARD_FIELD, card.issuerInfo().cardType().name());
      metadata.put(BANK_NAME_FIELD, card.issuerInfo().bankName());
      metadata.put(BANK_CARD_CATEGORY, card.category().name());
      //// TODO: 11/28/16 add cardNameHolder into Record!!!
      ImmutableRecord.Builder recordBuilder = ImmutableRecord.builder()
            .id(parseCardId(card))
            .title(card.nickName())
            .cardNumber(String.valueOf(card.number()))
            .cvv(String.valueOf(card.cvv()))
            .expDate(card.expDate())
            .financialService(card.issuerInfo().financialService())
            .t1(card.track1() != null ? card.track1() : "")
            .t2(card.track2() != null ? card.track2() : "")
            .t3("")
            .lastName(card.cardHolderLastName())
            .firstName(card.cardHolderFirstName())
            .middleName(card.cardHolderMiddleName())
            .metadata(metadata);

      if (addressInfo != null) {
         recordBuilder
               .city(addressInfo.city())
               .state(addressInfo.state())
               .streetName(addressInfo.address1())
               .country(addressInfo.address2())
               .zipCode(addressInfo.zip());
      }
      return recordBuilder.build();
   }

   private Integer parseCardId(BankCard card) {
      // RecordId is null until Record was not added on sc
      return card.id() != null ? Integer.parseInt(card.id()) : null;
   }
}
