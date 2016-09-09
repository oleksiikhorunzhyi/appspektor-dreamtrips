package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlLocationCommand extends Command<DtlLocation> implements InjectableAction {

   @Inject AttributesInteractor attributesInteractor;
   @Inject DtlFilterMerchantInteractor filterMerchantInteractor;

   private final DtlLocation dtlLocation;

   public static DtlLocationCommand change(DtlLocation location) {
      return new DtlLocationCommand(location);
   }

   public static DtlLocationCommand clear() {
      return change(DtlLocation.UNDEFINED);
   }

   private DtlLocationCommand(DtlLocation dtlLocation) {
      this.dtlLocation = dtlLocation;
   }

   @Override
   protected void run(CommandCallback<DtlLocation> callback) throws Throwable {
      filterMerchantInteractor.filterMerchantsActionPipe().clearReplays();
      attributesInteractor.attributesPipe().clearReplays();
      callback.onSuccess(dtlLocation);
      attributesInteractor.attributesPipe().send(new AttributesAction());
   }

   public boolean isResultDefined() {
      return getResult() != null && getResult().getLocationSourceType() != LocationSourceType.UNDEFINED;
   }
}
