package com.worldventures.dreamtrips.social.service.friends.interactor

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetCirclesCommand

import io.techery.janet.ActionPipe
import rx.schedulers.Schedulers

open class CirclesInteractor(sessionActionPipeCreator: SessionActionPipeCreator) {
   val pipe: ActionPipe<GetCirclesCommand>
         = sessionActionPipeCreator.createPipe(GetCirclesCommand::class.java, Schedulers.io())
}
