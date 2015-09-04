package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseFeedPresenter;
import com.worldventures.dreamtrips.modules.friends.api.GetCirclesQuery;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnBucketListClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnCreatePostClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFriendsClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnTripImageClickedEvent;

import java.util.List;

import javax.inject.Inject;

public abstract class ProfilePresenter<T extends ProfilePresenter.View, U extends User> extends BaseFeedPresenter<T> {

    public static final int HEADER_USER_POSITION = 0;
    public static final int HEADER_COUNT = 1;

    protected U user;

    @Inject
    SnappyRepository snappyRepository;

    List<Circle> circles;

    public ProfilePresenter() {
    }

    public ProfilePresenter(U user) {
        this.user = user;
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);

        attachUserToView(user);
        circles = snappyRepository.getCircles();
        loadCircles();
        loadProfile();
    }

    public abstract void openBucketList();

    public abstract void openTripImages();

    @Override
    public void onRefresh() {
        super.onRefresh();
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

    public interface View extends BaseFeedPresenter.View {
        Bundle getArguments();

        void openPost();

        void openFriends();

        void notifyUserChanged();

        void setUser(User user);
    }
}
