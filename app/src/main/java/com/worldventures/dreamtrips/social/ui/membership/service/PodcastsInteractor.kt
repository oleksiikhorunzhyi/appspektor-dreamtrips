package com.worldventures.dreamtrips.social.ui.membership.service

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.ui.membership.service.command.GetPodcastsCommand

import rx.schedulers.Schedulers

class PodcastsInteractor(sessionActionPipeCreator: SessionActionPipeCreator) {

   val podcastsActionPipe = sessionActionPipeCreator.createPipe(GetPodcastsCommand::class.java, Schedulers.io())

}
