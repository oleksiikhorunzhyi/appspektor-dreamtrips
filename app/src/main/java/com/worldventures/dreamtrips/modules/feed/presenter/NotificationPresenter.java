package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.MarkAsReadNotificationsCommand;
import com.worldventures.dreamtrips.modules.feed.api.NotificationsQuery;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import icepick.State;

public class NotificationPresenter extends Presenter<NotificationPresenter.View> {

    @State
    protected ArrayList<FeedItem> notifications;
    @Inject
    protected FeedEntityManager entityManager;
    @Inject
    SnappyRepository db;
    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    BucketInteractor bucketInteractor;

    public NotificationPresenter() {
    }

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        if (savedState == null) notifications = new ArrayList<>();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (notifications.size() != 0) {
            view.refreshNotifications(notifications);
        }
    }

    @Override
    public void onInjected() {
        super.onInjected();
        entityManager.setRequestingPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFeed();
    }

    public void reload() {
        refreshFeed();
    }

    public void refreshRequestsCount() {
        view.setRequestsCount(db.getFriendsRequestsCount());
    }

    public void onRefresh() {
        refreshFeed();
    }

    public void loadNext() {
        if (notifications.size() > 0) {
            doRequest(getNextPageFeedRequest(notifications.get(notifications.size() - 1).getCreatedAt()),
                    this::addFeedItems, this::loadMoreItemsError);
        }
        TrackingHelper.loadMoreNotifications();
    }

    public void onEventMainThread(HeaderCountChangedEvent event) {
        view.setRequestsCount(db.getFriendsRequestsCount());
    }

    private DreamTripsRequest<ArrayList<ParentFeedItem>> getRefreshFeedRequest(Date date) {
        return new NotificationsQuery();
    }

    private DreamTripsRequest<ArrayList<ParentFeedItem>> getNextPageFeedRequest(Date date) {
        return new NotificationsQuery(date);
    }

    private void loadMoreItemsError(SpiceException spiceException) {
        addFeedItems(new ArrayList<>());
    }

    private void addFeedItems(List<ParentFeedItem> olderItems) {
        boolean noMoreFeeds = olderItems == null || olderItems.size() == 0;
        view.updateLoadingStatus(false, noMoreFeeds);
        //
        notifications.addAll(Queryable.from(olderItems)
                .filter(ParentFeedItem::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());
        view.refreshNotifications(notifications);
        markAsRead(olderItems);
    }

    private void refreshFeed() {
        view.startLoading();
        doRequest(getRefreshFeedRequest(Calendar.getInstance().getTime()),
                this::refreshFeedSucceed, this::refreshFeedError);
    }

    private void refreshFeedError(SpiceException exception) {
        super.handleError(exception);
        view.finishLoading();
        view.refreshNotifications(notifications);
    }

    private void refreshFeedSucceed(List<ParentFeedItem> freshItems) {
        boolean noMoreFeeds = freshItems == null || freshItems.size() == 0;
        view.updateLoadingStatus(false, noMoreFeeds);
        //
        view.finishLoading();
        notifications.clear();
        notifications.addAll(Queryable.from(freshItems)
                .filter(ParentFeedItem::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());

        view.refreshNotifications(notifications);
        markAsRead(freshItems);
    }

    private void markAsRead(List<ParentFeedItem> olderItems) {
        if (olderItems.size() > 0 && olderItems.get(0).getItems().size() > 0) {
            Date since = olderItems.get(olderItems.size() - 1).getItems().get(0).getCreatedAt();
            Date before = olderItems.get(0).getItems().get(0).getCreatedAt();
            doRequest(new MarkAsReadNotificationsCommand(since, before), aVoid -> {
            });
        }
    }

    public interface View extends RxView {

        void setRequestsCount(int count);

        void startLoading();

        void finishLoading();

        void refreshNotifications(List<FeedItem> notifications);

        void updateLoadingStatus(boolean loading, boolean noMoreElements);
    }
}
