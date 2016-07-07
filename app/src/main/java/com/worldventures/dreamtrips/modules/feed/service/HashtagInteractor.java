package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.modules.feed.command.LoadNextFeedsByHashtagsCommand;
import com.worldventures.dreamtrips.modules.feed.command.RefreshFeedsByHashtagsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostDescriptionCreatedCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class HashtagInteractor {

    ActionPipe<HashtagSuggestionCommand> suggestionPipe;
    ActionPipe<PostDescriptionCreatedCommand> descPickedPipe;
    ActionPipe<RefreshFeedsByHashtagsCommand> refreshFeedsByHashtagsPipe;
    ActionPipe<LoadNextFeedsByHashtagsCommand> loadNextFeedsByHashtagsPipe;

    @Inject
    public HashtagInteractor(Janet janet) {
        this.suggestionPipe = janet.createPipe(HashtagSuggestionCommand.class);
        this.descPickedPipe = janet.createPipe(PostDescriptionCreatedCommand.class);
        refreshFeedsByHashtagsPipe = janet.createPipe(RefreshFeedsByHashtagsCommand.class);
        loadNextFeedsByHashtagsPipe = janet.createPipe(LoadNextFeedsByHashtagsCommand.class);
    }

    public ActionPipe<HashtagSuggestionCommand> getSuggestionPipe() {
        return suggestionPipe;
    }

    public ActionPipe<PostDescriptionCreatedCommand> getDescPickedPipe() {
        return descPickedPipe;
    }


    public ActionPipe<RefreshFeedsByHashtagsCommand> getRefreshFeedsByHashtagsPipe() {
        return refreshFeedsByHashtagsPipe;
    }

    public ActionPipe<LoadNextFeedsByHashtagsCommand> getLoadNextFeedsByHashtagsPipe() {
        return loadNextFeedsByHashtagsPipe;
    }
}
