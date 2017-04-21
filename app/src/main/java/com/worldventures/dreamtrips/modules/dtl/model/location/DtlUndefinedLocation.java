package com.worldventures.dreamtrips.modules.dtl.model.location;

import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;

public final class DtlUndefinedLocation {

   public static final DtlLocation INSTANCE = ImmutableDtlLocation.builder()
         .locationSourceType(LocationSourceType.UNDEFINED)
         .isExternal(false)
         .build();
}
