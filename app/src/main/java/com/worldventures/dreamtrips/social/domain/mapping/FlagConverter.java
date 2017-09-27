package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.dreamtrips.api.flagging.model.FlagReason;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Flag;

import io.techery.mappery.MapperyContext;

public class FlagConverter implements Converter<FlagReason, Flag> {
   @Override
   public Class<FlagReason> sourceClass() {
      return FlagReason.class;
   }

   @Override
   public Class<Flag> targetClass() {
      return Flag.class;
   }

   @Override
   public Flag convert(MapperyContext mapperyContext, FlagReason flagReason) {
      return new Flag(flagReason.id(), flagReason.name(), flagReason.requireDescription());
   }

}
