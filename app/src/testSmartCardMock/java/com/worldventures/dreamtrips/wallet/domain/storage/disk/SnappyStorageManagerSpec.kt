package com.worldventures.dreamtrips.wallet.domain.storage.disk

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.snappydb.DB
import com.worldventures.dreamtrips.BaseSpec

class SnappyStorageManagerSpec : BaseSpec({

   describe("Storage migration") {

      context("Migration context") {
         val objectFromDB = Any()
         val versionKey = "version_TestKey"
         val fieldKey = "TestKey"

         it("should be called migration for existing version") {
            val db: DB = mock()
            val storage: ModelStorage = mock()
            val diskStorage: DiskStorage = TestDiskStorage(db)

            val storedVersion = 0
            val newVersion = 1

            whenever(db.exists(versionKey)).thenReturn(true)
            whenever(db.getInt(versionKey)).thenReturn(storedVersion)

            whenever(db.exists(fieldKey)).thenReturn(true)
            whenever(db.getObject(fieldKey, Any::class.java)).thenReturn(objectFromDB)

            whenever(storage.key).thenReturn(fieldKey)
            whenever(storage.version).thenReturn(newVersion)
            whenever(storage.migrate(db, storedVersion)).thenReturn(true)

            SnappyStorageManager(diskStorage, setOf(storage)).init()

            verify(storage, times(1)).migrate(db, storedVersion)
            verify(db, times(1)).put(versionKey, newVersion)
         }

         it("should not be called migration for existing version") {
            val db: DB = mock()
            val storage: ModelStorage = mock()
            val diskStorage: DiskStorage = TestDiskStorage(db)

            val storedVersion = 1
            val newVersion = 1

            whenever(db.exists(versionKey)).thenReturn(true)
            whenever(db.getInt(versionKey)).thenReturn(storedVersion)

            whenever(db.exists(fieldKey)).thenReturn(true)
            whenever(db.getObject(fieldKey, Any::class.java)).thenReturn(objectFromDB)

            whenever(storage.key).thenReturn(fieldKey)
            whenever(storage.version).thenReturn(newVersion)
            whenever(storage.migrate(db, storedVersion)).thenReturn(true)

            SnappyStorageManager(diskStorage, setOf(storage)).init()

            verify(storage, times(0)).migrate(db, storedVersion)
            verify(db, times(0)).put(versionKey, newVersion)
         }

         it("should be called migration for not existing version") {
            val db: DB = mock()
            val storage: ModelStorage = mock()
            val diskStorage: DiskStorage = TestDiskStorage(db)

            val oldVersion = 0
            val newVersion = 1

            whenever(db.exists(versionKey)).thenReturn(false)

            whenever(db.exists(fieldKey)).thenReturn(true)
            whenever(db.getObject(fieldKey, Any::class.java)).thenReturn(objectFromDB)

            whenever(storage.version).thenReturn(newVersion)
            whenever(storage.key).thenReturn(fieldKey)
            whenever(storage.migrate(db, oldVersion)).thenReturn(true)

            SnappyStorageManager(diskStorage, setOf(storage)).init()

            verify(storage, times(1)).migrate(db, oldVersion)
            verify(db, times(1)).put(versionKey, newVersion)
         }

      }

   }
})