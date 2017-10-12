package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.GetTransactionResponse;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.GetUrlTokenResponse;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ImmutableGetTransactionResponse;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ImmutableGetUrlTokenResponse;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ThrstInfo;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.TransactionDetails;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class TransactionResponseConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.GetTransactionResponseSdk, GetTransactionResponse> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.GetTransactionResponseSdk> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.GetTransactionResponseSdk.class;
   }

   @Override
   public Class<GetTransactionResponse> targetClass() {
      return GetTransactionResponse.class;
   }

   @Override
   public GetTransactionResponse convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.GetTransactionResponseSdk pilot) {
      return ImmutableGetTransactionResponse.builder()
            .transactionId(pilot.transactionId())
            .merchantId(pilot.merchantId())
            .userId(pilot.userId())
            .transactionType(pilot.transactionType())
            .checkinTimestamp(pilot.checkinTimestamp())
            .billImagePath(pilot.billImagePath())
            .pointsAmount(pilot.pointsAmount())
            .billTotal(pilot.billTotal())
            .transactionStatus(pilot.transactionStatus())
            .build();
   }
}
