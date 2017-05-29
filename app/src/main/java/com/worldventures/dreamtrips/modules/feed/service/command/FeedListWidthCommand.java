package com.worldventures.dreamtrips.modules.feed.service.command;

import android.content.Context;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.feed.model.util.FeedListWidth;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FeedListWidthCommand extends Command<FeedListWidth> implements CachedAction<FeedListWidth>, InjectableAction {
   @Inject Context context;

   private int width;
   private FeedListWidth cachedSize;

   public FeedListWidthCommand(int width) {
      this.width = width;
   }

   @Override
   protected void run(CommandCallback<FeedListWidth> callback) throws Throwable {
      FeedListWidth feedListWidth = ViewUtils.isLandscapeOrientation(context) ? FeedListWidth.forLandscape(width) :
            FeedListWidth.forPortrait(width);
      if (cachedSize != null) {
         if (cachedSize.getWidthInPortrait() != 0) {
            if (feedListWidth.getWidthInPortrait() != 0) {
               feedListWidth.setWidthInPortrait(Math.min(feedListWidth.getWidthInPortrait(), cachedSize.getWidthInPortrait()));
            } else {
               feedListWidth.setWidthInPortrait(cachedSize.getWidthInPortrait());
            }
         }
         if (cachedSize.getWidthInLandscape() != 0) {
            feedListWidth.setWidthInLandscape(Math.max(feedListWidth.getWidthInLandscape(), cachedSize.getWidthInLandscape()));
         }
      }
      callback.onSuccess(feedListWidth);
   }

   @Override
   public FeedListWidth getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, FeedListWidth cache) {
      cachedSize = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }
}
