package com.worldventures.dreamtrips.wallet.domain.converter;


import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import java.util.HashMap;

import io.techery.janet.smartcard.model.ImmutableRecord;
import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;

public class BankCardToRecordConverter implements com.worldventures.dreamtrips.modules.mapping.converter.Converter<BankCard, Record> {
   private final static String ADDRESS1_FIELD = "address1";
   private final static String ADDRESS2_FIELD = "address2";
   private final static String CITY_FIELD = "city";
   private final static String STATE_FIELD = "state";
   private final static String ZIP_FIELD = "zip";

   private final static String TYPE_CARD_FIELD = "type_card";
   private final static String BANK_NAME_FIELD = "bank_name";

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
      metadata.put(ADDRESS1_FIELD, addressInfo.address1());
      metadata.put(ADDRESS2_FIELD, addressInfo.address2());
      metadata.put(CITY_FIELD, addressInfo.city());
      metadata.put(STATE_FIELD, addressInfo.state());
      metadata.put(ZIP_FIELD, addressInfo.zip());

      metadata.put(TYPE_CARD_FIELD, card.issuerInfo().cardType().name());
      metadata.put(BANK_NAME_FIELD, card.issuerInfo().bankName());

      // TODO: use normal id
      return ImmutableRecord.builder()
            .id(Integer.parseInt(card.id()))
            .title(card.title())
            .cardNumber(String.valueOf(card.number()))
            .cvv(String.valueOf(card.cvv()))
            .expiryMonth(card.expiryMonth())
            .expiryYear(card.expiryYear())
            .financialService(card.issuerInfo().financialService())
            .t1("")
            .t2("")
            .t3("")
            .metadata(metadata)
            .build();   }
}
