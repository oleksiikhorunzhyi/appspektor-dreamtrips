package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.bank_info.model.BankInfo;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;

class BankInfoConverter implements Converter<BankInfo, RecordIssuerInfo> {

   @Override
   public Class<BankInfo> sourceClass() {
      return BankInfo.class;
   }

   @Override
   public Class<RecordIssuerInfo> targetClass() {
      return RecordIssuerInfo.class;
   }

   @Override
   public RecordIssuerInfo convert(MapperyContext mapperyContext, BankInfo bankInfo) {
      final String cardType = bankInfo.cardType() == null ? null : bankInfo.cardType().toUpperCase();
      final String brand = bankInfo.brand() == null ? null : bankInfo.brand().toUpperCase();
      final String bankName = bankInfo.bank() == null ? "" : bankInfo.bank();

      return ImmutableRecordIssuerInfo.builder()
            .bankName(bankName)
            .cardType(BankCard.CardType.valueOf(cardType))
            .financialService(Record.FinancialService.valueOf(brand))
            .build();
   }
}
