package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.repository.SnappyAction;
import com.worldventures.dreamtrips.core.repository.SnappyResult;

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