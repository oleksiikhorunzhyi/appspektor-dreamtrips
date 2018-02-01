package com.worldventures.dreamtrips.social.service.friends.storage.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.friends.storage.delegat.FriendStorageDelegate

class AddFriendOperation(val user: User) : FriendStorageDelegate.FriendListStorageOperation(false) {
   override fun perform(items: MutableList<User>?): MutableList<User> {
      user.relationship = User.Relationship.OUTGOING_REQUEST
      return items?.apply { indexOf(user).also { if (it != -1) set(it, user) } } ?: mutableListOf()
   }
}
