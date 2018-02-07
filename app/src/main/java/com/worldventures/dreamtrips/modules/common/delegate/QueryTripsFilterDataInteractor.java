package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.trips.service.command.TripsFilterDataCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class QueryTripsFilterDataInteractor {

   ActionPipe<TripsFilterDataCommand> pipe;

   @Inject
   public QueryTripsFilterDataInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.pipe = sessionActionPipeCreator.createPipe(TripsFilterDataCommand.class, Schedulers.io());
   }

   public ActionPipe<TripsFilterDataCommand> pipe() {
      return pipe;
   }
}

