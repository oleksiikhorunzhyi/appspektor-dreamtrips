package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditPostCommand;

import io.techery.janet.ActionPipe;

public class PostsInteractor {

   private ActionPipe<CreatePostCommand> createPostPipe;
   private ActionPipe<EditPostCommand> editPostPipe;
   private ActionPipe<DeletePostCommand> deletePostPipe;

   public PostsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      createPostPipe = sessionActionPipeCreator.createPipe(CreatePostCommand.class);
      editPostPipe = sessionActionPipeCreator.createPipe(EditPostCommand.class);
      deletePostPipe = sessionActionPipeCreator.createPipe(DeletePostCommand.class);
   }

   public ActionPipe<CreatePostCommand> createPostPipe() {
      return createPostPipe;
   }

   public ActionPipe<EditPostCommand> getEditPostPipe() {
      return editPostPipe;
   }

   public ActionPipe<DeletePostCommand> deletePostPipe() {
      return deletePostPipe;
   }
}
