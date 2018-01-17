package com.worldventures.dreamtrips.social.ui.video.service

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.ui.video.service.command.DetermineHeadersCommand
import com.worldventures.dreamtrips.social.ui.video.service.command.SortVideo360CategoriesCommand
import rx.schedulers.Schedulers

class VideoHelperInteractor(sessionActionPipeCreator: SessionActionPipeCreator) {

   val headerPipe = sessionActionPipeCreator.createPipe(DetermineHeadersCommand::class.java, Schedulers.io())
   val sort360VideoPipe = sessionActionPipeCreator.createPipe(SortVideo360CategoriesCommand::class.java, Schedulers.io())

}
