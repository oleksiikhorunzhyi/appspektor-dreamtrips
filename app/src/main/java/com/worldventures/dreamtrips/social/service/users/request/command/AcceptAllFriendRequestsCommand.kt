package com.worldventures.dreamtrips.social.service.users.request.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.friends.AcceptAllFriendRequestsHttpAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import rx.schedulers.Schedulers
import javax.inject.Inject

@CommandAction
class AcceptAllFriendRequestsCommand(val circleId: String) : CommandWithError<Void>(), InjectableAction {

   @Inject lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: CommandCallback<Void>) {
      janet.createPipe(AcceptAllFriendRequestsHttpAction::class.java, Schedulers.io())
            .createObservableResult(AcceptAllFriendRequestsHttpAction(circleId))
            .subscribe({ callback.onSuccess(null) }, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_fail_to_accept_friend_request

}
