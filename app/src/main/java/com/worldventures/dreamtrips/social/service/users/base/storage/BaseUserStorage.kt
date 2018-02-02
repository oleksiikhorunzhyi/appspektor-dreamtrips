package com.worldventures.dreamtrips.social.service.users.base.storage

import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserStorageCommand
import com.worldventures.janet.cache.CacheBundle
import com.worldventures.janet.cache.storage.ActionStorage
import com.worldventures.janet.cache.storage.ClearableStorage
import com.worldventures.janet.cache.storage.PaginatedStorage
import com.worldventures.janet.cache.storage.PaginatedStorage.BUNDLE_REFRESH

abstract class BaseUserStorage : PaginatedStorage<BaseUserStorageCommand.UsersCachedModel>,
      ActionStorage<BaseUserStorageCommand.UsersCachedModel>, ClearableStorage {

   private var cash: BaseUserStorageCommand.UsersCachedModel? = null

   override fun save(params: CacheBundle?, data: BaseUserStorageCommand.UsersCachedModel?) {
      cash = data
   }

   override fun get(action: CacheBundle?): BaseUserStorageCommand.UsersCachedModel? {
      if (action != null && action.get(BUNDLE_REFRESH, false)) clearMemory()
      return cash
   }

   override fun clearMemory() {
      cash?.items?.clear()
      cash = null
   }
}
