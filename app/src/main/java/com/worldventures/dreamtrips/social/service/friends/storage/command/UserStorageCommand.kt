package com.worldventures.dreamtrips.social.service.friends.storage.command

import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.core.janet.cache.storage.PaginatedStorage
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.modules.common.list_storage.command.ListStorageCommand
import com.worldventures.dreamtrips.social.service.friends.storage.delegat.FriendStorageDelegate
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class UserStorageCommand(operation: FriendStorageDelegate.FriendListStorageOperation) : ListStorageCommand<User>(operation) {

   val isRefresh = operation.refresh

   override fun getCacheOptions(): CacheOptions {
      return ImmutableCacheOptions.builder()
            .params(CacheBundleImpl().apply { put(PaginatedStorage.BUNDLE_REFRESH, isRefresh) }).build()
   }
}
