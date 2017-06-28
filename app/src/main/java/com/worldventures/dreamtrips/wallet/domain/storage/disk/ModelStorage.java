package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.repository.SnappyAction;
import com.worldventures.dreamtrips.core.repository.SnappyResult;

public interface ModelStorage {

   boolean migrate(DB db, int oldVersion) throws SnappydbException;

   String getKey();

   int getVersion();

   void execute(SnappyAction action);

   <T> Optional<T> executeWithResult(SnappyResult<T> action);

}