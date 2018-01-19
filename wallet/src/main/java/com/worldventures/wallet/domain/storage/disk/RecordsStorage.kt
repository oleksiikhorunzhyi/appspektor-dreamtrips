package com.worldventures.wallet.domain.storage.disk

import com.worldventures.wallet.domain.entity.record.Record

interface RecordsStorage : ModelStorage {

   fun saveRecords(items: List<Record>?)

   fun readRecords(): List<Record>

   fun deleteAllRecords()

   // default card
   fun saveDefaultRecordId(id: String?)

   fun readDefaultRecordId(): String?

   fun deleteDefaultRecordId()

   // offline mode
   fun saveOfflineModeState(enabled: Boolean)

   fun readOfflineModeState(): Boolean
}
