package com.worldventures.dreamtrips.modules.feed.api.response;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import timber.log.Timber;

public class RequestCountResponseListener implements NewDreamTripsHttpService.ResponseListener {

   protected final SnappyRepository db;

   public RequestCountResponseListener(SnappyRepository db) {
      this.db = db;
   }

   @Override
   public void onResponse(BaseHttpAction baseHttpAction) {
      saveHeaderCount(baseHttpAction);
   }

   private void saveHeaderCount(BaseHttpAction baseHttpAction) {
      if (baseHttpAction instanceof AuthorizedHttpAction) {
         try {
            int friendRequestsCount = ((AuthorizedHttpAction) baseHttpAction).getFriendRequestCount();
            int unreadNotificationsCount = ((AuthorizedHttpAction) baseHttpAction).getUnreadNotifactionsCount();
            int totalCount = friendRequestsCount + unreadNotificationsCount;

            db.saveFriendRequestsCount(friendRequestsCount);
            db.saveNotificationsCount(unreadNotificationsCount);
            db.saveBadgeNotificationsCount(totalCount);
            Timber.d("Saving headers friendRequestsCount = %d,unreadNotificationsCount = %d ", friendRequestsCount, unreadNotificationsCount);
         } catch (IllegalArgumentException e) {
            Timber.e("Failed to parse headers");
         }
      }
   }

}
