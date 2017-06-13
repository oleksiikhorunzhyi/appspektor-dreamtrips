package com.worldventures.dreamtrips.wallet.domain.storage.disk

import com.snappydb.DB
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.core.repository.SnappyAction
import com.worldventures.dreamtrips.core.repository.SnappyResult
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record

class TestRecordsStorage(offlineModeEnabled: Boolean = false) : RecordsStorage {

   private val records: MutableList<Record> = mutableListOf()
   private var defaultRecordId: String? = null
   private var offlineModeState: Boolean = offlineModeEnabled

   override fun migrate(db: DB?, oldVersion: Int) = true

   override fun getKey() = "TestRecordsStorage"

   override fun getVersion() = 0

   override fun execute(action: SnappyAction?) {}

   override fun <T : Any?> executeWithResult(action: SnappyResult<T>?): Optional<T> = Optional.absent<T>()

   override fun saveRecords(items: MutableList<Record>?) {
      records.clear()
      items?.let { records.addAll(items) }
   }

   override fun readRecords(): MutableList<Record> {
      return records
   }

   override fun deleteAllRecords() {
      records.clear()
   }

   override fun saveDefaultRecordId(id: String?) {
      defaultRecordId = id
   }

   override fun readDefaultRecordId(): String? = defaultRecordId

   override fun deleteDefaultRecordId() {
      defaultRecordId = null
   }

   override fun saveOfflineModeState(enabled: Boolean) {
      offlineModeState = enabled
   }

   override fun readOfflineModeState() = offlineModeState

}