package com.worldventures.dreamtrips.modules.trips.service;

import com.worldventures.dreamtrips.modules.trips.command.GetTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsLocationsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class TripMapInteractor {

   private ActionPipe<GetTripsLocationsCommand> mapObjectsActionPipe;
   private ActionPipe<GetTripsByUidCommand> tripsByUidPipe;

   public TripMapInteractor(Janet janet) {
      mapObjectsActionPipe = janet.createPipe(GetTripsLocationsCommand.class, Schedulers.io());
      tripsByUidPipe = janet.createPipe(GetTripsByUidCommand.class, Schedulers.io());
   }

   public ActionPipe<GetTripsLocationsCommand> mapObjectsPipe() {
      return mapObjectsActionPipe;
   }

   public ActionPipe<GetTripsByUidCommand> tripsByUidPipe() {
      return tripsByUidPipe;
   }
}
