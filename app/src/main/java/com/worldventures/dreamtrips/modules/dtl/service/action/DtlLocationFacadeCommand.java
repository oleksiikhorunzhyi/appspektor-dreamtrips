package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlLocationFacadeCommand extends ValueCommandAction<DtlLocation> {

   public static DtlLocationFacadeCommand change(DtlLocation location) {
      return new DtlLocationFacadeCommand(location);
   }

   public static DtlLocationFacadeCommand clear() {
      return change(DtlLocation.UNDEFINED);
   }

   private DtlLocationFacadeCommand(DtlLocation dtlLocation) {
      super(dtlLocation);
   }

   public boolean isResultDefined() {
      return getResult() != null && getResult().getLocationSourceType() != LocationSourceType.UNDEFINED;
   }
}
