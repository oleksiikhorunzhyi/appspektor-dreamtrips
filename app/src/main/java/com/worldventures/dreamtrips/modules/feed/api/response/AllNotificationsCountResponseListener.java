package com.worldventures.dreamtrips.modules.feed.api.response;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import java.util.Arrays;

public class AllNotificationsCountResponseListener extends RequestCountResponseListener {

   public AllNotificationsCountResponseListener(SnappyRepository db) {
      super(db, Arrays.asList(SnappyRepository.EXCLUSIVE_NOTIFICATIONS_COUNT, SnappyRepository.FRIEND_REQUEST_COUNT));
   }

}
