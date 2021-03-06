package com.worldventures.wallet.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.wallet.service.nxt.model.NxtSession;

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
      return new NxtSession(nxtSession.token());
   }
}
