package com.worldventures.dreamtrips.social.service.friends.storage.operation

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.friends.storage.delegat.FriendStorageDelegate

class ChangeCircleOperation(val userId: Int, private val changeCircleAction: (MutableList<Circle>) -> Unit)
   : FriendStorageDelegate.FriendListStorageOperation(false) {
   override fun perform(items: MutableList<User>?) = items?.apply {
      changeCircleAction.invoke(firstOrNull { it.id == userId }?.circles ?: mutableListOf())
   }
}
