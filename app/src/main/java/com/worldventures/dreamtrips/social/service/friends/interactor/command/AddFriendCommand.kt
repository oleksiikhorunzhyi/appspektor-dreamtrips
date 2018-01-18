package com.worldventures.dreamtrips.social.service.friends.interactor.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.friends.SendFriendRequestHttpAction
import io.techery.janet.Command

import javax.inject.Inject

import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class AddFriendCommand(val user: User, val circleId: String) : CommandWithError<User>(), InjectableAction {

   @field:Inject lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<User>) {
      janet.createPipe(SendFriendRequestHttpAction::class.java)
            .createObservableResult(SendFriendRequestHttpAction(user.id, circleId))
            .subscribe({ callback.onSuccess(user) }, callback::onFail)
   }

   override fun getFallbackErrorMessage(): Int = R.string.error_failed_to_send_friend_request
}
