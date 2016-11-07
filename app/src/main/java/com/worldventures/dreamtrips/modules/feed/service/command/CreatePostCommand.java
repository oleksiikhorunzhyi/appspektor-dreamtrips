package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.post.CreatePostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreatePostCommand extends MappableApiActionCommand<CreatePostHttpAction,
      TextualPost, TextualPost> {

   private CreatePhotoPostEntity createPhotoPostEntity;

   public CreatePostCommand(CreatePhotoPostEntity createPhotoPostEntity) {
      this.createPhotoPostEntity = createPhotoPostEntity;
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
