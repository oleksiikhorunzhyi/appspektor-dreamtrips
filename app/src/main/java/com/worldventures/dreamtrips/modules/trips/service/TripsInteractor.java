package com.worldventures.dreamtrips.modules.trips.service;

import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class TripsInteractor {

   private final ActionPipe<GetTripDetailsCommand> detailsPipe;
   private final ActionPipe<GetTripsCommand> tripsPipe;

   @Inject
   public TripsInteractor(Janet janet) {
      detailsPipe = janet.createPipe(GetTripDetailsCommand.class, Schedulers.io());
      tripsPipe = janet.createPipe(GetTripsCommand.class, Schedulers.io());
   }

   public ActionPipe<GetTripDetailsCommand> detailsPipe() {
      return detailsPipe;
   }

   public ActionPipe<GetTripsCommand> tripsPipe() {
      return tripsPipe;
   }
}
