package com.worldventures.dreamtrips.wallet.domain.converter;


import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import java.util.HashMap;

import io.techery.janet.smartcard.model.ImmutableRecord;
import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.*;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.ADDRESS1_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.ADDRESS2_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.BANK_NAME_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.CITY_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.STATE_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.TYPE_CARD_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.ZIP_FIELD;

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
      if (addressInfo != null) {
         metadata.put(ADDRESS1_FIELD, addressInfo.address1());
         metadata.put(ADDRESS2_FIELD, addressInfo.address2());
         metadata.put(CITY_FIELD, addressInfo.city());
         metadata.put(STATE_FIELD, addressInfo.state());
         metadata.put(ZIP_FIELD, addressInfo.zip());
      }
      metadata.put(TYPE_CARD_FIELD, card.issuerInfo().cardType().name());
      metadata.put(BANK_NAME_FIELD, card.issuerInfo().bankName());
      metadata.put(BANK_CARD_CATEGORY, card.category().name());

      // TODO: use normal id
      return ImmutableRecord.builder()
            .id(Integer.parseInt(card.id()))
            .title(card.title())
            .cardNumber(String.valueOf(card.number()))
            .cvv(String.valueOf(card.cvv()))
            .expiryMonth(card.expiryMonth())
            .expiryYear(card.expiryYear())
            .financialService(card.issuerInfo().financialService())
            .t1(card.track1() != null ? card.track1() : "")
            .t2(card.track2() != null ? card.track2() : "")
            .t3("")
            .metadata(metadata)
            .build();
   }
}
