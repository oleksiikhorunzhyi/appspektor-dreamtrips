package com.worldventures.dreamtrips.social.service.friends.storage

import com.worldventures.core.janet.cache.CacheBundle
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.janet.cache.storage.PaginatedStorage
import com.worldventures.core.janet.cache.storage.PaginatedStorage.BUNDLE_REFRESH
import com.worldventures.dreamtrips.social.service.friends.storage.command.SortRequestsStorageCommand

class IncomingRequestsCountStorage : PaginatedStorage<Int>, ActionStorage<Int> {

   private var incomingCount = 0

   override fun save(params: CacheBundle?, data: Int?) {
      incomingCount = if (params != null && params.get(BUNDLE_REFRESH, false)) 0
      else data ?: 0
   }

   override fun get(action: CacheBundle?): Int {
      if (action != null && action.get(BUNDLE_REFRESH, false)) incomingCount = 0
      return incomingCount
   }

   override fun getActionClass() = SortRequestsStorageCommand::class.java
}
