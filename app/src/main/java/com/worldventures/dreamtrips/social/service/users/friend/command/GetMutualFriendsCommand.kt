package com.worldventures.dreamtrips.social.service.users.friend.command

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.friends.GetMutualFriendsHttpAction
import com.worldventures.dreamtrips.api.friends.model.ImmutableMutualFriendsParams
import com.worldventures.dreamtrips.social.service.users.base.command.GetUsersCommand
import io.techery.janet.Command

import io.techery.janet.command.annotations.CommandAction

@CommandAction
class GetMutualFriendsCommand(val userId: Int, val page: Int, val perPage: Int) : GetUsersCommand() {

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<User>>) {
      janet.createPipe(GetMutualFriendsHttpAction::class.java)
            .createObservableResult(GetMutualFriendsHttpAction(ImmutableMutualFriendsParams.builder()
                  .userId(userId).page(page).perPage(perPage).build()))
            .map { it.response() }
            .map { this.convert(it) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_failed_to_load_mutual_friends
}
