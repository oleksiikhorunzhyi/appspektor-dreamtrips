package com.worldventures.dreamtrips.modules.membership.presenter.interactor;

import com.worldventures.dreamtrips.modules.membership.command.PodcastCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class PodcastInteractor {

    private final ActionPipe<PodcastCommand> pipe;

    @Inject
    public PodcastInteractor(Janet janet) {
        pipe = janet.createPipe(PodcastCommand.class);
    }

    public ActionPipe<PodcastCommand> pipe() {
        return pipe;
    }
}
