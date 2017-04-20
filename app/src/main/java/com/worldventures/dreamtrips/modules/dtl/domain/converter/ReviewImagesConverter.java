package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReviewImages;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewImages;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class ReviewImagesConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewImages, ReviewImages> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewImages> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewImages.class;
   }

   @Override
   public Class<ReviewImages> targetClass() {
      return ReviewImages.class;
   }

   @Override
   public ReviewImages convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewImages reviewImages) {
      return ImmutableReviewImages.builder()
            .normalUrl(reviewImages.normalUrl())
            .thumbnailUrl(reviewImages.thumbnailUrl())
          .build();
   }
}
