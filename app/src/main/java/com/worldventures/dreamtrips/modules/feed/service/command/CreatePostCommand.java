package com.worldventures.dreamtrips.modules.feed.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.post.CreatePostHttpAction;
import com.worldventures.dreamtrips.api.post.CreateVideoPostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithPhotoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class CreatePostCommand extends CommandWithError<TextualPost> implements InjectableAction {

   @Inject Janet janet;
   @Inject protected MapperyContext mapperyContext;

   private int id;
   private CreatePhotoPostEntity createMediaPostEntity;
   private boolean videoPost = false;

   public CreatePostCommand(PostCompoundOperationModel postCompoundOperationModel) {
      id = postCompoundOperationModel.id();
      PostBody postBody = postCompoundOperationModel.body();
      createMediaPostEntity = new CreatePhotoPostEntity();
      createMediaPostEntity.setDescription(postBody.text());
      createMediaPostEntity.setLocation(postBody.location());

      if (postCompoundOperationModel.type() == PostBody.Type.PHOTO) {
         Queryable.from(((PostWithPhotoAttachmentBody) postBody).uploadedPhotos())
               .forEachR(photo -> createMediaPostEntity.addAttachment(new CreatePhotoPostEntity.Attachment(photo.getUid())));
      } else if (postCompoundOperationModel.type() == PostBody.Type.VIDEO) {
         videoPost = true;
         createMediaPostEntity.addAttachment(new CreatePhotoPostEntity.Attachment(((PostWithVideoAttachmentBody) postBody)
               .videoUid()));
      }
   }

   @Override
   protected void run(CommandCallback<TextualPost> callback) throws Throwable {
      if (videoPost) uploadVideoPost(callback);
      else uploadPost(callback);
   }

   private void uploadPost(CommandCallback<TextualPost> callback) {
      CreatePostHttpAction action = new CreatePostHttpAction(mapperyContext.convert(createMediaPostEntity, PostData.class));
      janet.createPipe(CreatePostHttpAction.class)
            .createObservableResult(action)
            .map(httpAction -> httpAction.response())
            .map(this::mapCommandResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void uploadVideoPost(CommandCallback<TextualPost> callback) {
      CreateVideoPostHttpAction action = new CreateVideoPostHttpAction(mapperyContext.convert(createMediaPostEntity, PostData.class));
      janet.createPipe(CreateVideoPostHttpAction.class)
            .createObservableResult(action)
            .map(httpAction -> {
               TextualPost textualPost = new TextualPost();
               textualPost.setUid(httpAction.uid());
               return textualPost;
            }).subscribe(callback::onSuccess, callback::onFail);
   }

   private TextualPost mapCommandResult(Object httpCommandResult) {
      if (httpCommandResult instanceof Iterable) {
         return (TextualPost) mapperyContext.convert((Iterable<?>) httpCommandResult, TextualPost.class);
      } else {
         return mapperyContext.convert(httpCommandResult, TextualPost.class);
      }
   }

   public int getId() {
      return id;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_create_post;
   }

}
