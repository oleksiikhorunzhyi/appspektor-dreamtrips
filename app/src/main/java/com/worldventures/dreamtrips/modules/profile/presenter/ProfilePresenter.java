package com.worldventures.dreamtrips.modules.profile.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.profile.api.GetProfileQuery;

import java.io.File;

public abstract class ProfilePresenter<T extends ProfilePresenter.View> extends Presenter<T> {

    protected User user;

    public ProfilePresenter() {
    }

    public ProfilePresenter(User user) {
        this.user = user;
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        setUserProfileInfo();
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
        view.setCoverImage(Uri.fromFile(new File(user.getCoverPath())));
    }

    protected void onProfileLoaded(User user) {
        this.user = user;
        //
        setUserProfileInfo();
        view.finishLoading();
        view.setTripImagesCount(user.getTripImagesCount());
        view.setBucketItemsCount(user.getBucketListItemsCount());
    }

    protected void loadProfile() {
        view.startLoading();
        doRequest(new GetProfileQuery(user), this::onProfileLoaded);
    }

    public void openFriends() {
        activityRouter.openFriends();
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

        void setBucketItemsCount(int count);

        void setGold();

        void setPlatinum();

        void setMember();
    }
}
