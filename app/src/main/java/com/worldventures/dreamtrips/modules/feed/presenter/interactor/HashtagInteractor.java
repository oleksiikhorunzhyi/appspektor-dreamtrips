package com.worldventures.dreamtrips.modules.feed.presenter.interactor;

import com.worldventures.dreamtrips.modules.feed.command.LoadNextFeedsByHashtagsCommand;
import com.worldventures.dreamtrips.modules.feed.command.RefreshFeedsByHashtagsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class HashtagInteractor {
    private final ActionPipe<RefreshFeedsByHashtagsCommand> refreshFeedsByHashtagsPipe;
    private final ActionPipe<LoadNextFeedsByHashtagsCommand> loadNextFeedsByHashtagsPipe;

    public HashtagInteractor(Janet janet) {
        refreshFeedsByHashtagsPipe = janet.createPipe(RefreshFeedsByHashtagsCommand.class);
        loadNextFeedsByHashtagsPipe = janet.createPipe(LoadNextFeedsByHashtagsCommand.class);
    }

    public ActionPipe<RefreshFeedsByHashtagsCommand> getRefreshFeedsByHashtagsPipe() {
        return refreshFeedsByHashtagsPipe;
    }

    public ActionPipe<LoadNextFeedsByHashtagsCommand> getLoadNextFeedsByHashtagsPipe() {
        return loadNextFeedsByHashtagsPipe;
    }
}
