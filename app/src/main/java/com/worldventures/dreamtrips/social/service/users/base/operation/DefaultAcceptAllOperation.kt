package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.User
import com.worldventures.core.model.User.Relationship.FRIEND
import com.worldventures.core.model.User.Relationship.OUTGOING_REQUEST
import com.worldventures.core.model.User.Relationship.REJECTED

class DefaultAcceptAllOperation : BaseUserStorageOperation(false) {

   override fun perform(items: MutableList<User>, haveMoreItemsAction: (Boolean) -> Unit) = items.apply {
      forEach { if (isIncomingRequest(it)) it.relationship = FRIEND }
   }

   private fun isIncomingRequest(user: User) = user.relationship != OUTGOING_REQUEST
         && user.relationship != REJECTED
}
