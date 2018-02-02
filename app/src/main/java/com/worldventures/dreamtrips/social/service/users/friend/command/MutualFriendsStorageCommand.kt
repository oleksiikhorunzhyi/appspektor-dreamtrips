package com.worldventures.dreamtrips.social.service.users.friend.command

import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserStorageCommand
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.storage.KeyValueStorage
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class MutualFriendsStorageCommand(private val storageKey: String, operation: BaseUserStorageOperation)
   : BaseUserStorageCommand(operation) {

   override fun getCacheOptions(): CacheOptions {
      val cashedOptions = super.getCacheOptions()
      cashedOptions.params?.put(KeyValueStorage.BUNDLE_KEY_VALUE, storageKey)
      return cashedOptions
   }

}
