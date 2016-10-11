package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.schedulers.Schedulers;

public class DtlLocationInteractor {

   private final ActionPipe<DtlLocationCommand> locationPipe;
   private final ActionPipe<DtlNearbyLocationAction> nearbyLocationPipe;
   private final ActionPipe<DtlSearchLocationAction> searchLocationPipe;

   public DtlLocationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {

      locationPipe = sessionActionPipeCreator.createPipe(DtlLocationCommand.class, Schedulers.io());
      nearbyLocationPipe = sessionActionPipeCreator.createPipe(DtlNearbyLocationAction.class, Schedulers.io());
      searchLocationPipe = sessionActionPipeCreator.createPipe(DtlSearchLocationAction.class, Schedulers.io());

      connectSearchCancelLatest();
      clear();
   }

   public ReadActionPipe<DtlLocationCommand> locationPipe() {
      return locationPipe;
   }

   public ActionPipe<DtlNearbyLocationAction> nearbyLocationPipe() {
      return nearbyLocationPipe;
   }

   public ActionPipe<DtlSearchLocationAction> searchLocationPipe() {
      return searchLocationPipe;
   }

   public void clear() {
      locationPipe.send(DtlLocationCommand.clear());
   }

   public void change(DtlLocation dtlLocation) {
      locationPipe.send(DtlLocationCommand.change(dtlLocation));
   }

   private void connectSearchCancelLatest() {
      searchLocationPipe.observe().subscribe(new ActionStateSubscriber<DtlSearchLocationAction>().onStart(action -> nearbyLocationPipe.cancelLatest()));
   }
}
