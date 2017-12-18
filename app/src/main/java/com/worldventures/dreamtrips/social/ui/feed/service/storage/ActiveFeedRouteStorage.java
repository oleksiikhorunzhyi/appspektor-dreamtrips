package com.worldventures.dreamtrips.social.ui.feed.service.storage;

import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ActiveFeedRouteCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedCellListWidthProvider;

public class ActiveFeedRouteStorage extends MemoryStorage<FeedCellListWidthProvider.FeedType> implements ActionStorage<FeedCellListWidthProvider.FeedType> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return ActiveFeedRouteCommand.class;
   }
}
