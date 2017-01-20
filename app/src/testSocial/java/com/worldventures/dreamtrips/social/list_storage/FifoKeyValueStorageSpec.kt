package com.worldventures.dreamtrips.social.list_storage

import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl
import com.worldventures.dreamtrips.core.janet.cache.storage.FifoKeyValueStorage
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FifoKeyValueStorageSpec : BaseSpec({

   describe("FIFO key value storage") {

      it ("should return same content when querying storage") {
         val storage = FifoKeyValueStorage<String, Int>()
         val testObject = getTestStorageObject()
         storage.save(testObject.key, testObject.value)
         assertEquals(storage.get(testObject.key), testObject.value)
         assertEquals(storage.size, 1)
      }

      it ("should start erasing values when size is exceeded") {
         val storage = FifoKeyValueStorage<String, Int>()
         val maxStorageSize = 2
         storage.setMaxSize(maxStorageSize)
         val testObject1 = getTestStorageObject()
         storage.save(testObject1.key, testObject1.value)
         val testObject2 = getTestStorageObject()
         storage.save(testObject2.key, testObject2.value)
         val testObject3 = getTestStorageObject()
         storage.save(testObject3.key, testObject3.value)

         assertEquals(storage.size, maxStorageSize)
         assertNull(storage.get(testObject1.key))
         assertEquals(storage.get(testObject2.key), testObject2.value)
         assertEquals(storage.get(testObject3.key), testObject3.value)
      }

      it ("should correctly erase content of the storage") {
         val storage = FifoKeyValueStorage<String, Int>()
         val testObject = getTestStorageObject()
         storage.save(testObject.key, testObject.value)
         storage.clearMemory()
         assertNull(storage.get(testObject.key))
      }
   }
}) {
   companion object {
      var testValuesCount: Int = 0

      fun getCacheBundle(key: String): CacheBundle {
         val bundle = CacheBundleImpl()
         bundle.put(FifoKeyValueStorage.BUNDLE_KEY_VALUE, key)
         return bundle
      }

      fun getTestStorageObject(): TestStorageObject {
         testValuesCount++
         return TestStorageObject(getCacheBundle("" + testValuesCount), testValuesCount)
      }
   }
}

data class TestStorageObject(val key: CacheBundle, val value: Int)