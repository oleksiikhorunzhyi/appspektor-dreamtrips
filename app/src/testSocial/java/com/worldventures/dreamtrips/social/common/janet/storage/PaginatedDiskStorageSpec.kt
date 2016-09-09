package com.worldventures.dreamtrips.social.common.janet.storage

import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedDiskStorage
import rx.functions.Action1
import rx.functions.Func0

class PaginatedDiskStorageSpec() : PaginatedStorageSpec({
   FakeDiskStorage()
}) {
   class FakeDiskStorage : PaginatedDiskStorage<Any>() {
      val list: MutableList<Any> = mutableListOf()

      override fun getSaveAction(): Action1<MutableList<Any>> {
         return Action1 {
            list.clear()
            list.addAll(it)
         }
      }

      override fun getRestoreAction(): Func0<MutableList<Any>> {
         return Func0 { list }
      }
   }
}
