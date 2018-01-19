package com.worldventures.wallet.domain.storage.disk

import com.snappydb.DB
import com.snappydb.SnappydbException
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.domain.storage.SnappyCrypter

private const val RECORDS_LIST = "WALLET_CARDS_LIST"
private const val DEFAULT_RECORD_ID = "DEFAULT_WALLET_CARD_ID"
private const val OFFLINE_MODE_STATE = "OFFLINE_MODE_STATE"

class PersistentRecordsStorage(storage: SnappyStorage, snappyCrypter: SnappyCrypter) : CryptedModelStorage(storage, snappyCrypter), RecordsStorage {

   @Throws(SnappydbException::class)
   override fun migrate(db: DB, oldVersion: Int): Boolean {
      if (oldVersion == 0) {
         db.del(RECORDS_LIST)
         db.del(DEFAULT_RECORD_ID)
      }
      return true
   }

   override fun getKey() = RECORDS_LIST

   override fun getVersion() = 1

   // records
   override fun saveRecords(items: List<Record>?) {
      putEncrypted(RECORDS_LIST, items)
   }

   override fun readRecords(): List<Record> = getEncryptedList(RECORDS_LIST)

   override fun deleteAllRecords() {
      execute { db -> db.del(RECORDS_LIST) }
   }

   // default record id
   override fun saveDefaultRecordId(id: String?) {
      putEncrypted(DEFAULT_RECORD_ID, id)
   }

   override fun readDefaultRecordId(): String? =
         getEncrypted(DEFAULT_RECORD_ID, String::class.java)

   override fun deleteDefaultRecordId() {
      execute { db -> db.del(DEFAULT_RECORD_ID) }
   }

   // offline mode
   override fun saveOfflineModeState(enabled: Boolean) {
      put(OFFLINE_MODE_STATE, enabled)
   }

   override fun readOfflineModeState(): Boolean =
         getOrDefault(OFFLINE_MODE_STATE, Boolean::class.javaPrimitiveType, false)
}
