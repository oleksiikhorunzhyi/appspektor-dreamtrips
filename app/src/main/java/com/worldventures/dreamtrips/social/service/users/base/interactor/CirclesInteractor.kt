package com.worldventures.dreamtrips.social.service.users.base.interactor

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesCommand

import io.techery.janet.ActionPipe
import rx.schedulers.Schedulers

open class CirclesInteractor(pipeCreator: SessionActionPipeCreator) {
   val pipe: ActionPipe<GetCirclesCommand> = pipeCreator.createPipe(GetCirclesCommand::class.java, Schedulers.io())
}
