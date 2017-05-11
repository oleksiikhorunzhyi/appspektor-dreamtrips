package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReviewText;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewText;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class ReviewTextConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewText, ReviewText> {

    @Override
    public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewText> sourceClass() {
        return com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewText.class;
    }

    @Override
    public Class<ReviewText> targetClass() {
        return ReviewText.class;
    }

    @Override
    public ReviewText convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewText errors) {
        return ImmutableReviewText.builder()
                .field(errors.field())
                .message(errors.message())
                .code(errors.code())
                .build();
    }
}
