package com.worldventures.dreamtrips.wallet.domain.converter;


import android.text.TextUtils;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;

import java.util.Map;

import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.ADDRESS1_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.ADDRESS2_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.BANK_CARD_CATEGORY;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.BANK_NAME_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.CITY_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.STATE_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.TYPE_CARD_FIELD;
import static com.worldventures.dreamtrips.wallet.domain.converter.Converter.ZIP_FIELD;

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
      Map<String, String> metadata = record.metadata();
      AddressInfo addressInfo = ImmutableAddressInfo.builder().build();
      if (record.metadata() != null && !TextUtils.isEmpty(record.metadata().get(CITY_FIELD))) {
         addressInfo = ImmutableAddressInfo.builder()
               .address1(metadata.get(ADDRESS1_FIELD))
               .address2(metadata.get(ADDRESS2_FIELD))
               .city(metadata.get(CITY_FIELD))
               .state(metadata.get(STATE_FIELD))
               .zip(metadata.get(ZIP_FIELD))
               .build();
      }

      return ImmutableBankCard.builder()
            .id(String.valueOf(record.id()))
            .title(record.title())
            .cvv(Integer.parseInt(record.cvv()))
            .number(Long.parseLong(record.cardNumber()))
            .issuerInfo(ImmutableRecordIssuerInfo.builder()
                  .bankName(metadata.get(BANK_NAME_FIELD))
                  .cardType(BankCard.CardType.valueOf(metadata.get(TYPE_CARD_FIELD)))
                  .financialService(record.financialService())
                  .build())
            .expiryMonth(record.expiryMonth())
            .expiryYear(record.expiryYear())
            .addressInfo(addressInfo)
            .category(Card.Category.valueOf(metadata.get(BANK_CARD_CATEGORY)))
            .build();
   }
}
