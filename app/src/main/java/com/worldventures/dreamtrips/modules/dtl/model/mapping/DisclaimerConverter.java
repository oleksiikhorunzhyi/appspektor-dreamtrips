package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.Disclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.ImmutableDisclaimer;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class DisclaimerConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Disclaimer, Disclaimer> {

   @Override
   public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.Disclaimer> sourceClass() {
      return com.worldventures.dreamtrips.api.dtl.merchants.model.Disclaimer.class;
   }

   @Override
   public Class<Disclaimer> targetClass() {
      return Disclaimer.class;
   }

   @Override
   public Disclaimer convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.Disclaimer disclaimer) {
      return ImmutableDisclaimer.builder()
            .type(disclaimer.type())
            .text(disclaimer.text())
            .build();
   }
}
