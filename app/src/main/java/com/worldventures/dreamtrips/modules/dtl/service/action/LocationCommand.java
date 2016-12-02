package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlUndefinedLocation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LocationCommand extends ValueCommandAction<DtlLocation> {

   public static LocationCommand change(DtlLocation location) {
      return new LocationCommand(location);
   }

   public static LocationCommand clear() {
      return change(DtlUndefinedLocation.INSTANCE);
   }

   private LocationCommand(DtlLocation dtlLocation) {
      super(dtlLocation);
   }

   public boolean isResultDefined() {
      return getResult() != null && getResult().locationSourceType() != LocationSourceType.UNDEFINED;
   }
}
