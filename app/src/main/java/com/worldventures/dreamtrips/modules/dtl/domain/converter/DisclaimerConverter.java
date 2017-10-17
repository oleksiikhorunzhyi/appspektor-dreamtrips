package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.Disclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.ImmutableDisclaimer;

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
