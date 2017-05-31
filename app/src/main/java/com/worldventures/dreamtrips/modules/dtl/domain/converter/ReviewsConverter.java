package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReviews;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewSettings;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Reviews;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import io.techery.mappery.MapperyContext;

public class ReviewsConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Reviews, Reviews> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.Reviews> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.Reviews.class;
   }

   @Override
   public Class<Reviews> targetClass() {
      return Reviews.class;
   }

   @Override
   public Reviews convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.Reviews reviews) {
      return ImmutableReviews.builder()
            .total(reviews.total())
            .userHasPendingReview(reviews.userHasPendingReview())
            .ratingAverage(reviews.ratingAverage())
            .reviews(reviews.reviews() != null ? mapperyContext.convert(reviews.reviews(), Review.class) : null)
            .reviewSettings(reviews.reviewSettings() != null ? mapperyContext.convert(reviews.reviewSettings(), ReviewSettings.class) : null)
            .build();
   }
}
