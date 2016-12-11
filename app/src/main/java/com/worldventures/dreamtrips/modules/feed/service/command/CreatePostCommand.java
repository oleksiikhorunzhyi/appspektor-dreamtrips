package com.worldventures.dreamtrips.modules.feed.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.post.CreatePostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithAttachmentBody;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreatePostCommand extends MappableApiActionCommand<CreatePostHttpAction,
      TextualPost, TextualPost> {

   private CreatePhotoPostEntity createPhotoPostEntity;

   public CreatePostCommand(PostWithAttachmentBody postWithAttachmentBody) {
      createPhotoPostEntity = new CreatePhotoPostEntity();
      createPhotoPostEntity.setDescription(postWithAttachmentBody.text());
      createPhotoPostEntity.setLocation(postWithAttachmentBody.location());
      if (postWithAttachmentBody.uploadedPhotos() != null)
         Queryable.from(postWithAttachmentBody.uploadedPhotos())
               .forEachR(photo -> createPhotoPostEntity.addAttachment(new CreatePhotoPostEntity.Attachment(photo.getUid())));
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
      return new CreatePostHttpAction(mapperyContext.convert(createPhotoPostEntity, PostData.class));
   }

   @Override
   protected Class<CreatePostHttpAction> getHttpActionClass() {
      return CreatePostHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_create_post;
   }
}
