package com.worldventures.core.modules.facebook.service.storage;

import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.ClearableStorage;
import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.core.modules.facebook.model.FacebookAlbumsGraph;
import com.worldventures.core.modules.facebook.service.command.GetAlbumsCommand;

public class FacebookAlbumsStorage extends MemoryStorage<FacebookAlbumsGraph>
      implements ActionStorage<FacebookAlbumsGraph>, ClearableStorage {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetAlbumsCommand.class;
   }
}
