package com.worldventures.janet.cache.storage;

public interface ClearableStorage {
   /**
    * Will be called from @see  ClearStoragesCommand to clear memory cache of storages on logout
    */
   void clearMemory();
}
