package com.worldventures.dreamtrips.modules.facebook.service.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.ClearableStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookAlbumsGraph;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;

public class FacebookAlbumsStorage extends MemoryStorage<FacebookAlbumsGraph>
      implements ActionStorage<FacebookAlbumsGraph>, ClearableStorage {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetAlbumsCommand.class;
   }
}

