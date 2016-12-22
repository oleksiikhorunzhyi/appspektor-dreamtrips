package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlUndefinedLocation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LocationFacadeCommand extends ValueCommandAction<DtlLocation> {

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
