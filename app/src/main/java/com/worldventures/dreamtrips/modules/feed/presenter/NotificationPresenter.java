package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.MarkAsreadNotificationsCommand;
import com.worldventures.dreamtrips.modules.feed.api.NotificationsQuery;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationPresenter extends BaseFeedPresenter<NotificationPresenter.View> {

    public NotificationPresenter() {
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedModel>> getRefreshFeedRequest(Date date) {
        return new NotificationsQuery(date);
        // return new GetAccountFeedQuery(date);
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedModel>> getNextPageFeedRequest(Date date) {
        return new NotificationsQuery(date);
        // return new GetAccountFeedQuery(date);
    }


    public void reload() {
        refreshFeed();
    }

    @Override
    protected void addFeedItems(List<ParentFeedModel> olderItems) {
        super.addFeedItems(olderItems);
        markAsRead(olderItems);
    }

    @Override
    protected void refreshFeedSucceed(List<ParentFeedModel> freshItems) {
        super.refreshFeedSucceed(freshItems);
        markAsRead(freshItems);
    }

    private void markAsRead(List<ParentFeedModel> olderItems) {
        if (olderItems.size() > 0 && olderItems.get(0).getItems().size() > 0) {
            Date from = olderItems.get(0).getItems().get(0).getCreatedAt();
            Date to = olderItems.get(olderItems.size() - 1).getItems().get(0).getCreatedAt();
            doRequest(new MarkAsreadNotificationsCommand(from, to), aVoid -> {
            });
        }
    }

    public interface View extends Presenter.View, BaseFeedPresenter.View {

    }
}
