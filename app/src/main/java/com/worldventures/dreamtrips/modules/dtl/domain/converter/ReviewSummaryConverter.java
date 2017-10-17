package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReviewSummary;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewSummary;

import io.techery.mappery.MapperyContext;

public class ReviewSummaryConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewSummary, ReviewSummary> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewSummary> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewSummary.class;
   }

   @Override
   public Class<ReviewSummary> targetClass() {
      return ReviewSummary.class;
   }

   @Override
   public ReviewSummary convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewSummary reviewSummary) {
      return ImmutableReviewSummary.builder()
            .total(reviewSummary.total())
            .ratingAverage(reviewSummary.ratingAverage())
            .userHasPendingReview(reviewSummary.userHasPendingReview())
            .build();
   }
}
