package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.User

class DefaultDeleteRequestOperation(val user: User) : BaseUserStorageOperation(false) {
   override fun perform(items: MutableList<User>, haveMoreItemsAction: (Boolean) -> Unit) = items.apply { remove(user) }
}
