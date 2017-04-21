package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.post.UpdatePostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class EditPostCommand extends MappableApiActionCommand<UpdatePostHttpAction, TextualPost, TextualPost>
   implements InjectableAction {

   private String id;
   private CreatePhotoPostEntity createPhotoPostEntity;

   public EditPostCommand(String id, CreatePhotoPostEntity createPhotoPostEntity) {
      this.id = id;
      this.createPhotoPostEntity = createPhotoPostEntity;
   }

   @Override
   protected Class<TextualPost> getMappingTargetClass() {
      return TextualPost.class;
   }

   @Override
   protected Object mapHttpActionResult(UpdatePostHttpAction httpAction) {
      return httpAction.response();
   }

   @Override
   protected UpdatePostHttpAction getHttpAction() {
      return new UpdatePostHttpAction(id,
            mapperyContext.convert(createPhotoPostEntity, PostData.class));
   }

   @Override
   protected Class<UpdatePostHttpAction> getHttpActionClass() {
      return UpdatePostHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_update_post;
   }
}
