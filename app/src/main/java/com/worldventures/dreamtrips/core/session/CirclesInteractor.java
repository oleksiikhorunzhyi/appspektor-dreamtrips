package com.worldventures.dreamtrips.core.session;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.api.janet.command.GetCirclesCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class CirclesInteractor {

   ActionPipe<GetCirclesCommand> pipe;

   public CirclesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      pipe = sessionActionPipeCreator.createPipe(GetCirclesCommand.class, Schedulers.io());
   }

   public ActionPipe<GetCirclesCommand> pipe() {
      return pipe;
   }
}
