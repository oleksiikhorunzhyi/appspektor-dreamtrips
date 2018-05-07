package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.User

class DefaultRemoveFriendOperation(val user: User) : BaseUserStorageOperation(false) {
   override fun perform(items: MutableList<User>, haveMoreItemsAction: (Boolean) -> Unit) = items.apply {
      user.relationship = User.Relationship.NONE
      remove(user)
   }
}
