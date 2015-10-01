package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.bucketlist.api.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.api.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.api.FlagItemCommand;
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.ProfileClickedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFeedReloadEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetFlagContentQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import icepick.State;


public abstract class BaseFeedPresenter<V extends BaseFeedPresenter.View> extends Presenter<V> {

    private boolean loading = true;
    private boolean noMoreFeeds = false;

    @State
    protected ArrayList<FeedItem> feedItems;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        if (savedState == null) feedItems = new ArrayList<>();
    }

    @Override
    public void takeView(V view) {
        super.takeView(view);
        if (feedItems.size() != 0) {
            view.refreshFeedItems(feedItems, !noMoreFeeds);
        } else {
            refreshFeed();
        }
    }

    /////////////////////////////////////
    ////// Feed refresh
    /////////////////////////////////////

    public void onRefresh() {
        refreshFeed();
    }

    protected abstract DreamTripsRequest<ArrayList<ParentFeedItem>> getRefreshFeedRequest(Date date);

    protected void refreshFeed() {
        view.startLoading();
        doRequest(getRefreshFeedRequest(Calendar.getInstance().getTime()),
                this::refreshFeedSucceed, this::refreshFeedError);
    }

    private void refreshFeedError(SpiceException exception) {
        super.handleError(exception);
        view.finishLoading();
    }

    protected void refreshFeedSucceed(List<ParentFeedItem> freshItems) {
        loading = false;
        noMoreFeeds = freshItems == null || freshItems.size() == 0;
        view.finishLoading();
        feedItems.clear();
        feedItems.addAll(Queryable.from(freshItems)
                .filter(ParentFeedItem::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());

        view.refreshFeedItems(feedItems, !noMoreFeeds);
    }

    /////////////////////////////////////
    ////// Feed load more
    /////////////////////////////////////

    protected abstract DreamTripsRequest<ArrayList<ParentFeedItem>> getNextPageFeedRequest(Date date);

    public void scrolled(int totalItemCount, int lastVisible) {
        if (featureManager.available(Feature.SOCIAL)) {
            if (!loading && !noMoreFeeds
                    && lastVisible == totalItemCount - 1) {
                loading = true;
                loadMore();
            }
        }
    }

    private void loadMore() {
        if (feedItems.size() > 0) {
            doRequest(getNextPageFeedRequest(feedItems.get(feedItems.size() - 1).getCreatedAt()),
                    this::addFeedItems, this::loadMoreItemsError);
        }
    }

    protected void addFeedItems(List<ParentFeedItem> olderItems) {
        loading = false;
        noMoreFeeds = olderItems == null || olderItems.size() == 0;
        feedItems.addAll(Queryable.from(olderItems)
                .filter(ParentFeedItem::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());

        view.refreshFeedItems(feedItems, !noMoreFeeds);
    }

    protected void loadMoreItemsError(SpiceException spiceException) {
        addFeedItems(new ArrayList<>());
    }

    /////////////////////////////////////
    ////// Items changed events
    /////////////////////////////////////


    public void onEvent(FeedEntityDeletedEvent event) {
        itemDeleted(event.getEventModel());
    }

    public void onEvent(FeedItemAddedEvent event) {
        feedItems.add(0, event.getFeedItem());
        view.refreshFeedItems(feedItems, !noMoreFeeds);
    }

    public void onEvent(FeedEntityChangedEvent event) {
        Queryable.from(feedItems).forEachR(item -> {
            if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
                item.setItem(event.getFeedEntity());
            }
        });

        view.refreshFeedItems(feedItems, !noMoreFeeds);
    }

    public void onEvent(FeedEntityCommentedEvent event) {
        Queryable.from(feedItems).forEachR(item -> {
            if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
                item.setItem(event.getFeedEntity());
            }
        });

        view.refreshFeedItems(feedItems, !noMoreFeeds);
    }

    public void onEvent(OnFeedReloadEvent event) {
        refreshFeed();
    }

    public void onEvent(ProfileClickedEvent event) {
        if (view.isVisibleOnScreen()) openUser(event.getUser());
    }

    protected void openUser(User user) {
        NavigationBuilder.create().with(activityRouter)
                .data(new UserBundle(user))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(routeCreator.createRoute(user.getId()));
    }

    public void onEvent(LikesPressedEvent event) {
        if (view.isVisibleOnScreen()) {
            FeedEntity model = event.getModel();
            DreamTripsRequest command = model.isLiked() ?
                    new UnlikeEntityCommand(model.getUid()) :
                    new LikeEntityCommand(model.getUid());
            doRequest(command, element -> itemLiked(model.getUid()));
        }
    }

    private void itemLiked(String uid) {
        Queryable.from(feedItems).forEachR(event -> {
            FeedEntity item = event.getItem();

            if (item.getUid().equals(uid)) {
                item.setLiked(!item.isLiked());
                int currentCount = item.getLikesCount();
                currentCount = item.isLiked() ? currentCount + 1 : currentCount - 1;
                item.setLikesCount(currentCount);
            }
        });

        view.refreshFeedItems(feedItems, !noMoreFeeds);
    }

    public void onEvent(DeletePostEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeletePostCommand(event.getEntity().getItem().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));
    }

    public void onEvent(DeletePhotoEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeletePhotoCommand(event.getEntity().getItem().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));

    }

    public void onEvent(DeleteBucketEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeleteBucketItemCommand(event.getEventModel().getItem().getUid()),
                    aVoid -> itemDeleted(event.getEventModel()));

    }

    public void onEvent(EditBucketEvent event) {
        BucketBundle bundle = new BucketBundle();
        bundle.setType(event.getType());
        bundle.setBucketItemUid(event.getUid());

        fragmentCompass.removeEdit();
        if (view.isTabletLandscape()) {
            fragmentCompass.disableBackStack();
            fragmentCompass.setContainerId(R.id.container_details_floating);
            fragmentCompass.showContainer();
            NavigationBuilder.create().with(fragmentCompass).data(bundle).attach(Route.BUCKET_EDIT);
        } else {
            bundle.setLock(true);
            NavigationBuilder.create().with(activityRouter).data(bundle).move(Route.BUCKET_EDIT);
        }
    }

    private void itemDeleted(FeedItem eventModel) {
        List<FeedItem> filteredItems = Queryable.from(feedItems)
                .filter(element -> !element.equals(eventModel))
                .toList();

        feedItems.clear();
        feedItems.addAll(filteredItems);

        view.refreshFeedItems(feedItems, !noMoreFeeds);
    }

    public void onEvent(LoadFlagEvent event) {
        doRequest(new GetFlagContentQuery(), flags -> event.getFeedCell().showFlagDialog(flags));
    }

    public void onEvent(FeedFlaggedEvent event) {
        doRequest(new FlagItemCommand(event.getEntity().getUid(), event.getNameOfReason()), aVoid -> {
        });
    }

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        void refreshFeedItems(List<FeedItem> events, boolean needLoader);

    }

}
