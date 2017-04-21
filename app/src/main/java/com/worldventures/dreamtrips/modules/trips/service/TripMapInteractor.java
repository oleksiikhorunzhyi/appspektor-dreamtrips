package com.worldventures.dreamtrips.modules.trips.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.trips.command.CheckTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsLocationsCommand;
import com.worldventures.dreamtrips.modules.trips.model.Schedule;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class TripMapInteractor {

   private ActionPipe<GetTripsLocationsCommand> mapObjectsActionPipe;
   private ActionPipe<GetTripsByUidCommand> tripsByUidPipe;
   private ActionPipe<CheckTripsByUidCommand> checkTripsByUidPipe;

   @Inject
   public TripMapInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      mapObjectsActionPipe = sessionActionPipeCreator.createPipe(GetTripsLocationsCommand.class, Schedulers.io());
      tripsByUidPipe = sessionActionPipeCreator.createPipe(GetTripsByUidCommand.class, Schedulers.io());
      checkTripsByUidPipe = sessionActionPipeCreator.createPipe(CheckTripsByUidCommand.class, Schedulers.io());
   }

   public ActionPipe<GetTripsLocationsCommand> mapObjectsPipe() {
      return mapObjectsActionPipe;
   }

   public ActionPipe<GetTripsByUidCommand> tripsByUidPipe() {
      return tripsByUidPipe;
   }

   public ActionPipe<CheckTripsByUidCommand> checkTripsByUidPipe() {
      return checkTripsByUidPipe;
   }
}
