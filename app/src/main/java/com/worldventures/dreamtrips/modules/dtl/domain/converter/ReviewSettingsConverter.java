package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableReviewSettings;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewSettings;
import io.techery.mappery.MapperyContext;

public class ReviewSettingsConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewSettings, ReviewSettings> {

    @Override
    public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewSettings> sourceClass() {
        return com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewSettings.class;
    }

    @Override
    public Class<ReviewSettings> targetClass() {
        return ReviewSettings.class;
    }

    @Override
    public ReviewSettings convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.ReviewSettings reviewSettings) {
        return ImmutableReviewSettings.builder()
                .maximumCharactersAllowed(reviewSettings.maximumCharactersAllowed())
                .minimumCharactersAllowed(reviewSettings.minimumCharactersAllowed())
                .build();
    }
}
