package com.worldventures.dreamtrips.modules.dtl.model.mapping;


import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class MerchantMediaConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantMedia, MerchantMedia> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantMedia> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantMedia.class;
   }

   @Override
   public Class<MerchantMedia> targetClass() {
      return MerchantMedia.class;
   }

   @Override
   public MerchantMedia convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantMedia merchantMedia) {
      return ImmutableMerchantMedia.builder()
            .imagePath(merchantMedia.url())
            .category(merchantMedia.category())
            .width(merchantMedia.width())
            .height(merchantMedia.height())
            .build();
   }
}
