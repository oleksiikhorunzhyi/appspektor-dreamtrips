package com.worldventures.dreamtrips.modules.dtl.model.mapping;


import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;

public class MerchantMediaMapper implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantMedia, MerchantMedia> {

   public static final MerchantMediaMapper INSTANCE = new MerchantMediaMapper();

   @Override
   public MerchantMedia convert(com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantMedia source) {
      return ImmutableMerchantMedia.builder()
            .imagePath(source.url())
            .category(source.category())
            .width(source.width())
            .height(source.height())
            .build();
   }
}
