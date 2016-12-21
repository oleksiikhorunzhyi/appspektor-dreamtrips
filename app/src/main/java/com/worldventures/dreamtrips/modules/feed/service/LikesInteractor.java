package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.UnlikeEntityCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class LikesInteractor {

   private final ActionPipe<LikeEntityCommand> likePipe;
   private final ActionPipe<UnlikeEntityCommand> unlikePipe;

   public LikesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      likePipe = sessionActionPipeCreator.createPipe(LikeEntityCommand.class, Schedulers.io());
      unlikePipe = sessionActionPipeCreator.createPipe(UnlikeEntityCommand.class, Schedulers.io());
   }

   public ActionPipe<LikeEntityCommand> likePipe() {
      return likePipe;
   }

   public ActionPipe<UnlikeEntityCommand> unlikePipe() {
      return unlikePipe;
   }
}
