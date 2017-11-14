package com.worldventures.dreamtrips.social.ui.feed.service.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PendingLikesStorage {

   private final List<String> pendingLikes = Collections.synchronizedList(new ArrayList<>());

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
