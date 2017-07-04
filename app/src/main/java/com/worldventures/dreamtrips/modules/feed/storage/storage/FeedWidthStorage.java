package com.worldventures.dreamtrips.modules.feed.storage.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.feed.model.util.FeedListWidth;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedListWidthCommand;

public class FeedWidthStorage extends MemoryStorage<FeedListWidth> implements ActionStorage<FeedListWidth> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return FeedListWidthCommand.class;
   }
}
