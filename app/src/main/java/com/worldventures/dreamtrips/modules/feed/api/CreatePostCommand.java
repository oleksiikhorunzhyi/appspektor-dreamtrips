package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

public class CreatePostCommand extends Command<TextualPost> {

   private CreatePhotoPostEntity createPhotoPostEntity;

   public CreatePostCommand(CreatePhotoPostEntity createPhotoPostEntity) {
      super(TextualPost.class);
      this.createPhotoPostEntity = createPhotoPostEntity;
   }

   @Override
   public TextualPost loadDataFromNetwork() throws Exception {
      return getService().createPhotoPost(createPhotoPostEntity);
   }
}
