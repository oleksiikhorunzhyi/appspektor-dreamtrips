package com.worldventures.dreamtrips.social.ui.feed.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationMemoryStorage extends MemoryStorage<List<FeedItem>> {

   @Override
   public void save(@Nullable CacheBundle bundle, List<FeedItem> data) {
      if (bundle != null && (Boolean) bundle.get(NotificationsStorage.REFRESH)) {
         super.save(bundle, data);
      } else {
         addNewItemsIfCacheIsValid(bundle, data);
      }
   }

   private void addNewItemsIfCacheIsValid(@Nullable CacheBundle bundle, List<FeedItem> data) {
      List<FeedItem> newItems = new ArrayList<>();
      List<FeedItem> previousItems = get(bundle);
      if (previousItems != null) {
         newItems.addAll(previousItems);
         newItems.addAll(data);
         super.save(bundle, newItems);
      }
   }
}
