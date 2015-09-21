package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.FriendRequestsCountChangedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.MarkAsReadNotificationsCommand;
import com.worldventures.dreamtrips.modules.feed.api.NotificationsQuery;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class NotificationPresenter extends BaseFeedPresenter<NotificationPresenter.View> {

    @Inject
    SnappyRepository db;

    public NotificationPresenter() {
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedModel>> getRefreshFeedRequest(Date date) {
        return new NotificationsQuery(date);
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedModel>> getNextPageFeedRequest(Date date) {
        return new NotificationsQuery(date);
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
            Date since = olderItems.get(olderItems.size() - 1).getItems().get(0).getCreatedAt();
            Date before = olderItems.get(0).getItems().get(0).getCreatedAt();
            doRequest(new MarkAsReadNotificationsCommand(since, before), aVoid -> {
            });
        }
    }

    public void onEventMainThread(FriendRequestsCountChangedEvent event) {
        view.setRequestsCount(db.getFriendsRequestsCount());
    }

    public void refreshRequestsCount() {
        view.setRequestsCount(db.getFriendsRequestsCount());
    }

    public interface View extends Presenter.View, BaseFeedPresenter.View {
        void setRequestsCount(int count);
    }
}
