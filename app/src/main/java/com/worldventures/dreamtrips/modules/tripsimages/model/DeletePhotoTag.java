package com.worldventures.dreamtrips.modules.tripsimages.model;

import java.util.ArrayList;
import java.util.List;

public class DeletePhotoTag {

   private List<Integer> userIds;

   public DeletePhotoTag() {
      userIds = new ArrayList<>();
   }

   public DeletePhotoTag(List<Integer> userIds) {
      this.userIds = userIds;
   }

   public void addUserId(int userId) {
      userIds.add(userId);
   }
}
