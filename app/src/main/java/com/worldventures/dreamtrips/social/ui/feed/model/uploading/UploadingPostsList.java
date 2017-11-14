package com.worldventures.dreamtrips.social.ui.feed.model.uploading;

import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for avoiding registering ArrayList as model in cell adapter
 */
public class UploadingPostsList {

   private final List<PostCompoundOperationModel> photoPosts;

   public UploadingPostsList(List<PostCompoundOperationModel> photoPosts) {
      this.photoPosts = photoPosts;
   }

   public List<PostCompoundOperationModel> getPhotoPosts() {
      if (photoPosts == null) {
         return new ArrayList<>();
      }
      return photoPosts;
   }
}
