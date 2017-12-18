package com.worldventures.dreamtrips.social.ui.feed.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CreatePostCompoundOperationCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreatePhotosCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreateVideoCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ProcessAttachmentsAndPost;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class PostsInteractor {

   private final ActionPipe<CreatePostCommand> createPostPipe;
   private final ActionPipe<PostCreatedCommand> postCreatedPipe;
   private final ActionPipe<CreatePhotosCommand> createPhotosPipe;
   private final ActionPipe<CreateVideoCommand> createVideoPipe;
   private final ActionPipe<EditPostCommand> editPostPipe;
   private final ActionPipe<DeletePostCommand> deletePostPipe;
   private final ActionPipe<CreatePostCompoundOperationCommand> createPostCompoundOperationPipe;
   private final ActionPipe<ProcessAttachmentsAndPost> ProcessAttachmentsAndPostPipe;

   public PostsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      createPostPipe = sessionActionPipeCreator.createPipe(CreatePostCommand.class, Schedulers.io());
      postCreatedPipe = sessionActionPipeCreator.createPipe(PostCreatedCommand.class, Schedulers.io());
      editPostPipe = sessionActionPipeCreator.createPipe(EditPostCommand.class, Schedulers.io());
      deletePostPipe = sessionActionPipeCreator.createPipe(DeletePostCommand.class, Schedulers.io());
      createVideoPipe = sessionActionPipeCreator.createPipe(CreateVideoCommand.class, Schedulers.io());
      createPhotosPipe = sessionActionPipeCreator.createPipe(CreatePhotosCommand.class, Schedulers.io());
      createPostCompoundOperationPipe = sessionActionPipeCreator.createPipe(CreatePostCompoundOperationCommand.class, Schedulers
            .io());
      ProcessAttachmentsAndPostPipe = sessionActionPipeCreator.createPipe(ProcessAttachmentsAndPost.class, Schedulers.io());
   }

   public ActionPipe<CreatePostCommand> createPostPipe() {
      return createPostPipe;
   }

   public ActionPipe<CreateVideoCommand> createVideoPipe() {
      return createVideoPipe;
   }

   public ActionPipe<PostCreatedCommand> postCreatedPipe() {
      return postCreatedPipe;
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

   public ActionPipe<ProcessAttachmentsAndPost> processAttachmentsAndPostPipe() {
      return ProcessAttachmentsAndPostPipe;
   }
}
