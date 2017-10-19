package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.FlaggingReviewParams;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableFlaggingReviewParams;

import io.techery.mappery.MapperyContext;

public class FlaggingConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.SdkFlaggingResponse, FlaggingReviewParams> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.SdkFlaggingResponse> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.SdkFlaggingResponse.class;
   }

   @Override
   public Class<FlaggingReviewParams> targetClass() {
      return FlaggingReviewParams.class;
   }

   @Override
   public FlaggingReviewParams convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.SdkFlaggingResponse review) {
      return ImmutableFlaggingReviewParams.builder()
            .message(review.message())
            .build();
   }
}
