package com.worldventures.dreamtrips.social.service.friends.storage.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.friends.storage.delegat.FriendStorageDelegate

class EmptyListOperation(val list: List<User>, refresh: Boolean) : FriendStorageDelegate.FriendListStorageOperation(refresh) {
   override fun perform(items: MutableList<User>?) = items?.apply { addAll(list) } ?: mutableListOf()
}
