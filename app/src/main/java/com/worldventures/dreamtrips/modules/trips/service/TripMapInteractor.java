package com.worldventures.dreamtrips.modules.trips.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsLocationsCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class TripMapInteractor {

   private ActionPipe<GetTripsLocationsCommand> mapObjectsActionPipe;
   private ActionPipe<GetTripsByUidCommand> tripsByUidPipe;

   @Inject
   public TripMapInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      mapObjectsActionPipe = sessionActionPipeCreator.createPipe(GetTripsLocationsCommand.class, Schedulers.io());
      tripsByUidPipe = sessionActionPipeCreator.createPipe(GetTripsByUidCommand.class, Schedulers.io());
   }

   public ActionPipe<GetTripsLocationsCommand> mapObjectsPipe() {
      return mapObjectsActionPipe;
   }

   public ActionPipe<GetTripsByUidCommand> tripsByUidPipe() {
      return tripsByUidPipe;
   }
}
