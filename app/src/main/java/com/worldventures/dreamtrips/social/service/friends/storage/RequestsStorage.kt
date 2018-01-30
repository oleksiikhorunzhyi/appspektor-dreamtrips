package com.worldventures.dreamtrips.social.service.friends.storage

import com.worldventures.janet.cache.storage.ActionStorage
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetRequestsCommand

class RequestsStorage : PaginatedMemoryStorage<User>(), ActionStorage<List<User>> {
   override fun getActionClass() = GetRequestsCommand::class.java
}
