package com.worldventures.dreamtrips.social.ui.reptools.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.reptools.service.command.GetSuccessStoriesCommand;
import com.worldventures.dreamtrips.social.ui.reptools.service.command.LikeSuccessStoryCommand;
import com.worldventures.dreamtrips.social.ui.reptools.service.command.UnlikeSuccessStoryCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class SuccessStoriesInteractor {

   private final ActionPipe<GetSuccessStoriesCommand> getSuccessStoriesPipe;
   private final ActionPipe<LikeSuccessStoryCommand> likeSuccessStoryPipe;
   private final ActionPipe<UnlikeSuccessStoryCommand> unlikeSuccessStoryPipe;

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
