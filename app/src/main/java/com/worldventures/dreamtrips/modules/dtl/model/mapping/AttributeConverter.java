package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableAttribute;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class AttributeConverter implements Converter<com.worldventures.dreamtrips.api.dtl.attributes.model.Attribute, Attribute> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.attributes.model.Attribute> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.attributes.model.Attribute.class;
   }

   @Override
   public Class<Attribute> targetClass() {
      return Attribute.class;
   }

   @Override
   public Attribute convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.attributes.model.Attribute attribute) {
      return ImmutableAttribute.builder()
            .id(attribute.id())
            .type(attribute.type())
            .name(attribute.displayName())
            .displayName(attribute.displayName())
            .merchantCount(attribute.merchantCount())
            .partnerCount(attribute.partnerCount())
            .build();
   }
}
