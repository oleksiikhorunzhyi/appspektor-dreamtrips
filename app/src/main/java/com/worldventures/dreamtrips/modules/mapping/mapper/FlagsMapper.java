package com.worldventures.dreamtrips.modules.mapping.mapper;

import com.worldventures.dreamtrips.api.flagging.model.FlagReason;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.Mapper;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

public class FlagsMapper implements Mapper<FlagReason, Flag> {

   @Override
   public Flag map(FlagReason flagReason) {
      return new Flag(flagReason.id(), flagReason.name(), flagReason.requireDescription());
   }
}
