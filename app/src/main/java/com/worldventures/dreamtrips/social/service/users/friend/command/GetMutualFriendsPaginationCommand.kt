package com.worldventures.dreamtrips.social.service.users.friend.command

import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.storage.KeyValueStorage
import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserPaginationCommand
import com.worldventures.dreamtrips.social.service.users.base.command.GetUsersCommand
import io.techery.janet.command.annotations.CommandAction
import rx.Observable

@CommandAction
class GetMutualFriendsPaginationCommand(private val storageKey: String, refresh: Boolean, getUserOperation: (page: Int, perPage: Int) -> Observable<out GetUsersCommand>)
   : BaseUserPaginationCommand(refresh, getUserOperation) {

   override fun getCacheOptions(): CacheOptions {
      val option = super.getCacheOptions()
      option.params()?.put(KeyValueStorage.BUNDLE_KEY_VALUE, storageKey)
      return option
   }
}
