package com.worldventures.dreamtrips.social.service.friends.interactor.command

import android.support.annotation.StringRes
import com.worldventures.core.janet.CommandWithError
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.friends.HideFriendRequestHttpAction
import io.techery.janet.Command

import javax.inject.Inject

import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class DeleteFriendRequestCommand(val user: User, val action: Action) : CommandWithError<User>(), InjectableAction {

   @field:Inject lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<User>) {
      janet.createPipe(HideFriendRequestHttpAction::class.java)
            .createObservableResult(HideFriendRequestHttpAction(user.id))
            .subscribe({ callback.onSuccess(user) }, callback::onFail)
   }

   override fun getFallbackErrorMessage(): Int = action.errorMessageRes

   enum class Action(@StringRes val errorMessageRes: Int) {
      HIDE(R.string.error_fail_to_hide_friend_request),
      CANCEL(R.string.error_fail_to_cancel_friend_request)
   }
}
