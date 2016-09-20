package com.worldventures.dreamtrips.modules.membership.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.membership.command.GetPodcastsCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class PodcastsInteractor {

   private final ActionPipe<GetPodcastsCommand> podcastsActionPipe;

   @Inject
   public PodcastsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.podcastsActionPipe = sessionActionPipeCreator.createPipe(GetPodcastsCommand.class, Schedulers.io());
   }

   public ActionPipe<GetPodcastsCommand> podcastsActionPipe() {
      return podcastsActionPipe;
   }
}
