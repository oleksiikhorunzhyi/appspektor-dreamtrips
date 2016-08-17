package com.worldventures.dreamtrips.modules.feed.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Command;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

public class EditPostCommand extends Command<TextualPost> {

   private String uid;
   private CreatePhotoPostEntity entity;

   public EditPostCommand(String uid, CreatePhotoPostEntity entity) {
      super(TextualPost.class);
      this.uid = uid;
      this.entity = entity;
   }

   @Override
   public TextualPost loadDataFromNetwork() throws Exception {
      return getService().editPost(uid, entity);
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_update_post;
   }
}
