package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.modules.feed.service.command.GetCommentsCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class CommentsInteractor {

   private final ActionPipe<GetCommentsCommand> commentsPipe;

   @Inject
   public CommentsInteractor(Janet janet) {
      this.commentsPipe = janet.createPipe(GetCommentsCommand.class, Schedulers.io());
   }

   public ActionPipe<GetCommentsCommand> commentsPipe() {
      return commentsPipe;
   }
}
