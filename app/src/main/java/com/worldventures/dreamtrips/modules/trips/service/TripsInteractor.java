package com.worldventures.dreamtrips.modules.trips.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class TripsInteractor {

   private final ActionPipe<GetTripDetailsCommand> detailsPipe;
   private final ActionPipe<GetTripsCommand> tripsPipe;

   @Inject
   public TripsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      detailsPipe = sessionActionPipeCreator.createPipe(GetTripDetailsCommand.class, Schedulers.io());
      tripsPipe = sessionActionPipeCreator.createPipe(GetTripsCommand.class, Schedulers.io());
   }

   public ActionPipe<GetTripDetailsCommand> detailsPipe() {
      return detailsPipe;
   }

   public ActionPipe<GetTripsCommand> tripsPipe() {
      return tripsPipe;
   }
}
