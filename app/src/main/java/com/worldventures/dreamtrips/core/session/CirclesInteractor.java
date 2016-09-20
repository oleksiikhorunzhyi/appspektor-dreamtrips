package com.worldventures.dreamtrips.core.session;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class CirclesInteractor {

   ActionPipe<CirclesCommand> pipe;

   public CirclesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      pipe = sessionActionPipeCreator.createPipe(CirclesCommand.class, Schedulers.io());
   }

   public ActionPipe<CirclesCommand> pipe() {
      return pipe;
   }
}
