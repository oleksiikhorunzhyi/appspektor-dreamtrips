package com.worldventures.dreamtrips.social.service.friends.storage.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.friends.storage.delegat.FriendStorageDelegate

class RemoveFriendOperation(val user: User) : FriendStorageDelegate.FriendListStorageOperation(false) {
   override fun perform(items: MutableList<User>?) = items?.apply {
      user.relationship = User.Relationship.NONE
      remove(user)
   } ?: mutableListOf()
}
