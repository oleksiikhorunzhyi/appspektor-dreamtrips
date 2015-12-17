package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseFeedPresenter;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnBucketListClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnCreatePostClickEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFriendsClickedEvent;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnTripImageClickedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;

import java.util.List;

import javax.inject.Inject;

public abstract class ProfilePresenter<T extends ProfilePresenter.View, U extends User> extends BaseFeedPresenter<T> {

    protected U user;
    protected List<Circle> circles;

    @Inject
    SnappyRepository snappyRepository;

    public ProfilePresenter() {
    }

    public ProfilePresenter(U user) {
        this.user = user;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        circles = snappyRepository.getCircles();
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);

        attachUserToView(user);
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

    public interface View extends BaseFeedPresenter.View {
        Bundle getArguments();

        void openPost();

        void openFriends();

        void openTripImages(Route route, TripsImagesBundle tripImagesBundle);

        void openBucketList(Route route, ForeignBucketTabsBundle foreignBucketBundle);

        void notifyUserChanged();

        void setUser(User user);
    }
}
