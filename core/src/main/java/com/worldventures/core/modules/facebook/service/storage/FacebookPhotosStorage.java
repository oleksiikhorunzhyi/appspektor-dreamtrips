package com.worldventures.core.modules.facebook.service.storage;

import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.janet.cache.storage.ClearableStorage;
import com.worldventures.janet.cache.storage.MemoryStorage;
import com.worldventures.core.modules.facebook.model.FacebookPhotosGraph;
import com.worldventures.core.modules.facebook.service.command.GetPhotosCommand;

public class FacebookPhotosStorage extends MemoryStorage<FacebookPhotosGraph>
      implements ActionStorage<FacebookPhotosGraph>, ClearableStorage {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetPhotosCommand.class;
   }
}

