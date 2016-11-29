package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LocationFacadeCommand extends ValueCommandAction<DtlLocation> {

   public static LocationFacadeCommand change(DtlLocation location) {
      return new LocationFacadeCommand(location);
   }

   public static LocationFacadeCommand clear() {
      return change(DtlLocation.undefined());
   }

   private LocationFacadeCommand(DtlLocation dtlLocation) {
      super(dtlLocation);
   }

   public boolean isResultDefined() {
      return getResult() != null && getResult().locationSourceType() != LocationSourceType.UNDEFINED;
   }
}
