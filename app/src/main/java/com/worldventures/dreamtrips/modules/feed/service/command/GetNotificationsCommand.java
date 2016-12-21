package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.feed.GetFeedNotificationsHttpAction;
import com.worldventures.dreamtrips.api.feed.ImmutableGetFeedNotificationsHttpAction;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.storage.NotificationsStorage;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetNotificationsCommand extends BaseGetFeedCommand<GetFeedNotificationsHttpAction>
      implements CachedAction<List<FeedItem>> {

   private List<FeedItem> cachedData;

   private boolean refresh;

   public GetNotificationsCommand(boolean refresh) {
      this.refresh = refresh;
   }

   @Override
   protected void run(CommandCallback<List<FeedItem>> callback) throws Throwable {
      if (!refresh && cachedData != null && cachedData.size() > 0) {
         before = cachedData.get(cachedData.size() - 1).getCreatedAt();
      }
      if (cachedData != null && !cachedData.isEmpty()) callback.onProgress(0);
      //
      super.run(callback);
   }

   @Override
   protected void itemsLoaded(CommandCallback<List<FeedItem>> callback, List<FeedItem> items) {
      clearCachedDataIfNeeded();
      super.itemsLoaded(callback, items);
   }

   private void clearCachedDataIfNeeded() {
      if (refresh) cachedData = null;
   }

   public List<FeedItem> getItems() {
      List<FeedItem> items = new ArrayList<>();
      if (cachedData != null) items.addAll(cachedData);
      if (getResult() != null) items.addAll(getResult());
      return items;
   }

   @Override
   public List<FeedItem> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<FeedItem> cache) {
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
   protected Class<GetFeedNotificationsHttpAction> provideHttpActionClass() {
      return GetFeedNotificationsHttpAction.class;
   }

   @Override
   protected GetFeedNotificationsHttpAction provideRequest() {
      GetFeedNotificationsHttpAction.Params params
            = ImmutableGetFeedNotificationsHttpAction.Params.builder()
            .pageSize(TIMELINE_LIMIT)
            .before(before)
            .build();
      return new GetFeedNotificationsHttpAction(params);
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
