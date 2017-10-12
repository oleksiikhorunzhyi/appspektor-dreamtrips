package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ImmutableThrstInfo;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ThrstInfo;
import com.worldventures.core.converter.Converter;

import io.techery.mappery.MapperyContext;

public class TransactionTokenConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ThrstInfo, ThrstInfo> {

    @Override
    public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.ThrstInfo> sourceClass() {
        return com.worldventures.dreamtrips.api.dtl.merchants.model.ThrstInfo.class;
    }

    @Override
    public Class<ThrstInfo> targetClass() {
        return ThrstInfo.class;
    }

    @Override
    public ThrstInfo convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.ThrstInfo errors) {
        return ImmutableThrstInfo.builder()
                .token(errors.token())
                .redirectUrl(errors.redirectUrl())
                .build();
    }
}
