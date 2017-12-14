package com.worldventures.dreamtrips.social.service.friends.interactor.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.friends.AnswerFriendRequestsHttpAction
import com.worldventures.dreamtrips.api.friends.model.FriendRequestParams
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
abstract class ActOnFriendRequestCommand(val user: User) : CommandWithError<User>(), InjectableAction {

   @field:Inject lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: CommandCallback<User>?) {
      janet.createPipe(AnswerFriendRequestsHttpAction::class.java)
            .createObservableResult(AnswerFriendRequestsHttpAction(getRequestParams()))
            .subscribe({ callback?.onSuccess(user) }, { callback?.onFail(it) })
   }

   abstract fun getRequestParams(): FriendRequestParams

   @CommandAction
   class Accept(user: User, val circleId: String) : ActOnFriendRequestCommand(user) {
      override fun getRequestParams(): FriendRequestParams {
         return FriendRequestParams.confirm(user.id, circleId)
      }

      override fun getFallbackErrorMessage(): Int = R.string.error_fail_to_accept_friend_request
   }

   @CommandAction
   class Reject(user: User) : ActOnFriendRequestCommand(user) {
      override fun getRequestParams(): FriendRequestParams {
         return FriendRequestParams.reject(user.id)
      }

      override fun getFallbackErrorMessage(): Int = R.string.error_fail_to_reject_friend_request
   }
}
