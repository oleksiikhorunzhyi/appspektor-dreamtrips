package com.worldventures.dreamtrips.modules.feed.service;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class SuggestedPhotoInteractor {

    private final ActionPipe<SuggestedPhotoCommand> suggestedPhotoCommandActionPipe;

    @Inject
    public SuggestedPhotoInteractor(Janet janet) {
        suggestedPhotoCommandActionPipe = janet.createPipe(SuggestedPhotoCommand.class);
    }

    public ActionPipe<SuggestedPhotoCommand> getSuggestedPhotoCommandActionPipe() {
        return suggestedPhotoCommandActionPipe;
    }
}
