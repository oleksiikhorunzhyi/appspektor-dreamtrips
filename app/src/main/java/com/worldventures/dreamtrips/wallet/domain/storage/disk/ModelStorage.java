package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.repository.SnappyAction;
import com.worldventures.dreamtrips.core.repository.SnappyResult;

public abstract class ModelStorage {

   private DiskStorage storage;

   protected void bindStorage(DiskStorage storage) {
      this.storage = storage;
   }

   protected abstract boolean migrate(DB db, int oldVersion) throws SnappydbException;

   protected abstract String getKey();

   protected abstract int getVersion();

   protected void execute(SnappyAction action) {
      storage.execute(action);
   }

   protected <T> Optional<T> executeWithResult(SnappyResult<T> action) {
      return storage.executeWithResult(action);
   }
}
