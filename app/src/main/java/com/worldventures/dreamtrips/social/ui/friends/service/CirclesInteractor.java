package com.worldventures.dreamtrips.social.ui.friends.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetCirclesCommand;

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
