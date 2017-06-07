package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.FieldErrors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.FormErrors;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableFormErrors;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class FormErrorsConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.FormErrors, FormErrors> {

    @Override
    public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.FormErrors> sourceClass() {
        return com.worldventures.dreamtrips.api.dtl.merchants.model.FormErrors.class;
    }

    @Override
    public Class<FormErrors> targetClass() {
        return FormErrors.class;
    }

    @Override
    public FormErrors convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.FormErrors errors) {
        return ImmutableFormErrors.builder()
                .fieldErrors(errors.fieldErrors() != null ? mapperyContext.convert(errors.fieldErrors(), FieldErrors.class) : null)
                .build();
    }
}
