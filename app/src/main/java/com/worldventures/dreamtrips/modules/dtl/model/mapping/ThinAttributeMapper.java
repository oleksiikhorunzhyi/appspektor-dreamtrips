package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableThinAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinAttribute;

public class ThinAttributeMapper implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.ThinAttribute, ThinAttribute> {

   public static final ThinAttributeMapper INSTANCE = new ThinAttributeMapper();

   @Override
   public ThinAttribute convert(com.worldventures.dreamtrips.api.dtl.merchants.model.ThinAttribute attribute) {
      return ImmutableThinAttribute.builder().name(attribute.name()).build();
   }
}
