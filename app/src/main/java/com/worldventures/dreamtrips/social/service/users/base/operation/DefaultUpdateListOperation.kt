package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.User

class DefaultUpdateListOperation(val users: List<User>, refresh: Boolean) : BaseUserStorageOperation(refresh) {
   override fun perform(items: MutableList<User>, haveMoreItemsAction: (Boolean) -> Unit) = items.apply {
      haveMoreItemsAction.invoke(users.isNotEmpty())
      addAll(users)
   }
}
