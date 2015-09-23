package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
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
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.ProfileClickedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFeedReloadEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import icepick.State;


public abstract class BaseFeedPresenter<V extends BaseFeedPresenter.View> extends Presenter<V> {

    private int previousTotal = 0;
    private boolean loading = true;

    @State
    protected ArrayList<BaseEventModel> feedItems;

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
            view.refreshFeedItems(feedItems);
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

    protected abstract DreamTripsRequest<ArrayList<ParentFeedModel>> getRefreshFeedRequest(Date date);

    protected void refreshFeed() {
        view.startLoading();
        doRequest(getRefreshFeedRequest(Calendar.getInstance().getTime()),
                this::refreshFeedSucceed, this::refreshFeedError);
    }

    private void refreshFeedError(SpiceException exception) {
        super.handleError(exception);
        view.finishLoading();
    }

    protected void refreshFeedSucceed(List<ParentFeedModel> freshItems) {
        loading = false;
        view.finishLoading();
        feedItems.clear();
        feedItems.addAll(Queryable.from(freshItems)
                .filter(ParentFeedModel::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());

        view.refreshFeedItems(feedItems);
    }

    /////////////////////////////////////
    ////// Feed load more
    /////////////////////////////////////

    protected abstract DreamTripsRequest<ArrayList<ParentFeedModel>> getNextPageFeedRequest(Date date);

    public void scrolled(int totalItemCount, int lastVisible) {
        if (featureManager.available(Feature.SOCIAL)) {
            if (!loading
                    && lastVisible == totalItemCount - 1) {
                loading = true;
                loadMore();
            }
        }
    }

    private void loadMore() {
        if (feedItems.size() > 0) {
            doRequest(getNextPageFeedRequest(feedItems.get(feedItems.size() - 1).getCreatedAt()),
                    this::addFeedItems);
        }
    }

    protected void addFeedItems(List<ParentFeedModel> olderItems) {
        loading = false;
        feedItems.addAll(Queryable.from(olderItems)
                .filter(ParentFeedModel::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());

        view.refreshFeedItems(feedItems);
    }

    /////////////////////////////////////
    ////// Items changed events
    /////////////////////////////////////


    public void onEvent(FeedEntityDeletedEvent event) {
        itemDeleted(event.getEventModel());
    }

    public void onEvent(FeedItemAddedEvent event) {
        feedItems.add(0, event.getBaseEventModel());
        view.refreshFeedItems(feedItems);
    }

    public void onEvent(FeedEntityChangedEvent event) {
        Queryable.from(feedItems).forEachR(item -> {
            if (item.getItem().equals(event.getFeedEntity())) {
                event.getFeedEntity().updateSocialContent(item.getItem());

                item.setItem(event.getFeedEntity());
            }
        });

        view.refreshFeedItems(feedItems);
    }

    public void onEvent(FeedEntityCommentedEvent event) {
        Queryable.from(feedItems).forEachR(item -> {
            if (item.getItem().equals(event.getFeedEntity())) {
                item.setItem(event.getFeedEntity());
            }
        });

        view.refreshFeedItems(feedItems);
    }

    public void onEvent(OnFeedReloadEvent event) {
        refreshFeed();
    }

    public void onEvent(ProfileClickedEvent event) {
        if (view.isVisibleOnScreen()) openUser(event.getUser());
    }

    private void openUser(User user) {
        NavigationBuilder.create().with(activityRouter)
                .data(new UserBundle(user))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(routeCreator.createRoute(user.getId()));
    }

    public void onEvent(LikesPressedEvent event) {
        if (view.isVisibleOnScreen()) {
            BaseEventModel model = event.getModel();
            DreamTripsRequest command = model.getItem().isLiked() ?
                    new UnlikeEntityCommand(model.getItem().getUid()) :
                    new LikeEntityCommand(model.getItem().getUid());
            doRequest(command, element -> itemLiked(model.getItem().getUid()));
        }
    }

    private void itemLiked(String uid) {
        Queryable.from(feedItems).forEachR(event -> {
            IFeedObject item = event.getItem();

            if (item.getUid().equals(uid)) {
                item.setLiked(!item.isLiked());
                int currentCount = item.getLikesCount();
                currentCount = item.isLiked() ? currentCount + 1 : currentCount - 1;
                item.setLikesCount(currentCount);
            }
        });

        view.refreshFeedItems(feedItems);
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
        bundle.setBucketItemId(event.getUid());

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

    private void itemDeleted(BaseEventModel eventModel) {
        List<BaseEventModel> filteredItems = Queryable.from(feedItems)
                .filter(element -> !element.equals(eventModel))
                .toList();

        feedItems.clear();
        feedItems.addAll(filteredItems);

        view.refreshFeedItems(feedItems);
    }

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        void refreshFeedItems(List<BaseEventModel> events);
    }

}
