package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableNxtSession;
import com.worldventures.dreamtrips.wallet.domain.entity.NxtSession;

import io.techery.mappery.MapperyContext;

class NxtSessionConverter implements Converter<com.worldventures.dreamtrips.api.smart_card.nxt.model.NxtSession, NxtSession> {

   @Override
   public Class<com.worldventures.dreamtrips.api.smart_card.nxt.model.NxtSession> sourceClass() {
      return com.worldventures.dreamtrips.api.smart_card.nxt.model.NxtSession.class;
   }

   @Override
   public Class<NxtSession> targetClass() {
      return NxtSession.class;
   }

   @Override
   public NxtSession convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.smart_card.nxt.model.NxtSession nxtSession) {
      return ImmutableNxtSession.builder()
            .token(nxtSession.token())
            .build();
   }
}
