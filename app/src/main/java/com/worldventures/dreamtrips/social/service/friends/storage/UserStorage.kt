package com.worldventures.dreamtrips.social.service.friends.storage

import com.worldventures.core.janet.cache.CacheBundle
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.janet.cache.storage.PaginatedStorage
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage
import com.worldventures.dreamtrips.social.service.friends.storage.command.UserStorageCommand

class UserStorage : PaginatedMemoryStorage<User>(), ActionStorage<List<User>> {

   override fun get(params: CacheBundle?): MutableList<User> {
      if (params != null && params.get(PaginatedStorage.BUNDLE_REFRESH, false)) clearMemory()
      return super.get(params)
   }

   override fun getActionClass() = UserStorageCommand::class.java
}
