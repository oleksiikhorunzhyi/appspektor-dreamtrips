package com.worldventures.dreamtrips.social.service.users.friend.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.friends.RemoveFromFriendsHttpAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class RemoveFriendCommand(val user: User) : CommandWithError<User>(), InjectableAction {

   @Inject lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<User>) {
      janet.createPipe(RemoveFromFriendsHttpAction::class.java)
            .createObservableResult(RemoveFromFriendsHttpAction(user.id))
            .subscribe({ callback.onSuccess(user) }, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_failed_to_unfriend_user
}
