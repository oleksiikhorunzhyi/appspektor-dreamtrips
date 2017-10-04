package com.worldventures.dreamtrips.social.ui.friends.storage;

import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetRequestsCommand;

import java.util.List;

public class RequestsStorage extends PaginatedMemoryStorage<User> implements ActionStorage<List<User>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetRequestsCommand.class;
   }
}
