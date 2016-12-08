package com.worldventures.dreamtrips.modules.feed.model.uploading;

import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;

import java.util.List;

/**
 * Wrapper for avoiding registering ArrayList as model in cell adapter
 */
public class UploadingPostsList {

   private List<PostCompoundOperationModel> photoPosts;

   public UploadingPostsList(List<PostCompoundOperationModel> photoPosts) {
      this.photoPosts = photoPosts;
   }

   public List<PostCompoundOperationModel> getPhotoPosts() {
      return photoPosts;
   }
}
