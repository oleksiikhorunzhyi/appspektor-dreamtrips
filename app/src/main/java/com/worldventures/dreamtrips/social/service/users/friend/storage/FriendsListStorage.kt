package com.worldventures.dreamtrips.social.service.users.friend.storage

import com.worldventures.dreamtrips.social.service.users.base.storage.BaseUserStorage
import com.worldventures.dreamtrips.social.service.users.friend.command.FriendListStorageCommand

class FriendsListStorage : BaseUserStorage() {
   override fun getActionClass() = FriendListStorageCommand::class.java
}
