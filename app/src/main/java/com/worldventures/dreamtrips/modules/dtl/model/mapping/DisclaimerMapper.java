package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.innahema.collections.query.functions.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.Disclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.ImmutableDisclaimer;

public class DisclaimerMapper implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.Disclaimer, Disclaimer> {

   public static final DisclaimerMapper INSTANCE = new DisclaimerMapper();

   @Override
   public Disclaimer convert(com.worldventures.dreamtrips.api.dtl.merchants.model.Disclaimer disclaimer) {
      return ImmutableDisclaimer.builder()
            .type(disclaimer.type())
            .text(disclaimer.text())
            .build();
   }
}
