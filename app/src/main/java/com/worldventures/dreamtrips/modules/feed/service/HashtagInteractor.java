package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostDescriptionCreatedCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class HashtagInteractor {

    ActionPipe<HashtagSuggestionCommand> suggestionPipe;
    ActionPipe<PostDescriptionCreatedCommand> descPickedPipe;


    public HashtagInteractor(Janet janet) {
        this.suggestionPipe = janet.createPipe(HashtagSuggestionCommand.class);
        this.descPickedPipe = janet.createPipe(PostDescriptionCreatedCommand.class);
    }

    public ActionPipe<HashtagSuggestionCommand> getSuggestionPipe() {
        return suggestionPipe;
    }

    public ActionPipe<PostDescriptionCreatedCommand> getDescPickedPipe() {
        return descPickedPipe;
    }
}
