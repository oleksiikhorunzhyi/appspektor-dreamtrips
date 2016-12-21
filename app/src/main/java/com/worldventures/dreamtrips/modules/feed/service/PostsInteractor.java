package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.background_uploading.service.CreatePostCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePhotosCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditPostCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class PostsInteractor {

   private ActionPipe<CreatePostCommand> createPostPipe;
   private ActionPipe<CreatePhotosCommand> createPhotosPipe;
   private ActionPipe<EditPostCommand> editPostPipe;
   private ActionPipe<DeletePostCommand> deletePostPipe;
   private ActionPipe<CreatePostCompoundOperationCommand> createPostCompoundOperationPipe;

   public PostsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      createPostPipe = sessionActionPipeCreator.createPipe(CreatePostCommand.class, Schedulers.io());
      editPostPipe = sessionActionPipeCreator.createPipe(EditPostCommand.class, Schedulers.io());
      deletePostPipe = sessionActionPipeCreator.createPipe(DeletePostCommand.class, Schedulers.io());
      createPhotosPipe = sessionActionPipeCreator.createPipe(CreatePhotosCommand.class, Schedulers.io());
      createPostCompoundOperationPipe = sessionActionPipeCreator.createPipe(CreatePostCompoundOperationCommand.class, Schedulers.io());
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

   public ActionPipe<CreatePhotosCommand> createPhotosPipe() {
      return createPhotosPipe;
   }

   public ActionPipe<CreatePostCompoundOperationCommand> createPostCompoundOperationPipe() {
      return createPostCompoundOperationPipe;
   }
}
