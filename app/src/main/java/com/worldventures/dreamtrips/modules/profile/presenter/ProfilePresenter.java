package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;
import android.support.annotation.StringRes;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedQuery;
import com.worldventures.dreamtrips.modules.feed.event.CommentsPressedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
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
    public static final int HEADERS_COUNT = 2;

    public static final int NEW_POST_POSITION = 2;


    protected U user;

    private int previousTotal = 0;
    private boolean loading = true;

    @Inject
    SnappyRepository snappyRepository;

    List<Circle> circles;
    private ReloadFeedModel reloadFeedModel = new ReloadFeedModel();

    protected DreamSpiceAdapterController<ParentFeedModel> adapterController = new DreamSpiceAdapterController<ParentFeedModel>() {
        @Override
        public SpiceRequest<ArrayList<ParentFeedModel>> getReloadRequest() {
            return getRefreshRequest();
        }

        @Override
        public SpiceRequest<ArrayList<ParentFeedModel>> getNextPageRequest(int currentCount) {
            return ProfilePresenter.this.getNextPageRequest(currentCount / GetFeedQuery.LIMIT);

            //TODO
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
            reloadFeedModel.setVisible(false);
        }

        @Override
        public void onFinish(LoadType type, List<ParentFeedModel> items, SpiceException spiceException) {
            if (adapterController != null) {
                view.finishLoading();
                if (spiceException != null) {
                    view.onFeedError();
                    if (view.getAdapter().getItems().size() <= HEADERS_COUNT) {
                        reloadFeedModel.setVisible(true);
                        view.getAdapter().notifyDataSetChanged();
                    }
                }
                if (type.equals(LoadType.RELOAD)) {
                    resetLazyLoadFields();
                }
            }
        }
    };

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
        checkPostShown();
    }

    @Override
    public void onResume() {
        if (view.getAdapter().getCount() <= HEADERS_COUNT) {
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
        }
    }

    private void checkPostShown() {
        if (snappyRepository.hasPost()) makePost();
    }

    public abstract void openBucketList();

    public abstract void openTripImages();

    public void onRefresh() {
        loadProfile();
    }

    protected void onProfileLoaded(U user) {
        attachUserToView(user);
        if (view.getAdapter().getCount() <= HEADERS_COUNT) {
            loadFeed();
        } else {
            view.finishLoading();
        }
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
        if (featureManager.available(Feature.SOCIAL))
            adapterController.reload();
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

    public void onEvent(CommentsPressedEvent event) {
        eventBus.cancelEventDelivery(event);
        view.openComments(event.getModel());
    }

    private void addReloadFeedView() {
        view.getAdapter().remove(HEADER_RELOAD_POSITION);
        view.getAdapter().addItem(HEADER_RELOAD_POSITION, this.reloadFeedModel);
    }

    public void scrolled(int totalItemCount, int lastVisible) {
        if (featureManager.available(Feature.SOCIAL)) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
            if (!loading
                    && lastVisible == totalItemCount - 1
                    && (totalItemCount - 1) % GetFeedQuery.LIMIT == 0) {
                loading = true;
                adapterController.loadNext();
            }
        }
    }

    protected abstract SpiceRequest<ArrayList<ParentFeedModel>> getRefreshRequest();

    protected abstract SpiceRequest<ArrayList<ParentFeedModel>> getNextPageRequest(int count);

    public void onEvent(OnBucketListClickedEvent event) {
        openBucketList();
    }

    public void onEvent(OnTripImageClickedEvent event) {
        openTripImages();
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

    public interface View extends Presenter.View {
        Bundle getArguments();

        void startLoading();

        void finishLoading();

        BaseArrayListAdapter getAdapter();

        void onFeedError();

        void setFriendButtonText(@StringRes int res);

        void openComments(BaseFeedModel baseFeedModel);

        void openPost();

        void openFriends();

        void showPostContainer();

        void notifyUserChanged();

        void setUser(User user);
    }
}
