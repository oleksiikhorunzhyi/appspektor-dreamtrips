package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlLocationCommand extends ValueCommandAction<DtlLocation> {

   public static DtlLocationCommand change(DtlLocation location) {
      return new DtlLocationCommand(location);
   }

   public static DtlLocationCommand clear() {
      return change(DtlLocation.UNDEFINED);
   }

   private DtlLocationCommand(DtlLocation dtlLocation) {
      super(dtlLocation);
   }

   public boolean isResultDefined() {
      return getResult() != null && getResult().getLocationSourceType() != LocationSourceType.UNDEFINED;
   }
}
