package com.worldventures.dreamtrips.modules.feed.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.post.CreatePostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreatePostCommand extends MappableApiActionCommand<CreatePostHttpAction,
      TextualPost, TextualPost> {

   private CreatePhotoPostEntity createPhotoPostEntity;

   public CreatePostCommand(String text, Location location, List<Photo> photos) {
      createPhotoPostEntity = new CreatePhotoPostEntity();
      createPhotoPostEntity.setDescription(text);
      createPhotoPostEntity.setLocation(location);
      if (photos != null)
         Queryable.from(photos).forEachR(photo -> createPhotoPostEntity
               .addAttachment(new CreatePhotoPostEntity.Attachment(photo.getUid())));
   }

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
