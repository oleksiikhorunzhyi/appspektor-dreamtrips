package com.worldventures.dreamtrips.social.common.janet.storage

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedStorage
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

abstract class PaginatedStorageSpec(provideStorage:() -> PaginatedStorage<List<Any>>): Spek({

   var storage = provideStorage()

   describe("Paginated storage") {

      beforeEach {
         storage = provideStorage()
      }

      it ("should contain combined list from first and second page") {
         storage.save(null, firstPageList)
         storage.save(null, secondPageList)
         val result = storage.get(getDefaultParams())
         Assertions.assertThat(result.size).isEqualTo(firstPageList.size + secondPageList.size)
         Assertions.assertThat(result[0]).isEqualTo(firstPageList[0])
         val expectedFirstPageStartingIndex = firstPageList.size;
         Assertions.assertThat(result[expectedFirstPageStartingIndex]).isEqualTo(secondPageList[0])
      }

      it ("should reset storage when getting refresh params") {
         storage.save(getDefaultParams(), firstPageList)
         storage.save(getDefaultParams(), secondPageList)
         storage.save(getParamsForRefresh(), secondPageList)
         val result = storage.get(getDefaultParams())
         assertEquals(result[0], secondPageList[0])
      }

      it ("should work correctly with nullable params") {
         storage.save(null, firstPageList)
         val result = storage.get(null)
         assertEquals(result[0], firstPageList[0])
      }
   }
}) {
   companion object {
      val firstPageList = listOf(1, 2)
      val secondPageList = listOf(3, 4)

      fun getDefaultParams(): CacheBundle {
         return CacheBundleImpl();
      }

      fun getParamsForRefresh(): CacheBundle {
         val cacheBundle = CacheBundleImpl()
         cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, true)
         return cacheBundle
      }
   }
}
