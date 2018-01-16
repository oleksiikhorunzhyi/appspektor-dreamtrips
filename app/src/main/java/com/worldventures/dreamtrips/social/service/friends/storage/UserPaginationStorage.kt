package com.worldventures.dreamtrips.social.service.friends.storage

import com.worldventures.core.janet.cache.CacheBundle
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.janet.cache.storage.ClearableStorage
import com.worldventures.core.janet.cache.storage.PaginatedStorage
import com.worldventures.core.janet.cache.storage.PaginatedStorage.BUNDLE_REFRESH
import com.worldventures.dreamtrips.social.service.friends.interactor.command.UserPaginationCommand

class UserPaginationStorage : PaginatedStorage<UserPaginationCommand.PaginationCachedModel>, ClearableStorage
      , ActionStorage<UserPaginationCommand.PaginationCachedModel> {

   private var userPaginationCachedModel: UserPaginationCommand.PaginationCachedModel? = null

   override fun save(params: CacheBundle?, data: UserPaginationCommand.PaginationCachedModel?) {
      userPaginationCachedModel = if (params != null && params.get(BUNDLE_REFRESH, false)) {
         UserPaginationCommand.PaginationCachedModel(1, true)
      } else data
   }

   override fun get(action: CacheBundle?): UserPaginationCommand.PaginationCachedModel? {
      if (action != null && action.get(BUNDLE_REFRESH, false)) {
         userPaginationCachedModel = UserPaginationCommand
               .PaginationCachedModel(1, true)
      }
      return userPaginationCachedModel
   }

   override fun clearMemory() {
      userPaginationCachedModel = null
   }

   override fun getActionClass() = UserPaginationCommand::class.java
}
