package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.feed.api.GetAccountFeedQuery;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;

import java.util.ArrayList;
import java.util.Date;

public class FeedPresenter extends BaseFeedPresenter<FeedPresenter.View> {

    public FeedPresenter() {
    }

    protected DreamTripsRequest<ArrayList<ParentFeedModel>> getRefreshFeedRequest(Date date) {
        return new GetAccountFeedQuery(date);
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedModel>> getNextPageFeedRequest(Date date) {
        return new GetAccountFeedQuery(date);
    }

    public interface View extends BaseFeedPresenter.View {
    }
}
