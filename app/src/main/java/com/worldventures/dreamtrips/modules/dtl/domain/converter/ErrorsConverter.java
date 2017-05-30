package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Errors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableErrors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.InnerErrors;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class ErrorsConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Errors, Errors> {

    @Override
    public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.Errors> sourceClass() {
        return com.worldventures.dreamtrips.api.dtl.merchants.model.Errors.class;
    }

    @Override
    public Class<Errors> targetClass() {
        return Errors.class;
    }

    @Override
    public Errors convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.Errors errors) {
        return ImmutableErrors.builder()
                .message(errors.message())
                .code(errors.code())
                .innerError(errors.innerError() != null ? mapperyContext.convert(errors.innerError(), InnerErrors.class) : null)
                .build();
    }
}
