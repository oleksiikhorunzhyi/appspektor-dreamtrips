package com.worldventures.dreamtrips.social.service.users.base.storage

import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserPaginationCommand.PaginationCachedModel
import com.worldventures.janet.cache.CacheBundle
import com.worldventures.janet.cache.storage.ActionStorage
import com.worldventures.janet.cache.storage.ClearableStorage
import com.worldventures.janet.cache.storage.PaginatedStorage
import com.worldventures.janet.cache.storage.PaginatedStorage.BUNDLE_REFRESH

abstract class BaseUserPaginationStorage : PaginatedStorage<PaginationCachedModel>, ActionStorage<PaginationCachedModel>,
      ClearableStorage {

   private var cachedModel: PaginationCachedModel? = null

   override fun save(params: CacheBundle?, data: PaginationCachedModel?) {
      cachedModel = data
   }

   override fun get(action: CacheBundle?): PaginationCachedModel? {
      if (isRefresh(action)) {
         clearMemory()
      }
      return cachedModel
   }

   private fun isRefresh(params: CacheBundle?) = params != null && params.get(BUNDLE_REFRESH, true)

   override fun clearMemory() {
      cachedModel = null
   }

}
