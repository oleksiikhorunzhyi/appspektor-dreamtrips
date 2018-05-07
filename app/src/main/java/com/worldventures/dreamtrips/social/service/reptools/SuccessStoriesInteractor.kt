package com.worldventures.dreamtrips.social.service.reptools

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.service.reptools.command.GetSuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.LikeSuccessStoryCommand
import com.worldventures.dreamtrips.social.service.reptools.command.RefreshSuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.SuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.UnlikeSuccessStoryCommand
import com.worldventures.dreamtrips.social.service.reptools.command.UpdateSuccessStoryLikeStatusCommand
import rx.schedulers.Schedulers

class SuccessStoriesInteractor(pipeCreator: SessionActionPipeCreator) {

   val getSuccessStoriesPipe = pipeCreator.createPipe(GetSuccessStoriesCommand::class.java, Schedulers.io())
   val likeSuccessStoryPipe = pipeCreator.createPipe(LikeSuccessStoryCommand::class.java, Schedulers.io())
   val unlikeSuccessStoryPipe = pipeCreator.createPipe(UnlikeSuccessStoryCommand::class.java, Schedulers.io())
   val successStoriesPipe = pipeCreator.createPipe(SuccessStoriesCommand::class.java, Schedulers.io())
   val updateLikeStatusPipe = pipeCreator.createPipe(UpdateSuccessStoryLikeStatusCommand::class.java, Schedulers.io())

   init {
      likeSuccessStoryPipe.observeSuccess()
            .map { it.result }
            .subscribe { successStoriesPipe.send(UpdateSuccessStoryLikeStatusCommand(it, true)) }

      unlikeSuccessStoryPipe.observeSuccess()
            .map { it.result }
            .subscribe { successStoriesPipe.send(UpdateSuccessStoryLikeStatusCommand(it, false)) }

      getSuccessStoriesPipe.observeSuccess()
            .map { it.result }
            .subscribe { successStoriesPipe.send(RefreshSuccessStoriesCommand(it)) }
   }
}
