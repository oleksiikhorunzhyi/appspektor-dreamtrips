package com.worldventures.dreamtrips.social.service.friends.storage.operation

import com.worldventures.core.model.User
import com.worldventures.core.model.User.Relationship.OUTGOING_REQUEST
import com.worldventures.core.model.User.Relationship.REJECTED
import com.worldventures.dreamtrips.social.service.friends.storage.delegat.FriendStorageDelegate

class AcceptAllOperation : FriendStorageDelegate.FriendListStorageOperation(false) {

   override fun perform(items: MutableList<User>?) = items?.apply {
      forEach { if (isIncomingRequest(it)) items.remove(it) }
   } ?: mutableListOf()

   private fun isIncomingRequest(user: User) = user.relationship != OUTGOING_REQUEST
         && user.relationship != REJECTED
}
