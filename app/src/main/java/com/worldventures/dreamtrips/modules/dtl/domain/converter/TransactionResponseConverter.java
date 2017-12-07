package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.dtl.merchants.model.GetTransactionResponseSdk;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.GetTransactionResponse;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ImmutableGetTransactionResponse;

import io.techery.mappery.MapperyContext;

public class TransactionResponseConverter implements Converter<GetTransactionResponseSdk, GetTransactionResponse> {

   @Override
   public Class<GetTransactionResponseSdk> sourceClass() {
      return GetTransactionResponseSdk.class;
   }

   @Override
   public Class<GetTransactionResponse> targetClass() {
      return GetTransactionResponse.class;
   }

   @Override
   public GetTransactionResponse convert(MapperyContext mapperyContext, GetTransactionResponseSdk pilot) {
      return ImmutableGetTransactionResponse.builder()
            .transactionId(pilot.transactionId())
            .merchantId(pilot.merchantId())
            .userId(pilot.userId())
            .transactionType(pilot.transactionType())
            .checkinTimestamp(pilot.checkinTimestamp())
            .billImagePath(pilot.billImagePath())
            .pointsAmount(pilot.pointsAmount())
            .totalPoints(pilot.totalPoints())
            .billTotal(pilot.billTotal())
            .transactionStatus(pilot.transactionStatus())
            .subTotal(pilot.subTotal())
            .tax(pilot.tax())
            .tip(pilot.tip())
            .currencyCode(pilot.currencyCode())
            .currencySymbol(pilot.currencySymbol())
            .build();
   }
}
