package com.worldventures.dreamtrips.social.service.users.liker.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation

class RemoveFriendOperation(val user: User) : BaseUserStorageOperation(false) {
   override fun perform(items: MutableList<User>, haveMoreItemsAction: (Boolean) -> Unit) = items.apply {
      user.relationship = User.Relationship.NONE
      indexOf(user).also { if (it != -1) set(it, user) }
   }
}
