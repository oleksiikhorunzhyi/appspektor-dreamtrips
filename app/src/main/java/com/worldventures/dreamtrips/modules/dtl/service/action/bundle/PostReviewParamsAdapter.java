package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import android.support.annotation.Nullable;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableRequestReviewParams;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.RequestReviewParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PostReviewParamsAdapter {

   private final PostReviewActionParams postReviewActionParams;
   private final UserReviewInfoProvider userReviewInfoProvider;

   public PostReviewParamsAdapter(PostReviewActionParams postReviewActionParams, UserReviewInfoProvider userReviewInfoProvider) {
      this.postReviewActionParams = postReviewActionParams;
      this.userReviewInfoProvider = userReviewInfoProvider;
   }

   public String rating() {
      return String.valueOf(postReviewActionParams.rating());
   }

   public String verified() {
      return String.valueOf(postReviewActionParams.verified());
   }

   public String comment() {
      return postReviewActionParams.comment();
   }

   public RequestReviewParams reviewParams() {
      return ImmutableRequestReviewParams.builder()
            .brandId(userReviewInfoProvider.brandId())
            .productId(postReviewActionParams.productId())
            .build();
   }

   public String fingerprint() {
      return userReviewInfoProvider.fingerprint();
   }

   public String ipAddress() {
      return userReviewInfoProvider.ipAddress();
   }

   @Nullable
   public List<File> attachments() {
      if (postReviewActionParams.attachments() != null) {
         List<File> files = new ArrayList<>();
         for (PhotoPickerModel photo : postReviewActionParams.attachments()) {
            files.add(new File(photo.getAbsolutePath()));
         }
         return files;
      } else {
         return null;
      }
   }
}
