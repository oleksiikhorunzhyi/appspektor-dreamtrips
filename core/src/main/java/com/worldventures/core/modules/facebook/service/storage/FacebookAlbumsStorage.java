package com.worldventures.core.modules.facebook.service.storage;

import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.janet.cache.storage.ClearableStorage;
import com.worldventures.janet.cache.storage.MemoryStorage;
import com.worldventures.core.modules.facebook.model.FacebookAlbumsGraph;
import com.worldventures.core.modules.facebook.service.command.GetAlbumsCommand;

public class FacebookAlbumsStorage extends MemoryStorage<FacebookAlbumsGraph>
      implements ActionStorage<FacebookAlbumsGraph>, ClearableStorage {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetAlbumsCommand.class;
   }
}

