package com.worldventures.dreamtrips.modules.feed.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationMemoryStorage extends MemoryStorage<List<FeedItem>> {

   @Override
   public void save(@Nullable CacheBundle bundle, List<FeedItem> data) {
      if (bundle.get(NotificationsStorage.REFRESH)) {
         super.save(bundle, data);
      } else {
         List<FeedItem> newItems = new ArrayList<>();
         newItems.addAll(get(bundle));
         newItems.addAll(data);
         super.save(bundle, newItems);
      }
   }
}
