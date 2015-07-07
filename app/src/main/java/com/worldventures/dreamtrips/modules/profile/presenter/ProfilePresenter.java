package com.worldventures.dreamtrips.modules.profile.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.GetCirclesQuery;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.List;

import javax.inject.Inject;

public abstract class ProfilePresenter<T extends ProfilePresenter.View> extends Presenter<T> {

    protected User user;

    @Inject
    SnappyRepository snappyRepository;

    public ProfilePresenter() {
    }

    public ProfilePresenter(User user) {
        this.user = user;
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        setUserProfileInfo();
        loadCircles();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    public abstract void openBucketList();

    public abstract void openTripImages();

    public void onRefresh() {
        loadProfile();
    }

    protected void setUserProfileInfo() {
        view.setUserName(user.getFullName());
        view.setDateOfBirth(DateTimeUtils.convertDateToString(user.getBirthDate(),
                DateFormat.getMediumDateFormat(context)));
        view.setEnrollDate(DateTimeUtils.convertDateToString(user.getEnrollDate(),
                DateFormat.getMediumDateFormat(context)));
        view.setUserId(user.getUsername());
        view.setFrom(user.getLocation());

        if (user.isGold())
            view.setGold();
        else if (user.isPlatinum())
            view.setPlatinum();
        else
            view.setMember();

        view.setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
        view.setCoverImage(Uri.parse(user.getBackgroundPhotoUrl()));
    }

    protected void onProfileLoaded(User user) {
        this.user = user;
        //
        setUserProfileInfo();
        view.finishLoading();
        view.setTripImagesCount(user.getTripImagesCount());
        view.setBucketItemsCount(user.getBucketListItemsCount());
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        onProfileError();
    }

    protected void onProfileError() {
        view.finishLoading();
    }

    protected abstract void loadProfile();

    public void openFriends() {
        activityRouter.openFriends();
    }

    ///Circles

    private void loadCircles() {
        doRequest(new GetCirclesQuery(), this::saveCircles);
    }

    private void saveCircles(List<Circle> circles) {
        snappyRepository.saveCircles(circles);
    }

    public interface View extends Presenter.View {
        Bundle getArguments();

        void startLoading();

        void finishLoading();

        void setAvatarImage(Uri uri);

        void setCoverImage(Uri uri);

        void setDateOfBirth(String format);

        void setFrom(String location);

        void setUserName(String username);

        void setUserId(String username);

        void setEnrollDate(String date);

        void setTripImagesCount(int count);

        void setTripsCount(int count);

        void setSocial(Boolean isEnabled);

        void setBucketItemsCount(int count);

        void setGold();

        void setPlatinum();

        void setMember();
    }
}
