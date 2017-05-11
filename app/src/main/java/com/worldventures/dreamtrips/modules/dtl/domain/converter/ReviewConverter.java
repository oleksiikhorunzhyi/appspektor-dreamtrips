package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Errors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReview;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Review;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewImages;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.UserImage;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class ReviewConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Review, Review> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.Review> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.Review.class;
   }

   @Override
   public Class<Review> targetClass() {
      return Review.class;
   }

   @Override
   public Review convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.Review review) {
      return ImmutableReview.builder()
            .lastModeratedTimeUtc(review.lastModeratedTimeUtc())
            .reviewId(review.reviewId())
            .brand(review.brand())
            .userNickName(review.userNickName())
            .userImage(review.userImage() != null ? mapperyContext.convert(review.userImage(), UserImage.class) : null)
            .reviewText(review.reviewText())
            .rating(review.rating())
            .verified(review.verified())
            .errors(review.errors() != null ? mapperyContext.convert(review.errors(), Errors.class) : null)
            .reviewImagesList(review.reviewImagesList() != null ? mapperyContext.convert(review.reviewImagesList(), ReviewImages.class) : null)
            .build();
   }
}
