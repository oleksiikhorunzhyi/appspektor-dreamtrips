package com.worldventures.dreamtrips.social.ui.membership.service

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.ui.membership.service.command.GetPodcastsCommand

import javax.inject.Inject
import javax.inject.Singleton

import rx.schedulers.Schedulers

@Singleton
class PodcastsInteractor @Inject
constructor(sessionActionPipeCreator: SessionActionPipeCreator) {

   val podcastsActionPipe = sessionActionPipeCreator.createPipe(GetPodcastsCommand::class.java, Schedulers.io())

}
