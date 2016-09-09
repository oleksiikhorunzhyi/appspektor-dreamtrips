package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.schedulers.Schedulers;

public class DtlLocationInteractor {

   private final ActionPipe<DtlLocationCommand> locationPipe;
   private final ActionPipe<DtlNearbyLocationAction> nearbyLocationPipe;
   private final ActionPipe<DtlSearchLocationAction> searchLocationPipe;

   public DtlLocationInteractor(Janet janet) {

      locationPipe = janet.createPipe(DtlLocationCommand.class, Schedulers.io());
      nearbyLocationPipe = janet.createPipe(DtlNearbyLocationAction.class, Schedulers.io());
      searchLocationPipe = janet.createPipe(DtlSearchLocationAction.class, Schedulers.io());
      //
      searchLocationPipe.observe().subscribe(new ActionStateSubscriber<DtlSearchLocationAction>().onStart(action -> {
         nearbyLocationPipe.cancelLatest();
      }));
      locationPipe.send(DtlLocationCommand.clear());
   }

   public ActionPipe<DtlLocationCommand> locationPipe() {
      return locationPipe;
   }

   public ActionPipe<DtlNearbyLocationAction> nearbyLocationPipe() {
      return nearbyLocationPipe;
   }

   public ActionPipe<DtlSearchLocationAction> searchLocationPipe() {
      return searchLocationPipe;
   }
}
