package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.User

abstract class BaseUserStorageOperation(val refresh: Boolean) {
   abstract fun perform(items: MutableList<User>, haveMoreItemsAction: (Boolean) -> Unit): MutableList<User>
}
