package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.api.GetNotificationFeedHttpAction;
import com.worldventures.dreamtrips.modules.feed.service.storage.NotificationsStorage;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetNotificationsCommand extends BaseGetFeedCommand<GetNotificationFeedHttpAction>
      implements CachedAction<List<FeedItem<FeedEntity>>> {

   private List<FeedItem<FeedEntity>> cachedData;

   private boolean refresh;

   public GetNotificationsCommand(boolean refresh) {
      this.refresh = refresh;
   }

   @Override
   protected void run(CommandCallback<List<FeedItem<FeedEntity>>> callback) throws Throwable {
      if (!refresh && cachedData != null && cachedData.size() > 0) {
         before = getBeforeDateString(cachedData.get(cachedData.size() - 1).getCreatedAt());
      }
      if (cachedData != null && !cachedData.isEmpty()) callback.onProgress(0);
      //
      super.run(callback);
   }

   @Override
   protected void itemsLoaded(CommandCallback<List<FeedItem<FeedEntity>>> callback, List<FeedItem<FeedEntity>> items) {
      clearCachedDataIfNeeded();
      super.itemsLoaded(callback, items);
   }

   private void clearCachedDataIfNeeded() {
      if (refresh) cachedData = null;
   }

   public List<FeedItem<FeedEntity>> getItems() {
      List<FeedItem<FeedEntity>> items = new ArrayList<>();
      if (cachedData != null) items.addAll(cachedData);
      if (getResult() != null) items.addAll(getResult());
      return items;
   }

   @Override
   public List<FeedItem<FeedEntity>> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<FeedItem<FeedEntity>> cache) {
      cachedData = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(NotificationsStorage.REFRESH, refresh);
      return ImmutableCacheOptions
            .builder()
            .params(cacheBundle)
            .build();
   }

   @Override
   protected Class<GetNotificationFeedHttpAction> provideHttpActionClass() {
      return GetNotificationFeedHttpAction.class;
   }

   @Override
   protected GetNotificationFeedHttpAction provideRequest() {
      return new GetNotificationFeedHttpAction(TIMELINE_LIMIT, before);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_notifications;
   }

   public static GetNotificationsCommand refresh() {
      return new GetNotificationsCommand(true);
   }

   public static GetNotificationsCommand loadMore() {
      return new GetNotificationsCommand(false);
   }
}
