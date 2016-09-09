package com.worldventures.dreamtrips.modules.membership.service;

import com.worldventures.dreamtrips.modules.membership.command.GetPodcastsCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class PodcastsInteractor {

   private final ActionPipe<GetPodcastsCommand> podcastsActionPipe;

   @Inject
   public PodcastsInteractor(Janet janet) {
      this.podcastsActionPipe = janet.createPipe(GetPodcastsCommand.class, Schedulers.io());
   }

   public ActionPipe<GetPodcastsCommand> podcastsActionPipe() {
      return podcastsActionPipe;
   }
}
