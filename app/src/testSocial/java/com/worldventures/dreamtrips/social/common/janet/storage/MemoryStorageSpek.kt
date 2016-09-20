package com.worldventures.dreamtrips.social.common.janet.storage

import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage
import org.jetbrains.spek.api.Spek
import kotlin.test.assertTrue

class MemoryStorageSpek: Spek({

   val storage = MemoryStorage<Any>()
   val testObject = Any()

   describe("Memory storage") {
      it ("should return the same object") {
         storage.save(null, testObject)
         assertTrue(testObject == storage.get(null))
      }
   }
})
