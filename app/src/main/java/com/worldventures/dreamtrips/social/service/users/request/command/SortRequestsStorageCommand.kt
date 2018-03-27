package com.worldventures.dreamtrips.social.service.users.request.command

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.base.model.AcceptanceHeaderModel
import com.worldventures.dreamtrips.social.service.users.base.model.RequestHeaderModel
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class SortRequestsStorageCommand(
      private val acceptedCount: Int,
      private val users: List<User>,
      val isNoMoreItems: Boolean,
      private val incomingHeaderTitle: String,
      private val outgoingHeaderTitle: String
) : Command<List<Any>>() {

   override fun run(callback: CommandCallback<List<Any>>) {
      val sortedItems = mutableListOf<Any>()
      val incomingItems = users.filter { it.relationship == User.Relationship.INCOMING_REQUEST }

      if (acceptedCount != 0 || incomingItems.isNotEmpty()) {
         val incomingHeader = RequestHeaderModel(incomingHeaderTitle, true)
         incomingHeader.count = incomingItems.size
         sortedItems.add(incomingHeader)
      }

      if (acceptedCount != 0) {
         sortedItems.add(AcceptanceHeaderModel(acceptedCount))
      } else if (incomingItems.isNotEmpty()) {
         sortedItems.addAll(incomingItems)
      }

      val outgoing = users.filter {
         it.relationship == User.Relationship.OUTGOING_REQUEST
               || it.relationship == User.Relationship.REJECTED
      }

      if (outgoing.isNotEmpty()) {
         sortedItems.add(RequestHeaderModel(outgoingHeaderTitle))
         sortedItems.addAll(outgoing)
      }

      callback.onSuccess(sortedItems)
   }
}
