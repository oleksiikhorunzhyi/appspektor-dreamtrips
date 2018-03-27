package com.worldventures.dreamtrips.social.service.users.friend.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation

class AcceptFriendOperation(val user: User) : BaseUserStorageOperation(false) {
   override fun perform(items: MutableList<User>, haveMoreItemsAction: (Boolean) -> Unit) = items.apply {
      user.relationship = User.Relationship.FRIEND
      indexOf(user).also { if (it != -1) set(it, user) else add(user) }
   }
}
