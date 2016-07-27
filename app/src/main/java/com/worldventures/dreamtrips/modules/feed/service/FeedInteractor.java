package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetUserTimelineCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class FeedInteractor {

    private final ActionPipe<GetAccountFeedCommand.Refresh> refreshAccountFeedPipe;
    private final ActionPipe<GetAccountFeedCommand.LoadNext> loadNextAccountFeedPipe;
    //
    private final ActionPipe<GetUserTimelineCommand.Refresh> refreshUserTimelinePipe;
    private final ActionPipe<GetUserTimelineCommand.LoadNext> loadNextUserTimelinePipe;
    //
    private final ActionPipe<GetAccountTimelineCommand.Refresh> refreshAccountTimelinePipe;
    private final ActionPipe<GetAccountTimelineCommand.LoadNext> loadNextAccountTimelinePipe;

    @Inject
    public FeedInteractor(Janet janet) {
        refreshAccountFeedPipe = janet.createPipe(GetAccountFeedCommand.Refresh.class, Schedulers.io());
        loadNextAccountFeedPipe = janet.createPipe(GetAccountFeedCommand.LoadNext.class, Schedulers.io());
        //
        refreshUserTimelinePipe = janet.createPipe(GetUserTimelineCommand.Refresh.class, Schedulers.io());
        loadNextUserTimelinePipe = janet.createPipe(GetUserTimelineCommand.LoadNext.class, Schedulers.io());
        //
        refreshAccountTimelinePipe = janet.createPipe(GetAccountTimelineCommand.Refresh.class, Schedulers.io());
        loadNextAccountTimelinePipe = janet.createPipe(GetAccountTimelineCommand.LoadNext.class, Schedulers.io());
    }

    public ActionPipe<GetAccountFeedCommand.Refresh> getRefreshAccountFeedPipe() {
        return refreshAccountFeedPipe;
    }

    public ActionPipe<GetAccountFeedCommand.LoadNext> getLoadNextAccountFeedPipe() {
        return loadNextAccountFeedPipe;
    }

    public ActionPipe<GetUserTimelineCommand.Refresh> getRefreshUserTimelinePipe() {
        return refreshUserTimelinePipe;
    }

    public ActionPipe<GetUserTimelineCommand.LoadNext> getLoadNextUserTimelinePipe() {
        return loadNextUserTimelinePipe;
    }

    public ActionPipe<GetAccountTimelineCommand.Refresh> getRefreshAccountTimelinePipe() {
        return refreshAccountTimelinePipe;
    }

    public ActionPipe<GetAccountTimelineCommand.LoadNext> getLoadNextAccountTimelinePipe() {
        return loadNextAccountTimelinePipe;
    }
}
