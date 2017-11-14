package com.worldventures.wallet.domain.storage.disk;

import com.worldventures.core.repository.SnappyAction;
import com.worldventures.core.repository.SnappyResult;
import com.worldventures.core.storage.complex_objects.Optional;

public interface SnappyStorage {

   void execute(SnappyAction action);

   <T> Optional<T> executeWithResult(SnappyResult<T> action);

}