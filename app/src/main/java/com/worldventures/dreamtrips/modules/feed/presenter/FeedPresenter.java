package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.feed.api.GetAccountFeedQuery;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

public class FeedPresenter extends BaseFeedPresenter<FeedPresenter.View> {

    @Inject
    SnappyRepository db;

    public FeedPresenter() {
    }


    @Override
    public void takeView(View view) {
        super.takeView(view);
    }

    protected DreamTripsRequest<ArrayList<ParentFeedModel>> getRefreshFeedRequest(Date date) {
        return new GetAccountFeedQuery(date);
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedModel>> getNextPageFeedRequest(Date date) {
        return new GetAccountFeedQuery(date);
    }

    public void onEventMainThread(HeaderCountChangedEvent event) {
        view.setRequestsCount(db.getFriendsRequestsCount());
    }

    public void refreshRequestsCount() {
        view.setRequestsCount(db.getFriendsRequestsCount());
    }

    public interface View extends BaseFeedPresenter.View {
        void setRequestsCount(int count);
    }
}
