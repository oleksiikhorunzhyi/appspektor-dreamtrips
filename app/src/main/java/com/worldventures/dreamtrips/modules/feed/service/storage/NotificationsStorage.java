package com.worldventures.dreamtrips.modules.feed.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.command.GetNotificationsCommand;

import java.util.ArrayList;
import java.util.List;

public class NotificationsStorage implements ActionStorage<List<FeedItem>> {

   public static final String REFRESH = "REFRESH";

   private final SnappyRepository snappyRepository;
   private final NotificationMemoryStorage memoryStorage;

   public NotificationsStorage(SnappyRepository snappyRepository, NotificationMemoryStorage memoryStorage) {
      this.snappyRepository = snappyRepository;
      this.memoryStorage = memoryStorage;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetNotificationsCommand.class;
   }

   @Override
   public synchronized void save(@Nullable CacheBundle params, List<FeedItem> data) {
      memoryStorage.save(params, data);
      if (params.get(NotificationsStorage.REFRESH)) {
         snappyRepository.saveNotifications(data);
      } else {
         List<FeedItem> notifications = new ArrayList<>();
         notifications.addAll(0, memoryStorage.get(params));
         snappyRepository.saveNotifications(notifications);
      }
   }

   @Override
   public synchronized List<FeedItem> get(@Nullable CacheBundle params) {
      List<FeedItem> items = memoryStorage.get(params);
      if (items == null) {
         items = snappyRepository.getNotifications();
      }
      return items;
   }
}
