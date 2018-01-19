package com.worldventures.dreamtrips.social.service.users.friend.storage

import com.worldventures.dreamtrips.social.service.users.base.storage.BaseUserPaginationStorage
import com.worldventures.dreamtrips.social.service.users.friend.command.GetFriendsPaginationCommand

class GetFriendsPaginationStorage : BaseUserPaginationStorage() {
   override fun getActionClass() = GetFriendsPaginationCommand::class.java
}
