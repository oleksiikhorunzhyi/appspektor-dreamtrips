package com.worldventures.dreamtrips.social.ui.feed.api.response;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.core.janet.api_lib.ResponseListener;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;
import com.worldventures.dreamtrips.modules.common.command.NotificationCountChangedCommand;

import timber.log.Timber;

public class RequestCountResponseListener implements ResponseListener {

   protected final UserNotificationInteractor userNotificationInteractor;

   public RequestCountResponseListener(UserNotificationInteractor userNotificationInteractor) {
      this.userNotificationInteractor = userNotificationInteractor;
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
            userNotificationInteractor.notificationCountChangedPipe()
                  .send(new NotificationCountChangedCommand(friendRequestsCount, unreadNotificationsCount, totalCount));
            Timber.d("Saving headers friendRequestsCount = %d,unreadNotificationsCount = %d ", friendRequestsCount, unreadNotificationsCount);
         } catch (IllegalArgumentException e) {
            Timber.e("Failed to parse headers");
         }
      }
   }
}
