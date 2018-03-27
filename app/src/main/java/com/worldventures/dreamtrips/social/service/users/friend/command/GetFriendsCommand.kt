package com.worldventures.dreamtrips.social.service.users.friend.command

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.friends.GetFriendsHttpAction
import com.worldventures.dreamtrips.api.friends.model.FriendsParams
import com.worldventures.dreamtrips.api.friends.model.ImmutableFriendsParams
import com.worldventures.dreamtrips.social.service.users.base.command.GetUsersCommand
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class GetFriendsCommand(val circle: Circle?, val query: String?, val page: Int, val perPage: Int)
   : GetUsersCommand() {

   constructor(circle: Circle, page: Int, perPage: Int) : this(circle, null, page, perPage)

   constructor(query: String, page: Int, perPage: Int) : this(null, query, page, perPage)

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<User>>) {
      janet.createPipe(GetFriendsHttpAction::class.java)
            .createObservableResult(GetFriendsHttpAction(provideFriendsParams()))
            .map { it.response() }
            .map { this.convert(it) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   private fun provideFriendsParams(): FriendsParams {
      return ImmutableFriendsParams.builder().let { builder ->
         builder.page(page).perPage(perPage).query(query)
         circle?.id?.let { builder.circleId(it) }
         builder.build()
      }
   }

   override fun getFallbackErrorMessage() = R.string.error_failed_to_load_friends

}
