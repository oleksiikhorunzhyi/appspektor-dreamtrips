package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.ImmutableCacheOptions;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class NotificationCountChangedCommand extends Command<NotificationCountChangedCommand.NotificationCounterResult>
      implements CachedAction<NotificationCountChangedCommand.NotificationCounterResult> {

   private NotificationCountChangedCommand.NotificationCounterResult notificationCounterResult;

   private boolean restoreFromCache;

   public NotificationCountChangedCommand() {
      restoreFromCache = true;
   }

   public NotificationCountChangedCommand(int friendNotificationCount, int exclusiveNotificationCount, int badgeNotificationsCount) {
      notificationCounterResult = new NotificationCounterResult(friendNotificationCount, exclusiveNotificationCount, badgeNotificationsCount);
      restoreFromCache = false;
   }

   @Override
   protected void run(CommandCallback<NotificationCountChangedCommand.NotificationCounterResult> callback) throws Throwable {
      callback.onSuccess(notificationCounterResult);
   }

   public int getFriendNotificationCount() {
      return getResult().friendNotificationCount;
   }

   public int getExclusiveNotificationCount() {
      return getResult().exclusiveNotificationCount;
   }

   public int getBadgeNotificationsCount() {
      return getResult().badgeNotificationsCount;
   }

   @Override
   public NotificationCounterResult getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, NotificationCounterResult cache) {
      if (cache != null) {
         notificationCounterResult = cache;
      }
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(restoreFromCache)
            .build();
   }

   public static class NotificationCounterResult {
      private int friendNotificationCount;
      private int exclusiveNotificationCount;
      private int badgeNotificationsCount;

      public NotificationCounterResult(int friendNotificationCount, int exclusiveNotificationCount, int badgeNotificationsCount) {
         this.friendNotificationCount = friendNotificationCount;
         this.exclusiveNotificationCount = exclusiveNotificationCount;
         this.badgeNotificationsCount = badgeNotificationsCount;
      }

      public int getFriendNotificationCount() {
         return friendNotificationCount;
      }

      public int getExclusiveNotificationCount() {
         return exclusiveNotificationCount;
      }

      public int getBadgeNotificationsCount() {
         return badgeNotificationsCount;
      }
   }
}
