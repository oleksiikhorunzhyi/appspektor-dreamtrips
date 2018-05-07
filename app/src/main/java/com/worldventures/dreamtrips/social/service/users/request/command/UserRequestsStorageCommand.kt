package com.worldventures.dreamtrips.social.service.users.request.command

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserStorageCommand
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import io.techery.janet.ActionHolder
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class UserRequestsStorageCommand(operation: BaseUserStorageOperation) : BaseUserStorageCommand(operation) {

   var acceptedCount = 0; private set

   override fun run(callback: CommandCallback<List<User>>) {
      callback.onSuccess(operation.perform(items, this::haveMoreItemsAction).apply {
         val acceptedRequests = filter { it.relationship == User.Relationship.FRIEND }
         acceptedCount = acceptedRequests.size
         removeAll(acceptedRequests)
      })
   }

   override fun onRestore(holder: ActionHolder<*>?, cache: UsersCachedModel?) {
      super.onRestore(holder, cache)
      acceptedCount = (cache as? UserRequestCachedModel)?.acceptedCount ?: 0
   }

   override fun getCacheData() = UserRequestCachedModel(items, haveMoreItems, acceptedCount)

   class UserRequestCachedModel(items: MutableList<User>, haveMoreItems: Boolean, val acceptedCount: Int) : UsersCachedModel(items, haveMoreItems)
}
