package com.worldventures.dreamtrips.social.service.users.friend.storage

import com.worldventures.dreamtrips.core.janet.cache.storage.FifoKeyValueStorage
import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserPaginationCommand.PaginationCachedModel
import com.worldventures.dreamtrips.social.service.users.friend.command.GetMutualFriendsPaginationCommand
import com.worldventures.janet.cache.CacheBundle
import com.worldventures.janet.cache.storage.ActionStorage
import com.worldventures.janet.cache.storage.PaginatedStorage
import com.worldventures.janet.cache.storage.PaginatedStorage.BUNDLE_REFRESH

class GetMutualFriendsPaginationStorage : FifoKeyValueStorage<String, PaginationCachedModel>(),
      PaginatedStorage<PaginationCachedModel>, ActionStorage<PaginationCachedModel> {

   override fun get(params: CacheBundle?): PaginationCachedModel {
      if (isRefresh(params)) save(params, PaginationCachedModel(1))
      return super.get(params)
   }

   fun isRefresh(params: CacheBundle?) = params != null && params.get(BUNDLE_REFRESH, true)

   override fun getActionClass() = GetMutualFriendsPaginationCommand::class.java
}
