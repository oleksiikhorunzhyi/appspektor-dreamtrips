package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinAttribute;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class ThinAttributeConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ThinAttribute, ThinAttribute> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.ThinAttribute> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.ThinAttribute.class;
   }

   @Override
   public Class<ThinAttribute> targetClass() {
      return ThinAttribute.class;
   }

   @Override
   public ThinAttribute convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.ThinAttribute thinAttribute) {
      return ImmutableThinAttribute.builder()
            .name(thinAttribute.name())
            .build();
   }
}
