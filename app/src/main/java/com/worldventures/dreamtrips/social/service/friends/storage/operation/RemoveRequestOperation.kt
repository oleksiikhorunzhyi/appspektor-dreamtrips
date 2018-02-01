package com.worldventures.dreamtrips.social.service.friends.storage.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.friends.storage.delegat.FriendStorageDelegate

class RemoveRequestOperation(val user: User) : FriendStorageDelegate.FriendListStorageOperation(false) {
   override fun perform(items: MutableList<User>?) = items?.apply { remove(user) } ?: mutableListOf()
}
