package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.DeleteItemHttpAction;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.api.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.DownloadPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnBucketListClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnCreatePostClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFriendsClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnTripImageClickedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DownloadImageCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public abstract class ProfilePresenter<T extends ProfilePresenter.View, U extends User> extends Presenter<T> {

    protected U user;
    protected List<Circle> circles;

    @State
    protected ArrayList<FeedItem> feedItems;
    @Inject
    protected FeedEntityManager entityManager;

    @Inject
    SnappyRepository snappyRepository;
    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    BucketInteractor bucketInteractor;

    public ProfilePresenter() {
    }

    public ProfilePresenter(U user) {
        this.user = user;
    }

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        if (savedState == null) feedItems = new ArrayList<>();
    }

    @Override
    public void onInjected() {
        super.onInjected();
        entityManager.setRequestingPresenter(this);
        circles = snappyRepository.getCircles();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFeed();
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        if (feedItems.size() != 0) {
            view.refreshFeedItems(feedItems);
        }
        //
        attachUserToView(user);
        loadProfile();
    }

    protected void onProfileLoaded(U user) {
        attachUserToView(user);
        view.finishLoading();
    }

    private void attachUserToView(U user) {
        this.user = user;
        view.setUser(this.user);
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.finishLoading();
    }

    public void makePost() {
        view.openPost();
    }

    protected abstract void loadProfile();

    public abstract void openBucketList();

    public abstract void openTripImages();

    public void openFriends() {
        if (featureManager.available(Feature.SOCIAL)) {
            view.openFriends();
        }
    }

    public void onEvent(OnBucketListClickedEvent event) {
        if (event.getUserId() == user.getId() && view.isVisibleOnScreen()) {
            openBucketList();
        }
    }

    public void onEvent(OnTripImageClickedEvent event) {
        if (event.getUserId() == user.getId() && view.isVisibleOnScreen()) {
            openTripImages();
        }
    }

    public void onEvent(OnFriendsClickedEvent event) {
        openFriends();
    }

    public void onEvent(OnCreatePostClickEvent event) {
        makePost();
    }

    public User getUser() {
        return user;
    }

    public void onRefresh() {
        refreshFeed();
        loadProfile();
    }

    public void onEvent(DownloadPhotoEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DownloadImageCommand(context, event.url));
    }

    public void onEvent(EditBucketEvent event) {
        if (!view.isVisibleOnScreen()) return;
        //
        BucketBundle bundle = new BucketBundle();
        bundle.setType(event.type());
        bundle.setBucketItem(event.bucketItem());

        view.showEdit(bundle);
    }

    public void onEvent(DeleteBucketEvent event) {
        if (view.isVisibleOnScreen()) {
            BucketItem item = event.getEntity();

            view.bind(bucketInteractor.deleteItemPipe()
                    .createObservable(new DeleteItemHttpAction(item.getUid()))
                    .observeOn(AndroidSchedulers.mainThread()))
                    .subscribe(new ActionStateSubscriber<DeleteItemHttpAction>()
                            .onSuccess(deleteItemAction -> itemDeleted(item)));
        }
    }

    private void itemDeleted(FeedEntity feedEntity) {
        List<FeedItem> filteredItems = Queryable.from(feedItems)
                .filter(element -> !element.getItem().equals(feedEntity))
                .toList();

        feedItems.clear();
        feedItems.addAll(filteredItems);

        view.refreshFeedItems(feedItems);
    }

    public void onEvent(FeedEntityDeletedEvent event) {
        itemDeleted(event.getEventModel());
    }

    public void onEvent(FeedItemAddedEvent event) {
        feedItems.add(0, event.getFeedItem());
        view.refreshFeedItems(feedItems);
    }

    public void onEvent(FeedEntityChangedEvent event) {
        Queryable.from(feedItems).forEachR(item -> {
            if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
                FeedEntity feedEntity = event.getFeedEntity();
                if (feedEntity.getOwner() == null) {
                    feedEntity.setOwner(item.getItem().getOwner());
                }
                item.setItem(feedEntity);
            }
        });

        view.refreshFeedItems(feedItems);
    }

    public void onEvent(FeedEntityCommentedEvent event) {
        Queryable.from(feedItems).forEachR(item -> {
            if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
                item.setItem(event.getFeedEntity());
            }
        });

        view.refreshFeedItems(feedItems);
    }

    public void onEvent(LikesPressedEvent event) {
        if (view.isVisibleOnScreen()) {
            FeedEntity model = event.getModel();
            if (model.isLiked()) {
                entityManager.unlike(model);
            } else {
                entityManager.like(model);
            }
        }
    }

    public void onEvent(EntityLikedEvent event) {
        itemLiked(event.getFeedEntity());
    }

    public void onEvent(DeletePostEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeletePostCommand(event.getEntity().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));
    }

    public void onEvent(DeletePhotoEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeletePhotoCommand(event.getEntity().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));

    }

    protected void loadMoreItemsError(SpiceException spiceException) {
        addFeedItems(new ArrayList<>());
    }

    protected abstract DreamTripsRequest<ArrayList<ParentFeedItem>> getRefreshFeedRequest(Date date);

    protected abstract DreamTripsRequest<ArrayList<ParentFeedItem>> getNextPageFeedRequest(Date date);

    public void loadNext() {
        if (feedItems.size() > 0) {
            doRequest(getNextPageFeedRequest(feedItems.get(feedItems.size() - 1).getCreatedAt()),
                    this::addFeedItems, this::loadMoreItemsError);
        }
    }

    private void itemLiked(FeedEntity feedEntity) {
        Queryable.from(feedItems).forEachR(feedItem -> {
            FeedEntity item = feedItem.getItem();
            if (item.getUid().equals(feedEntity.getUid())) {
                item.syncLikeState(feedEntity);
            }
        });

        view.refreshFeedItems(feedItems);
    }

    protected void refreshFeed() {
        view.startLoading();
        doRequest(getRefreshFeedRequest(Calendar.getInstance().getTime()),
                this::refreshFeedSucceed, this::refreshFeedError);
    }

    private void refreshFeedError(SpiceException exception) {
        super.handleError(exception);
        view.finishLoading();
        view.refreshFeedItems(feedItems);
    }

    protected void addFeedItems(List<ParentFeedItem> olderItems) {
        boolean noMoreElements = olderItems == null || olderItems.size() == 0;
        view.updateLoadingStatus(false, noMoreElements);
        //
        feedItems.addAll(Queryable.from(olderItems)
                .filter(ParentFeedItem::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());
        view.refreshFeedItems(feedItems);
    }

    protected void refreshFeedSucceed(List<ParentFeedItem> freshItems) {
        boolean noMoreElements = freshItems == null || freshItems.size() == 0;
        view.updateLoadingStatus(false, noMoreElements);
        //
        view.finishLoading();
        feedItems.clear();
        feedItems.addAll(Queryable.from(freshItems)
                .filter(ParentFeedItem::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());
        view.refreshFeedItems(feedItems);
    }

    public interface View extends RxView {
        
        Bundle getArguments();

        void openPost();

        void openFriends();

        void openTripImages(Route route, TripsImagesBundle tripImagesBundle);

        void openBucketList(Route route, ForeignBucketTabsBundle foreignBucketBundle);

        void notifyUserChanged();

        void setUser(User user);

        void startLoading();

        void finishLoading();

        void refreshFeedItems(List<FeedItem> events);

        void showEdit(BucketBundle bucketBundle);

        void updateLoadingStatus(boolean loading, boolean noMoreElements);
    }
}
