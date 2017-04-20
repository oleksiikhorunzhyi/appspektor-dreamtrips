package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.FlaggingReviewParams;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableFlaggingReviewParams;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReview;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.UserImage;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

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
