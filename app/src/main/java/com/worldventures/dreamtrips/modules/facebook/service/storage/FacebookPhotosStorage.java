package com.worldventures.dreamtrips.modules.facebook.service.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.ClearableStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhotosGraph;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetPhotosCommand;

public class FacebookPhotosStorage extends MemoryStorage<FacebookPhotosGraph>
      implements ActionStorage<FacebookPhotosGraph>, ClearableStorage {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetPhotosCommand.class;
   }
}

