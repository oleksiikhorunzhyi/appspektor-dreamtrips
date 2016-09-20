package com.worldventures.dreamtrips.core.janet.cache.storage;

import com.worldventures.dreamtrips.modules.common.api.janet.command.ClearStoragesCommand;

public interface ClearableStorage {
   /**
    * Will be called from @see {@link ClearStoragesCommand} to clear memory cache of storages on logout
    */
   void clearMemory();
}
