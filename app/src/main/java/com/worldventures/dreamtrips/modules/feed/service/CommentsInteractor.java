package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetCommentsCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class CommentsInteractor {

   private final ActionPipe<GetCommentsCommand> commentsPipe;
   private final ActionPipe<CreateCommentCommand> createCommentPipe;
   private final ActionPipe<EditCommentCommand> editCommentPipe;
   private final ActionPipe<DeleteCommentCommand> deleteCommentPipe;

   @Inject
   public CommentsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      commentsPipe = sessionActionPipeCreator.createPipe(GetCommentsCommand.class, Schedulers.io());
      createCommentPipe = sessionActionPipeCreator.createPipe(CreateCommentCommand.class, Schedulers.io());
      editCommentPipe = sessionActionPipeCreator.createPipe(EditCommentCommand.class, Schedulers.io());
      deleteCommentPipe = sessionActionPipeCreator.createPipe(DeleteCommentCommand.class, Schedulers.io());
   }

   public ActionPipe<GetCommentsCommand> commentsPipe() {
      return commentsPipe;
   }

   public ActionPipe<CreateCommentCommand> createCommentPipe() {
      return createCommentPipe;
   }

   public ActionPipe<EditCommentCommand> editCommentPipe() {
      return editCommentPipe;
   }

   public ActionPipe<DeleteCommentCommand> deleteCommentPipe() {
      return deleteCommentPipe;
   }
}
