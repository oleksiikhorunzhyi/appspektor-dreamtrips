package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.FieldErrors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableFieldErrors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewText;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class FieldErrorsConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.FieldErrors, FieldErrors> {

    @Override
    public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.FieldErrors> sourceClass() {
        return com.worldventures.dreamtrips.api.dtl.merchants.model.FieldErrors.class;
    }

    @Override
    public Class<FieldErrors> targetClass() {
        return FieldErrors.class;
    }

    @Override
    public FieldErrors convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.FieldErrors errors) {
        return ImmutableFieldErrors.builder()
                .reviewText(errors.reviewText() != null ? mapperyContext.convert(errors.reviewText(), ReviewText.class) : null)
                .build();
    }
}
