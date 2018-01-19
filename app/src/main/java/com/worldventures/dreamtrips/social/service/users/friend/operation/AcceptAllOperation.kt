package com.worldventures.dreamtrips.social.service.users.friend.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation

class AcceptAllOperation(val action: () -> Unit) : BaseUserStorageOperation(false) {
   override fun perform(items: MutableList<User>, haveMoreItemsAction: (Boolean) -> Unit): MutableList<User> {
      action.invoke()
      return items
   }
}
