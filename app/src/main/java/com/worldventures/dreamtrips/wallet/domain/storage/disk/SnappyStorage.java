package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.repository.SnappyAction;
import com.worldventures.dreamtrips.core.repository.SnappyResult;

public interface SnappyStorage {

   void execute(SnappyAction action);

   <T> Optional<T> executeWithResult(SnappyResult<T> action);

}