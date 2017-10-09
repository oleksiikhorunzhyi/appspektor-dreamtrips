package com.worldventures.dreamtrips.modules.common.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.command.NotificationCountChangedCommand;

public class NotificationCounterStorage implements ActionStorage<NotificationCountChangedCommand.NotificationCounterResult> {

   private SnappyRepository snappyRepository;
   private MemoryStorage<NotificationCountChangedCommand.NotificationCounterResult> memoryStorage;

   public NotificationCounterStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
      memoryStorage = new MemoryStorage<>();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return NotificationCountChangedCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, NotificationCountChangedCommand.NotificationCounterResult data) {
      memoryStorage.save(params, data);
      snappyRepository.saveBadgeNotificationsCount(data.getBadgeNotificationsCount());
      snappyRepository.saveFriendRequestsCount(data.getFriendNotificationCount());
      snappyRepository.saveNotificationsCount(data.getExclusiveNotificationCount());
   }

   @Override
   public NotificationCountChangedCommand.NotificationCounterResult get(@Nullable CacheBundle action) {
      NotificationCountChangedCommand.NotificationCounterResult data = memoryStorage.get(action);
      if (data == null) {
         data = new NotificationCountChangedCommand.NotificationCounterResult(snappyRepository.getFriendsRequestsCount(),
               snappyRepository.getExclusiveNotificationsCount(), snappyRepository.getBadgeNotificationsCount());
      }
      return data;
   }
}
