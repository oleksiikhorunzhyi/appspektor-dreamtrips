package com.worldventures.dreamtrips.social.service.friends.interactor.command

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import io.techery.janet.ActionPipe
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class ChangeCirclesCommand(private val user: User, private val circlePipe: ActionPipe<GetCirclesCommand>
                           , private val successAction: (user: User, List<Circle>) -> Unit) : Command<Unit>() {
   override fun run(callback: CommandCallback<Unit>) {
      circlePipe.createObservableResult(GetCirclesCommand())
            .subscribe({ callback.onSuccess(successAction.invoke(user, it.result)) }
                  , callback::onFail)
   }
}
