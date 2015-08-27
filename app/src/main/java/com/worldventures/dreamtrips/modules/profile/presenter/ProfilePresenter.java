package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedObjectChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;
import com.worldventures.dreamtrips.modules.friends.api.GetCirclesQuery;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.ReloadFeedModel;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnBucketListClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnCreatePostClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFeedReloadEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFriendsClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnTripImageClickedEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public abstract class ProfilePresenter<T extends ProfilePresenter.View, U extends User> extends Presenter<T> {

    public static final int HEADER_USER_POSITION = 0;
    public static final int HEADER_RELOAD_POSITION = 1;

    public static final int HEADER_COUNT = 2;

    protected U user;

    private int previousTotal = 0;
    private boolean loading = true;

    @Inject
    SnappyRepository snappyRepository;

    List<Circle> circles;
    private ReloadFeedModel reloadFeedModel = new ReloadFeedModel();


    public ProfilePresenter() {
    }

    public ProfilePresenter(U user) {
        this.user = user;
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        attachUserToView(user);
        addReloadFeedView();
        circles = snappyRepository.getCircles();
        loadCircles();
        loadProfile();
        loadFeed();
    }

    public abstract void openBucketList();

    public abstract void openTripImages();

    public void onRefresh() {
        loadProfile();
        loadFeed();
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

    public void loadFeed() {
        view.startLoading();
        if (featureManager.available(Feature.SOCIAL)) {
            resetLazyLoadFields();
            doRequest(getRefreshRequest(), this::refreshFeedItems, spiceException -> {
                reloadFeedModel.setVisible(true);
                view.getAdapter().notifyItemChanged(HEADER_RELOAD_POSITION);
                view.finishLoading();
            });
        }
    }

    public void makePost() {
        view.openPost();
    }

    protected abstract void loadProfile();

    public void openFriends() {
        if (featureManager.available(Feature.SOCIAL)) {
            if (circles.isEmpty()) {
                view.startLoading();
                doRequest(new GetCirclesQuery(), circles -> {
                    view.finishLoading();
                    saveCircles(circles);
                    openFriends();
                });
            } else {
                view.openFriends();
            }
        }
    }

    ///Circles

    private void loadCircles() {
        if (featureManager.available(Feature.SOCIAL))
            doRequest(new GetCirclesQuery(), this::saveCircles);
    }

    protected void saveCircles(List<Circle> circles) {
        this.circles = circles;
        snappyRepository.saveCircles(circles);
    }

    private void resetLazyLoadFields() {
        previousTotal = 0;
        loading = false;
    }

    private void addReloadFeedView() {
        view.getAdapter().remove(HEADER_RELOAD_POSITION);
        view.getAdapter().addItem(HEADER_RELOAD_POSITION, this.reloadFeedModel);
    }

    public void scrolled(int totalItemCount, int lastVisible) {
        if (featureManager.available(Feature.SOCIAL) && view.getAdapter().getItemCount() > HEADER_COUNT) {
            if (totalItemCount > 0 && totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
            if (!loading
                    && lastVisible == totalItemCount - 1) {
                loading = true;
                doRequest(getNextPageRequest(), this::addFeedItems);
            }
        }
    }


    public void onEvent(FeedObjectChangedEvent event) {
        view.getAdapter().itemUpdated(event.getFeedObject());
    }


    public void refreshFeedItems(List<ParentFeedModel> feedItems) {
        reloadFeedModel.setVisible(false);
        view.finishLoading();
        view.getAdapter().clear();
        view.getAdapter().addItems(HEADER_RELOAD_POSITION, Queryable.from(feedItems)
                .filter(ParentFeedModel::isSingle).map(element -> element.getItems().get(0)).toList());
    }

    public void addFeedItems(List<ParentFeedModel> feedItems) {
        view.finishLoading();
        view.getAdapter().addItems(Queryable.from(feedItems)
                .filter(ParentFeedModel::isSingle).map(element -> element.getItems().get(0)).toList());
    }

    public void onEvent(LikesPressedEvent event) {
        BaseEventModel model = event.getModel();
        DreamTripsRequest command = model.getItem().isLiked() ?
                new UnlikeEntityCommand(model.getItem().getUid()) :
                new LikeEntityCommand(model.getItem().getUid());
        doRequest(command, element -> itemLiked(model));
    }

    //TODO Refactor this after apperean release
    public void onEvent(EntityLikedEvent event) {
        BaseEventModel model = (BaseEventModel) Queryable.from(view.getAdapter().getItems())
                .firstOrDefault(element -> element instanceof BaseEventModel &&
                        (((BaseEventModel) element).getItem().getUid().equals(event.getId())));

        if (model != null) {
            itemLiked(model);
        }
    }

    private void itemLiked(BaseEventModel model) {
        model.getItem().setLiked(!model.getItem().isLiked());
        int likesCount = model.getItem().getLikesCount();

        if (model.getItem().isLiked()) likesCount++;
        else likesCount--;

        model.getItem().setLikesCount(likesCount);

        itemChanged(model);
    }

    private void itemChanged(BaseEventModel baseFeedModel) {
        view.getAdapter().itemUpdated(baseFeedModel);
    }

    protected abstract SpiceRequest<ArrayList<ParentFeedModel>> getRefreshRequest();

    protected abstract SpiceRequest<ArrayList<ParentFeedModel>> getNextPageRequest();

    public void onEvent(OnBucketListClickedEvent event) {
        if (event.getUserId() == user.getId()) {
            openBucketList();
        }
    }

    public void onEvent(OnTripImageClickedEvent event) {
        if (event.getUserId() == user.getId()) {
            openTripImages();
        }
    }

    public void onEvent(OnFriendsClickedEvent event) {
        openFriends();
    }

    public void onEvent(OnCreatePostClickEvent event) {
        makePost();
    }

    public void onEvent(OnFeedReloadEvent event) {
        loadFeed();
    }

    public void onEvent(FeedItemAddedEvent event) {
        view.insertItem(event.getBaseEventModel());
    }

    public interface View extends Presenter.View {
        Bundle getArguments();

        void startLoading();

        void finishLoading();

        BaseArrayListAdapter getAdapter();

        void openPost();

        void openFriends();

        void notifyUserChanged();

        void setUser(User user);

        void insertItem(BaseEventModel baseEventModel);
    }
}
