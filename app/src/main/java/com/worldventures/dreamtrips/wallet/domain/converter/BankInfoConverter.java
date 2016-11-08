package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.bank_info.model.BankInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import io.techery.janet.smartcard.model.Record;

public class BankInfoConverter implements Converter<BankInfo, RecordIssuerInfo> {

   @Override
   public RecordIssuerInfo from(BankInfo in) {
      String cardType = in.cardType() == null ? null : in.cardType().toUpperCase();
      String brand = in.brand() == null ? null : in.brand().toUpperCase();
      return ImmutableRecordIssuerInfo.builder()
            .bankName(in.bank())
            .cardType(BankCard.CardType.valueOf(cardType))
            .financialService(Record.FinancialService.valueOf(brand))
            .build();
   }

   @Override
   public BankInfo to(RecordIssuerInfo in) {
      throw new UnsupportedOperationException("Not supported. Implement it if you CAN");
   }
}
