package com.worldventures.dreamtrips.modules.feed.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.post.CreatePostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithPhotoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreatePostCommand extends MappableApiActionCommand<CreatePostHttpAction,
      TextualPost, TextualPost> {

   private int id;
   private CreatePhotoPostEntity createMediaPostEntity;

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
         createMediaPostEntity.addAttachment(new CreatePhotoPostEntity.Attachment(((PostWithVideoAttachmentBody) postBody)
               .videoUid()));
      }
   }

   public int getId() {
      return id;
   }

   @Override
   protected Class<TextualPost> getMappingTargetClass() {
      return TextualPost.class;
   }

   @Override
   protected Object mapHttpActionResult(CreatePostHttpAction httpAction) {
      return httpAction.response();
   }

   @Override
   protected CreatePostHttpAction getHttpAction() {
      return new CreatePostHttpAction(mapperyContext.convert(createMediaPostEntity, PostData.class));
   }

   @Override
   protected Class<CreatePostHttpAction> getHttpActionClass() {
      return CreatePostHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_create_post;
   }

   @Override
   protected TextualPost mapCommandResult(Object httpCommandResult) {
      if (httpCommandResult == null) {
         return null;
      }
      return super.mapCommandResult(httpCommandResult);
   }
}
