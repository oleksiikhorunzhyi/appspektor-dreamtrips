package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.GetUrlTokenResponse;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ImmutableGetUrlTokenResponse;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ThrstInfo;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class UrlTokenConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.GetUrlTokenResponseSdk, GetUrlTokenResponse> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.GetUrlTokenResponseSdk> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.GetUrlTokenResponseSdk.class;
   }

   @Override
   public Class<GetUrlTokenResponse> targetClass() {
      return GetUrlTokenResponse.class;
   }

   @Override
   public GetUrlTokenResponse convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.GetUrlTokenResponseSdk review) {
      return ImmutableGetUrlTokenResponse.builder()
                     .thrstInfo(review.thrstInfo() != null ? mapperyContext.convert(review.thrstInfo(), ThrstInfo.class) : null)
                     //.transaction(review.transaction() != null ? mapperyContext.convert(review.transaction(), Transaction.class) : null)
            .build();
   }
}
