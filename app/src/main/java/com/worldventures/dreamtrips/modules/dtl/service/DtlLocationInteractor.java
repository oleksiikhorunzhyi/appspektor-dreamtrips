package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationFacadeCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.schedulers.Schedulers;

public class DtlLocationInteractor {

   private final ActionPipe<DtlLocationCommand> locationSourcePipe;
   private final ActionPipe<DtlLocationFacadeCommand> locationFacadePipe;
   private final ActionPipe<DtlNearbyLocationAction> nearbyLocationPipe;
   private final ActionPipe<DtlSearchLocationAction> searchLocationPipe;

   public DtlLocationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {

      locationSourcePipe = sessionActionPipeCreator.createPipe(DtlLocationCommand.class, Schedulers.io());
      locationFacadePipe = sessionActionPipeCreator.createPipe(DtlLocationFacadeCommand.class, Schedulers.io());
      nearbyLocationPipe = sessionActionPipeCreator.createPipe(DtlNearbyLocationAction.class, Schedulers.io());
      searchLocationPipe = sessionActionPipeCreator.createPipe(DtlSearchLocationAction.class, Schedulers.io());

      connectLocationPipes();
      connectSearchCancelLatest();
      clear();
   }

   public ReadActionPipe<DtlLocationCommand> locationSourcePipe() {
      return locationSourcePipe;
   }

   public ReadActionPipe<DtlLocationFacadeCommand> locationFacadePipe() {
      return locationFacadePipe;
   }

   public ActionPipe<DtlNearbyLocationAction> nearbyLocationPipe() {
      return nearbyLocationPipe;
   }

   public ActionPipe<DtlSearchLocationAction> searchLocationPipe() {
      return searchLocationPipe;
   }

   public void clear() {
      locationSourcePipe.send(DtlLocationCommand.clear());
   }

   public void changeSourceLocation(DtlLocation dtlLocation) {
      locationSourcePipe.send(DtlLocationCommand.change(dtlLocation));
   }

   public void changeFacadeLocation(DtlLocation dtlLocation) {
      locationFacadePipe.send(DtlLocationFacadeCommand.change(dtlLocation));
   }

   private void connectSearchCancelLatest() {
      searchLocationPipe.observe().subscribe(new ActionStateSubscriber<DtlSearchLocationAction>()
            .onStart(action -> nearbyLocationPipe.cancelLatest()));
   }

   private void connectLocationPipes() {
      locationSourcePipe.observeSuccess()
            .map(DtlLocationCommand::getResult)
            .map(DtlLocationFacadeCommand::change)
            .subscribe(locationFacadePipe::send);
   }
}
