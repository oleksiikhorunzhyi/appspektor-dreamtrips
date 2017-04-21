package com.worldventures.dreamtrips.wallet.domain.storage.disk

import com.snappydb.DB
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.core.repository.SnappyAction
import com.worldventures.dreamtrips.core.repository.SnappyResult

class TestDiskStorage(private val db: DB) : DiskStorage {

   override fun <T : Any> executeWithResult(action: SnappyResult<T>): Optional<T> {
      return Optional.of(action.call(db))
   }

   override fun execute(action: SnappyAction) {
      action.call(db)
   }
}