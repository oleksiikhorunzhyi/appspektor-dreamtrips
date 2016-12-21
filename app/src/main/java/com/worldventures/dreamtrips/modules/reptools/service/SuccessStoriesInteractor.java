package com.worldventures.dreamtrips.modules.reptools.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.reptools.service.command.GetSuccessStoriesCommand;
import com.worldventures.dreamtrips.modules.reptools.service.command.LikeSuccessStoryCommand;
import com.worldventures.dreamtrips.modules.reptools.service.command.UnlikeSuccessStoryCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class SuccessStoriesInteractor {

   private ActionPipe<GetSuccessStoriesCommand> getSuccessStoriesPipe;
   private ActionPipe<LikeSuccessStoryCommand> likeSuccessStoryPipe;
   private ActionPipe<UnlikeSuccessStoryCommand> unlikeSuccessStoryPipe;

   public SuccessStoriesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      getSuccessStoriesPipe = sessionActionPipeCreator.createPipe(GetSuccessStoriesCommand.class, Schedulers.io());
      likeSuccessStoryPipe = sessionActionPipeCreator.createPipe(LikeSuccessStoryCommand.class, Schedulers.io());
      unlikeSuccessStoryPipe = sessionActionPipeCreator.createPipe(UnlikeSuccessStoryCommand.class, Schedulers.io());
   }

   public ActionPipe<GetSuccessStoriesCommand> getSuccessStoriesPipe() {
      return getSuccessStoriesPipe;
   }

   public ActionPipe<LikeSuccessStoryCommand> likeSuccessStoryPipe() {
      return likeSuccessStoryPipe;
   }

   public ActionPipe<UnlikeSuccessStoryCommand> unlikeSuccessStoryPipe() {
      return unlikeSuccessStoryPipe;
   }
}
