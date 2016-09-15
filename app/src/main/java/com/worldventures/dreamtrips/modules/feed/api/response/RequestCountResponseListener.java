package com.worldventures.dreamtrips.modules.feed.api.response;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.InterceptingOkClient;

import java.util.List;

import retrofit.client.Header;
import timber.log.Timber;

public class RequestCountResponseListener implements InterceptingOkClient.ResponseHeaderListener {

   protected final SnappyRepository db;
   protected final List<String> keys;

   public RequestCountResponseListener(SnappyRepository db, List<String> keys) {
      this.keys = keys;
      this.db = db;
   }

   @Override
   public void onResponse(List<Header> headers) {
      saveHeaderCount(headers);
   }

   protected void saveHeaderCount(List<Header> headers) {
      final boolean[] hasNotifications = {false};
      final int[] badgeNotifications = {0};
      Queryable.from(headers).filter(h -> {
         return keys.contains(h.getName());
      }).forEachR(h -> {
         hasNotifications[0] = true;
         int count = 0;
         try {
            count = Integer.parseInt(h.getValue());
         } catch (Exception e) {
            Timber.w(e, "Can't parse notification count for HEADER '%s'", h.getName());
         }
         db.saveCountFromHeader(h.getName(), count);
         badgeNotifications[0] += count;
      });
      if (hasNotifications[0]) db.saveBadgeNotificationsCount(badgeNotifications[0]);
   }

}
