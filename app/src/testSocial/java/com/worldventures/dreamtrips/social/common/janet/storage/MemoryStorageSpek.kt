package com.worldventures.dreamtrips.social.common.janet.storage

import com.worldventures.janet.cache.storage.MemoryStorage
import com.worldventures.dreamtrips.BaseSpec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class MemoryStorageSpek : BaseSpec({

   describe("Memory storage") {

      it("should return the same object") {
         val storage = MemoryStorage<Any>()
         val testObject = Any()

         storage.save(null, testObject)
         assertTrue(testObject == storage.get(null))
      }
   }
})
