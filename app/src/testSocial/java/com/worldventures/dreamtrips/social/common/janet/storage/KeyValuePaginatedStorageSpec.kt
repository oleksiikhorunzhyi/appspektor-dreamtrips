package com.worldventures.dreamtrips.social.common.janet.storage

import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl
import com.worldventures.dreamtrips.core.janet.cache.storage.KeyValuePaginatedMemoryStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.KeyValueStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.Storage
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class KeyValuePaginatedStorageSpec : BaseSpec({

   describe("Key value paginated memory storage")  {

      context("when using pagination logic") {
         var storage : Storage<List<Any>> = KeyValuePaginatedMemoryStorage()

         beforeEachTest {
            storage = KeyValuePaginatedMemoryStorage()
         }

         it("should contain combined list from first and second page") {
            storage.save(getDefaultParams(), firstPageList)
            storage.save(getDefaultParams(), secondPageList)
            val result = storage.get(getDefaultParams())
            Assertions.assertThat(result.size).isEqualTo(firstPageList.size + secondPageList.size)
            Assertions.assertThat(result[0]).isEqualTo(firstPageList[0])
            val expectedFirstPageStartingIndex = firstPageList.size;
            Assertions.assertThat(result[expectedFirstPageStartingIndex]).isEqualTo(secondPageList[0])
         }

         it("should reset storage when getting refresh params") {
            storage.save(getDefaultParams(), firstPageList)
            storage.save(getDefaultParams(), secondPageList)
            storage.save(getParamsForRefresh(), secondPageList)
            val result = storage.get(getDefaultParams())
            assertEquals(result[0], secondPageList[0])
         }
      }

      context("when using key value logic") {

         var storage : Storage<List<Any>> = KeyValuePaginatedMemoryStorage()

         it ("should contain different data for different keys") {
            val firstKey = "keyA";
            val firstKeyBundle = CacheBundleImpl()
            val firstKeyData = listOf(1)
            firstKeyBundle.put(KeyValueStorage.BUNDLE_KEY_VALUE, firstKey)
            storage.save(firstKeyBundle, firstKeyData)

            val secondKey = "keyB";
            val secondKeyBundle = CacheBundleImpl()
            val secondKeyData = listOf(2)
            secondKeyBundle.put(KeyValueStorage.BUNDLE_KEY_VALUE, secondKey)
            storage.save(secondKeyBundle, secondKeyData)

            assertEquals(storage.get(firstKeyBundle), firstKeyData)
            assertEquals(storage.get(secondKeyBundle), secondKeyData)
            assertNotEquals(storage.get(firstKeyBundle), storage.get(secondKeyBundle))
         }
      }
   }

}) {

   companion object {
      val firstPageList = listOf(1, 2)
      val secondPageList = listOf(3, 4)
      val TEST_KEY = "test"

      fun getDefaultParams(): CacheBundle {
         val cacheBundle = CacheBundleImpl()
         cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, false)
         cacheBundle.put(KeyValueStorage.BUNDLE_KEY_VALUE, TEST_KEY)
         return cacheBundle
      }

      fun getParamsForRefresh(): CacheBundle {
         val cacheBundle = CacheBundleImpl()
         cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, true)
         cacheBundle.put(KeyValueStorage.BUNDLE_KEY_VALUE, TEST_KEY)
         return cacheBundle
      }
   }
}