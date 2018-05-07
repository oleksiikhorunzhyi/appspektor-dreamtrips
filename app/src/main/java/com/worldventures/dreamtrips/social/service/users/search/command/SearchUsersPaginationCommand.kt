package com.worldventures.dreamtrips.social.service.users.search.command

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserPaginationCommand
import com.worldventures.dreamtrips.social.service.users.base.command.GetUsersCommand
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable

@CommandAction
class SearchUsersPaginationCommand(
      refresh: Boolean,
      val query: String,
      getUserOperation: (page: Int, perPage: Int) -> Observable<out GetUsersCommand>
) : BaseUserPaginationCommand(refresh, getUserOperation) {

   override fun run(callback: Command.CommandCallback<List<User>>) {
      if (query.length < MIN_SEARCH_QUERY_LENGTH) callback.onSuccess(listOf())
      else super.run(callback)
   }

   companion object {
      const val MIN_SEARCH_QUERY_LENGTH = 3
   }
}
