package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.CommentReview;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Errors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.FlaggingReviewParams;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableCommentReview;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableFlaggingReviewParams;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewImages;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.UserImage;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class CommentReviewConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.CommentReview, CommentReview> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.CommentReview> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.CommentReview.class;
   }

   @Override
   public Class<CommentReview> targetClass() {
      return CommentReview.class;
   }

   @Override
   public CommentReview convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.CommentReview review) {
      return ImmutableCommentReview.builder()
            .reviewId(review.reviewId())
            .brand(review.brand())
            .userNickName(review.userNickName())
            .userImage(review.userImage())
            .reviewText(review.reviewText())
            .rating(review.rating())
            .verified(review.verified())
            .errors(review.errors() != null ? mapperyContext.convert(review.errors(), Errors.class) : null)
            .build();
   }
}
