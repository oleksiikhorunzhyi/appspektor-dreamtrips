package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.GetCommentsCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class CommentsInteractor {

   private final ActionPipe<GetCommentsCommand> commentsPipe;

   @Inject
   public CommentsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.commentsPipe = sessionActionPipeCreator.createPipe(GetCommentsCommand.class, Schedulers.io());
   }

   public ActionPipe<GetCommentsCommand> commentsPipe() {
      return commentsPipe;
   }
}
