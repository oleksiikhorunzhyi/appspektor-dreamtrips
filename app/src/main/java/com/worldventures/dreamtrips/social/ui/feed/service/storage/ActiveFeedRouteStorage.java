package com.worldventures.dreamtrips.social.ui.feed.service.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ActiveFeedRouteCommand;

public class ActiveFeedRouteStorage extends MemoryStorage<Route> implements ActionStorage<Route> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return ActiveFeedRouteCommand.class;
   }
}
