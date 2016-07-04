package com.worldventures.dreamtrips.modules.feed.presenter.interactor;

import com.worldventures.dreamtrips.modules.feed.command.GetFeedsByHashtagsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class HashtagInteractor {
    private final ActionPipe<GetFeedsByHashtagsCommand> getFeedsByHashtagsPipe;
//    private final ActionPipe<GetHashtagsByQueryCommand> getHashtagsByQueryPipe;

    public HashtagInteractor(Janet janet) {
        getFeedsByHashtagsPipe = janet.createPipe(GetFeedsByHashtagsCommand.class);
//        getHashtagsByQueryPipe = janet.createPipe(GetHashtagsByQueryCommand.class);
    }

    public ActionPipe<GetFeedsByHashtagsCommand> getFeedsByHashtagsPipe() {
        return getFeedsByHashtagsPipe;
    }
    
//    public ActionPipe<GetFeedsByHashtagsCommand> getHashtagsByQueryPipe() {
//        return getHashtagsByQueryPipe;
//    }
}
