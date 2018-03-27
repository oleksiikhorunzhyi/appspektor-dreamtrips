package com.worldventures.dreamtrips.social.service.users.search.storage

import com.worldventures.dreamtrips.social.service.users.base.storage.BaseUserPaginationStorage
import com.worldventures.dreamtrips.social.service.users.search.command.SearchUsersPaginationCommand

class SearchUsersPaginationStorage : BaseUserPaginationStorage() {
   override fun getActionClass() = SearchUsersPaginationCommand::class.java
}
