package com.worldventures.wallet.domain.storage.disk;

import com.worldventures.core.repository.SnappyAction;
import com.worldventures.core.repository.SnappyResult;
import com.worldventures.core.storage.complex_objects.Optional;

abstract class BaseModelStorage implements ModelStorage {

   private final SnappyStorage storage;

   BaseModelStorage(SnappyStorage storage) {
      this.storage = storage;
   }

   @Override
   public void execute(SnappyAction action) {
      storage.execute(action);
   }

   @Override
   public <T> Optional<T> executeWithResult(SnappyResult<T> action) {
      return storage.executeWithResult(action);
   }

}