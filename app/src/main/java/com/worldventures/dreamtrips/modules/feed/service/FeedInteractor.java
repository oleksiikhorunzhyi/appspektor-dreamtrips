package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedQueryCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class FeedInteractor {

    private final ActionPipe<GetAccountFeedQueryCommand.Refresh> refreshAccountFeedQueryPipe;
    private final ActionPipe<GetAccountFeedQueryCommand.LoadNext> loadNextAccountFeedQueryPipe;

    @Inject
    public FeedInteractor(Janet janet) {
        refreshAccountFeedQueryPipe = janet.createPipe(GetAccountFeedQueryCommand.Refresh.class);
        loadNextAccountFeedQueryPipe = janet.createPipe(GetAccountFeedQueryCommand.LoadNext.class);
    }

    public ActionPipe<GetAccountFeedQueryCommand.Refresh> getRefreshAccountFeedQueryPipe() {
        return refreshAccountFeedQueryPipe;
    }

    public ActionPipe<GetAccountFeedQueryCommand.LoadNext> getLoadNextAccountFeedQueryPipe() {
        return loadNextAccountFeedQueryPipe;
    }
}
