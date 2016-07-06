package com.worldventures.dreamtrips.modules.feed.presenter.interactor;

import com.worldventures.dreamtrips.modules.feed.command.GetFeedsByHashtagsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class HashtagInteractor {
    private final ActionPipe<GetFeedsByHashtagsCommand> refreshFeedsByHashtagsPipe;
    private final ActionPipe<GetFeedsByHashtagsCommand> loadNextFeedsByHashtagsPipe;
//    private final ActionPipe<GetHashtagsByQueryCommand> getHashtagsByQueryPipe;

    public HashtagInteractor(Janet janet) {
        refreshFeedsByHashtagsPipe = janet.createPipe(GetFeedsByHashtagsCommand.class);
        loadNextFeedsByHashtagsPipe = janet.createPipe(GetFeedsByHashtagsCommand.class);
//        getHashtagsByQueryPipe = janet.createPipe(GetHashtagsByQueryCommand.class);
    }

    public ActionPipe<GetFeedsByHashtagsCommand> getRefreshFeedsByHashtagsPipe() {
        return refreshFeedsByHashtagsPipe;
    }

    public ActionPipe<GetFeedsByHashtagsCommand> getLoadNextFeedsByHashtagsPipe() {
        return loadNextFeedsByHashtagsPipe;
    }
    
//    public ActionPipe<GetFeedsByHashtagsCommand> getHashtagsByQueryPipe() {
//        return getHashtagsByQueryPipe;
//    }
}
