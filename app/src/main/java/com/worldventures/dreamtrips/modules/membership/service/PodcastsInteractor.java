package com.worldventures.dreamtrips.modules.membership.service;

import com.worldventures.dreamtrips.modules.membership.command.PodcastCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class PodcastsInteractor {

    private final ActionPipe<PodcastCommand> podcastsActionPipe;

    @Inject
    public PodcastsInteractor(Janet janet) {
        this.podcastsActionPipe = janet.createPipe(PodcastCommand.class, Schedulers.io());
    }

    public ActionPipe<PodcastCommand> podcastsActionPipe() {
        return podcastsActionPipe;
    }
}
