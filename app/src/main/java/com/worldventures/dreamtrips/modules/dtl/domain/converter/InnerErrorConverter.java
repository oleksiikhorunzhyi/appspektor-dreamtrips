package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.FormErrors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableInnerErrors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.InnerErrors;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class InnerErrorConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.InnerError, InnerErrors> {

    @Override
    public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.InnerError> sourceClass() {
        return com.worldventures.dreamtrips.api.dtl.merchants.model.InnerError.class;
    }

    @Override
    public Class<InnerErrors> targetClass() {
        return InnerErrors.class;
    }

    @Override
    public InnerErrors convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.InnerError errors) {
        return ImmutableInnerErrors.builder()
                .formErrors(errors.formErrors() != null ? mapperyContext.convert(errors.formErrors(), FormErrors.class) : null)
                .build();
    }
}
