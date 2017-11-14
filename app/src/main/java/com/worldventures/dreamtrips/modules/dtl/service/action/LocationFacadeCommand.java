package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.core.janet.ValueCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlUndefinedLocation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public final class LocationFacadeCommand extends ValueCommandAction<DtlLocation> {

   public static LocationFacadeCommand change(DtlLocation location) {
      return new LocationFacadeCommand(location);
   }

   public static LocationFacadeCommand clear() {
      return change(DtlUndefinedLocation.INSTANCE);
   }

   private LocationFacadeCommand(DtlLocation dtlLocation) {
      super(dtlLocation);
   }
}
