package com.worldventures.dreamtrips.social.service.users.friend.storage

import com.worldventures.core.janet.cache.CacheBundle
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.janet.cache.storage.PaginatedStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.FifoKeyValueStorage
import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserStorageCommand.UsersCachedModel
import com.worldventures.dreamtrips.social.service.users.friend.command.MutualFriendsStorageCommand

class MutualFriendsStorage : FifoKeyValueStorage<String, UsersCachedModel>(), PaginatedStorage<UsersCachedModel>,
      ActionStorage<UsersCachedModel> {

   override fun get(params: CacheBundle?): UsersCachedModel {
      if (refresh(params)) save(params, UsersCachedModel(mutableListOf(), true))
      return super.get(params)
   }

   fun refresh(params: CacheBundle?) = params != null && params.get(PaginatedStorage.BUNDLE_REFRESH, true)

   override fun getActionClass() = MutualFriendsStorageCommand::class.java

}
