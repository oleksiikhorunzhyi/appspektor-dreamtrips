package com.worldventures.dreamtrips.social.service.users.search.storage

import com.worldventures.dreamtrips.social.service.users.base.storage.BaseUserStorage
import com.worldventures.dreamtrips.social.service.users.search.command.SearchedUsersStorageCommand

class SearchedUsersStorage : BaseUserStorage() {
   override fun getActionClass() = SearchedUsersStorageCommand::class.java
}
