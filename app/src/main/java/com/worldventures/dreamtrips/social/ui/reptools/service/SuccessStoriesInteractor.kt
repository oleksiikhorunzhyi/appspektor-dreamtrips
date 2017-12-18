package com.worldventures.dreamtrips.social.ui.reptools.service

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.ui.reptools.service.command.GetSuccessStoriesCommand
import com.worldventures.dreamtrips.social.ui.reptools.service.command.LikeSuccessStoryCommand
import com.worldventures.dreamtrips.social.ui.reptools.service.command.UnlikeSuccessStoryCommand
import rx.schedulers.Schedulers

class SuccessStoriesInteractor(sessionActionPipeCreator: SessionActionPipeCreator) {

   val successStoriesPipe = sessionActionPipeCreator.createPipe(GetSuccessStoriesCommand::class.java, Schedulers.io())
   val likeSuccessStoryPipe = sessionActionPipeCreator.createPipe(LikeSuccessStoryCommand::class.java, Schedulers.io())
   val unlikeSuccessStoryPipe = sessionActionPipeCreator.createPipe(UnlikeSuccessStoryCommand::class.java, Schedulers.io())
}
