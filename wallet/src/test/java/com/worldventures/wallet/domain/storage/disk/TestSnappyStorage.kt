package com.worldventures.wallet.domain.storage.disk

import com.snappydb.DB
import com.worldventures.core.repository.SnappyAction
import com.worldventures.core.repository.SnappyResult
import com.worldventures.core.storage.complex_objects.Optional

class TestSnappyStorage(private val db: DB) : SnappyStorage {

   override fun <T : Any> executeWithResult(action: SnappyResult<T>): Optional<T> {
      return Optional.of(action.call(db))
   }

   override fun execute(action: SnappyAction) {
      action.call(db)
   }

}