package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.core.repository.SnappyAction;
import com.worldventures.core.repository.SnappyResult;
import com.worldventures.core.storage.complex_objects.Optional;

public interface ModelStorage {

   boolean migrate(DB db, int oldVersion) throws SnappydbException;

   String getKey();

   int getVersion();

   void execute(SnappyAction action);

   <T> Optional<T> executeWithResult(SnappyResult<T> action);

}