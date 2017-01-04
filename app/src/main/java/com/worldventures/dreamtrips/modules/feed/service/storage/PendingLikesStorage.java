package com.worldventures.dreamtrips.modules.feed.service.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PendingLikesStorage {
   private List<String> pendingLikes = Collections.synchronizedList(new ArrayList<>());

   public PendingLikesStorage() {
   }

   public void add(String uid) {
      pendingLikes.add(uid);
   }

   public void remove(String uid) {
      pendingLikes.remove(uid);
   }

   public boolean contains(String uid) {
      return pendingLikes.contains(uid);
   }
}
